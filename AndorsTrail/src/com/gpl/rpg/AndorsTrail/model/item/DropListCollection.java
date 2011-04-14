package com.gpl.rpg.AndorsTrail.model.item;

import java.util.ArrayList;
import java.util.HashMap;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.item.DropList.DropItem;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectArrayTokenizer;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectFieldParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectTokenizer;
import com.gpl.rpg.AndorsTrail.util.L;

public final class DropListCollection {
	public static final String DROPLIST_STARTITEMS = "startitems";
	
	private final HashMap<String, DropList> droplists = new HashMap<String, DropList>();
	
	public DropList getDropList(String name) {
		if (name == null || name.length() <= 0) return null;
		
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!droplists.containsKey(name)) {
				L.log("WARNING: Cannot find droplist \"" + name + "\".");
			}
		}
		return droplists.get(name);
	}
	
	private static final ResourceObjectTokenizer droplistResourceTokenizer = new ResourceObjectTokenizer(2);
	private static final ResourceObjectTokenizer droplistItemResourceTokenizer = new ResourceObjectTokenizer(4);
	public void initialize(final ItemTypeCollection itemTypes, String droplistString) {
		droplistResourceTokenizer.tokenizeRows(droplistString, new ResourceObjectFieldParser() {
			@Override
			public void matchedRow(String[] parts) {
				// [id|items[itemID|quantity_Min|quantity_Max|chance|]|];
				
				String droplistID = parts[0];
				
				final ArrayList<DropItem> items = new ArrayList<DropItem>();
				ResourceObjectArrayTokenizer.tokenize(parts[1], droplistItemResourceTokenizer, new ResourceObjectFieldParser() {
					@Override
					public void matchedRow(String[] parts) {
						items.add(new DropItem(
								itemTypes.getItemTypeByTag(parts[0]) 					// Itemtype
								, ResourceFileParser.parseChance(parts[3]) 				// Chance
								, ResourceFileParser.parseQuantity(parts[1], parts[2]) 	// Quantity
							));
					}
				});
				
				DropItem[] items_ = items.toArray(new DropItem[items.size()]);
				final DropList droplist = new DropList(items_);
				if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
	    			if (droplistID.trim().length() <= 0) {
	    				L.log("WARNING: Droplist with empty id.");
	    			} else {
    					if (droplists.containsKey(droplistID)) {
    						L.log("OPTIMIZE: Droplist " + droplistID + " is duplicated.");
    					}
	    			}
	    			if (items.size() <= 0) {
	    				L.log("OPTIMIZE: Droplist \"" + droplistID + "\" has no dropped items.");
	    			}
	    		}
				droplists.put(droplistID, droplist);
			}
		});
	}
	
	// Selftest method. Not part of the game logic.
	public boolean verifyExistsDroplist(int itemTypeID) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			for (DropList d : droplists.values()) {
				if (d.contains(itemTypeID)) return true;
			}
		}
		return false;
	}
}
