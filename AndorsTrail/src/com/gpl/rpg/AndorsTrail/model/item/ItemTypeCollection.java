package com.gpl.rpg.AndorsTrail.model.item;

import java.util.HashMap;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectFieldParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectTokenizer;
import com.gpl.rpg.AndorsTrail.util.L;

public final class ItemTypeCollection {
	private static final String ITEMTYPE_GOLD = "gold";
	
	private final HashMap<String, ItemType> itemTypes = new HashMap<String, ItemType>();
	public final HashMap<String, ItemType> TEST_itemTypes = itemTypes;
	
	public ItemType getItemType(String id) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!itemTypes.containsKey(id)) {
				L.log("WARNING: Cannot find ItemType for id \"" + id + "\".");
			}
		}
		return itemTypes.get(id);
	}

	public static boolean isGoldItemType(String itemTypeID) {
		if (itemTypeID == null) return false;
		return itemTypeID.equals(ITEMTYPE_GOLD);
	}
	
	private static final ResourceObjectTokenizer itemResourceTokenizer = new ResourceObjectTokenizer(39);
	public void initialize(final DynamicTileLoader tileLoader, final ActorConditionTypeCollection actorConditionTypes, String itemlist) {
		itemResourceTokenizer.tokenizeRows(itemlist, new ResourceObjectFieldParser() {
			@Override
			public void matchedRow(String[] parts) {
				final String itemTypeName = parts[2];
				String id = parts[0];
				if (id == null || id.length() <= 0) {
					if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
						L.log("OPTIMIZE: ItemType \"" + itemTypeName + "\" has empty id.");
					}
					id = itemTypeName;
				}
				
				final ItemTraits_OnEquip equipEffect = ResourceFileParser.parseItemTraits_OnEquip(actorConditionTypes, parts, 7);
				final ItemTraits_OnUse useEffect = ResourceFileParser.parseItemTraits_OnUse(actorConditionTypes, parts, 20, false);
				final ItemTraits_OnUse hitEffect = ResourceFileParser.parseItemTraits_OnUse(actorConditionTypes, parts, 26, true);
				final ItemTraits_OnUse killEffect = ResourceFileParser.parseItemTraits_OnUse(actorConditionTypes, parts, 33, false);
				
				final int baseMarketCost = Integer.parseInt(parts[6]);
				final boolean hasManualPrice = ResourceFileParser.parseBoolean(parts[5], false);
				final ItemType itemType = new ItemType(
						id
	        			, ResourceFileParser.parseImageID(tileLoader, parts[1])
	        			, itemTypeName
			        	, Integer.parseInt(parts[3]) 												// category
	        			, ResourceFileParser.parseInt(parts[4], ItemType.DISPLAYTYPE_ORDINARY) 		// Displaytype
	        			, hasManualPrice								 							// hasManualPrice
	        			, baseMarketCost 															// Base market cost
	        			, equipEffect
	        			, useEffect
	        			, hitEffect
	        			, killEffect
	    			);
				if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
					if (!hasManualPrice) {
						if (itemType.effects_hit != null || itemType.effects_kill != null) {
							L.log("OPTIMIZE: Item " + id + " uses automatic pricing, but has kill- or hit effects. Should probably use manual pricing?");
						}
						if (itemType.effects_equip == null && itemType.effects_use == null) {
							L.log("OPTIMIZE: Item " + id + " uses automatic pricing, but has no equip- or use effects. Should probably use manual pricing?");
						} else if (!itemType.isUsable() && !itemType.isEquippable()) {
							L.log("OPTIMIZE: Item " + id + " uses automatic pricing, but is neither usable nor equippable. Should probably use manual pricing?");
						}
					} else {
						if (baseMarketCost != 0 && itemType.isQuestItem()) {
							L.log("OPTIMIZE: Item " + id + " is a quest item, but has a base market price specified.");
						} else if (baseMarketCost == 0 && itemType.isOrdinaryItem()) {
							L.log("OPTIMIZE: Item " + id + " does not have a base market price specified (and is an ordinary item).");
						}
					}
					
	    			if (itemType.isEquippable()) {
	    				if (itemType.effects_equip == null && itemType.effects_hit == null && itemType.effects_kill == null ) {
	        				L.log("OPTIMIZE: Item " + id + " is equippable, but has no equip effect.");
	    				}
	    			} else {
	    				if (itemType.effects_equip != null || itemType.effects_hit != null || itemType.effects_kill != null ) {
	        				L.log("OPTIMIZE: Item " + id + " is not equippable, but has equip, hit or kill effect.");
	    				}
	    			}
	    			if (itemType.isUsable()) {
	    				if (itemType.effects_use == null) {
	        				L.log("OPTIMIZE: Item " + id + " is usable, but has no use effect.");
	    				}
	    			} else {
	    				if (itemType.effects_use != null) {
	    					L.log("OPTIMIZE: Item " + id + " is not usable, but has use effect.");
	    				}
	    			}
	    		}
				if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
	    			if (itemTypes.containsKey(id)) {
	    				L.log("OPTIMIZE: Item " + id + " may be duplicated.");
	    			}
	    		}
				
				itemTypes.put(id, itemType);
			}
		});
    }
	
	// Selftest method. Not part of the game logic.
	public void verifyData(DropListCollection dropLists) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			for (ItemType t : itemTypes.values()) {
				if (dropLists.verifyExistsDroplist(t.id)) continue;
				//if (conversations.verifyExistsReplyThatRequiresItem(t.id)) continue;
				L.log("OPTIMIZE: Item " + t.id + " is not dropped by any droplist.");
			}	
		}
	}
}
  