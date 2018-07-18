package com.gpl.rpg.AndorsTrail.resource.parsers;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import com.gpl.rpg.AndorsTrail.model.map.MapCollection;
import com.gpl.rpg.AndorsTrail.model.map.WorldMapSegment;
import com.gpl.rpg.AndorsTrail.model.map.WorldMapSegment.NamedWorldMapArea;
import com.gpl.rpg.AndorsTrail.model.map.WorldMapSegment.WorldMapSegmentMap;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Pair;
import com.gpl.rpg.AndorsTrail.util.XmlResourceParserUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

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
					if (s.equals("segment")) {
						WorldMapSegment segment = parseSegment(xrp, maps);
						maps.worldMapSegments.put(segment.name, segment);
					}
				}
			}
			xrp.close();
		} catch (Exception e) {
			L.log("Error reading worldmap: " + e.toString());
		}
	}

	private static WorldMapSegment parseSegment(XmlResourceParser xrp, final MapCollection maps) throws XmlPullParserException, IOException {
		String segmentName = xrp.getAttributeValue(null, "id");
		final WorldMapSegment segment = new WorldMapSegment(segmentName);

		final ArrayList<Pair<String, String>> mapsInNamedAreas = new ArrayList<Pair<String,String>>();
		XmlResourceParserUtils.readCurrentTagUntilEnd(xrp, new XmlResourceParserUtils.TagHandler() {
			@Override
			public void handleTag(XmlResourceParser xrp, String tagName) throws XmlPullParserException, IOException {
				if (tagName.equals("map")) {
					String mapName = xrp.getAttributeValue(null, "id");
					if (maps.findPredefinedMap(mapName) == null) return;
					Coord mapPosition = new Coord(
							xrp.getAttributeIntValue(null, "x", -1),
							xrp.getAttributeIntValue(null, "y", -1)
						);
					WorldMapSegmentMap map = new WorldMapSegmentMap(mapName, mapPosition);
					segment.maps.put(mapName, map);

					String namedArea = xrp.getAttributeValue(null, "area");
					if (namedArea != null) mapsInNamedAreas.add(new Pair<String, String>(mapName, namedArea));
				} else if (tagName.equals("namedarea")) {
					String id = xrp.getAttributeValue(null, "id");
					String name = xrp.getAttributeValue(null, "name");
					String type = xrp.getAttributeValue(null, "type");
					segment.namedAreas.put(id, new NamedWorldMapArea(id, name, type));
				}
			}
		});

		for (Pair<String, String> m : mapsInNamedAreas) {
			segment.namedAreas.get(m.second).mapNames.add(m.first);
		}

		return segment;
	}

}
