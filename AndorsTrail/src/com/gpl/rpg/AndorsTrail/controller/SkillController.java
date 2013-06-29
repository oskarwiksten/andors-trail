package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import android.util.FloatMath;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.AttackResult;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.ability.SkillInfo;
import com.gpl.rpg.AndorsTrail.model.ability.traits.AbilityModifierTraits;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemCategory;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropList.DropItem;
import com.gpl.rpg.AndorsTrail.util.ConstRange;

public final class SkillController {
	private final ControllerContext controllers;
	private final WorldContext world;

	public SkillController(ControllerContext controllers, WorldContext world) {
		this.controllers = controllers;
		this.world = world;
	}

	public void applySkillEffects(Player player) {
		player.attackChance += SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_CHANCE * player.getSkillLevel(SkillCollection.SKILL_WEAPON_CHANCE);
		player.damagePotential.addToMax(SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_DAMAGE_MAX * player.getSkillLevel(SkillCollection.SKILL_WEAPON_DMG));
		player.damagePotential.add(SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_DAMAGE_MIN * player.getSkillLevel(SkillCollection.SKILL_WEAPON_DMG), false);
		player.blockChance += SkillCollection.PER_SKILLPOINT_INCREASE_DODGE * player.getSkillLevel(SkillCollection.SKILL_DODGE);
		player.damageResistance += SkillCollection.PER_SKILLPOINT_INCREASE_BARKSKIN * player.getSkillLevel(SkillCollection.SKILL_BARKSKIN);
		if (player.hasCriticalSkillEffect()) {
			if (player.criticalSkill > 0) {
				player.criticalSkill += player.criticalSkill * SkillCollection.PER_SKILLPOINT_INCREASE_MORE_CRITICALS_PERCENT * player.getSkillLevel(SkillCollection.SKILL_MORE_CRITICALS) / 100;
			}
		}
		if (player.hasCriticalMultiplierEffect()) {
			player.criticalMultiplier += player.criticalMultiplier * SkillCollection.PER_SKILLPOINT_INCREASE_BETTER_CRITICALS_PERCENT * player.getSkillLevel(SkillCollection.SKILL_BETTER_CRITICALS) / 100;
		}
		controllers.actorStatsController.addActorMaxAP(player, SkillCollection.PER_SKILLPOINT_INCREASE_SPEED * player.getSkillLevel(SkillCollection.SKILL_SPEED), false);
		/*final int berserkLevel = player.getSkillLevel(Skills.SKILL_BERSERKER);
		if (berserkLevel > 0) {
			final int berserkHealth = player.health.max * Skills.BERSERKER_STARTS_AT_HEALTH_PERCENT / 100;
			if (player.health.current <= berserkHealth) {
				player.traits.attackChance += Skills.PER_SKILLPOINT_INCREASE_BERSERKER_WEAPON_CHANCE * berserkLevel;
				player.traits.damagePotential.addToMax(Skills.PER_SKILLPOINT_INCREASE_BERSERKER_WEAPON_DAMAGE_MAX * berserkLevel);
				player.traits.damagePotential.add(Skills.PER_SKILLPOINT_INCREASE_BERSERKER_WEAPON_DAMAGE_MIN * berserkLevel, false);
				player.traits.blockChance += Skills.PER_SKILLPOINT_INCREASE_BERSERKER_DODGE * berserkLevel;
			}
		}*/
	}
	
	public static int getDropChanceRollBias(DropItem item, Player player) {
		if (player == null) return 0;
		
		if (ItemTypeCollection.isGoldItemType(item.itemType.id)) {
			return getRollBias(item, player, SkillCollection.SKILL_COINFINDER, SkillCollection.PER_SKILLPOINT_INCREASE_COINFINDER_CHANCE_PERCENT);
		} else if (!item.itemType.isOrdinaryItem()) {
			return getRollBias(item, player, SkillCollection.SKILL_MAGICFINDER, SkillCollection.PER_SKILLPOINT_INCREASE_MAGICFINDER_CHANCE_PERCENT);
		} else {
			return 0;
		}
	}
	
