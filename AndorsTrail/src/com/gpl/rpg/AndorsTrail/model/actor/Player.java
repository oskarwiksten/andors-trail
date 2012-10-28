package com.gpl.rpg.AndorsTrail.model.actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import android.util.SparseIntArray;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.ActorStatsController;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;
import com.gpl.rpg.AndorsTrail.savegames.LegacySavegameFormatReaderForPlayer;
import com.gpl.rpg.AndorsTrail.savegames.LegacySavegameFormatReaderForPlayer.LegacySavegameData_Player;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.Range;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class Player extends Actor {
	public static final int DEFAULT_PLAYER_MOVECOST = 6;
	public static final int DEFAULT_PLAYER_ATTACKCOST = 4;
	public static final Size DEFAULT_PLAYER_SIZE = new Size(1, 1);
	public final Coord lastPosition;
	public final Coord nextPosition;
	public int level;
	public int totalExperience;
	public final Range levelExperience; // ranges from 0 to the delta-amount of exp required for next level
	public final Inventory inventory;
	private final HashMap<String, HashSet<Integer> > questProgress = new HashMap<String, HashSet<Integer> >();
	public int useItemCost;
	public int reequipCost;
	private final SparseIntArray skillLevels = new SparseIntArray();
	public String spawnMap;
	public String spawnPlace;
	public int availableSkillIncreases = 0;
	private final HashMap<String, Integer> alignments = new HashMap<String, Integer>();
	
	public Player() {
		super(
			new ActorTraits(
				TileManager.CHAR_HERO
				, 0  // attackCost
				, 0  // attackChance
				, 0  // criticalSkill
				, 0  // criticalMultiplier
				, new Range() // damagePotential
				, 0  // blockChance
				, 0  // damageResistance
				, DEFAULT_PLAYER_MOVECOST
				, null)
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
		baseTraits.attackCost = DEFAULT_PLAYER_ATTACKCOST;
		baseTraits.attackChance = 60;
		baseTraits.criticalSkill = 0;
		baseTraits.criticalMultiplier = 1;
		baseTraits.damagePotential.set(1, 1);
		baseTraits.blockChance = 0;
		baseTraits.damageResistance = 0;
		baseTraits.maxAP = 10;
		baseTraits.maxHP = 25;
		this.name = name;
		baseTraits.moveCost = DEFAULT_PLAYER_MOVECOST;
		useItemCost = 5;
		reequipCost = 5;

		level = 1;
		totalExperience = 1;
		availableSkillIncreases = 0;
		skillLevels.clear();
		alignments.clear();
		recalculateLevelExperience();
		
		Loot startItems = new Loot();
		dropLists.getDropList(DropListCollection.DROPLIST_STARTITEMS).createRandomLoot(startItems, this);
		inventory.add(startItems);
		
		if (AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
			spawnMap = "debugmap";
			spawnPlace = "start";
		} else {
			spawnMap = "home";
			spawnPlace = "rest";
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

	
	
	// ====== PARCELABLE ===================================================================

	public static Player readFromParcel(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		Player player;
		if (fileversion < 34) player = LegacySavegameFormatReaderForPlayer.readFromParcel_pre_v34(src, world, fileversion);
		else player = new Player(src, world, fileversion);

		LegacySavegameFormatReaderForPlayer.upgradeSavegame(player, world, fileversion);
		
		return player;
	}
	
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
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		super.writeToParcel(dest, flags);
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

