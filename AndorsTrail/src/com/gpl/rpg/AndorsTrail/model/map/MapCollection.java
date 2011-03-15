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
						final String desc = "Map \"" + m.name + "\", place \"" + o.id + "\"";
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
							if (!o.id.equalsIgnoreCase(place.place)) {
								L.log("WARNING: " + desc + " references destination place \"" + o.place + "\" on map \"" + o.map + "\", but that place does not reference back to this place.");
								continue;
							}
							if (!o.position.size.equals(place.position.size)) {
								L.log("WARNING: " + desc + " references destination place \"" + o.place + "\" on map \"" + o.map + "\", with different mapchange size.");
								continue;
							}
						}
					} else if (o.type == MapObject.MAPEVENT_KEYAREA) {
						if (o.id == null || o.id.length() <= 0) {
							L.log("WARNING: Map \"" + m.name + "\" contains keyarea without phraseid.");
							continue;
						} 
						world.conversations.getPhrase(o.id); // Will warn inside if not available.
					} else if (o.type == MapObject.MAPEVENT_SIGN) {
						if (o.id == null || o.id.length() <= 0) {
							L.log("WARNING: Map \"" + m.name + "\" contains sign without phraseid.");
							continue;
						} 
						world.conversations.getPhrase(o.id); // Will warn inside if not available.
					} else if (o.type == MapObject.MAPEVENT_REST) {
						if (o.id == null || o.id.length() <= 0) {
							L.log("WARNING: Map \"" + m.name + "\" contains rest area without id.");
							continue;
						}
						if (m.findEventObject(MapObject.MAPEVENT_REST, o.id) != o) {
							L.log("WARNING: Map \"" + m.name + "\" contains duplicate rest area with id \"" + o.id + "\".");
							continue;
						}
					}
				}
				
				for (int i = 0; i < m.spawnAreas.length; ++i) {
					MonsterSpawnArea uniqueArea = m.spawnAreas[i];
					if (!uniqueArea.isUnique) continue;
					
					for (int j = 0; j < i; ++j) {
						MonsterSpawnArea nonUniqueArea = m.spawnAreas[j];
						if (nonUniqueArea.isUnique) continue;
						if (nonUniqueArea.area.intersects(uniqueArea.area)) {
							L.log("WARNING: Map \"" + m.name + "\" contains unique spawnarea at " + uniqueArea.area.toString() + " that intersects a nonunique spawnarea. Consider placing the unique spawn first to make sure that this monster has a place to spawn.");
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
					if (o.type == MapObject.MAPEVENT_KEYAREA) {
						if (o.requireQuestProgress == null) continue;
						requiredStages.add(o.requireQuestProgress.toString());
					}
				}
			}
		}
	}
	
	// Selftest method. Not part of the game logic.
	public void DEBUG_getUsedPhrases(HashSet<String> usedPhrases) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			for (LayeredWorldMap m : predefinedMaps) {
				for (MapObject o : m.eventObjects) {
					if (o.type == MapObject.MAPEVENT_KEYAREA || o.type == MapObject.MAPEVENT_SIGN) {
						if (o.id == null || o.id.length() <= 0) continue;
						usedPhrases.add(o.id);
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
