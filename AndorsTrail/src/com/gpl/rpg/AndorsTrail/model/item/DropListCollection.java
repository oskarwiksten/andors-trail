package com.gpl.rpg.AndorsTrail.model.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.item.DropList.DropItem;
import com.gpl.rpg.AndorsTrail.resource.ResourceLoader;
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
	
	private final ConstRange one = new ConstRange(1, 1);
	private final ConstRange ten = new ConstRange(10, 10);
	private final ConstRange five = new ConstRange(5, 5);
	private ConstRange parseQuantity(String v) {
		if (v.equals("1")) return one;
		else if (v.equals("5")) return five;
		else if (v.equals("10")) return ten;
		return ResourceLoader.parseRange(v);
	}
	
	private final ConstRange always = one;
	private final ConstRange often = new ConstRange(100, 70);
	private final ConstRange animalpart = new ConstRange(100, 30);
	private final ConstRange seldom = new ConstRange(100, 25);
	private final ConstRange very_seldom = new ConstRange(100, 5);
	private final ConstRange unique = new ConstRange(100, 1);
	private ConstRange parseChance(String v) {
		if (v.equals("100")) return always;
		else if (v.equals("70")) return often;
		else if (v.equals("30")) return animalpart;
		else if (v.equals("25")) return seldom;
		else if (v.equals("5")) return very_seldom;
		else if (v.equals("1")) return unique;
		else if (v.indexOf('/') >= 0) {
			int c = v.indexOf('/');
			int a = ResourceLoader.parseInt(v.substring(0, c), 1);
			int b = ResourceLoader.parseInt(v.substring(c+1), 100);
			return new ConstRange(b, a);
		}
		else return new ConstRange(100, ResourceLoader.parseInt(v, 10));
	}
	public void initialize(ItemTypeCollection itemTypes, String droplistString) {
		HashMap<String, ArrayList<DropItem> > rows = new HashMap<String, ArrayList<DropItem> >();
		Matcher rowMatcher = ResourceLoader.rowPattern.matcher(droplistString);
    	while(rowMatcher.find()) {
    		String[] parts = rowMatcher.group(1).split(ResourceLoader.columnSeparator, -1);
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
          			, parseChance(parts[2])
          			, parseQuantity(parts[3])
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
