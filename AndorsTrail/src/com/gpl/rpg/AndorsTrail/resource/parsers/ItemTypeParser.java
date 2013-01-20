package com.gpl.rpg.AndorsTrail.resource.parsers;

import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.ItemCategoryCollection;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnEquip;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonCollectionParserFor;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonFieldNames;
import com.gpl.rpg.AndorsTrail.util.Pair;
import org.json.JSONException;
import org.json.JSONObject;

public final class ItemTypeParser extends JsonCollectionParserFor<ItemType> {

	private final DynamicTileLoader tileLoader;
	private final ItemTraitsParser itemTraitsParser;
	private final ItemCategoryCollection itemCategories;
	
	public ItemTypeParser(DynamicTileLoader tileLoader, ActorConditionTypeCollection actorConditionsTypes, ItemCategoryCollection itemCategories) {
		this.tileLoader = tileLoader;
		this.itemTraitsParser = new ItemTraitsParser(actorConditionsTypes);
		this.itemCategories = itemCategories;
	}

	@Override
	public Pair<String, ItemType> parseObject(JSONObject o) throws JSONException {
		final String id = o.getString(JsonFieldNames.ItemType.itemTypeID);
		final String itemTypeName = o.getString(JsonFieldNames.ItemType.name);
		final ItemTraits_OnEquip equipEffect = itemTraitsParser.parseItemTraits_OnEquip(o.optJSONObject(JsonFieldNames.ItemType.equipEffect));
		final ItemTraits_OnUse useEffect = itemTraitsParser.parseItemTraits_OnUse(o.optJSONObject(JsonFieldNames.ItemType.useEffect));
		final ItemTraits_OnUse hitEffect = itemTraitsParser.parseItemTraits_OnUse(o.optJSONObject(JsonFieldNames.ItemType.hitEffect));
		final ItemTraits_OnUse killEffect = itemTraitsParser.parseItemTraits_OnUse(o.optJSONObject(JsonFieldNames.ItemType.killEffect));
		
		final int baseMarketCost = o.getInt(JsonFieldNames.ItemType.baseMarketCost);
		final boolean hasManualPrice = o.optInt(JsonFieldNames.ItemType.hasManualPrice, 0) > 0;
		final ItemType itemType = new ItemType(
				id
    			, ResourceParserUtils.parseImageID(tileLoader, o.getString(JsonFieldNames.ItemType.iconID))
    			, itemTypeName
	        	, itemCategories.getItemCategory(o.getString(JsonFieldNames.ItemType.category))
    			, o.optInt(JsonFieldNames.ItemType.displaytype, ItemType.DISPLAYTYPE_ORDINARY)
    			, hasManualPrice
    			, baseMarketCost
    			, equipEffect
    			, useEffect
    			, hitEffect
    			, killEffect
			);
		return new Pair<String, ItemType>(id, itemType);
	}
}