	public static int getDropQuantityRollBias(DropItem item, Player player) {
		if (player == null) return 0;
		if (!ItemTypeCollection.isGoldItemType(item.itemType.id)) return 0;
		
		return getRollBias(item, player, SkillCollection.SKILL_COINFINDER, SkillCollection.PER_SKILLPOINT_INCREASE_COINFINDER_QUANTITY_PERCENT);
	}
	
	private static int getRollBias(DropItem item, Player player, int skill, int perSkillpointIncrease) {
		return getRollBias(item.chance, player, skill, perSkillpointIncrease);
	}
	
	private static int getRollBias(ConstRange chance, Player player, int skill, int perSkillpointIncrease) {
		int skillLevel = player.getSkillLevel(skill);
		if (skillLevel <= 0) return 0;
		return chance.current * skillLevel * perSkillpointIncrease / 100;
	}
	
	
	private static boolean canLevelupSkillWithQuest(Player player, SkillInfo skill) {
		final int playerSkillLevel = player.getSkillLevel(skill.id);
		if (skill.hasMaxLevel()) {
			if (playerSkillLevel >= skill.maxLevel) return false;
		}
		if (!skill.canLevelUpSkillTo(player, playerSkillLevel + 1)) return false;
		return true;
	}
	public static boolean canLevelupSkillManually(Player player, SkillInfo skill) {
		if (!player.hasAvailableSkillpoints()) return false;
		if (!canLevelupSkillWithQuest(player, skill)) return false;
		if (skill.levelupVisibility == SkillInfo.LEVELUP_TYPE_ONLY_BY_QUESTS) return false;
		if (skill.levelupVisibility == SkillInfo.LEVELUP_TYPE_FIRST_LEVEL_REQUIRES_QUEST) {
			if (!player.hasSkill(skill.id)) return false;
		}
		return true;
	}
	public void levelUpSkillManually(Player player, SkillInfo skill) {
		if (!canLevelupSkillManually(player, skill)) return;
		player.availableSkillIncreases -= 1;
		addSkillLevel(skill.id);
	}
	public boolean levelUpSkillByQuest(Player player, SkillInfo skill) {
		if (!canLevelupSkillWithQuest(player, skill)) return false;
		addSkillLevel(skill.id);
		return true;
	}

	public void addSkillLevel(int skillID) {
		Player player = world.model.player;
		player.skillLevels.put(skillID, player.skillLevels.get(skillID) + 1);
		controllers.actorStatsController.recalculatePlayerStats(player);
	}
	
	public static int getActorConditionEffectChanceRollBias(ActorConditionEffect effect, Player player) {
		if (effect.chance.isMax()) return 0;
		
		int result = 0;
		result += getActorConditionEffectChanceRollBiasFromResistanceSkills(effect, player);
		result += getActorConditionEffectChanceRollBias(effect, player, SkillCollection.SKILL_SHADOW_BLESS, SkillCollection.PER_SKILLPOINT_INCREASE_RESISTANCE_SHADOW_BLESS);
		return result;
	}
	
	private static int getActorConditionEffectChanceRollBiasFromResistanceSkills(ActorConditionEffect effect, Player player) {
		int skill;
		switch (effect.conditionType.conditionCategory) {
		case ActorConditionType.ACTORCONDITIONTYPE_MENTAL:
			skill = SkillCollection.SKILL_RESISTANCE_MENTAL; break;
		case ActorConditionType.ACTORCONDITIONTYPE_PHYSICAL_CAPACITY:
			skill = SkillCollection.SKILL_RESISTANCE_PHYSICAL_CAPACITY; break;
		case ActorConditionType.ACTORCONDITIONTYPE_BLOOD_DISORDER:
			skill = SkillCollection.SKILL_RESISTANCE_BLOOD_DISORDER; break;
		default:
			return 0;
		}
		
		return getActorConditionEffectChanceRollBias(effect, player, skill, SkillCollection.PER_SKILLPOINT_INCREASE_RESISTANCE_CHANCE_PERCENT);
	}
	
