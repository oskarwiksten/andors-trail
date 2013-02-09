package com.gpl.rpg.AndorsTrail.resource.parsers;

import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropList.DropItem;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonCollectionParserFor;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonFieldNames;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonParserFor;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class DropListParser extends JsonCollectionParserFor<DropList> {

	private final JsonParserFor<DropItem> dropItemParser;

	public DropListParser(final ItemTypeCollection itemTypes) {
		this.dropItemParser = new JsonParserFor<DropItem>() {
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

		JSONArray array = o.getJSONArray(JsonFieldNames.DropList.items);
		final ArrayList<DropItem> items = new ArrayList<DropItem>();
		dropItemParser.parseRows(array, items);

		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (items.size() <= 0) {
				L.log("OPTIMIZE: Droplist \"" + droplistID + "\" has no dropped items.");
			}
		}

		return new Pair<String, DropList>(droplistID, new DropList(items));
	}
}
