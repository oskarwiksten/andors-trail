package com.gpl.rpg.AndorsTrail.resource.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class JsonParserFor<T> {
	public void parseRows(JSONArray array, ArrayList<T> dest) throws JSONException {
		if (array == null) return;

		for (int i = 0; i < array.length(); ++i) {
			JSONObject o = array.getJSONObject(i);
			dest.add(parseObject(o));
		}
	}

	protected abstract T parseObject(JSONObject o) throws JSONException;
}