	private static int getActorConditionEffectChanceRollBias(ActorConditionEffect effect, Player player, int skill, int chanceIncreasePerSkillLevel) {
		// Note that the bias should be negative, making it less likely that the chance roll will succeed
		return getRollBias(effect.chance, player, skill, -chanceIncreasePerSkillLevel);
	}
	
	public static boolean rollForSkillChance(Player player, int skill, int chancePerSkillLevel) {
		int skillLevel = player.getSkillLevel(skill);
		if (skillLevel <= 0) return false;
		return Constants.roll100(chancePerSkillLevel * skillLevel);
	}
	private void addConditionToActor(Actor target, String conditionName, int magnitude, int duration) {
		ActorConditionType conditionType = world.actorConditionsTypes.getActorConditionType(conditionName);
		ActorConditionEffect effect = new ActorConditionEffect(conditionType, magnitude, duration, null);
		controllers.actorStatsController.applyActorCondition(target, effect);
	}
			
	public void applySkillEffectsFromPlayerAttack(AttackResult result, Monster monster) {
		if (!result.isHit) return;
		
		Player player = world.model.player;
		
		if (player.getAttackChance() - monster.getBlockChance() > SkillCollection.CONCUSSION_THRESHOLD) {
			if (rollForSkillChance(player, SkillCollection.SKILL_CONCUSSION, SkillCollection.PER_SKILLPOINT_INCREASE_CONCUSSION_CHANCE)) {
				addConditionToActor(monster, "concussion", 1, 5);
			}
		}
		
		if (result.isCriticalHit) {
			if (rollForSkillChance(player, SkillCollection.SKILL_CRIT2, SkillCollection.PER_SKILLPOINT_INCREASE_CRIT2_CHANCE)) {
				addConditionToActor(monster, "crit2", 1, 5);
			}
			
			if (rollForSkillChance(player, SkillCollection.SKILL_CRIT1, SkillCollection.PER_SKILLPOINT_INCREASE_CRIT1_CHANCE)) {
				addConditionToActor(monster, "crit1", 1, 5);
			}
		}
	}
	
	public void applySkillEffectsFromMonsterAttack(AttackResult result, Monster monster) {
		if (!result.isHit) {
			if (rollForSkillChance(world.model.player, SkillCollection.SKILL_TAUNT, SkillCollection.PER_SKILLPOINT_INCREASE_TAUNT_CHANCE)) {
				controllers.actorStatsController.changeActorAP(monster, -SkillCollection.TAUNT_AP_LOSS, false, false);
			}
		}
	}

