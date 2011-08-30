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
	public boolean hasCriticalMultiplierEffect() { return criticalMultiplier != 0 && criticalMultiplier != 1; }
	public boolean hasCriticalAttacks() { return hasCriticalChanceEffect() && hasCriticalMultiplierEffect(); }

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
	
	public int calculateCost(boolean isWeapon) {
		final int costBC = (int) (3*Math.pow(Math.max(0, blockChance), 2.5) + 28*blockChance);
		final int costAC = (int) (0.4*Math.pow(Math.max(0,attackChance), 2.5) - 6*Math.pow(Math.abs(Math.min(0,attackChance)),2.7));
		final int costAP = isWeapon ?
				(int) (0.2*Math.pow(10.0f/attackCost, 8) - 25*attackCost)
				: -3125 * attackCost;
		final int costDR = 1325*damageResistance;
		final int costDMG_Min = isWeapon ?
				(int) (10*Math.pow(Math.max(0, damagePotential.current), 2.5))
				:(int) (10*Math.pow(Math.max(0, damagePotential.current), 3) + damagePotential.current*80);
		final int costDMG_Max = isWeapon ?
				(int) (2*Math.pow(Math.max(0, damagePotential.max), 2.1))
				:(int) (2*Math.pow(Math.max(0, damagePotential.max), 3) + damagePotential.max*20);
		final int costCC = (int) (2.2*Math.pow(criticalChance, 3));
		final int costCM = (int) (50*Math.pow(Math.max(0, criticalMultiplier), 2));
		
		return costBC + costAC + costAP + costDR + costDMG_Min + costDMG_Max + costCC + costCM;
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
