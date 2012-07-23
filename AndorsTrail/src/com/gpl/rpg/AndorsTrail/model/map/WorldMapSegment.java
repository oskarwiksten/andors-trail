package com.gpl.rpg.AndorsTrail.model.map;

import java.util.HashMap;

import com.gpl.rpg.AndorsTrail.util.Coord;

public final class WorldMapSegment {
	public final String name;
	public final HashMap<String, WorldMapSegmentMap> maps = new HashMap<String, WorldMapSegmentMap>();
	
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
}
