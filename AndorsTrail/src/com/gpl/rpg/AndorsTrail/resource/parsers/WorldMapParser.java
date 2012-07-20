package com.gpl.rpg.AndorsTrail.resource.parsers;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import com.gpl.rpg.AndorsTrail.model.map.MapCollection;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.util.L;

public final class WorldMapParser {
	public static void read(Resources r, int xmlResourceId, final MapCollection maps) {
		read(r.getXml(xmlResourceId), maps);
	}
	
	private static void read(XmlResourceParser xrp, final MapCollection maps) {
		try {
			int eventType;
			while ((eventType = xrp.next()) != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					String s = xrp.getName();
					if (s.equals("div")) {
						String mapName = xrp.getAttributeValue(null, "id");
						PredefinedMap map = maps.findPredefinedMap(mapName);
						if (map == null) continue;
						map.worldMapPosition.x = xrp.getAttributeIntValue(null, "x-posx", -1);
						map.worldMapPosition.y = xrp.getAttributeIntValue(null, "x-posy", -1);
					} 
				}
            }
            xrp.close();
		} catch (Exception e) {
			L.log("Error reading worldmap: " + e.toString());
		}
	}

}
