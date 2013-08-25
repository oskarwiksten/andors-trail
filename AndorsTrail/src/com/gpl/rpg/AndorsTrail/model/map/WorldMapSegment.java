package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.util.Coord;

import java.util.HashMap;
import java.util.HashSet;

public final class WorldMapSegment {
	public final String name;
	public final HashMap<String, WorldMapSegmentMap> maps = new HashMap<String, WorldMapSegmentMap>();
	public final HashMap<String, NamedWorldMapArea> namedAreas = new HashMap<String, NamedWorldMapArea>();

	public WorldMapSegment(String name) {
		this.name = name;
	}

	public boolean containsMap(String mapName) { return maps.containsKey(mapName); }

	public static final class WorldMapSegmentMap {
		public final String mapName;
		public final Coord worldPosition;
		public WorldMapSegmentMap(String mapName, Coord worldPosition) {
			this.mapName = mapName;
			this.worldPosition = worldPosition;
		}
	}

	// Towns, cities, villages, taverns, named dungeons
	public static final class NamedWorldMapArea {
		public final String id;
		public final String name;
		public final String type; // "settlement" or "other"
		public final HashSet<String> mapNames = new HashSet<String>();
		public NamedWorldMapArea(String id, String name, String type) {
			this.id = id;
			this.name = name;
			this.type = type;
		}
	}
}
