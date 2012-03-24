package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.ability.SkillInfo;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropList.DropItem;
import com.gpl.rpg.AndorsTrail.util.ConstRange;

public final class SkillController {
	public static void applySkillEffects(Player player) {
		CombatTraits combatTraits = player.combatTraits;
		combatTraits.attackChance += SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_CHANCE * player.getSkillLevel(SkillCollection.SKILL_WEAPON_CHANCE);
		combatTraits.damagePotential.addToMax(SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_DAMAGE_MAX * player.getSkillLevel(SkillCollection.SKILL_WEAPON_DMG));
		combatTraits.damagePotential.add(SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_DAMAGE_MIN * player.getSkillLevel(SkillCollection.SKILL_WEAPON_DMG), false);
		combatTraits.blockChance += SkillCollection.PER_SKILLPOINT_INCREASE_DODGE * player.getSkillLevel(SkillCollection.SKILL_DODGE);
		combatTraits.damageResistance += SkillCollection.PER_SKILLPOINT_INCREASE_BARKSKIN * player.getSkillLevel(SkillCollection.SKILL_BARKSKIN);
		if (combatTraits.hasCriticalChanceEffect()) {
			combatTraits.criticalChance += combatTraits.criticalChance * SkillCollection.PER_SKILLPOINT_INCREASE_MORE_CRITICALS_PERCENT * player.getSkillLevel(SkillCollection.SKILL_MORE_CRITICALS) / 100;
		}
		if (combatTraits.hasCriticalMultiplierEffect()) {
			combatTraits.criticalMultiplier += combatTraits.criticalMultiplier * SkillCollection.PER_SKILLPOINT_INCREASE_BETTER_CRITICALS_PERCENT * player.getSkillLevel(SkillCollection.SKILL_BETTER_CRITICALS) / 100;
		}
		player.ap.addToMax(SkillCollection.PER_SKILLPOINT_INCREASE_SPEED * player.getSkillLevel(SkillCollection.SKILL_SPEED));
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
	
	
	public static boolean canLevelupSkill(Player player, SkillInfo skill) {
		if (player.availableSkillIncreases <= 0) return false;
		if (skill.isQuestSkill) return false;
		final int playerSkillLevel = player.getSkillLevel(skill.id);
		if (skill.hasMaxLevel()) {
			if (playerSkillLevel >= skill.maxLevel) return false;
		}
		if (!skill.canLevelUpSkillTo(player, playerSkillLevel + 1)) return false;
		return true;
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
}