	public static void applySkillEffectsFromItemProficiencies(Player player) {
		Player playerTraits = player;
		
		ItemType mainWeapon = ItemController.getMainWeapon(player);
		if (mainWeapon != null) {
			playerTraits.attackChance += SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_AC * getSkillLevelForItemType(player, mainWeapon);
		}
		
		final int unarmedLevel = player.getSkillLevel(SkillCollection.SKILL_WEAPON_PROFICIENCY_UNARMED);
		if (unarmedLevel > 0) {
			if (isUnarmed(player)) {
				playerTraits.attackChance += SkillCollection.PER_SKILLPOINT_INCREASE_UNARMED_AC * unarmedLevel;
				playerTraits.damagePotential.addToMax(SkillCollection.PER_SKILLPOINT_INCREASE_UNARMED_DMG * unarmedLevel);
				playerTraits.damagePotential.add(SkillCollection.PER_SKILLPOINT_INCREASE_UNARMED_DMG * unarmedLevel, false);
				playerTraits.blockChance += SkillCollection.PER_SKILLPOINT_INCREASE_UNARMED_BC * unarmedLevel;
			}
		}
		
		ItemType shield = player.inventory.wear[Inventory.WEARSLOT_SHIELD];
		if (shield != null && shield.isShield()) {
			playerTraits.damageResistance += SkillCollection.PER_SKILLPOINT_INCREASE_SHIELD_PROF_DR * getSkillLevelForItemType(player, shield);
		}
		
		final int unarmoredLevel = player.getSkillLevel(SkillCollection.SKILL_ARMOR_PROFICIENCY_UNARMORED);
		if (unarmoredLevel > 0) {
			if (isUnarmored(player)) {
				playerTraits.blockChance += SkillCollection.PER_SKILLPOINT_INCREASE_UNARMORED_BC * unarmoredLevel;
			}
		}
		
		int skillLevelLightArmor = player.getSkillLevel(SkillCollection.SKILL_ARMOR_PROFICIENCY_LIGHT);
		int skillLevelHeavyArmor = player.getSkillLevel(SkillCollection.SKILL_ARMOR_PROFICIENCY_HEAVY);
		for (int slot = 0; slot < Inventory.NUM_WORN_SLOTS; ++slot) {
			if (!Inventory.isArmorSlot(slot)) continue;
			
			ItemType itemType = player.inventory.wear[slot];
			if (itemType == null) continue;
			if (itemType.effects_equip == null) continue;
			
			int skill = getProficiencySkillForItemCategory(itemType.category);
			if (skill == SkillCollection.SKILL_ARMOR_PROFICIENCY_LIGHT) {
				if (skillLevelLightArmor > 0) {
					playerTraits.blockChance += getPercentage(itemType.effects_equip.stats.increaseBlockChance, SkillCollection.PER_SKILLPOINT_INCREASE_LIGHT_ARMOR_BC_PERCENT * skillLevelLightArmor, 0);
				}
			} else if (skill == SkillCollection.SKILL_ARMOR_PROFICIENCY_HEAVY) { 
				if (skillLevelHeavyArmor > 0) {
					playerTraits.blockChance += getPercentage(itemType.effects_equip.stats.increaseBlockChance, SkillCollection.PER_SKILLPOINT_INCREASE_HEAVY_ARMOR_BC_PERCENT * skillLevelHeavyArmor, 0);
					playerTraits.moveCost -= getPercentage(itemType.effects_equip.stats.increaseMoveCost, SkillCollection.PER_SKILLPOINT_INCREASE_HEAVY_ARMOR_MOVECOST_PERCENT * skillLevelHeavyArmor, 0);
					playerTraits.attackCost -= getPercentage(itemType.effects_equip.stats.increaseAttackCost, SkillCollection.PER_SKILLPOINT_INCREASE_HEAVY_ARMOR_ATKCOST_PERCENT * skillLevelHeavyArmor, 0);
				}
			}
		}
	}
	
	private static boolean isUnarmed(Player player) {
		if (hasItemWithWeight(player, Inventory.WEARSLOT_WEAPON)) return false;
		if (hasItemWithWeight(player, Inventory.WEARSLOT_SHIELD)) return false;
		return true;
	}
	private static boolean isUnarmored(Player player) {
		for (int slot = 0; slot < Inventory.NUM_WORN_SLOTS; ++slot) {
			if (!Inventory.isArmorSlot(slot)) continue;
			if (hasItemWithWeight(player, slot)) return false;
		}
		return true;
	}
	private static boolean hasItemWithWeight(Player player, int slot) {
		ItemType itemType = player.inventory.wear[slot];
		if (itemType == null) return false;
		if (itemType.category.getSize() == ItemCategory.SIZE_NONE) return false;
		return true;
	}

	private static int getSkillLevelForItemType(final Player player, ItemType itemType) {
		int skill = getProficiencySkillForItemCategory(itemType.category);
		if (skill == -1) return 0;
		return player.getSkillLevel(skill);
	}
	
