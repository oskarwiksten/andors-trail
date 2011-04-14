package com.gpl.rpg.AndorsTrail.model.item;

import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectFieldParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectTokenizer;
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
		}
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Cannot find ItemType for searchtag \"" + searchTag + "\".");
		}
		return null;
	}
	

	private static final ResourceObjectTokenizer itemResourceTokenizer = new ResourceObjectTokenizer(37);
	public void initialize(final DynamicTileLoader tileLoader, final ActorConditionTypeCollection actorConditionTypes, String itemlist) {
		itemResourceTokenizer.tokenizeRows(itemlist, new ResourceObjectFieldParser() {
			@Override
			public void matchedRow(String[] parts) {
				final String itemTypeName = parts[2];
				String searchTag = parts[0];
				if (searchTag == null || searchTag.length() <= 0) {
					if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
						L.log("OPTIMIZE: ItemType \"" + itemTypeName + "\" has empty searchtag.");
					}
					searchTag = itemTypeName;
				}
				
				final ItemTraits_OnEquip equipEffect = ResourceFileParser.parseItemTraits_OnEquip(actorConditionTypes, parts, 5);
				final ItemTraits_OnUse useEffect = ResourceFileParser.parseItemTraits_OnUse(actorConditionTypes, parts, 18, false);
				final ItemTraits_OnUse hitEffect = ResourceFileParser.parseItemTraits_OnUse(actorConditionTypes, parts, 24, true);
				final ItemTraits_OnUse killEffect = ResourceFileParser.parseItemTraits_OnUse(actorConditionTypes, parts, 31, false);
				
				final int nextId = itemTypes.size();
				final ItemType itemType = new ItemType(
	        			nextId
	        			, ResourceFileParser.parseImageID(tileLoader, parts[1])
	        			, itemTypeName
			        	, searchTag
	        			, Integer.parseInt(parts[3])
	        			, Integer.parseInt(parts[4])
	        			, equipEffect
	        			, useEffect
	        			, hitEffect
	        			, killEffect
	    			);
				if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
	    			if (itemType.isEquippable()) {
	    				if (itemType.effects_equip == null && itemType.effects_hit == null && itemType.effects_kill == null ) {
	        				L.log("OPTIMIZE: Item " + searchTag + " is equippable, but has no equip effect.");
	    				}
	    			} else {
	    				if (itemType.effects_equip != null || itemType.effects_hit != null || itemType.effects_kill != null ) {
	        				L.log("OPTIMIZE: Item " + searchTag + " is not equippable, but has equip, hit or kill effect.");
	    				}
	    			}
	    			if (itemType.isUsable()) {
	    				if (itemType.effects_use == null) {
	        				L.log("OPTIMIZE: Item " + searchTag + " is usable, but has no use effect.");
	    				}
	    			} else {
	    				if (itemType.effects_use != null) {
	    					L.log("OPTIMIZE: Item " + searchTag + " is not usable, but has use effect.");
	    				}
	    			}
	    		}
				itemTypes.add(itemType);
	    		
	    		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
	    			if (getItemTypeByTag(searchTag).id != nextId) {
	    				L.log("OPTIMIZE: Item " + searchTag + " may be duplicated.");
	    			}
	    		}
			}
		});
    }
}
  