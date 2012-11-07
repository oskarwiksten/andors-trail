package com.gpl.rpg.AndorsTrail.model.actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import android.util.FloatMath;
import android.util.SparseIntArray;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.ActorStatsController;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;
import com.gpl.rpg.AndorsTrail.savegames.LegacySavegameFormatReaderForPlayer;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.Range;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class Player extends Actor {
	public static final int DEFAULT_PLAYER_MOVECOST = 6;
	public static final int DEFAULT_PLAYER_ATTACKCOST = 4;
	public static final Size DEFAULT_PLAYER_SIZE = new Size(1, 1);
	public final Coord lastPosition;
	public final Coord nextPosition;
	
	// TODO: Should be privates
	public int level;
	public final PlayerBaseTraits baseTraits = new PlayerBaseTraits();
	public final Range levelExperience; // ranges from 0 to the delta-amount of exp required for next level
	public final Inventory inventory;
	public int availableSkillIncreases = 0;
	public int useItemCost;
	public int reequipCost;
	
	private int totalExperience;
	private final HashMap<String, HashSet<Integer> > questProgress = new HashMap<String, HashSet<Integer> >();
	private final SparseIntArray skillLevels = new SparseIntArray();
	private String spawnMap;
	private String spawnPlace;
	private final HashMap<String, Integer> alignments = new HashMap<String, Integer>();
	
	public class PlayerBaseTraits {
		public int maxAP;
		public int maxHP;
		public int moveCost;
		public int attackCost;
		public int attackChance;
		public int criticalSkill;
		public float criticalMultiplier;
		public final Range damagePotential = new Range();
		public int blockChance;
		public int damageResistance;
		public int useItemCost;
		public int reequipCost;
	}

	public void resetStatsToBaseTraits() {
		this.ap.max = this.baseTraits.maxAP;
		this.health.max = this.baseTraits.maxHP;
		this.moveCost = this.baseTraits.moveCost;
		this.attackCost = this.baseTraits.attackCost;
		this.attackChance = this.baseTraits.attackChance;
		this.criticalSkill = this.baseTraits.criticalSkill;
		this.criticalMultiplier = this.baseTraits.criticalMultiplier;
		this.damagePotential.set(this.baseTraits.damagePotential);
		this.blockChance = this.baseTraits.blockChance;
		this.damageResistance = this.baseTraits.damageResistance;
		this.useItemCost = this.baseTraits.useItemCost;
		this.reequipCost = this.baseTraits.reequipCost;
	}
	
	public Player() {
		super(
			TileManager.CHAR_HERO
			, DEFAULT_PLAYER_SIZE
			, true // isPlayer
			, false // isImmuneToCriticalHits
		);
		this.lastPosition = new Coord();
		this.nextPosition = new Coord();
		this.levelExperience = new Range();
		this.inventory = new Inventory();
	}
	
	public void initializeNewPlayer(ItemTypeCollection types, DropListCollection dropLists, String name) {
		baseTraits.maxAP = 10;
		baseTraits.maxHP = 25;
		baseTraits.moveCost = DEFAULT_PLAYER_MOVECOST;
		baseTraits.attackCost = DEFAULT_PLAYER_ATTACKCOST;
		baseTraits.attackChance = 60;
		baseTraits.criticalSkill = 0;
		baseTraits.criticalMultiplier = 1;
		baseTraits.damagePotential.set(1, 1);
		baseTraits.blockChance = 0;
		baseTraits.damageResistance = 0;
		baseTraits.useItemCost = 5;
		baseTraits.reequipCost = 5;
		this.name = name;
		this.level = 1;
		this.totalExperience = 1;
		this.inventory.clear();
		this.questProgress.clear();
		this.skillLevels.clear();
		this.availableSkillIncreases = 0;
		this.alignments.clear();
		recalculateLevelExperience();
		
		Loot startItems = new Loot();
		dropLists.getDropList(DropListCollection.DROPLIST_STARTITEMS).createRandomLoot(startItems, this);
		inventory.add(startItems);
		
		if (AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
			this.spawnMap = "debugmap";
			this.spawnPlace = "start";
		} else {
			this.spawnMap = "home";
			this.spawnPlace = "rest";
		}
		
		ActorStatsController.recalculatePlayerCombatTraits(this);
	}
	
	public boolean hasExactQuestProgress(QuestProgress progress) { return hasExactQuestProgress(progress.questID, progress.progress); }
	public boolean hasExactQuestProgress(String questID, int progress) {
		if (!questProgress.containsKey(questID)) return false;
		return questProgress.get(questID).contains(progress); 
	}
	public boolean hasAnyQuestProgress(String questID) {
		return questProgress.containsKey(questID);
	}
	public boolean addQuestProgress(QuestProgress progress) {
		if (hasExactQuestProgress(progress.questID, progress.progress)) return false;
		if (!questProgress.containsKey(progress.questID)) questProgress.put(progress.questID, new HashSet<Integer>());
		questProgress.get(progress.questID).add(progress.progress); 
		return true; //Progress was added.
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
	
	public int getSkillLevel(int skillID) {
		return skillLevels.get(skillID);
	}
	public boolean hasSkill(int skillID) {
		return getSkillLevel(skillID) > 0;
	}
	public void addSkillLevel(int skillID) {
		skillLevels.put(skillID, skillLevels.get(skillID) + 1);
		ActorStatsController.recalculatePlayerCombatTraits(this);
	}
	public boolean nextLevelAddsNewSkillpoint() {
    	return thisLevelAddsNewSkillpoint(level + 1);
	}
	public static boolean thisLevelAddsNewSkillpoint(int level) {
    	return ((level - Constants.FIRST_SKILL_POINT_IS_GIVEN_AT_LEVEL) % Constants.NEW_SKILL_POINT_EVERY_N_LEVELS == 0);
	}
	public boolean hasAvailableSkillpoints() {
		return availableSkillIncreases > 0;
	}

	public int getAlignment(String faction) {
		Integer v = alignments.get(faction);
		if (v == null) return 0;
		return v;
	}
	public void addAlignment(String faction, int delta) {
		int newValue = getAlignment(faction) + delta;
		alignments.put(faction, newValue);
	}

	public void setSpawnPlace(String spawnMap, String spawnPlace) {
		this.spawnPlace = spawnPlace;
		this.spawnMap = spawnMap;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getReequipCost() { return reequipCost; }
	public int getUseItemCost() { return useItemCost; }
	public int getAvailableSkillIncreases() { return availableSkillIncreases; }
	public int getLevel() { return level; }
	public int getTotalExperience() { return totalExperience; }
	public int getGold() { return inventory.gold; }
	public String getSpawnMap() { return spawnMap; }
	public String getSpawnPlace() { return spawnPlace; }
	

	public int getActorStats(int statID) {
		switch (statID) {
		case ActorTraits.STAT_ACTOR_MAX_HP: return baseTraits.maxHP;
		case ActorTraits.STAT_ACTOR_MAX_AP: return baseTraits.maxAP;
		case ActorTraits.STAT_ACTOR_MOVECOST: return baseTraits.moveCost;
		}
		return 0;
	}
	public int getCombatStats(int statID) {
		switch (statID) {
		case CombatTraits.STAT_COMBAT_ATTACK_COST: return baseTraits.attackCost;
		case CombatTraits.STAT_COMBAT_ATTACK_CHANCE: return baseTraits.attackChance;
		case CombatTraits.STAT_COMBAT_CRITICAL_SKILL: return baseTraits.criticalSkill;
		case CombatTraits.STAT_COMBAT_CRITICAL_MULTIPLIER: return (int) FloatMath.floor(baseTraits.criticalMultiplier);
		case CombatTraits.STAT_COMBAT_DAMAGE_POTENTIAL_MIN: return baseTraits.damagePotential.current;
		case CombatTraits.STAT_COMBAT_DAMAGE_POTENTIAL_MAX: return baseTraits.damagePotential.max;
		case CombatTraits.STAT_COMBAT_BLOCK_CHANCE: return baseTraits.blockChance;
		case CombatTraits.STAT_COMBAT_DAMAGE_RESISTANCE: return baseTraits.damageResistance;
		}
		return 0;
	}
	
	// ====== PARCELABLE ===================================================================

	public static Player readFromParcel(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		/*	Player player;
		if (fileversion < 34) player = LegacySavegameFormatReaderForPlayer.readFromParcel_pre_v34(src, world, fileversion);
		else player = new Player(src, world, fileversion);
		*/
		Player player = new Player(src, world, fileversion);

		LegacySavegameFormatReaderForPlayer.upgradeSavegame(player, world, fileversion);
		
		return player;
	}
	
	/*
	public Player(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		super(src, world, fileversion, true, false, DEFAULT_PLAYER_SIZE, null);
		this.lastPosition = new Coord(src, fileversion);
		this.nextPosition = new Coord(src, fileversion);
		this.level = src.readInt();
		this.totalExperience = src.readInt();
		this.levelExperience = new Range();
		this.recalculateLevelExperience();
		this.inventory = new Inventory(src, world, fileversion);
		this.useItemCost = src.readInt();
		this.reequipCost = src.readInt();
		final int numSkills = src.readInt();
		for(int i = 0; i < numSkills; ++i) {
			final int skillID = src.readInt();
			this.skillLevels.put(skillID, src.readInt());
		}
		this.spawnMap = src.readUTF();
		this.spawnPlace = src.readUTF();

		final int numquests = src.readInt();
		for(int i = 0; i < numquests; ++i) {
			final String questID = src.readUTF();
			questProgress.put(questID, new HashSet<Integer>());
			final int numprogress = src.readInt();
			for(int j = 0; j < numprogress; ++j) {
				int progress = src.readInt();
				questProgress.get(questID).add(progress);
			}
		}
		
		this.availableSkillIncreases = src.readInt();
		
		final int numAlignments = src.readInt();
		for(int i = 0; i < numAlignments; ++i) {
			final String faction = src.readUTF();
			final int alignment = src.readInt();
			alignments.put(faction, alignment);
		}
	}
	*/
	
	public Player(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		this();
		
		this.name = src.readUTF();
		this.iconID = src.readInt();
		
		if (fileversion <= 33) {
			LegacySavegameFormatReaderForPlayer.readCombatTraitsPreV034(src, fileversion);
			/*this.iconID = */src.readInt();
			/*this.tileSize = */new Size(src, fileversion);
		}
		
		this.baseTraits.maxAP = src.readInt();
		this.baseTraits.maxHP = src.readInt();
		this.moveCost = src.readInt();
		
		this.baseTraits.attackCost = src.readInt();
		this.baseTraits.attackChance = src.readInt();
		this.baseTraits.criticalSkill = src.readInt();
		if (fileversion <= 20) {
			this.baseTraits.criticalMultiplier = src.readInt();
		} else {
			this.baseTraits.criticalMultiplier = src.readFloat();
		}
		this.baseTraits.damagePotential.readFromParcel(src, fileversion);
		this.baseTraits.blockChance = src.readInt();
		this.baseTraits.damageResistance = src.readInt();
		
		if (fileversion <= 16) {
			this.baseTraits.moveCost = this.moveCost;
		} else {
			this.baseTraits.moveCost = src.readInt();
		}
				
		this.ap.set(new Range(src, fileversion));
		this.health.set(new Range(src, fileversion));
		this.position.set(new Coord(src, fileversion));
		if (fileversion > 16) {
			final int n = src.readInt();
			for(int i = 0; i < n ; ++i) {
				this.conditions.add(new ActorCondition(src, world, fileversion));
			}
		}
		
		this.lastPosition.readFromParcel(src, fileversion);
		this.nextPosition.readFromParcel(src, fileversion);
		this.level = src.readInt();
		this.totalExperience = src.readInt();
		this.inventory.readFromParcel(src, world, fileversion);
		
		if (fileversion <= 13) LegacySavegameFormatReaderForPlayer.readQuestProgressPreV13(this, src, world, fileversion);

		this.useItemCost = src.readInt();
		this.reequipCost = src.readInt();
		final int size2 = src.readInt();
		for(int i = 0; i < size2; ++i) {
			if (fileversion <= 21) {
				this.skillLevels.put(i, src.readInt());
			} else {
				final int skillID = src.readInt();
				this.skillLevels.put(skillID, src.readInt());
			}
		}
		this.spawnMap = src.readUTF();
		this.spawnPlace = src.readUTF();

		if (fileversion > 13) {
			final int numquests = src.readInt();
			for(int i = 0; i < numquests; ++i) {
				final String questID = src.readUTF();
				this.questProgress.put(questID, new HashSet<Integer>());
				final int numprogress = src.readInt();
				for(int j = 0; j < numprogress; ++j) {
					int progress = src.readInt();
					this.questProgress.get(questID).add(progress);
				}
			}
		}
		
		this.availableSkillIncreases = 0;
		if (fileversion > 21) {
			this.availableSkillIncreases = src.readInt();
		}
		
		if (fileversion >= 26) {
			final int size3 = src.readInt();
			for(int i = 0; i < size3; ++i) {
				final String faction = src.readUTF();
				final int alignment = src.readInt();
				this.alignments.put(faction, alignment);
			}
		}
	}
	
	/*
	public Player(LegacySavegameData_Player savegameData) {
		super(savegameData, true);
		this.lastPosition = savegameData.lastPosition;
		this.nextPosition = savegameData.nextPosition;
		this.level = savegameData.level;
		this.totalExperience = savegameData.totalExperience;
		this.levelExperience = new Range();
		this.recalculateLevelExperience();
		this.inventory = savegameData.inventory;
		this.useItemCost = savegameData.useItemCost;
		this.reequipCost = savegameData.reequipCost;
		for(int i = 0; i < savegameData.skillLevels.size(); ++i) {
			this.skillLevels.put(savegameData.skillLevels.keyAt(i), savegameData.skillLevels.valueAt(i));
		}
		this.spawnMap = savegameData.spawnMap;
		this.spawnPlace = savegameData.spawnPlace;
		this.questProgress.putAll(savegameData.questProgress);
		this.availableSkillIncreases = savegameData.availableSkillIncreases;
		this.alignments.putAll(savegameData.alignments);
	}
	*/
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeUTF(name);
		dest.writeInt(iconID);
		dest.writeInt(baseTraits.maxAP);
		dest.writeInt(baseTraits.maxHP);
		dest.writeInt(moveCost);
		dest.writeInt(baseTraits.attackCost);
		dest.writeInt(baseTraits.attackChance);
		dest.writeInt(baseTraits.criticalSkill);
		dest.writeFloat(baseTraits.criticalMultiplier);
		baseTraits.damagePotential.writeToParcel(dest, flags);
		dest.writeInt(baseTraits.blockChance);
		dest.writeInt(baseTraits.damageResistance);
		dest.writeInt(baseTraits.moveCost);
		
		ap.writeToParcel(dest, flags);
		health.writeToParcel(dest, flags);
		position.writeToParcel(dest, flags);
		dest.writeInt(conditions.size());
		for (ActorCondition c : conditions) {
			c.writeToParcel(dest, flags);
		}
		lastPosition.writeToParcel(dest, flags);
		nextPosition.writeToParcel(dest, flags);
		dest.writeInt(level);
		dest.writeInt(totalExperience);
		inventory.writeToParcel(dest, flags);
		dest.writeInt(useItemCost);
		dest.writeInt(reequipCost);
		dest.writeInt(skillLevels.size());
		for (int i = 0; i < skillLevels.size(); ++i) {
			dest.writeInt(skillLevels.keyAt(i));
			dest.writeInt(skillLevels.valueAt(i));
		}
		dest.writeUTF(spawnMap);
		dest.writeUTF(spawnPlace);
		dest.writeInt(questProgress.size());
		for(Entry<String, HashSet<Integer> > e : questProgress.entrySet()) {
			dest.writeUTF(e.getKey());
			dest.writeInt(e.getValue().size());
			for(int progress : e.getValue()) {
				dest.writeInt(progress);
			}
		}
		dest.writeInt(availableSkillIncreases);
		dest.writeInt(alignments.size());
		for(Entry<String, Integer> e : alignments.entrySet()) {
			dest.writeUTF(e.getKey());
			dest.writeInt(e.getValue());
		}
	}
}

