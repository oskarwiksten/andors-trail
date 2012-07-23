package com.gpl.rpg.AndorsTrail.resource.parsers;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import com.gpl.rpg.AndorsTrail.model.map.MapCollection;
import com.gpl.rpg.AndorsTrail.model.map.WorldMapSegment;
import com.gpl.rpg.AndorsTrail.model.map.WorldMapSegment.WorldMapSegmentMap;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.XmlResourceParserUtils;

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

	private static WorldMapSegment parseSegment(XmlResourceParser xrp, MapCollection maps) throws XmlPullParserException, IOException {
		String segmentName = xrp.getAttributeValue(null, "id");
		final WorldMapSegment segment = new WorldMapSegment(segmentName);
		
		XmlResourceParserUtils.readCurrentTagUntilEnd(xrp, new XmlResourceParserUtils.TagHandler() {
			@Override
			public void handleTag(XmlResourceParser xrp, String tagName) throws XmlPullParserException, IOException {
				if (tagName.equals("map")) {
					String mapName = xrp.getAttributeValue(null, "id");
					Coord mapPosition = new Coord(
							xrp.getAttributeIntValue(null, "x", -1),
							xrp.getAttributeIntValue(null, "y", -1)
						);
					WorldMapSegmentMap map = new WorldMapSegmentMap(mapName, mapPosition);
					segment.maps.put(mapName, map);
				}
			}
		});
		
		return segment;
	}

}
