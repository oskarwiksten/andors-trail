package com.gpl.rpg.AndorsTrail.resource.parsers;

import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.ItemCategoryCollection;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnEquip;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer.ResourceParserFor;
import com.gpl.rpg.AndorsTrail.util.Pair;

public final class ItemTypeParser extends ResourceParserFor<ItemType> {

	private final DynamicTileLoader tileLoader;
	private final ItemTraitsParser itemTraitsParser;
	private final ItemCategoryCollection itemCategories;
	
	public ItemTypeParser(DynamicTileLoader tileLoader, ActorConditionTypeCollection actorConditionsTypes, ItemCategoryCollection itemCategories) {
		super(39);
		this.tileLoader = tileLoader;
		this.itemTraitsParser = new ItemTraitsParser(actorConditionsTypes);
		this.itemCategories = itemCategories;
	}

	@Override
	public Pair<String, ItemType> parseRow(String[] parts) {
		final String itemTypeName = parts[2];
		String id = parts[0];
		final ItemTraits_OnEquip equipEffect = itemTraitsParser.parseItemTraits_OnEquip(parts, 7);
		final ItemTraits_OnUse useEffect = itemTraitsParser.parseItemTraits_OnUse(parts, 20, false);
		final ItemTraits_OnUse hitEffect = itemTraitsParser.parseItemTraits_OnUse(parts, 26, true);
		final ItemTraits_OnUse killEffect = itemTraitsParser.parseItemTraits_OnUse(parts, 33, false);
		
		final int baseMarketCost = Integer.parseInt(parts[6]);
		final boolean hasManualPrice = ResourceParserUtils.parseBoolean(parts[5], false);
		final ItemType itemType = new ItemType(
				id
    			, ResourceParserUtils.parseImageID(tileLoader, parts[1])
    			, itemTypeName
	        	, itemCategories.getItemCategory(parts[3])									// category
    			, ResourceParserUtils.parseInt(parts[4], ItemType.DISPLAYTYPE_ORDINARY) 	// Displaytype
    			, hasManualPrice								 							// hasManualPrice
    			, baseMarketCost 															// Base market cost
    			, equipEffect
    			, useEffect
    			, hitEffect
    			, killEffect
			);
		return new Pair<String, ItemType>(id, itemType);
	}
}
