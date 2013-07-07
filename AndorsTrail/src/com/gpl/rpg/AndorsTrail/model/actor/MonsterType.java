package com.gpl.rpg.AndorsTrail.model.actor;

import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class MonsterType {
	public static final int MONSTERCLASS_HUMANOID = 0;
	public static final int MONSTERCLASS_INSECT = 1;
	public static final int MONSTERCLASS_DEMON = 2;
	public static final int MONSTERCLASS_CONSTRUCT = 3;
	public static final int MONSTERCLASS_ANIMAL = 4;
	public static final int MONSTERCLASS_GIANT = 5;
	public static final int MONSTERCLASS_UNDEAD = 6;
	public static final int MONSTERCLASS_REPTILE = 7;
	public static final int MONSTERCLASS_GHOST = 8;
	
	public final String id;
	public final String name;
	public final String spawnGroup;
	public final int exp;
	public final DropList dropList;
	public final String phraseID;
	public final boolean isUnique; // Unique monsters are not respawned.
	public final String faction;
	public final int monsterClass;
	public final int aggressionType;

	public final Size tileSize;
	public final int iconID;
	public final int maxAP;
	public final int maxHP;
	public final int moveCost;
	public final int attackCost;
	public final int attackChance;
	public final int criticalSkill;
	public final float criticalMultiplier;
	public final ConstRange damagePotential;
	public final int blockChance;
	public final int damageResistance;
	public final ItemTraits_OnUse[] onHitEffects;
	
	public MonsterType(
			String id, 
			String name,
			String spawnGroup, 
			int exp, 
			DropList dropList, 
			String phraseID,
			boolean isUnique,
			String faction,
			int monsterClass,
			int aggressionType,
			Size tileSize,
			int iconID,
			int maxAP,
			int maxHP,
			int moveCost,
			int attackCost,
			int attackChance,
			int criticalSkill,
			float criticalMultiplier,
			ConstRange damagePotential,
			int blockChance,
			int damageResistance,
			ItemTraits_OnUse[] onHitEffects) {
		this.id = id;
		this.name = name;
		this.spawnGroup = spawnGroup;
		this.exp = exp;
		this.dropList = dropList;
		this.phraseID = phraseID;
		this.faction = faction;
		this.isUnique = isUnique;
		this.monsterClass = monsterClass;
		this.aggressionType = aggressionType;
		this.tileSize = tileSize;
		this.iconID = iconID;
		this.maxAP = maxAP;
		this.maxHP = maxHP;
		this.moveCost = moveCost;
		this.attackCost = attackCost;
		this.attackChance = attackChance;
		this.criticalSkill = criticalSkill;
		this.criticalMultiplier = criticalMultiplier;
		this.damagePotential = damagePotential;
		this.blockChance = blockChance;
		this.damageResistance = damageResistance;
		this.onHitEffects = onHitEffects;
	}
	
	public static final int AGGRESSIONTYPE_NONE = 0;
	public static final int AGGRESSIONTYPE_HELP_OTHERS = 1; // Will move to help if the player attacks some other monster in the same spawn.
	public static final int AGGRESSIONTYPE_PROTECT_SPAWN = 2; // Will move to attack if the player stands inside the spawn.

	private static int getSuggestedAggressionType(int monsterClass) {
		switch (monsterClass) {
		case MONSTERCLASS_CONSTRUCT:
		case MONSTERCLASS_GIANT:
		case MONSTERCLASS_GHOST:
			return AGGRESSIONTYPE_NONE;
		case MONSTERCLASS_DEMON:
		case MONSTERCLASS_ANIMAL:
		case MONSTERCLASS_REPTILE:
		case MONSTERCLASS_INSECT:
			return AGGRESSIONTYPE_PROTECT_SPAWN;
		case MONSTERCLASS_UNDEAD:
		case MONSTERCLASS_HUMANOID:
			return AGGRESSIONTYPE_HELP_OTHERS;
		default:
			return AGGRESSIONTYPE_NONE;
		}
	}

	public boolean isImmuneToCriticalHits() {
		if (monsterClass == MONSTERCLASS_GHOST) return true;
		if (monsterClass == MONSTERCLASS_CONSTRUCT) return true;
		if (monsterClass == MONSTERCLASS_DEMON) return true;
		return false;
	}
	
	public boolean hasCombatStats() {
		if (attackCost != 10) return true; 
		if (attackChance != 0) return true;
		if (criticalSkill != 0) return true;
		if (criticalMultiplier != 0) return true;
		if (damagePotential != null) return true;
		if (blockChance != 0) return true;
		if (damageResistance != 0) return true;
		return false;
	}
}
