package com.gpl.rpg.AndorsTrail.model.actor;

import android.util.FloatMath;

import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.util.Range;

public class ActorTraits {
	public static final int STAT_ACTOR_MAX_HP = 0;
	public static final int STAT_ACTOR_MAX_AP = 1;
	public static final int STAT_ACTOR_MOVECOST = 2;

	public final int iconID;
	
	public int maxAP;
	public int maxHP;

	public int moveCost;
	public final int baseMoveCost;

	public int attackCost;
	public int attackChance;
	public int criticalSkill;
	public float criticalMultiplier;
	public final Range damagePotential;
	public int blockChance;
	public int damageResistance;
	
	public ItemTraits_OnUse[] onHitEffects;
	
	public ActorTraits(
			int iconID
			, int attackCost
			, int attackChance
			, int criticalSkill
			, float criticalMultiplier
			, Range damagePotential
			, int blockChance
			, int damageResistance
			, int standardMoveCost
			, ItemTraits_OnUse[] onHitEffects
			) {
		this.iconID = iconID;
		this.attackCost = attackCost;
		this.attackChance = attackChance;
		this.criticalSkill = criticalSkill;
		this.criticalMultiplier = criticalMultiplier;
		this.damagePotential = damagePotential;
		this.blockChance = blockChance;
		this.damageResistance = damageResistance;
		this.baseMoveCost = standardMoveCost;
		this.onHitEffects = onHitEffects;
	}
	
	public int getMovesPerTurn() { return (int) Math.floor(maxAP / moveCost); }
	public boolean hasAttackChanceEffect() { return attackChance != 0; }
	public boolean hasAttackDamageEffect() { return damagePotential.max != 0; }
	public boolean hasBlockEffect() { return blockChance != 0; }
	public boolean hasCriticalSkillEffect() { return criticalSkill != 0; }
	public boolean hasCriticalMultiplierEffect() { return criticalMultiplier != 0 && criticalMultiplier != 1; }
	public boolean hasCriticalAttacks() { return hasCriticalSkillEffect() && hasCriticalMultiplierEffect(); }
	public int getEffectiveCriticalChance() {
		if (criticalSkill <= 0) return 0;
		int v = (int) (-5 + 2 * FloatMath.sqrt(5*criticalSkill));
		if (v < 0) return 0;
		return v;
	}
	
	public int getActorStats(int statID) {
		switch (statID) {
		case STAT_ACTOR_MAX_HP: return maxHP;
		case STAT_ACTOR_MAX_AP: return maxAP;
		case STAT_ACTOR_MOVECOST: return moveCost;
		}
		return 0;
	}
	
	public int getCombatStats(int statID) {
		switch (statID) {
		case CombatTraits.STAT_COMBAT_ATTACK_COST: return attackCost;
		case CombatTraits.STAT_COMBAT_ATTACK_CHANCE: return attackChance;
		case CombatTraits.STAT_COMBAT_CRITICAL_SKILL: return criticalSkill;
		case CombatTraits.STAT_COMBAT_CRITICAL_MULTIPLIER: return (int) FloatMath.floor(criticalMultiplier);
		case CombatTraits.STAT_COMBAT_DAMAGE_POTENTIAL_MIN: return damagePotential.current;
		case CombatTraits.STAT_COMBAT_DAMAGE_POTENTIAL_MAX: return damagePotential.max;
		case CombatTraits.STAT_COMBAT_BLOCK_CHANCE: return blockChance;
		case CombatTraits.STAT_COMBAT_DAMAGE_RESISTANCE: return damageResistance;
		}
		return 0;
	}
}
