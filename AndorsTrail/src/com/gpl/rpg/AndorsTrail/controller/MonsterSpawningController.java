package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.listeners.MonsterSpawnListeners;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class MonsterSpawningController {
	private final ControllerContext controllers;
	private final WorldContext world;
	public final MonsterSpawnListeners monsterSpawnListeners = new MonsterSpawnListeners();

	public MonsterSpawningController(ControllerContext controllers, WorldContext world) {
		this.controllers = controllers;
		this.world = world;
	}

	public void spawnAllInArea(PredefinedMap map, LayeredTileMap tileMap, MonsterSpawnArea area, boolean respawnUniqueMonsters) {
		while (area.isSpawnable(respawnUniqueMonsters)) {
			final boolean wasAbleToSpawn = spawnInArea(map, tileMap, area, null);
			if (!wasAbleToSpawn) break;
		}
		controllers.actorStatsController.healAllMonsters(area);
	}

	public void maybeSpawn(PredefinedMap map, LayeredTileMap tileMap) {
		for (MonsterSpawnArea a : map.spawnAreas) {
			if (!a.isSpawnable(false)) continue;
			if (!a.rollShouldSpawn()) continue;
			spawnInArea(map, tileMap, a, world.model.player.position);
		}
	}

	public void spawnAll(PredefinedMap map, LayeredTileMap tileMap) {
		boolean respawnUniqueMonsters = false;
		if (!map.visited) respawnUniqueMonsters = true;
		for (MonsterSpawnArea a : map.spawnAreas) {
			spawnAllInArea(map, tileMap, a, respawnUniqueMonsters);
		}
	}

	private boolean spawnInArea(PredefinedMap map, LayeredTileMap tileMap, MonsterSpawnArea a, Coord playerPosition) {
		return spawnInArea(map, tileMap, a, a.getRandomMonsterType(world), playerPosition);
	}
	public boolean TEST_spawnInArea(PredefinedMap map, LayeredTileMap tileMap, MonsterSpawnArea a, MonsterType type) { return spawnInArea(map, tileMap, a, type, null); }
	private boolean spawnInArea(PredefinedMap map, LayeredTileMap tileMap, MonsterSpawnArea a, MonsterType type, Coord playerPosition) {
		Coord p = getRandomFreePosition(map, tileMap, a.area, type.tileSize, playerPosition);
		if (p == null) return false;
		Monster m = a.spawn(p, type);
		monsterSpawnListeners.onMonsterSpawned(map, m);
		return true;
	}

	public static Coord getRandomFreePosition(PredefinedMap map, LayeredTileMap tileMap, CoordRect area, Size requiredSize, Coord playerPosition) {
		CoordRect p = new CoordRect(requiredSize);
		for(int i = 0; i < 100; ++i) {
			p.topLeft.set(
					area.topLeft.x + Constants.rnd.nextInt(area.size.width)
					,area.topLeft.y + Constants.rnd.nextInt(area.size.height));
			if (!MonsterMovementController.monsterCanMoveTo(map, tileMap, p)) continue;
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

	public void activateSpawnArea(PredefinedMap map, LayeredTileMap tileMap, MonsterSpawnArea spawnArea, boolean spawnAllMonsters) {
		spawnArea.isSpawning = true;
		if (spawnAllMonsters) {
			boolean respawnUniqueMonsters = true;
			spawnAllInArea(map, tileMap, spawnArea, respawnUniqueMonsters);
		}
	}

	public void deactivateSpawnArea(MonsterSpawnArea spawnArea, boolean removeAllMonsters) {
		spawnArea.isSpawning = false;
		if (removeAllMonsters) {
			spawnArea.removeAllMonsters();
		}
	}
}
