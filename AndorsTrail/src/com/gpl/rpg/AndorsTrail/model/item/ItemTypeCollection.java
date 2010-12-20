package com.gpl.rpg.AndorsTrail.model.item;

import java.util.ArrayList;
import java.util.regex.Matcher;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceLoader;
import com.gpl.rpg.AndorsTrail.util.L;

public final class ItemTypeCollection {
	public static final int ITEMTYPE_GOLD = 0;
	
	private final ArrayList<ItemType> itemTypes = new ArrayList<ItemType>();
	public final ArrayList<ItemType> TEST_itemTypes = itemTypes;
	
	public ItemType getItemType(int id) {
		return itemTypes.get(id);
	}
	public ItemType getItemTypeByTag(String searchTag) {
		for(ItemType t : itemTypes) {
			if (t.searchTag.equalsIgnoreCase(searchTag)) return t;
			if (t.name.equalsIgnoreCase(searchTag)) return t;
		}
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Cannot find ItemType for searchtag \"" + searchTag + "\".");
		}
		return null;
	}
	
	public void initialize(DynamicTileLoader tileLoader, String itemlist) {
		int nextId = itemTypes.size();
    	Matcher rowMatcher = ResourceLoader.rowPattern.matcher(itemlist);
    	while(rowMatcher.find()) {
    		String[] parts = rowMatcher.group(1).split(ResourceLoader.columnSeparator, -1);
    		if (parts.length < 13) continue;
    		
    		final String itemTypeName = parts[2];
			String searchTag = parts[0];
			if (searchTag == null || searchTag.length() <= 0) searchTag = itemTypeName;
			
    		itemTypes.add(new ItemType(
        			nextId
        			, ResourceLoader.parseImageID(tileLoader, parts[1])
        			, itemTypeName
		        	, searchTag
        			, Integer.parseInt(parts[3])
        			, Integer.parseInt(parts[4])
        			, null
        			, ResourceLoader.parseRange(parts[5])
        			, ResourceLoader.parseCombatTraits(parts, 6)
    			));
    		
    		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    			if (getItemTypeByTag(searchTag).id != nextId) {
    				L.log("OPTIMIZE: Item " + searchTag + " may be duplicated.");
    			}
    		}
    		
        	++nextId;
    	}
    }
}
  