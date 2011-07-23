package com.gpl.rpg.AndorsTrail.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.gpl.rpg.AndorsTrail.util.Range;

public class CombatTraits {
	public static final int STAT_COMBAT_ATTACK_COST = 0;
	public static final int STAT_COMBAT_ATTACK_CHANCE = 1;
	public static final int STAT_COMBAT_CRITICAL_CHANCE = 2;
	public static final int STAT_COMBAT_CRITICAL_MULTIPLIER = 3;
	public static final int STAT_COMBAT_DAMAGE_POTENTIAL_MIN = 4;
	public static final int STAT_COMBAT_DAMAGE_POTENTIAL_MAX = 5;
	public static final int STAT_COMBAT_BLOCK_CHANCE = 6;
	public static final int STAT_COMBAT_DAMAGE_RESISTANCE = 7;

	public int attackCost;

	public int attackChance;
	public int criticalChance;
	public float criticalMultiplier;
	public final Range damagePotential;

	public int blockChance;
	public int damageResistance;
	
	public CombatTraits() {
		this.damagePotential = new Range();
	}
	public CombatTraits(CombatTraits copy) {
		this();
		set(copy);
	}
	public void set(CombatTraits copy) {
		if (copy == null) return;
		this.attackCost = copy.attackCost;
		this.attackChance = copy.attackChance;
		this.criticalChance = copy.criticalChance;
		this.criticalMultiplier = copy.criticalMultiplier;
		this.damagePotential.set(copy.damagePotential);
		this.blockChance = copy.blockChance;
		this.damageResistance = copy.damageResistance;
	}
	
	public boolean hasAttackChanceEffect() { return attackChance != 0; }
	public boolean hasAttackDamageEffect() { return damagePotential.max != 0; }
	public boolean hasBlockEffect() { return blockChance != 0; }
	public boolean hasCriticalChanceEffect() { return criticalChance != 0; }
	public boolean hasCriticalMultiplierEffect() { return criticalMultiplier != 0; }

	public int getAttacksPerTurn(final int maxAP) {
		return (int) Math.floor(maxAP / attackCost);
	}
	
	public int getCombatStats(int statID) {
		switch (statID) {
		case STAT_COMBAT_ATTACK_COST: return attackCost;
		case STAT_COMBAT_ATTACK_CHANCE: return attackChance;
		case STAT_COMBAT_CRITICAL_CHANCE: return criticalChance;
		case STAT_COMBAT_CRITICAL_MULTIPLIER: return (int) Math.floor(criticalMultiplier);
		case STAT_COMBAT_DAMAGE_POTENTIAL_MIN: return damagePotential.current;
		case STAT_COMBAT_DAMAGE_POTENTIAL_MAX: return damagePotential.max;
		case STAT_COMBAT_BLOCK_CHANCE: return blockChance;
		case STAT_COMBAT_DAMAGE_RESISTANCE: return damageResistance;
		}
		return 0;
	}

	
	// ====== PARCELABLE ===================================================================

	public CombatTraits(DataInputStream src, int fileversion) throws IOException {
		this.attackCost = src.readInt();
		this.attackChance = src.readInt();
		this.criticalChance = src.readInt();
		if (fileversion <= 20) {
			this.criticalMultiplier = src.readInt();
		} else {
			this.criticalMultiplier = src.readFloat();
		}
		this.damagePotential = new Range(src, fileversion);
		this.blockChance = src.readInt();
		this.damageResistance = src.readInt();
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeInt(attackCost);
		dest.writeInt(attackChance);
		dest.writeInt(criticalChance);
		dest.writeFloat(criticalMultiplier);
		damagePotential.writeToParcel(dest, flags);
		dest.writeInt(blockChance);
		dest.writeInt(damageResistance);
	}
}
