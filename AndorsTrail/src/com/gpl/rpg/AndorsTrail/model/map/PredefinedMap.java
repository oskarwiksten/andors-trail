package com.gpl.rpg.AndorsTrail.model.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class PredefinedMap {
	private static final long VISIT_RESET = 0;
	
	public final int xmlResourceId;
	public final String name;
	public final Size size;
	public final MapObject[] eventObjects;
	public final MonsterSpawnArea[] spawnAreas;
	public final ArrayList<Loot> groundBags = new ArrayList<Loot>();
	public boolean visited = false;
	public long lastVisitTime = VISIT_RESET;

	public final boolean[][] isWalkable;
	
	public PredefinedMap(int xmlResourceId, String name, Size size, boolean[][] isWalkable, MapObject[] eventObjects, MonsterSpawnArea[] spawnAreas, boolean hasFOW) {
		this.xmlResourceId = xmlResourceId;
		this.name = name;
		this.size = size;
		this.eventObjects = eventObjects;
		this.spawnAreas = spawnAreas;
		assert(size.width > 0);
		assert(size.height > 0);
		assert(isWalkable.length == size.width);
		assert(isWalkable[0].length == size.height);
		this.isWalkable = isWalkable;
	}
	
	public final boolean isWalkable(final Coord p) { 
		if (isOutside(p.x, p.y)) return false;
    	return isWalkable[p.x][p.y]; 
	}
    public final boolean isWalkable(final int x, final int y) {
    	if (isOutside(x, y)) return false;
    	return isWalkable[x][y];
    }
    public final boolean isWalkable(final CoordRect p) {
    	for (int y = 0; y < p.size.height; ++y) {
			for (int x = 0; x < p.size.width; ++x) {
				if (!isWalkable(p.topLeft.x + x, p.topLeft.y + y)) return false;
			}
		}
		return true;
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
    
    public MapObject findEventObject(int objectType, String name) {
    	for (MapObject o : eventObjects) {
    		if (o.type == objectType && name.equals(o.id)) return o;
    	}
    	return null;
    }
    public MapObject getEventObjectAt(final Coord p) {
    	for (MapObject o : eventObjects) {
    		if (o.position.contains(p)) {
    			return o;
    		}
    	}
		return null;
	}
    public boolean hasContainerAt(final Coord p) {
    	for (MapObject o : eventObjects) {
    		if (o.type == MapObject.MAPEVENT_CONTAINER) {
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
	
	private boolean spawnInArea(MonsterSpawnArea a, WorldContext context, Coord playerPosition) {
		return spawnInArea(a, a.getRandomMonsterType(context), playerPosition);
	}
	public boolean TEST_spawnInArea(MonsterSpawnArea a, MonsterType type) { return spawnInArea(a, type, null); }
	private boolean spawnInArea(MonsterSpawnArea a, MonsterType type, Coord playerPosition) {
		Coord p = getRandomFreePosition(a.area, type.tileSize, playerPosition);
		if (p == null) return false;
		a.spawn(p, type);
		return true;
	}
	
	public Coord getRandomFreePosition(CoordRect area, Size requiredSize, Coord playerPosition) {
		CoordRect p = new CoordRect(requiredSize);
		for(int i = 0; i < 100; ++i) {
			p.topLeft.set(
					area.topLeft.x + Constants.rnd.nextInt(area.size.width)
					,area.topLeft.y + Constants.rnd.nextInt(area.size.height));
			if (!monsterCanMoveTo(p)) continue;
			if (playerPosition != null && p.contains(playerPosition)) continue;
			return p.topLeft;
		} 
		return null; // Couldn't find a free spot.
	}
	
	public boolean monsterCanMoveTo(final CoordRect p) {
		if (!isWalkable(p)) return false;
		if (getMonsterAt(p) != null) return false;
		if (getEventObjectAt(p.topLeft) != null) return false;
    	return true;
	}
	
	public void spawnAll(WorldContext context) {
		boolean respawnUniqueMonsters = false;
		if (!visited) respawnUniqueMonsters = true;
		for (MonsterSpawnArea a : spawnAreas) {
			while (a.isSpawnable(respawnUniqueMonsters)) {
				final boolean wasAbleToSpawn = spawnInArea(a, context, null);
				if (!wasAbleToSpawn) break;
			}
			a.healAllMonsters();
		}
	}
	public boolean maybeSpawn(WorldContext context) {
		boolean hasSpawned = false;
		for (MonsterSpawnArea a : spawnAreas) {
			if (!a.isSpawnable(false)) continue;
			if (!a.rollShouldSpawn()) continue;
			if (spawnInArea(a, context, context.model.player.position)) hasSpawned = true;
		}
		return hasSpawned;
	}
	
	public void remove(Monster m) {
		for (MonsterSpawnArea a : spawnAreas) {
			a.remove(m);
		}
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
		groundBags.clear();
		for(MonsterSpawnArea a : spawnAreas) {
			a.reset();
		}
		visited = false;
		lastVisitTime = VISIT_RESET;
	}

	public boolean isRecentlyVisited() {
		if (lastVisitTime == VISIT_RESET) return false;
		return (System.currentTimeMillis() - lastVisitTime) < Constants.MAP_UNVISITED_RESPAWN_DURATION_MS;
	}
	public void updateLastVisitTime() {
		lastVisitTime = System.currentTimeMillis();
	}
	public void resetIfNotRecentlyVisited() {
		if (lastVisitTime == VISIT_RESET) return;
		if (isRecentlyVisited()) return;
		
		// We reset all non-unique spawn areas. This keeps the savegame file smaller, thus reducing load and save times. Also keeps the running memory usage slightly lower.
		for(MonsterSpawnArea a : spawnAreas) {
			if (!a.isUnique) a.reset();
		}
		lastVisitTime = VISIT_RESET;
	}

	public void createAllContainerLoot() {
		for (MapObject o : eventObjects) {
    		if (o.type == MapObject.MAPEVENT_CONTAINER) {
	    		Loot bag = getBagOrCreateAt(o.position.topLeft);
	    		o.dropList.createRandomLoot(bag);
    		}
    	}
	}

	
	
	// ====== PARCELABLE ===================================================================

	public void readFromParcel(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		final int size1 = src.readInt();
		for(int i = 0; i < size1; ++i) {
			this.spawnAreas[i].readFromParcel(src, world, fileversion);
		}
		
		if (fileversion <= 5) return;
		
		groundBags.clear();
		final int size2 = src.readInt();
		for(int i = 0; i < size2; ++i) {
			groundBags.add(new Loot(src, world, fileversion));
		}

		if (fileversion <= 11) return;
		visited = src.readBoolean();
		
		
		if (fileversion <= 15) {
			if (visited) {
				lastVisitTime = System.currentTimeMillis();
				createAllContainerLoot();
			}
			return;
		}
		lastVisitTime = src.readLong();
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeInt(spawnAreas.length);
		for(MonsterSpawnArea a : spawnAreas) {
			a.writeToParcel(dest, flags);
		}
		dest.writeInt(groundBags.size());
		for(Loot l : groundBags) {
			l.writeToParcel(dest, flags);
		}
		dest.writeBoolean(visited);
		dest.writeLong(lastVisitTime);
	}
}