	private static int getProficiencySkillForItemCategory(ItemCategory category) {
		final String itemCategoryID = category.id;
		if (category.isWeapon()) {
			if (itemCategoryID.equals("dagger") || itemCategoryID.equals("ssword")) 
				return SkillCollection.SKILL_WEAPON_PROFICIENCY_DAGGER;
			else if (itemCategoryID.equals("lsword") || itemCategoryID.equals("bsword")) 
				return SkillCollection.SKILL_WEAPON_PROFICIENCY_1HSWORD;
			else if (itemCategoryID.equals("2hsword")) 
				return SkillCollection.SKILL_WEAPON_PROFICIENCY_2HSWORD;
			else if (itemCategoryID.equals("axe") || itemCategoryID.equals("axe2h")) 
				return SkillCollection.SKILL_WEAPON_PROFICIENCY_AXE;
			else if (itemCategoryID.equals("club") || itemCategoryID.equals("staff") || itemCategoryID.equals("mace")
					|| itemCategoryID.equals("scepter") || itemCategoryID.equals("hammer") || itemCategoryID.equals("hammer2h"))
				return SkillCollection.SKILL_WEAPON_PROFICIENCY_BLUNT;
		} else if (category.isShield()) { 
			return SkillCollection.SKILL_ARMOR_PROFICIENCY_SHIELD;
		} else if (category.isArmor()) {
			int size = category.getSize();
			if (size == ItemCategory.SIZE_LIGHT) return SkillCollection.SKILL_ARMOR_PROFICIENCY_LIGHT;
			if (size == ItemCategory.SIZE_STD) return SkillCollection.SKILL_ARMOR_PROFICIENCY_LIGHT;
			if (size == ItemCategory.SIZE_LARGE) return SkillCollection.SKILL_ARMOR_PROFICIENCY_HEAVY;
		}
		return -1;
	}

	public static void applySkillEffectsFromFightingStyles(Player player) {
		Player playerTraits = player;
		ItemType mainHandItem = player.inventory.wear[Inventory.WEARSLOT_WEAPON];
		ItemType offHandItem = player.inventory.wear[Inventory.WEARSLOT_SHIELD];
		
		if (isWielding2HandItem(mainHandItem, offHandItem)) {
			int skillLevelFightStyle = player.getSkillLevel(SkillCollection.SKILL_FIGHTSTYLE_2HAND);
			int skillLevelSpecialization = player.getSkillLevel(SkillCollection.SKILL_SPECIALIZATION_2HAND);
			addPercentDamage(playerTraits, mainHandItem, skillLevelFightStyle * SkillCollection.PER_SKILLPOINT_INCREASE_FIGHTSTYLE_2HAND_DMG_PERCENT, 0);
			addPercentDamage(playerTraits, mainHandItem, skillLevelSpecialization * SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_2HAND_DMG_PERCENT, 0);
			addPercentAttackChance(playerTraits, mainHandItem, skillLevelSpecialization * SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_2HAND_AC_PERCENT, 0);
		}
		
		if (isWieldingWeaponAndShield(mainHandItem, offHandItem)) {
			int skillLevelFightStyle = player.getSkillLevel(SkillCollection.SKILL_FIGHTSTYLE_WEAPON_SHIELD);
			int skillLevelSpecialization = player.getSkillLevel(SkillCollection.SKILL_SPECIALIZATION_WEAPON_SHIELD);
			addPercentAttackChance(playerTraits, mainHandItem, skillLevelFightStyle * SkillCollection.PER_SKILLPOINT_INCREASE_FIGHTSTYLE_WEAPON_AC_PERCENT, 0);
			addPercentBlockChance(playerTraits, offHandItem, skillLevelFightStyle * SkillCollection.PER_SKILLPOINT_INCREASE_FIGHTSTYLE_SHIELD_BC_PERCENT, 0);
			addPercentAttackChance(playerTraits, mainHandItem, skillLevelSpecialization * SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_WEAPON_AC_PERCENT, 0);
			addPercentDamage(playerTraits, mainHandItem, skillLevelSpecialization * SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_WEAPON_DMG_PERCENT, 0);
		}
		
		if (isDualWielding(mainHandItem, offHandItem)) {
			int skillLevelFightStyle = player.getSkillLevel(SkillCollection.SKILL_FIGHTSTYLE_DUAL_WIELD);
			if (offHandItem.effects_equip != null) {
				AbilityModifierTraits offHandCombatTraits = offHandItem.effects_equip.stats;
				int attackCostMainHand = mainHandItem.effects_equip.stats.increaseAttackCost;
				int attackCostOffHand = offHandItem.effects_equip.stats.increaseAttackCost;
				int percent;
				if (skillLevelFightStyle == 2) {
					percent = SkillCollection.DUALWIELD_EFFICIENCY_LEVEL2;
					playerTraits.attackCost = Math.max(attackCostMainHand, attackCostOffHand);
				} else if (skillLevelFightStyle == 1) {
					percent = SkillCollection.DUALWIELD_EFFICIENCY_LEVEL1;
					playerTraits.attackCost = attackCostMainHand + getPercentage(attackCostOffHand, SkillCollection.DUALWIELD_LEVEL1_OFFHAND_AP_COST_PERCENT, 0);
				} else {
					percent = SkillCollection.DUALWIELD_EFFICIENCY_LEVEL0;
					playerTraits.attackCost = attackCostMainHand + attackCostOffHand;
				}

				final int increaseACFromWeaponProficiency = SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_AC * getSkillLevelForItemType(player, offHandItem);
				playerTraits.attackChance += getPercentage(increaseACFromWeaponProficiency, percent, 0);

				addPercentAttackChance(player, offHandItem, percent, 100);
				addPercentBlockChance(player, offHandItem, percent, 100);
				addPercentDamage(player, offHandItem, percent, 100);
				playerTraits.criticalSkill += getPercentage(offHandCombatTraits.increaseCriticalSkill, percent, 100);
			}
			
			int skillLevelSpecialization = player.getSkillLevel(SkillCollection.SKILL_SPECIALIZATION_DUAL_WIELD);
			addPercentAttackChance(playerTraits, mainHandItem, skillLevelSpecialization * SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_DUALWIELD_AC_PERCENT, 0);
			addPercentBlockChance(playerTraits, mainHandItem, skillLevelSpecialization * SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_DUALWIELD_BC_PERCENT, 0);
			addPercentAttackChance(playerTraits, offHandItem, skillLevelSpecialization * SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_DUALWIELD_AC_PERCENT, 0);
			addPercentBlockChance(playerTraits, offHandItem, skillLevelSpecialization * SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_DUALWIELD_BC_PERCENT, 0);
		}
	}

