package com.gpl.rpg.AndorsTrail.model.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.resource.parsers.MonsterTypeParser;
import com.gpl.rpg.AndorsTrail.util.L;

public final class MonsterTypeCollection {
	private final HashMap<String, MonsterType> monsterTypes = new HashMap<String, MonsterType>();
	
	public MonsterType getMonsterType(String id) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!monsterTypes.containsKey(id)) {
				L.log("WARNING: Cannot find MonsterType for id \"" + id + "\".");
			}
		}
		return monsterTypes.get(id);
	}

	public Collection<? extends MonsterType> getMonsterTypesFromTags(String tagsAndNames) {
		String[] parts = tagsAndNames.toLowerCase().split(",");
		ArrayList<MonsterType> result = new ArrayList<MonsterType>();
		for (MonsterType t : monsterTypes.values()) {
			if (t.matchesAny(parts)) result.add(t);
		}
		return result;
	}
	
	public MonsterType guessMonsterTypeFromName(String name) {
		for (MonsterType t : monsterTypes.values()) {
			if (t.name.equalsIgnoreCase(name)) return t;
		}
		return null;
	}
	
	public void initialize(MonsterTypeParser parser, String input) {
		parser.parseRows(input, monsterTypes);
	}	
	
	// Selftest method. Not part of the game logic.
	public void verifyData(WorldContext world) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		for (MonsterType t : monsterTypes.values()) {
    			if (t.phraseID != null && t.phraseID.length() > 0) {
    				if (!world.conversations.isValidPhraseID(t.phraseID)) {
    					L.log("WARNING: Cannot find phrase \"" + t.phraseID + "\" for MonsterType \"" + t.id + "\".");
    				}
    			}
    		}
    	}
	}

	// Selftest method. Not part of the game logic.
	public void verifyData(ConversationCollection conversations) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		for (MonsterType t : monsterTypes.values()) {
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
	public HashSet<String> DEBUG_getRequiredPhrases() {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		HashSet<String> requiredPhrases = new HashSet<String>();
    		for (MonsterType t : monsterTypes.values()) {
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
    		for (MonsterType t : monsterTypes.values()) {
    			if (t.dropList != null) usedDroplists.add(t.dropList);
    		}
    	}
	}
}
