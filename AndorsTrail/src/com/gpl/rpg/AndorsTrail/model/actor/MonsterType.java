package com.gpl.rpg.AndorsTrail.model.actor;

import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class MonsterType extends ActorTraits {
	public static final int MONSTERCLASS_HUMANOID = 0;
	public static final int MONSTERCLASS_INSECT = 1;
	public static final int MONSTERCLASS_DEMON = 2;
	public static final int MONSTERCLASS_CONSTRUCT = 3;
	public static final int MONSTERCLASS_ANIMAL = 4;
	public static final int MONSTERCLASS_GIANT = 5;
	public static final int MONSTERCLASS_UNDEAD = 6;
	public static final int MONSTERCLASS_REPTILE = 7;
	
	public final String id;
	public final String spawnGroup;
	public final int exp;
	public final DropList dropList;
	public final String phraseID;
	public final boolean isRespawnable;
	public final String faction;
	public final int monsterClass;

	public MonsterType(
			String id, 
			String name, 
			String spawnGroup, 
			int iconID, 
			Size tileSize, 
			int maxHP, 
			int maxAP, 
			int moveCost, 
			CombatTraits baseCombatTraits, 
			ItemTraits_OnUse onHitEffects,
			int exp, 
			DropList dropList, 
			String phraseID,
			boolean isRespawnable,
			String faction,
			int monsterClass) {
		super(iconID, tileSize, baseCombatTraits, moveCost, onHitEffects == null ? null : new ItemTraits_OnUse[] { onHitEffects });
		this.id = id;
		this.spawnGroup = spawnGroup;
		this.exp = exp;
		this.name = name;
		this.maxHP = maxHP;
		this.maxAP = maxAP;
		this.moveCost = moveCost;
		this.dropList = dropList;
		this.phraseID = phraseID;
		this.faction = faction;
		this.isRespawnable = isRespawnable;
		this.monsterClass = monsterClass;
	}
}
