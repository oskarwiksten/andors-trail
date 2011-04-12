package com.gpl.rpg.AndorsTrail.model.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.item.DropList.DropItem;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
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
	
	private static final ConstRange one = new ConstRange(1, 1);
	private static final ConstRange ten = new ConstRange(10, 10);
	private static final ConstRange five = new ConstRange(5, 5);
	private static ConstRange parseQuantity_OLD(String v) {
		if (v.equals("1")) return one;
		else if (v.equals("5")) return five;
		else if (v.equals("10")) return ten;
		return ResourceFileParser.parseRange_OLD(v);
	}
	
	public void initialize(ItemTypeCollection itemTypes, String droplistString) {
		HashMap<String, ArrayList<DropItem> > rows = new HashMap<String, ArrayList<DropItem> >();
		Matcher rowMatcher = ResourceFileParser.rowPattern.matcher(droplistString);
    	while(rowMatcher.find()) {
    		String[] parts = ResourceFileParser.splitColumns_OLD(rowMatcher.group(1), 4);//.split(ResourceFileParser.columnSeparator, -1);
    		if (parts.length < 4) continue;
    		
    		final String droplistID = parts[0];
    		
    		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    			if (droplistID.trim().length() <= 0) {
    				L.log("WARNING: Adding droplist with empty id.");
    			} else if (droplists.containsKey(droplistID)) {
    				L.log("WARNING: Droplist \"" + droplistID + "\" may be duplicated.");
    			}
    		}
    		
    		if (!rows.containsKey(droplistID)) rows.put(droplistID, new ArrayList<DropItem>());
    		rows.get(droplistID).add(new DropItem(
        			itemTypes.getItemTypeByTag(parts[1])
          			, ResourceFileParser.parseChance(parts[2])
          			, parseQuantity_OLD(parts[3])
      			));
    	}
    	
    	for(Entry<String, ArrayList<DropItem>> e : rows.entrySet()) {
    		droplists.put(e.getKey(), new DropList(e.getValue()));
    	}
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
