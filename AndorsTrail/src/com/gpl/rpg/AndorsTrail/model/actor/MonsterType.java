package com.gpl.rpg.AndorsTrail.model.actor;

import com.gpl.rpg.AndorsTrail.model.item.DropList;

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
	public final String spawnGroup;
	public final int exp;
	public final DropList dropList;
	public final String phraseID;
	public final boolean isUnique; // Unique monsters are not respawned.
	public final String faction;
	public final int monsterClass;
	public final ActorTraits baseTraits;

	public MonsterType(
			String id, 
			String spawnGroup, 
			int exp, 
			DropList dropList, 
			String phraseID,
			boolean isUnique,
			String faction,
			int monsterClass,
			ActorTraits baseTraits) {
		this.id = id;
		this.spawnGroup = spawnGroup;
		this.exp = exp;
		this.dropList = dropList;
		this.phraseID = phraseID;
		this.faction = faction;
		this.isUnique = isUnique;
		this.monsterClass = monsterClass;
		this.baseTraits = baseTraits;
	}

	public boolean isImmuneToCriticalHits() {
		if (monsterClass == MONSTERCLASS_GHOST) return true;
		else if (monsterClass == MONSTERCLASS_UNDEAD) return true;
		else if (monsterClass == MONSTERCLASS_DEMON) return true;
		return false;
	}
}
