package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.controller.VisualEffectController.BloodSplatter;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Size;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PredefinedMap {
	private static final long VISIT_RESET = 0;

	public final int xmlResourceId;
	public final String name;
	public final Size size;
	public final MapObject[] eventObjects;
	public final MapObjectReplace[] eventObjectReplaces;
	public final MonsterSpawnArea[] spawnAreas;
	public final ArrayList<Loot> groundBags = new ArrayList<Loot>();
	public boolean visited = false;
	public long lastVisitTime = VISIT_RESET;
	public String lastSeenLayoutHash = "";
	private final boolean isOutdoors;

	public final ArrayList<BloodSplatter> splatters = new ArrayList<BloodSplatter>();

	public PredefinedMap(int xmlResourceId, String name, Size size, MapObject[] eventObjects, MapObjectReplace[] eventObjectReplaces, MonsterSpawnArea[] spawnAreas, boolean isOutdoors) {
		this.xmlResourceId = xmlResourceId;
		this.name = name;
		this.size = size;
		this.eventObjects = eventObjects;
		this.eventObjectReplaces = eventObjectReplaces;
		this.spawnAreas = spawnAreas;
		assert(size.width > 0);
		assert(size.height > 0);
		this.isOutdoors = isOutdoors;
	}

	public final boolean isOutside(final Coord p) { return isOutside(p.x, p.y); }
	public final boolean isOutside(final int x, final int y) {
		if (x < 0) return true;
		if (y < 0) return true;
		if (x >= size.width) return true;
		if (y >= size.height) return true;
		return false;
	}
	public final boolean isOutside(final CoordRect area) {
		if (isOutside(area.topLeft)) return true;
		if (area.topLeft.x + area.size.width > size.width) return true;
		if (area.topLeft.y + area.size.height > size.height) return true;
		return false;
	}

	public MapObject findEventObject(MapObject.MapObjectType objectType, String name) {
		for (MapObject o : eventObjects) {
			if (o.type == objectType && name.equals(o.id)) return o;
		}
		return null;
	}
	public List<MapObject> getEventObjectsAt(final Coord p) {
		List<MapObject> result = new ArrayList<MapObject>();
		for (MapObject o : eventObjects) {
			if (!o.isActive) continue;
			if (o.position.contains(p)) {
				result.add(o);
			}
		}
		return result;
	}
	public boolean hasContainerAt(final Coord p) {
		for (MapObject o : eventObjects) {
			if (o.type == MapObject.MapObjectType.container) {
				if (o.position.contains(p)) {
					return true;
				}
			}
		}
		return false;
	}

	public Monster getMonsterAt(final CoordRect p) {
		for (MonsterSpawnArea a : spawnAreas) {
			Monster m = a.getMonsterAt(p);
			if (m != null) return m;
		}
		return null;
	}
	public Monster getMonsterAt(final Coord p) { return getMonsterAt(p.x, p.y); }
	public Monster getMonsterAt(final int x, final int y) {
		for (MonsterSpawnArea a : spawnAreas) {
			Monster m = a.getMonsterAt(x, y);
			if (m != null) return m;
		}
		return null;
	}

	public Monster findSpawnedMonster(final String monsterTypeID) {
		for (MonsterSpawnArea a : spawnAreas) {
			Monster m = a.findSpawnedMonster(monsterTypeID);
			if (m != null) return m;
		}
		return null;
	}

	public Loot getBagAt(final Coord p) {
		for (Loot l : groundBags) {
			if (l.position.equals(p)) return l;
		}
		return null;
	}
	public Loot getBagOrCreateAt(final Coord position) {
		Loot b = getBagAt(position);
		if (b != null) return b;
		boolean isContainer = hasContainerAt(position);
		b = new Loot(!isContainer);
		b.position.set(position);
		if (isOutside(position)) {
			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				L.log("WARNING: trying to place bag outside map. Map is " + size.toString() + ", bag tried to place at " + position.toString());
			}
			return b;
		}
		groundBags.add(b);
		return b;
	}
	public void itemDropped(ItemType itemType, int quantity, Coord position) {
		Loot l = getBagOrCreateAt(position);
		l.items.addItem(itemType, quantity);
	}
	public void removeGroundLoot(Loot loot) {
		groundBags.remove(loot);
	}
	public void reset() {
		resetTemporaryData();
		for(MonsterSpawnArea a : spawnAreas) {
			a.reset();
		}
		groundBags.clear();
		visited = false;
		lastSeenLayoutHash = "";
	}

	public boolean isRecentlyVisited() {
		if (lastVisitTime == VISIT_RESET) return false;
		return (System.currentTimeMillis() - lastVisitTime) < Constants.MAP_UNVISITED_RESPAWN_DURATION_MS;
	}
	public void updateLastVisitTime() {
		lastVisitTime = System.currentTimeMillis();
	}
	public void resetTemporaryData() {
		for(MonsterSpawnArea a : spawnAreas) {
			if (a.isUnique) a.resetShops();
			else a.reset();
		}
		splatters.clear();
		lastVisitTime = VISIT_RESET;
	}
	public boolean hasResetTemporaryData() {
		return lastVisitTime == VISIT_RESET;
	}
	public boolean hasPersistentData(WorldContext world) {
		if (!hasResetTemporaryData()) return true;
		if (this == world.model.currentMap) return true;
		if (!groundBags.isEmpty()) return true;
		for (MonsterSpawnArea a : spawnAreas) {
			if (a.isUnique) return true;
		}
		return false;
	}

	public void createAllContainerLoot() {
		for (MapObject o : eventObjects) {
			if (o.type == MapObject.MapObjectType.container) {
				Loot bag = getBagOrCreateAt(o.position.topLeft);
				o.dropList.createRandomLoot(bag, null);
			}
		}
	}



	// ====== PARCELABLE ===================================================================

	public void readFromParcel(DataInputStream src, WorldContext world, ControllerContext controllers, int fileversion) throws IOException {
		boolean shouldLoadPersistentData = true;
		if (fileversion >= 37) shouldLoadPersistentData = src.readBoolean();

		int loadedSpawnAreas = 0;
		if (shouldLoadPersistentData) {
			loadedSpawnAreas = src.readInt();
			for(int i = 0; i < loadedSpawnAreas; ++i) {
				if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
					if (i >= this.spawnAreas.length) {
						L.log("WARNING: Trying to load monsters from savegame in map " + this.name + " for spawn #" + i + ". This will totally fail.");
					}
				}
				this.spawnAreas[i].readFromParcel(src, world, fileversion);
			}

			groundBags.clear();
			if (fileversion <= 5) return;

			final int size2 = src.readInt();
			for(int i = 0; i < size2; ++i) {
				groundBags.add(new Loot(src, world, fileversion));
			}

			if (fileversion <= 11) return;

			if (fileversion < 37) visited = src.readBoolean();

			if (fileversion <= 15) {
				if (visited) {
					lastVisitTime = System.currentTimeMillis();
					createAllContainerLoot();
				}
				return;
			}
			lastVisitTime = src.readLong();

			if (visited) {
				if (fileversion > 30 && fileversion < 36) {
					/*int lastVisitVersion = */src.readInt();
				}
			}
		}
		if (fileversion >= 37) visited = true;

		if (fileversion < 36) lastSeenLayoutHash = "";
		else lastSeenLayoutHash = src.readUTF();

		for(int i = loadedSpawnAreas; i < spawnAreas.length; ++i) {
			MonsterSpawnArea area = this.spawnAreas[i];
			if (area.isUnique && visited) controllers.monsterSpawnController.spawnAllInArea(this, null, area, true);
			else area.reset();
		}
	}

	public void writeToParcel(DataOutputStream dest, WorldContext world, int flags) throws IOException {
		if (this.hasPersistentData(world)) {
			dest.writeBoolean(true);
			dest.writeInt(spawnAreas.length);
			for(MonsterSpawnArea a : spawnAreas) {
				a.writeToParcel(dest, flags);
			}
			dest.writeInt(groundBags.size());
			for(Loot l : groundBags) {
				l.writeToParcel(dest, flags);
			}
			dest.writeLong(lastVisitTime);
		} else {
			dest.writeBoolean(false);
		}
		dest.writeUTF(lastSeenLayoutHash);
	}

	public List<MonsterSpawnArea> applyObjectReplace(MapObjectReplace replace) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!replace.isActive) throw new RuntimeException("Trying to apply an inactive replace area. Should be checked in the code before. Check your stack trace !");
			if (replace.isApplied) throw new RuntimeException("Trying to reapply an applied replace area. Should be checked in the code before. Check your stack trace !");
		}

		
		for (MapObject obj : eventObjects) {
			if (!replace.position.contains(obj.position)) continue;
			if (obj.group.equals(replace.sourceGroup)) {
				obj.isActive = false;
			} else if (obj.group.equals(replace.targetGroup)) {
				obj.isActive = true;
			}
		}
		
		// MonsterSpawnAreas added to this list are marked as needing a full clean up (Spawn, or reset). 
		// This depends on the replace strategy.
		List<MonsterSpawnArea> triggerSpawn = null;
		for (MonsterSpawnArea area : spawnAreas) {
			if (!replace.position.contains(area.area)) continue;
			if (area.group.equals(replace.sourceGroup)) {
				area.isActive = false;
				//This strategy requires immediate deletion of all monsters.
				if (replace.strategy.equals(MapObjectReplace.SpawnStrategy.cleanUpAll)) {
					if (triggerSpawn == null) triggerSpawn = new ArrayList<MonsterSpawnArea>();
					triggerSpawn.add(area);
				}
			} else if (area.group.equals(replace.targetGroup)) {
				area.isActive = true;
				//Both other strategies require auto-spawning all monsters in the area.
				if (!replace.strategy.equals(MapObjectReplace.SpawnStrategy.doNothing)) {
					if (triggerSpawn == null) triggerSpawn = new ArrayList<MonsterSpawnArea>();
					triggerSpawn.add(area);
				}
			}
		}
		replace.isApplied = true;
		
		return triggerSpawn;
	}
}
