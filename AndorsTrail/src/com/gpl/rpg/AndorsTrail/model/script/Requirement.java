package com.gpl.rpg.AndorsTrail.model.script;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.resource.parsers.ResourceParserUtils;
import com.gpl.rpg.AndorsTrail.util.L;

public final class Requirement {
	public static enum RequirementType {
		questProgress
		,questLatestProgress // Highest quest stage reached must match.
		,inventoryRemove	// Player must have item(s) in inventory. Items will be removed when selecting reply.
		,inventoryKeep		// Player must have item(s) in inventory. Items will NOT be removed when selecting reply.
		,wear				// Player must be wearing item(s). Items will NOT be removed when selecting reply.
		,skillLevel			// Player needs to have a specific skill equal to or above a certain level
		,killedMonster
		,timerElapsed
		,consumedLess
		,consumedMore
		,bonemealsLess
		,bonemealsMore
	}
	
	public final RequirementType requireType;
	public final String requireID;
	public final int value;

	public Requirement(RequirementType requireType, String requireID, int value) {
		this.requireType = requireType;
		this.requireID = requireID;
		this.value = value;
	}
	
	public static Requirement fromString(String s) {
		String[] fields = s.split(":");
		if (fields.length == 2) {
			int value = ResourceParserUtils.parseInt(fields[1],0);
			return new Requirement(RequirementType.questProgress, fields[0], value);
		} else if (fields.length == 3) {
			int value = ResourceParserUtils.parseInt(fields[2],0);
			RequirementType type = RequirementType.valueOf(fields[0]);
			if (type != null) {
				return new Requirement(type, fields[1], value);
			} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				L.log("WARNING : Requirement type \""+fields[0]+"\" unknown for Requirement \""+s+"\"");
			}
		} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Requirement \"" + s + "\" does not match expected format.");
			
		}
		
		return null;
	}
	
	public boolean canFulfillRequirement(WorldContext w) {
		Player p = w.model.player;
		switch (this.requireType) {
		case questProgress : return p.hasExactQuestProgress(requireID, value);
		case questLatestProgress : return p.isLatestQuestProgress(requireID, value);
		case inventoryRemove :
		case inventoryKeep : 
			if (ItemTypeCollection.isGoldItemType(requireID)) {
				return p.inventory.gold >= value;
			} else {
				return p.inventory.hasItem(requireID, value);
			}
		case wear : return p.inventory.isWearing(requireID, value);
		case skillLevel : return p.getSkillLevel(SkillCollection.SkillID.valueOf(requireID)) >= value;
		case killedMonster : return w.model.statistics.getNumberOfKillsForMonsterType(requireID) >= value;
		case timerElapsed : return w.model.worldData.hasTimerElapsed(requireID, value);
		case consumedLess : 
			if (ItemTypeCollection.isGoldItemType(requireID)) {
				return w.model.statistics.getSpentGold() <= value;
			} else {
				return  w.model.statistics.getNumberOfUsedItem(requireID) <= value;
			}
		case consumedMore :
			if (ItemTypeCollection.isGoldItemType(requireID)) {
				return w.model.statistics.getSpentGold() >= value;
			} else {
				return  w.model.statistics.getNumberOfUsedItem(requireID) >= value;
			}
		case bonemealsLess : return w.model.statistics.getNumberOfUsedBonemealPotions() <= value;
		case bonemealsMore : return w.model.statistics.getNumberOfUsedBonemealPotions() >= value;
		default : return false;
		}
	}
	
	public void requirementFulfilled(Player p) {
		L.log("Entering requirement applying");
		switch (requireType) {
		case inventoryRemove : 
			if (ItemTypeCollection.isGoldItemType(requireID)) {
				p.inventory.gold -= value;
			} else {
				p.inventory.removeItem(requireID, value);
			}
		default : return;
		}
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer(requireType.toString());
		buf.append("--");
		buf.append(requireID);
		buf.append("--");
		buf.append(value);
		return buf.toString();
	}
}
