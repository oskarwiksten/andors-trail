package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.actor.Skills;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropList.DropItem;

public final class SkillController {
	public static void applySkillEffects(Player player) {
		player.traits.attackChance += Skills.PER_SKILLPOINT_INCREASE_WEAPON_CHANCE * player.getSkillLevel(Skills.SKILL_WEAPON_CHANCE);
		player.traits.damagePotential.addToMax(Skills.PER_SKILLPOINT_INCREASE_WEAPON_DAMAGE_MAX * player.getSkillLevel(Skills.SKILL_WEAPON_DMG));
		player.traits.damagePotential.add(Skills.PER_SKILLPOINT_INCREASE_WEAPON_DAMAGE_MIN * player.getSkillLevel(Skills.SKILL_WEAPON_DMG), false);
		player.traits.blockChance += Skills.PER_SKILLPOINT_INCREASE_DODGE * player.getSkillLevel(Skills.SKILL_DODGE);
		player.traits.damageResistance += Skills.PER_SKILLPOINT_INCREASE_BARKSKIN * player.getSkillLevel(Skills.SKILL_BARKSKIN);
		if (player.traits.hasCriticalChanceEffect()) {
			player.traits.criticalChance += player.traits.criticalChance * Skills.PER_SKILLPOINT_INCREASE_MORE_CRITICALS_PERCENT * player.getSkillLevel(Skills.SKILL_MORE_CRITICALS) / 100;
		}
		if (player.traits.hasCriticalMultiplierEffect()) {
			player.traits.criticalMultiplier += player.traits.criticalMultiplier * Skills.PER_SKILLPOINT_INCREASE_BETTER_CRITICALS_PERCENT * player.getSkillLevel(Skills.SKILL_BETTER_CRITICALS) / 100;
		}
		player.ap.addToMax(Skills.PER_SKILLPOINT_INCREASE_SPEED * player.getSkillLevel(Skills.SKILL_SPEED));
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
	
	public static int getChanceRollBias(DropItem item, Player player) {
		if (player == null) return 0;
		
		if (item.itemType.id == ItemTypeCollection.ITEMTYPE_GOLD) {
			return getRollBias(item, player, Skills.SKILL_COINFINDER, Skills.PER_SKILLPOINT_INCREASE_COINFINDER_CHANCE_PERCENT);
		} else if (!item.itemType.isOrdinaryItem()) {
			return getRollBias(item, player, Skills.SKILL_MAGICFINDER, Skills.PER_SKILLPOINT_INCREASE_MAGICFINDER_CHANCE_PERCENT);
		} else {
			return 0;
		}
	}
	
	public static int getQuantityRollBias(DropItem item, Player player) {
		if (player == null) return 0;
		if (item.itemType.id != ItemTypeCollection.ITEMTYPE_GOLD) return 0;
		
		return getRollBias(item, player, Skills.SKILL_COINFINDER, Skills.PER_SKILLPOINT_INCREASE_COINFINDER_QUANTITY_PERCENT);
	}
	
	private static int getRollBias(DropItem item, Player player, int skill, int perSkillpointIncrease) {
		int skillLevel = player.getSkillLevel(skill);
		if (skillLevel <= 0) return 0;
		return item.chance.current * skillLevel * perSkillpointIncrease / 100;
	}
}
