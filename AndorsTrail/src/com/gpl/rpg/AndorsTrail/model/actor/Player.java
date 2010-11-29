package com.gpl.rpg.AndorsTrail.model.actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.resource.TileStore;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.Range;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class Player extends Actor {
	public final Coord lastPosition;
	public final Coord nextPosition;
	public int level;
	public int totalExperience;
	public final Range levelExperience; // ranges from 0 to the delta-amount of exp required for next level
	public final Inventory inventory;
	private final HashSet<String> keys = new HashSet<String>();
	public int useItemCost;
	public int reequipCost;
	public final int[] skillLevels = new int[Skills.NUM_SKILLS];
	public String spawnMap;
	public String spawnPlace;
	
	public Player() {
		super(new ActorTraits(TileStore.CHAR_HERO, new Size(1, 1), new CombatTraits()));
		this.lastPosition = new Coord();
		this.nextPosition = new Coord();
		this.levelExperience = new Range();
		this.inventory = new Inventory();
	}
	
	public void initializeNewPlayer(ItemTypeCollection types, DropListCollection dropLists, String name) {
		CombatTraits combat = new CombatTraits();
		combat.attackCost = 3;
		combat.attackChance = 60;
		combat.criticalChance = 0;
		combat.criticalMultiplier = 1;
		combat.damagePotential.set(1, 1);
		combat.blockChance = 0;
		combat.damageResistance = 0;

		traits.baseCombatTraits.set(combat);
		
		traits.maxAP = 10;
		traits.maxHP = 25;
		
		traits.name = name;
		traits.moveCost = 6;
		useItemCost = 5;
		reequipCost = 5;

		level = 1;
		totalExperience = 1;
		recalculateLevelExperience();
		
		Loot startItems = new Loot();
		dropLists.getDropList(DropListCollection.DROPLIST_STARTITEMS).createRandomLoot(startItems);
		inventory.add(startItems);
		
		if (AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
			spawnMap = "debugmap";
			spawnPlace = "start";
		} else {
			spawnMap = "home";
			spawnPlace = "rest";
		}
		
		recalculateCombatTraits();
	}
	
	public boolean hasKey(String key) { return keys.contains(key); }
	public void addKey(String key) { if (!keys.contains(key)) keys.add(key); }
	
	public void recalculateCombatTraits() {
		traits.set(traits.baseCombatTraits);
		inventory.apply(traits);
	}

	public void recalculateLevelExperience() {
		int experienceRequiredToReachThisLevel = getRequiredExperience(level);
		levelExperience.set(getRequiredExperienceForNextLevel(level), totalExperience - experienceRequiredToReachThisLevel);
	}
	public void addExperience(int v) {
		totalExperience += v;
		levelExperience.add(v, true);
	}
	

	
	private static int getRequiredExperience(int currentLevel) {
		int v = 0;
		for(int i = 1; i < currentLevel; ++i) {
			v += getRequiredExperienceForNextLevel(i);
		}
		return v;
	}
	private static final int EXP_base = 55;
	private static final int EXP_D = 400;
	private static final int EXP_powbase = 2;
	private static int getRequiredExperienceForNextLevel(int currentLevel) {
		return (int) (EXP_base * Math.pow(currentLevel, EXP_powbase + currentLevel/EXP_D));
	}

	public boolean canLevelup() {
		return levelExperience.isMax();
	}
	
	
	// ====== PARCELABLE ===================================================================

	public Player(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		super(src, world, fileversion);
		this.lastPosition = new Coord(src, fileversion);
		this.nextPosition = new Coord(src, fileversion);
		this.level = src.readInt();
		this.totalExperience = src.readInt();
		this.levelExperience = new Range();
		this.recalculateLevelExperience();
		this.inventory = new Inventory(src, world, fileversion);
		this.keys.clear();
		final int size1 = src.readInt();
		for(int i = 0; i < size1; ++i) {
			this.keys.add(src.readUTF());
		}
		this.useItemCost = src.readInt();
		this.reequipCost = src.readInt();
		final int size2 = src.readInt();
		for(int i = 0; i < size2; ++i) {
			this.skillLevels[i] = src.readInt();
		}
		this.spawnMap = src.readUTF();
		this.spawnPlace = src.readUTF();
		
		if (fileversion <= 12) {
			this.useItemCost = 5;
			this.health.max += 5;
			this.health.current += 5;
			this.traits.maxHP += 5;
		}
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		super.writeToParcel(dest, flags);
		lastPosition.writeToParcel(dest, flags);
		nextPosition.writeToParcel(dest, flags);
		dest.writeInt(level);
		dest.writeInt(totalExperience);
		inventory.writeToParcel(dest, flags);
		dest.writeInt(keys.size());
		for (String k : keys) {
			dest.writeUTF(k);
		}
		dest.writeInt(useItemCost);
		dest.writeInt(reequipCost);
		dest.writeInt(Skills.NUM_SKILLS);
		for(int i = 0; i < Skills.NUM_SKILLS; ++i) {
			dest.writeInt(this.skillLevels[i]);
		}
		dest.writeUTF(spawnMap);
		dest.writeUTF(spawnPlace);
	}
}

