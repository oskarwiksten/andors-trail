package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.listeners.MonsterSpawnListeners;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class MonsterSpawningController {
	private final ViewContext view;
	private final WorldContext world;
    public final MonsterSpawnListeners monsterSpawnListeners = new MonsterSpawnListeners();

	public MonsterSpawningController(ViewContext view, WorldContext world) {
    	this.view = view;
    	this.world = world;
    }
	
	public void spawnAllInArea(PredefinedMap map, MonsterSpawnArea area, boolean respawnUniqueMonsters) {
		while (area.isSpawnable(respawnUniqueMonsters)) {
			final boolean wasAbleToSpawn = spawnInArea(map, area, null);
			if (!wasAbleToSpawn) break;
		}
		view.actorStatsController.healAllMonsters(area);
	}
	
	public void maybeSpawn(PredefinedMap map) {
		for (MonsterSpawnArea a : map.spawnAreas) {
			if (!a.isSpawnable(false)) continue;
			if (!a.rollShouldSpawn()) continue;
			spawnInArea(map, a, world.model.player.position);
		}
	}
	
	public void spawnAll(PredefinedMap map) {
		boolean respawnUniqueMonsters = false;
		if (!map.visited) respawnUniqueMonsters = true;
		for (MonsterSpawnArea a : map.spawnAreas) {
			spawnAllInArea(map, a, respawnUniqueMonsters);
		}
	}
	
	private boolean spawnInArea(PredefinedMap map, MonsterSpawnArea a, Coord playerPosition) {
		return spawnInArea(map, a, a.getRandomMonsterType(world), playerPosition);
	}
	public boolean TEST_spawnInArea(PredefinedMap map, MonsterSpawnArea a, MonsterType type) { return spawnInArea(map, a, type, null); }
	private boolean spawnInArea(PredefinedMap map, MonsterSpawnArea a, MonsterType type, Coord playerPosition) {
		Coord p = getRandomFreePosition(map, a.area, type.tileSize, playerPosition);
		if (p == null) return false;
		Monster m = a.spawn(p, type);
		monsterSpawnListeners.onMonsterSpawned(map, m);
		return true;
	}
	
	public static Coord getRandomFreePosition(PredefinedMap map, CoordRect area, Size requiredSize, Coord playerPosition) {
		CoordRect p = new CoordRect(requiredSize);
		for(int i = 0; i < 100; ++i) {
			p.topLeft.set(
					area.topLeft.x + Constants.rnd.nextInt(area.size.width)
					,area.topLeft.y + Constants.rnd.nextInt(area.size.height));
			if (!MonsterMovementController.monsterCanMoveTo(map, p)) continue;
			if (playerPosition != null && p.contains(playerPosition)) continue;
			return p.topLeft;
		} 
		return null; // Couldn't find a free spot.
	}
	
	public void remove(PredefinedMap map, Monster m) {
		for (MonsterSpawnArea a : map.spawnAreas) {
			a.remove(m);
		}
		monsterSpawnListeners.onMonsterRemoved(map, m, m.rectPosition);
	}

}
