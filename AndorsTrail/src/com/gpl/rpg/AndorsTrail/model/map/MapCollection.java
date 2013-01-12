package com.gpl.rpg.AndorsTrail.model.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.util.L;

public final class MapCollection {
	public final ArrayList<PredefinedMap> predefinedMaps = new ArrayList<PredefinedMap>();
	public final HashMap<String, WorldMapSegment> worldMapSegments = new HashMap<String, WorldMapSegment>();

	public MapCollection() {}
	
	public PredefinedMap findPredefinedMap(String name) {
    	for (PredefinedMap m : predefinedMaps) {
    		if (m.name.equals(name)) return m;
    	}
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Cannot find LayeredWorldMap for name \"" + name + "\".");
		}
    	return null;
    }

	public void reset() {
		for (PredefinedMap m : predefinedMaps) {
    		m.reset();
    	}
	}
	
	public String getWorldMapSegmentNameForMap(String mapName) {
		for (WorldMapSegment segment : worldMapSegments.values()) {
			if (segment.containsMap(mapName)) return segment.name;
		}
		return null;
	}


	// ====== PARCELABLE ===================================================================

	public void readFromParcel(DataInputStream src, WorldContext world, ViewContext view, int fileversion) throws IOException {
		int size;
		if (fileversion == 5) size = 11;
		else size = src.readInt();
		for(int i = 0; i < size; ++i) {
			predefinedMaps.get(i).readFromParcel(src, world, view, fileversion);
			if (i >= 40) {
				if (fileversion < 15) predefinedMaps.get(i).visited = false;
			}
		}
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		final int size = predefinedMaps.size();
		dest.writeInt(size);
		for(int i = 0; i < size; ++i) {
			predefinedMaps.get(i).writeToParcel(dest, flags);
		}
	}
}
