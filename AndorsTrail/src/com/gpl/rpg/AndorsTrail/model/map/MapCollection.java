package com.gpl.rpg.AndorsTrail.model.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.context.WorldContext;

public final class MapCollection {
	public final ArrayList<LayeredWorldMap> predefinedMaps = new ArrayList<LayeredWorldMap>();

	public MapCollection() {}
	
	public LayeredWorldMap findPredefinedMap(String name) {
    	for (LayeredWorldMap m : predefinedMaps) {
    		if (m.name.equals(name)) return m;
    	}
    	return null;
    }

	public void reset() {
		for (LayeredWorldMap m : predefinedMaps) {
    		m.reset();
    	}
	}


	// ====== PARCELABLE ===================================================================

	public void readFromParcel(DataInputStream src, WorldContext world) throws IOException {
		final int size = src.readInt();
		for(int i = 0; i < size; ++i) {
			predefinedMaps.get(i).readFromParcel(src, world);
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
