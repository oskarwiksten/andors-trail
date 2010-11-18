package com.gpl.rpg.AndorsTrail.model.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.util.L;

public final class MapCollection {
	public final ArrayList<LayeredWorldMap> predefinedMaps = new ArrayList<LayeredWorldMap>();

	public MapCollection() {}
	
	public LayeredWorldMap findPredefinedMap(String name) {
    	for (LayeredWorldMap m : predefinedMaps) {
    		if (m.name.equals(name)) return m;
    	}
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Cannot find LayeredWorldMap for name \"" + name + "\".");
		}
    	return null;
    }

	public void reset() {
		for (LayeredWorldMap m : predefinedMaps) {
    		m.reset();
    	}
	}


	// ====== PARCELABLE ===================================================================

	public void readFromParcel(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		int size;
		if (fileversion == 5) {
			size = 11;
		} else {
			size = src.readInt();
		}
		for(int i = 0; i < size; ++i) {
			predefinedMaps.get(i).readFromParcel(src, world, fileversion);
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
