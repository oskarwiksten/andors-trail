package com.gpl.rpg.AndorsTrail.model.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterTypeCollection;
import com.gpl.rpg.AndorsTrail.model.map.MapCollection;
import com.gpl.rpg.AndorsTrail.resource.parsers.DropListParser;
import com.gpl.rpg.AndorsTrail.util.L;

public final class DropListCollection {
	public static final String DROPLIST_STARTITEMS = "startitems";
	
	private final HashMap<String, DropList> droplists = new HashMap<String, DropList>();
	
	public DropList getDropList(String droplistID) {
		if (droplistID == null || droplistID.length() <= 0) return null;
		
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!droplists.containsKey(droplistID)) {
				L.log("WARNING: Cannot find droplist \"" + droplistID + "\".");
			}
		}
		return droplists.get(droplistID);
	}
	
	public void initialize(final DropListParser parser, String input) {
		parser.parseRows(input, droplists);
	}
	
	// Selftest method. Not part of the game logic.
	public boolean verifyExistsDroplistForItem(String itemTypeID) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			for (DropList d : droplists.values()) {
				if (d.contains(itemTypeID)) return true;
			}
		}
		return false;
	}

	// Selftest method. Not part of the game logic.
	public void verifyData(MonsterTypeCollection monsterTypes, ConversationCollection conversations, MapCollection maps) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			HashSet<DropList> usedDroplists = new HashSet<DropList>();
			monsterTypes.DEBUG_getUsedDroplists(usedDroplists);
			conversations.DEBUG_getUsedDroplists(usedDroplists, this);
			maps.DEBUG_getUsedDroplists(usedDroplists);
			usedDroplists.add(getDropList(DropListCollection.DROPLIST_STARTITEMS));
			
			for (Entry<String, DropList> e : droplists.entrySet()) {
				if (!usedDroplists.contains(e.getValue())) {
					L.log("OPTIMIZE: Droplist " + e.getKey() + " is not used by any monster or conversation phrase.");
				}
			}
		}	
	}
}
