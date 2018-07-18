package com.gpl.rpg.AndorsTrail.resource.parsers;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.item.DropList.DropItem;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonArrayParserFor;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonCollectionParserFor;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonFieldNames;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Pair;
import org.json.JSONException;
import org.json.JSONObject;

public final class DropListParser extends JsonCollectionParserFor<DropList> {

	private final JsonArrayParserFor<DropItem> dropItemParser;

	public DropListParser(final ItemTypeCollection itemTypes) {
		this.dropItemParser = new JsonArrayParserFor<DropItem>(DropItem.class) {
			@Override
			protected DropItem parseObject(JSONObject o) throws JSONException {
				return new DropItem(
						itemTypes.getItemType(o.getString(JsonFieldNames.DropItem.itemID))
						,ResourceParserUtils.parseChance(o.getString(JsonFieldNames.DropItem.chance))
						,ResourceParserUtils.parseQuantity(o.getJSONObject(JsonFieldNames.DropItem.quantity))
				);
			}
		};
	}

	@Override
	protected Pair<String, DropList> parseObject(JSONObject o) throws JSONException {
		String droplistID = o.getString(JsonFieldNames.DropList.dropListID);
		DropItem[] items = dropItemParser.parseArray(o.getJSONArray(JsonFieldNames.DropList.items));

		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (items == null) {
				L.log("OPTIMIZE: Droplist \"" + droplistID + "\" has no dropped items.");
			}
		}

		return new Pair<String, DropList>(droplistID, new DropList(items));
	}
}
