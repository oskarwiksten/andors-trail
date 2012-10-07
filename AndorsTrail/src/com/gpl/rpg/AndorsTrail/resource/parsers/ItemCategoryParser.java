package com.gpl.rpg.AndorsTrail.resource.parsers;

import com.gpl.rpg.AndorsTrail.model.item.ItemCategory;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer.ResourceParserFor;
import com.gpl.rpg.AndorsTrail.util.Pair;

public final class ItemCategoryParser extends ResourceParserFor<ItemCategory> {

	public ItemCategoryParser() {
		super(5);
	}

	@Override
	public Pair<String, ItemCategory> parseRow(String[] parts) {
		String id = parts[0];
		final ItemCategory itemType = new ItemCategory(
				id
    			, parts[1]										// displayName
    			, ResourceParserUtils.parseInt(parts[2], 0)		// actionType
    			, ResourceParserUtils.parseInt(parts[3], -1)	// inventorySlot
    			, ResourceParserUtils.parseInt(parts[4], 0)		// size
			);
		return new Pair<String, ItemCategory>(id, itemType);
	}
}
