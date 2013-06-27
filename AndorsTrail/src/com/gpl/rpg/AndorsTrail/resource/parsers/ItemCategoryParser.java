package com.gpl.rpg.AndorsTrail.resource.parsers;

import com.gpl.rpg.AndorsTrail.model.item.ItemCategory;
import com.gpl.rpg.AndorsTrail.resource.TranslationLoader;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonCollectionParserFor;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonFieldNames;
import com.gpl.rpg.AndorsTrail.util.Pair;
import org.json.JSONException;
import org.json.JSONObject;

public final class ItemCategoryParser extends JsonCollectionParserFor<ItemCategory> {

    private final TranslationLoader translationLoader;

    public ItemCategoryParser(TranslationLoader translationLoader) {

        this.translationLoader = translationLoader;
    }

    @Override
	protected Pair<String, ItemCategory> parseObject(JSONObject o) throws JSONException {
		final String id = o.getString(JsonFieldNames.ItemCategory.itemCategoryID);
		ItemCategory result = new ItemCategory(
				id
				,translationLoader.translateItemCategoryName(o.getString(JsonFieldNames.ItemCategory.name))
				,o.optInt(JsonFieldNames.ItemCategory.actionType, 0)
				,o.optInt(JsonFieldNames.ItemCategory.inventorySlot, -1)
				,o.optInt(JsonFieldNames.ItemCategory.size, 0)
		);
		return new Pair<String, ItemCategory>(id, result);
	}
}
