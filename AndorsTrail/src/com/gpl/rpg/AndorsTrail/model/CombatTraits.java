package com.gpl.rpg.AndorsTrail.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.gpl.rpg.AndorsTrail.util.Range;

public class CombatTraits {
	public int attackCost;

	public int attackChance;
	public int criticalChance;
	public int criticalMultiplier;
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
		this.attackCost = copy.attackCost;
		this.attackChance = copy.attackChance;
		this.criticalChance = copy.criticalChance;
		this.criticalMultiplier = copy.criticalMultiplier;
		this.damagePotential.set(copy.damagePotential);
		this.blockChance = copy.blockChance;
		this.damageResistance = copy.damageResistance;
	}
	
	public boolean hasAttackChanceEffect() { return attackChance != 0; }
	public boolean hasAttackDamageEffect() { return damagePotential.max > 0; }
	public boolean hasBlockEffect() { return blockChance != 0; }
	public boolean hasCriticalEffect() { return criticalChance > 0 && criticalMultiplier > 1; }
	
	
	// ====== PARCELABLE ===================================================================

	public CombatTraits(DataInputStream src) throws IOException {
		this.attackCost = src.readInt();
		this.attackChance = src.readInt();
		this.criticalChance = src.readInt();
		this.criticalMultiplier = src.readInt();
		this.damagePotential = new Range(src);
		this.blockChance = src.readInt();
		this.damageResistance = src.readInt();
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeInt(attackCost);
		dest.writeInt(attackChance);
		dest.writeInt(criticalChance);
		dest.writeInt(criticalMultiplier);
		damagePotential.writeToParcel(dest, flags);
		dest.writeInt(blockChance);
		dest.writeInt(damageResistance);
	}
}
