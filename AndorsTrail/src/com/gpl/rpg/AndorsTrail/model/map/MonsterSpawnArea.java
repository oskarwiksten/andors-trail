package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Range;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public final class MonsterSpawnArea {
	public final CoordRect area;
	public final Range quantity;
	private final Range spawnChance;
	public final String[] monsterTypeIDs;
	public final ArrayList<Monster> monsters = new ArrayList<Monster>();
	public final boolean isUnique; // unique == non-respawnable
	public final String group;
	public boolean isActive = true;

	public MonsterSpawnArea(CoordRect area, Range quantity, Range spawnChance, String[] monsterTypeIDs, boolean isUnique, String group) {
		this.area = area;
		this.quantity = quantity;
		this.spawnChance = spawnChance;
		this.monsterTypeIDs = monsterTypeIDs;
		this.isUnique = isUnique;
		this.group = group;
	}

	public Monster getMonsterAt(final Coord p) { return getMonsterAt(p.x, p.y); }
	public Monster getMonsterAt(final int x, final int y) {
		for (Monster m : monsters) {
			if (m.rectPosition.contains(x, y)) return m;
		}
		return null;
	}
	public Monster getMonsterAt(final CoordRect p) {
		for (Monster m : monsters) {
			if (m.rectPosition.intersects(p)) return m;
		}
		return null;
	}

	public Monster findSpawnedMonster(String monsterTypeID) {
		for (Monster m : monsters) {
			if (m.getMonsterTypeID().equals(monsterTypeID)) return m;
		}
		return null;
	}

	public void spawn(Coord p, WorldContext context) {
		final String monsterTypeID = monsterTypeIDs[Constants.rnd.nextInt(monsterTypeIDs.length)];
		spawn(p, monsterTypeID, context);
	}
	public MonsterType getRandomMonsterType(WorldContext context) {
		final String monsterTypeID = monsterTypeIDs[Constants.rnd.nextInt(monsterTypeIDs.length)];
		return context.monsterTypes.getMonsterType(monsterTypeID);
	}
	public void spawn(Coord p, String monsterTypeID, WorldContext context) {
		spawn(p, context.monsterTypes.getMonsterType(monsterTypeID));
	}
	public Monster spawn(Coord p, MonsterType type) {
		Monster m = new Monster(type);
		m.position.set(p);
		monsters.add(m);
		quantity.current++;
		return m;
	}

	public void remove(Monster m) {
		if (monsters.remove(m)) quantity.current--;
	}

	public boolean isSpawnable(boolean includeUniqueMonsters) {
		if (isUnique && !includeUniqueMonsters) return false;
		return (quantity.current < quantity.max && isActive);
	}

	public boolean rollShouldSpawn() {
		return Constants.rollResult(spawnChance);
	}

	public void reset() {
		monsters.clear();
		quantity.current = 0;
	}

	public void resetShops() {
		for (Monster m : monsters) {
			m.resetShopItems();
		}
	}


	// ====== PARCELABLE ===================================================================

	public void readFromParcel(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		monsters.clear();
		quantity.current = src.readInt();
		for(int i = 0; i < quantity.current; ++i) {
			monsters.add(Monster.readFromParcel(src, world, fileversion));
		}
	}

	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeInt(monsters.size());
		for (Monster m : monsters) {
			m.writeToParcel(dest, flags);
		}
	}
}
