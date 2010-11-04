package com.gpl.rpg.AndorsTrail.model.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class LayeredWorldMap {
	public static int LAYER_GROUND = 0;
	public static int LAYER_OBJECTS = 1;
	public static int LAYER_ABOVE = 2;
	
	public final String name;
	public final Size size;
	public final MapLayer[] layers;
	public final MapObject[] eventObjects;
	public final KeyArea[] keyAreas;
	public final MonsterSpawnArea[] spawnAreas;
	public final ArrayList<Loot> groundBags = new ArrayList<Loot>();
	//public final boolean hasFOW;
	//public final boolean[][] isVisible;
	public final boolean[][] isWalkable;
	
	public LayeredWorldMap(String name, Size size, MapLayer[] layers, boolean[][] isWalkable, MapObject[] eventObjects, KeyArea[] keyAreas, MonsterSpawnArea[] spawnAreas, boolean hasFOW) {
		this.name = name;
		this.size = size;
		this.eventObjects = eventObjects;
		this.keyAreas = keyAreas;
		this.spawnAreas = spawnAreas;
		assert(size.width > 0);
		assert(size.height > 0);
		assert(layers.length == 3);
		assert(isWalkable.length == size.width);
		assert(isWalkable[0].length == size.height);
		this.isWalkable = isWalkable;
		this.layers = layers;
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
    
    public MapObject findEventObject(int objectType, String name) {
    	for (MapObject o : eventObjects) {
    		if (o.type == objectType && o.title.equals(name)) return o;
    	}
    	return null;
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
	
	private boolean spawnInArea(MonsterSpawnArea a, WorldContext context) {
		return spawnInArea(a, a.getRandomMonsterType(context));
	}
	public boolean TEST_spawnInArea(MonsterSpawnArea a, MonsterType type) { return spawnInArea(a, type); }
	private boolean spawnInArea(MonsterSpawnArea a, MonsterType type) {
		Coord p = getRandomFreePosition(a.area, type.tileSize);
		if (p == null) return false;
		a.spawn(p, type);
		return true;
	}
	
	private Coord getRandomFreePosition(CoordRect area, Size requiredSize) {
		CoordRect p = new CoordRect(requiredSize);
		for(int i = 0; i < 100; ++i) {
			p.topLeft.set(
					area.topLeft.x + ModelContainer.rnd.nextInt(area.size.width)
					,area.topLeft.y + ModelContainer.rnd.nextInt(area.size.height));
			if (!isWalkable(p)) continue;
			if (getMonsterAt(p) != null) continue;
			return p.topLeft;
		} 
		return null; // Couldn't find a free spot.
	}
	
	public void spawnAll(WorldContext context, boolean respawnUniqueMonsters) {
		for (MonsterSpawnArea a : spawnAreas) {
			while (a.isSpawnable(respawnUniqueMonsters)) {
				spawnInArea(a, context);
			}
		}
	}
	public boolean maybeSpawn(WorldContext context) {
		boolean hasSpawned = false;
		for (MonsterSpawnArea a : spawnAreas) {
			if (!a.isSpawnable(false)) continue;
			if (!a.rollShouldSpawn()) continue;
			if (spawnInArea(a, context)) hasSpawned = true;
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
		b = new Loot();
		b.position.set(position);
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
	}
	
	
	
	// ====== PARCELABLE ===================================================================

	public void readFromParcel(DataInputStream src, WorldContext world) throws IOException {
		final int size1 = src.readInt();
		for(int i = 0; i < size1; ++i) {
			this.spawnAreas[i].readFromParcel(src, world);
		}
		
		/*
		groundBags.clear();
		final int size2 = src.readInt();
		for(int i = 0; i < size2; ++i) {
			groundBags.add(new Loot(src, world));
		}
		*/
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeInt(spawnAreas.length);
		for(MonsterSpawnArea a : spawnAreas) {
			a.writeToParcel(dest, flags);
		}
		
		/*
		dest.writeInt(groundBags.size());
		for(Loot l : groundBags) {
			l.writeToParcel(dest, flags);
		}
		*/
	}
}