	private static void addPercentAttackChance(Player player, ItemType itemType, int percentForPositiveValues, int percentForNegativeValues) {
		if (itemType.effects_equip == null) return;
		player.attackChance += getPercentage(itemType.effects_equip.stats.increaseAttackChance, percentForPositiveValues, percentForNegativeValues);
	}

	private static void addPercentBlockChance(Player player, ItemType itemType, int percentForPositiveValues, int percentForNegativeValues) {
		if (itemType.effects_equip == null) return;
		player.blockChance += getPercentage(itemType.effects_equip.stats.increaseBlockChance, percentForPositiveValues, percentForNegativeValues);
	}

	private static void addPercentDamage(Player player, ItemType itemType, int percentForPositiveValues, int percentForNegativeValues) {
		if (itemType.effects_equip == null) return;
		player.damagePotential.addToMax(getPercentage(itemType.effects_equip.stats.increaseMaxDamage, percentForPositiveValues, percentForNegativeValues));
		player.damagePotential.add(getPercentage(itemType.effects_equip.stats.increaseMinDamage, percentForPositiveValues, percentForNegativeValues), false);
	}

	private static int getPercentage(int originalValue, int percentForPositiveValues, int percentForNegativeValues) {
		if (originalValue == 0) {
			return 0;
		} else if (originalValue > 0) {
			return (int) FloatMath.floor(originalValue * percentForPositiveValues / 100.0f);
		} else {
			return (int) FloatMath.floor(originalValue * percentForNegativeValues / 100.0f);
		}
	}

	public static boolean isDualWielding(ItemType mainHandItem, ItemType offHandItem) {
		if (mainHandItem == null) return false;
		if (offHandItem == null) return false;
		return mainHandItem.isWeapon() && offHandItem.isWeapon();
	}

	private static boolean isWielding2HandItem(ItemType mainHandItem, ItemType offHandItem) {
		if (mainHandItem == null) return false;
		if (offHandItem != null) return false;
		return mainHandItem.isTwohandWeapon();
	}

	private static boolean isWieldingWeaponAndShield(ItemType mainHandItem, ItemType offHandItem) {
		if (mainHandItem == null) return false;
		if (offHandItem == null) return false;
		return mainHandItem.isWeapon() && offHandItem.isShield();
	}
}
