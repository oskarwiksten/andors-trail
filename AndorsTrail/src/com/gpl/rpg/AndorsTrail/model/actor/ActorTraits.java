package com.gpl.rpg.AndorsTrail.model.actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.util.FloatMath;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.savegames.LegacySavegameFormatReaderForPlayer.LegacySavegameData_Actor;
import com.gpl.rpg.AndorsTrail.util.Range;
import com.gpl.rpg.AndorsTrail.util.Size;

public class ActorTraits {
	public static final int STAT_ACTOR_MAX_HP = 0;
	public static final int STAT_ACTOR_MAX_AP = 1;
	public static final int STAT_ACTOR_MOVECOST = 2;

	public final int iconID;
	public final Size tileSize;
	
	public int maxAP;
	public int maxHP;

	public String name;
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
			, Size tileSize
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
		this.tileSize = tileSize;
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
	
	// ====== PARCELABLE ===================================================================

	public ActorTraits(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		this.iconID = src.readInt();
		this.tileSize = new Size(src, fileversion);
		this.maxAP = src.readInt();
		this.maxHP = src.readInt();
		this.name = src.readUTF();
		this.moveCost = src.readInt();
		this.attackCost = src.readInt();
		this.attackChance = src.readInt();
		this.criticalSkill = src.readInt();
		this.criticalMultiplier = src.readFloat();
		this.damagePotential = new Range(src, fileversion);
		this.blockChance = src.readInt();
		this.damageResistance = src.readInt();
		this.baseMoveCost = src.readInt();
	}
	
	public ActorTraits(LegacySavegameData_Actor savegameData) {
		this.iconID = savegameData.iconID;
		this.tileSize = savegameData.tileSize;
		this.maxAP = savegameData.maxAP;
		this.maxHP = savegameData.maxHP;
		this.name = savegameData.name;
		this.moveCost = savegameData.moveCost;
		this.attackCost = savegameData.baseAttackCost;
		this.attackChance = savegameData.baseAttackChance;
		this.criticalSkill = savegameData.baseCriticalSkill;
		this.criticalMultiplier = savegameData.baseCriticalMultiplier;
		this.damagePotential = savegameData.baseDamagePotential;
		this.blockChance = savegameData.baseBlockChance;
		this.damageResistance = savegameData.baseDamageResistance;
		this.baseMoveCost = savegameData.baseMoveCost;
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeInt(iconID);
		tileSize.writeToParcel(dest, flags);
		dest.writeInt(maxAP);
		dest.writeInt(maxHP);
		dest.writeUTF(name);
		dest.writeInt(moveCost);
		dest.writeInt(attackCost);
		dest.writeInt(attackChance);
		dest.writeInt(criticalSkill);
		dest.writeFloat(criticalMultiplier);
		damagePotential.writeToParcel(dest, flags);
		dest.writeInt(blockChance);
		dest.writeInt(damageResistance);
		dest.writeInt(baseMoveCost);
	}
}
