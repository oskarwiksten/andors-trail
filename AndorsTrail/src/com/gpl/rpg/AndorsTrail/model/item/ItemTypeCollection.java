package com.gpl.rpg.AndorsTrail.model.item;

import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectFieldParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectTokenizer;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
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
	

	private static ResourceObjectTokenizer tokenize37Fields = new ResourceObjectTokenizer(37);
	public void initialize(final DynamicTileLoader tileLoader, final ActorConditionTypeCollection actorConditionTypes, String itemlist) {
		tokenize37Fields.tokenizeRows(itemlist, new ResourceObjectFieldParser() {
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
				
				ItemTraits_OnEquip equipEffect = ResourceFileParser.parseItemTraits_OnEquip(actorConditionTypes, parts, 5);
				ItemTraits_OnUse useEffect = ResourceFileParser.parseItemTraits_OnUse(actorConditionTypes, parts, 18, false);
				ItemTraits_OnUse hitEffect = ResourceFileParser.parseItemTraits_OnUse(actorConditionTypes, parts, 24, true);
				ItemTraits_OnUse killEffect = ResourceFileParser.parseItemTraits_OnUse(actorConditionTypes, parts, 31, false);
				
				int nextId = itemTypes.size();
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
	
	public void initialize_DEBUGITEMS(WorldContext world) {
		int nextId = itemTypes.size();
		/*
		ItemType club1 = getItemTypeByTag("club1");
		
		ItemType itemType = new ItemType(
    			nextId
    			, club1.iconID
    			, "DEBUG club of lifesteal"
	        	, "debug_club_1"
    			, club1.category
    			, 1
    			, equipTraits
    			, useTraits
    			, null
    			, null
			);
		itemTypes.add(itemType);
		++nextId;
		*/
		
		ItemType ring_dmg1 = getItemTypeByTag("ring_dmg1");
		if (ring_dmg1 == null) return;
		ItemType itemType = new ItemType(
    			nextId
    			, ring_dmg1.iconID
    			, "DEBUG ring of maxHP"
	        	, "debug_ring_1"
    			, ring_dmg1.category
    			, 1
    			, new ItemTraits_OnEquip(10, 0, 0, null, null)
    			, null
    			, null
    			, null
			);
		itemTypes.add(itemType);
		++nextId;
		
		itemType = new ItemType(
    			nextId
    			, ring_dmg1.iconID
    			, "DEBUG ring of maxAP"
	        	, "debug_ring_2"
    			, ring_dmg1.category
    			, 1
    			, new ItemTraits_OnEquip(0, 5, 0, null, null)
    			, null
    			, null
    			, null
			);
		itemTypes.add(itemType);
		++nextId;
		
		itemType = new ItemType(
    			nextId
    			, ring_dmg1.iconID
    			, "DEBUG walkring"
	        	, "debug_ring_3"
    			, ring_dmg1.category
    			, 1
    			, new ItemTraits_OnEquip(0, 0, 2, null, null)
    			, null
    			, null
    			, null
			);
		itemTypes.add(itemType);
		++nextId;
		
		ActorConditionEffect[] effects = new ActorConditionEffect[] {
			new ActorConditionEffect(world.actorConditionsTypes.getActorConditionType("bless"), 3, ActorCondition.DURATION_FOREVER, new ConstRange(1,1))
		};
		itemType = new ItemType(
    			nextId
    			, ring_dmg1.iconID
    			, "DEBUG ring of bless"
	        	, "debug_ring_4"
    			, ring_dmg1.category
    			, 1
    			, new ItemTraits_OnEquip(0, 0, 0, null, effects)
    			, null
    			, null
    			, null
			);
		itemTypes.add(itemType);
		++nextId;
		
		effects = new ActorConditionEffect[] {
			new ActorConditionEffect(world.actorConditionsTypes.getActorConditionType("regen"), 1, ActorCondition.DURATION_FOREVER, new ConstRange(1,1))
		};
		itemType = new ItemType(
    			nextId
    			, ring_dmg1.iconID
    			, "DEBUG ring of regen"
	        	, "debug_ring_5"
    			, ring_dmg1.category
    			, 1
    			, new ItemTraits_OnEquip(0, 0, 0, null, effects)
    			, null
    			, null
    			, null
			);
		itemTypes.add(itemType);
		++nextId;
		
		itemType = new ItemType(
    			nextId
    			, ring_dmg1.iconID
    			, "DEBUG ring of hitheal"
	        	, "debug_ring_6"
    			, ring_dmg1.category
    			, 1
    			, null
    			, null
    			, new ItemTraits_OnUse(new ConstRange(1, 1), null, null, null)
    			, null
			);
		itemTypes.add(itemType);
		++nextId;
		
		effects = new ActorConditionEffect[] {
			new ActorConditionEffect(world.actorConditionsTypes.getActorConditionType("regen"), 2, 3, new ConstRange(1,1))
		};
		itemType = new ItemType(
    			nextId
    			, ring_dmg1.iconID
    			, "DEBUG ring of killeffect"
	        	, "debug_ring_7"
    			, ring_dmg1.category
    			, 1
    			, null
    			, null
    			, null
    			, new ItemTraits_OnUse(new ConstRange(4, 4), null, effects, null)
			);
		itemTypes.add(itemType);
		++nextId;
		
		effects = new ActorConditionEffect[] {
			new ActorConditionEffect(world.actorConditionsTypes.getActorConditionType("poison_weak"), 1, 3, new ConstRange(3, 2))
		};
		itemType = new ItemType(
    			nextId
    			, ring_dmg1.iconID
    			, "DEBUG ring of atkpoison"
	        	, "debug_ring_8"
    			, ring_dmg1.category
    			, 1
    			, null
    			, null
    			, new ItemTraits_OnUse(null, null, null, effects)
    			, null
			);
		itemTypes.add(itemType);
		++nextId;
		
		ItemType health_minor = getItemTypeByTag("health_minor");
		
		effects = new ActorConditionEffect[] {
			new ActorConditionEffect(world.actorConditionsTypes.getActorConditionType("poison_weak"), 1, 4, new ConstRange(1,1))
		};
		itemType = new ItemType(
    			nextId
    			, health_minor.iconID
    			, "DEBUG poison"
	        	, "debug_potion_1"
    			, health_minor.category
    			, 1
    			, null
    			, new ItemTraits_OnUse(null, null, effects, null)
    			, null
    			, null
			);
		itemTypes.add(itemType);
		++nextId;
		
		effects = new ActorConditionEffect[] {
			new ActorConditionEffect(world.actorConditionsTypes.getActorConditionType("poison_weak"), ActorCondition.MAGNITUDE_REMOVE_ALL, 0, new ConstRange(1,1))
		};
		itemType = new ItemType(
    			nextId
    			, health_minor.iconID
    			, "DEBUG antidote"
	        	, "debug_potion_2"
    			, health_minor.category
    			, 1
    			, null
    			, new ItemTraits_OnUse(null, null, effects, null)
    			, null
    			, null
			);
		itemTypes.add(itemType);
		++nextId;
	}
}
  