package com.gpl.rpg.AndorsTrail.model.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

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

	// Selftest method. Not part of the game logic.
	public void verifyData(WorldContext world) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			for (LayeredWorldMap m : predefinedMaps) {
				for (MapObject o : m.eventObjects) {
					if (o.type == MapObject.MAPEVENT_NEWMAP) {
						final String desc = "Map \"" + m.name + "\", place \"" + o.title + "\"";
						if (o.map == null || o.map.length() <= 0) {
							L.log("OPTIMIZE: " + desc + " has no destination map.");
						} else if (o.place == null || o.place.length() <= 0) {
							L.log("OPTIMIZE: " + desc + " has no destination place.");
						} else {
							LayeredWorldMap destination = findPredefinedMap(o.map);
							if (destination == null) {
								L.log("WARNING: " + desc + " references non-existing destination map \"" + o.map + "\".");
								continue;
							}
							MapObject place = destination.findEventObject(MapObject.MAPEVENT_NEWMAP, o.place);
							if (place == null) {
								L.log("WARNING: " + desc + " references non-existing destination place \"" + o.place + "\" on map \"" + o.map + "\".");
								continue;
							}
							
							if (!m.name.equalsIgnoreCase(place.map)) {
								L.log("WARNING: " + desc + " references destination place \"" + o.place + "\" on map \"" + o.map + "\", but that place does not reference back to this map.");
								continue;
							}
							if (!o.title.equalsIgnoreCase(place.place)) {
								L.log("WARNING: " + desc + " references destination place \"" + o.place + "\" on map \"" + o.map + "\", but that place does not reference back to this place.");
								continue;
							}
							if (!o.position.size.equals(place.position.size)) {
								L.log("WARNING: " + desc + " references destination place \"" + o.place + "\" on map \"" + o.map + "\", with different mapchange size.");
								continue;
							}
						}
					}
				}
	    	}
		}
	}

	// Selftest method. Not part of the game logic.
	public void DEBUG_getRequiredQuestStages(HashSet<String> requiredStages) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			for (LayeredWorldMap m : predefinedMaps) {
				for (MapObject o : m.eventObjects) {
					if (o.type == MapObject.MAPEVENT_SIGN) {
						if (o.questProgress == null) continue;
						requiredStages.add(o.questProgress.toString());
					}
				}
			}
		}
	}


	// ====== PARCELABLE ===================================================================

	public void readFromParcel(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		int size;
		if (fileversion == 5) size = 11;
		else size = src.readInt();
		for(int i = 0; i < size; ++i) {
			predefinedMaps.get(i).readFromParcel(src, world, fileversion);
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
