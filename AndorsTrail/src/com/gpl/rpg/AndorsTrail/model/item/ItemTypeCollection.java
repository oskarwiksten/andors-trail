package com.gpl.rpg.AndorsTrail.model.item;

import java.util.HashMap;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.resource.parsers.ItemTypeParser;
import com.gpl.rpg.AndorsTrail.util.L;

public final class ItemTypeCollection {
	private static final String ITEMTYPE_GOLD = "gold";
	
	private final HashMap<String, ItemType> itemTypes = new HashMap<String, ItemType>();
	
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
	
	public void initialize(final ItemTypeParser parser, String input) {
		parser.parseRows(input, itemTypes);
	}
	
	// Selftest method. Not part of the game logic.
	public void verifyData(DropListCollection dropLists) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			for (ItemType t : itemTypes.values()) {
				if (!t.hasManualPrice) {
					if (t.effects_hit != null || t.effects_kill != null) {
						L.log("OPTIMIZE: Item " + t.id + " uses automatic pricing, but has kill- or hit effects. Should probably use manual pricing?");
					}
					if (t.effects_equip == null && t.effects_use == null) {
						L.log("OPTIMIZE: Item " + t.id + " uses automatic pricing, but has no equip- or use effects. Should probably use manual pricing?");
					} else if (!t.isUsable() && !t.isEquippable()) {
						L.log("OPTIMIZE: Item " + t.id + " uses automatic pricing, but is neither usable nor equippable. Should probably use manual pricing?");
					}
				} else {
					if (t.baseMarketCost != 0 && t.isQuestItem()) {
						L.log("OPTIMIZE: Item " + t.id + " is a quest item, but has a base market price specified.");
					} else if (t.baseMarketCost == 0 && t.isOrdinaryItem()) {
						L.log("OPTIMIZE: Item " + t.id + " does not have a base market price specified (and is an ordinary item).");
					}
				}
				
    			if (t.isEquippable()) {
    				if (t.effects_equip == null && t.effects_hit == null && t.effects_kill == null ) {
        				L.log("OPTIMIZE: Item " + t.id + " is equippable, but has no equip effect.");
    				}
    			} else {
    				if (t.effects_equip != null || t.effects_hit != null || t.effects_kill != null ) {
        				L.log("OPTIMIZE: Item " + t.id + " is not equippable, but has equip, hit or kill effect.");
    				}
    			}
    			if (t.isUsable()) {
    				if (t.effects_use == null) {
        				L.log("OPTIMIZE: Item " + t.id + " is usable, but has no use effect.");
    				}
    			} else {
    				if (t.effects_use != null) {
    					L.log("OPTIMIZE: Item " + t.id + " is not usable, but has use effect.");
    				}
    			}
    			
				if (!dropLists.verifyExistsDroplistForItem(t.id)) {
					L.log("OPTIMIZE: Item " + t.id + " is not dropped by any droplist.");
				}
			}	
		}
	}
}
  