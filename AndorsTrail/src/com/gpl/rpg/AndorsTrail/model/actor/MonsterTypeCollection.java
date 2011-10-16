package com.gpl.rpg.AndorsTrail.model.actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.item.DropList.DropItem;
import com.gpl.rpg.AndorsTrail.model.map.MapCollection;
import com.gpl.rpg.AndorsTrail.resource.parsers.MonsterTypeParser;
import com.gpl.rpg.AndorsTrail.util.L;

public final class MonsterTypeCollection {
	private final HashMap<String, MonsterType> monsterTypesById = new HashMap<String, MonsterType>();
	public final HashMap<String, MonsterType> DEBUG_monsterTypesById = monsterTypesById;
	
	public MonsterType getMonsterType(String id) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!monsterTypesById.containsKey(id)) {
				L.log("WARNING: Cannot find MonsterType for id \"" + id + "\".");
			}
		}
		return monsterTypesById.get(id);
	}

	public ArrayList<MonsterType> getMonsterTypesFromSpawnGroup(String spawnGroup) {
		ArrayList<MonsterType> result = new ArrayList<MonsterType>();
		for (MonsterType t : monsterTypesById.values()) {
			if (t.spawnGroup.equalsIgnoreCase(spawnGroup)) result.add(t);
		}
		
		return result;
	}
	
	public MonsterType guessMonsterTypeFromName(String name) {
		for (MonsterType t : monsterTypesById.values()) {
			if (t.name.equalsIgnoreCase(name)) return t;
		}
		return null;
	}
	
	public void initialize(MonsterTypeParser parser, String input) {
		parser.parseRows(input, monsterTypesById);
	}	
	
	// Selftest method. Not part of the game logic.
	public void verifyData(WorldContext world, ConversationCollection conversations) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		for (MonsterType t : monsterTypesById.values()) {
    			if (t.phraseID != null) {
    				if (!conversations.isValidPhraseID(t.phraseID)) {
    					L.log("WARNING: Cannot find phrase \"" + t.phraseID + "\" for MonsterType \"" + t.id + "\".");
    				}
    			}
    			
    			if (t.dropList != null && t.isRespawnable && t.phraseID == null) {
    				int averageItemDropGold = 0;
    				for (DropItem item : t.dropList.DEBUG_items) {
    					averageItemDropGold += item.itemType.baseMarketCost * item.quantity.averagef() * item.chance.current / item.chance.max;
    				}
    				
    				float goldPerExpReward = (float) averageItemDropGold / t.exp;
    				boolean warn = false;
    				if (goldPerExpReward > 0.5) warn = true;
    				else if (averageItemDropGold > 30 && goldPerExpReward > 0.3) warn = true;
    				
    				if (warn) L.log("Monster type " + t.id + " rewards " + averageItemDropGold + " gold drop on average, which is a bit high for the exp: " + t.exp + "  (average " + goldPerExpReward + " gold per exp)");
    			}
    		}
    	}
	}

	// Selftest method. Not part of the game logic.
	public void verifyData(ConversationCollection conversations) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		for (MonsterType t : monsterTypesById.values()) {
    			if (t.phraseID != null && t.phraseID.length() > 0) {
    				if (conversations.DEBUG_leadsToTradeReply(t.phraseID)) {
    					if (t.dropList == null) {
    						L.log("WARNING: MonsterType \"" + t.id + "\" has conversation \"" + t.phraseID + "\" that leads to a trade, but the monster type does not have a droplist.");
    					}
    				}
    			}
    		}
    	}
	}

	// Selftest method. Not part of the game logic.
	public void verifyData(MapCollection maps) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		HashSet<String> availableMonsterIDs = new HashSet<String>(monsterTypesById.keySet());
    		HashSet<String> usedSpawnedMonsterIDs = new HashSet<String>();
    		maps.DEBUG_getSpawnedMonsterIDs(usedSpawnedMonsterIDs);
    		
    		availableMonsterIDs.removeAll(usedSpawnedMonsterIDs);
    		for (String monsterTypeID : availableMonsterIDs) {
    			L.log("WARNING: MonsterType \"" + monsterTypeID + "\" is never used on any spawnarea.");
    		}
    	}
	}

	// Selftest method. Not part of the game logic.
	public HashSet<String> DEBUG_getRequiredPhrases() {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		HashSet<String> requiredPhrases = new HashSet<String>();
    		for (MonsterType t : monsterTypesById.values()) {
    			if (t.phraseID != null && t.phraseID.length() > 0) {
    				requiredPhrases.add(t.phraseID);
    			}
    		}
    		return requiredPhrases;
    	} else {
    		return null;
    	}
	}
	
	// Selftest method. Not part of the game logic.
	public void DEBUG_getUsedDroplists(HashSet<DropList> usedDroplists) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		for (MonsterType t : monsterTypesById.values()) {
    			if (t.dropList != null) usedDroplists.add(t.dropList);
    		}
    	}
	}
}
