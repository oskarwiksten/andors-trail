package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.PathFinder.EvaluateWalkable;
import com.gpl.rpg.AndorsTrail.controller.listeners.MonsterMovementListeners;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class MonsterMovementController implements EvaluateWalkable {
	private final ControllerContext controllers;
	private final WorldContext world;
	public final MonsterMovementListeners monsterMovementListeners = new MonsterMovementListeners();

	public MonsterMovementController(ControllerContext controllers, WorldContext world) {
		this.controllers = controllers;
		this.world = world;
	}

	public void moveMonsters() {
		long currentTime = System.currentTimeMillis();

		for (MonsterSpawnArea a : world.model.currentMap.spawnAreas) {
			for (Monster m : a.monsters) {
				if (m.nextActionTime <= currentTime) {
					moveMonster(m, a);
				}
			}
		}
	}

	public void attackWithAgressiveMonsters() {
		for (MonsterSpawnArea a : world.model.currentMap.spawnAreas) {
			for (Monster m : a.monsters) {
				if (!m.isAgressive()) continue;
				if (!m.isAdjacentTo(world.model.player)) continue;

				int aggressionChanceBias = world.model.player.getSkillLevel(SkillCollection.SkillID.evasion) * SkillCollection.PER_SKILLPOINT_INCREASE_EVASION_MONSTER_ATTACK_CHANCE_PERCENTAGE;
				if (Constants.roll100(Constants.MONSTER_AGGRESSION_CHANCE_PERCENT - aggressionChanceBias)) {
					monsterMovementListeners.onMonsterSteppedOnPlayer(m);
					controllers.combatController.monsterSteppedOnPlayer(m);
					return;
				}
			}
		}
	}

	public static boolean monsterCanMoveTo(final PredefinedMap map, final LayeredTileMap tilemap, final CoordRect p) {
		if (tilemap != null) {
			if (!tilemap.isWalkable(p)) return false;
		}
		if (map.getMonsterAt(p) != null) return false;

		for (MapObject m : map.eventObjects) {
			if (m == null) continue;
			if (!m.position.intersects(p)) continue;
			switch (m.type) {
				case newmap:
				case keyarea:
				case rest:
					return false;
			}
		}
		return true;
	}

	private void moveMonster(final Monster m, final MonsterSpawnArea area) {
		PredefinedMap map = world.model.currentMap;
		LayeredTileMap tileMap = world.model.currentTileMap;
		m.nextActionTime += getMillisecondsPerMove(m);
		if (m.movementDestination == null) {
			// Monster has waited and should start to move again.
			m.movementDestination = new Coord(m.position);
			if (Constants.rnd.nextBoolean()) {
				m.movementDestination.x = area.area.topLeft.x + Constants.rnd.nextInt(area.area.size.width);
			} else {
				m.movementDestination.y = area.area.topLeft.y + Constants.rnd.nextInt(area.area.size.height);
			}
		} else if (m.position.equals(m.movementDestination)) {
			// Monster has been moving and arrived at the destination.
			cancelCurrentMonsterMovement(m);
		} else {
			determineMonsterNextPosition(m, area, world.model.player.position);

			if (!monsterCanMoveTo(map, tileMap, m.nextPosition)) {
				cancelCurrentMonsterMovement(m);
				return;
			}
			if (m.nextPosition.contains(world.model.player.position)) {
				if (!m.isAgressive()) {
					cancelCurrentMonsterMovement(m);
					return;
				}
				monsterMovementListeners.onMonsterSteppedOnPlayer(m);
				controllers.combatController.monsterSteppedOnPlayer(m);
			} else {
				moveMonsterToNextPosition(m, map);
			}
		}
	}

	private void determineMonsterNextPosition(Monster m, MonsterSpawnArea area, Coord playerPosition) {
		if (m.isAgressive()) {
			boolean searchForPath = false;
			if (m.getMovementAggressionType() == MonsterType.AggressionType.protectSpawn) {
				if (area.area.contains(playerPosition)) searchForPath = true;
			} else if (m.getMovementAggressionType() == MonsterType.AggressionType.wholeMap) {
				searchForPath = true;
			}
			if (searchForPath) {
				if (findPathFor(m, playerPosition)) return;
			}
		}

		// Monster is moving in a straight line.
		m.nextPosition.topLeft.set(
				m.position.x + sgn(m.movementDestination.x - m.position.x)
				,m.position.y + sgn(m.movementDestination.y - m.position.y)
			);
	}

	private static void cancelCurrentMonsterMovement(final Monster m) {
		m.movementDestination = null;
		m.nextActionTime += getMillisecondsPerMove(m) * Constants.rollValue(Constants.monsterWaitTurns);
	}

	private static int getMillisecondsPerMove(Monster m) {
		return Constants.MONSTER_MOVEMENT_TURN_DURATION_MS * m.getMoveCost() / m.getMaxAP();
	}

	private static int sgn(int i) {
		if (i <= -1) return -1;
		if (i >= 1) return 1;
		return 0;
	}

	private final PathFinder pathfinder = new PathFinder(Constants.MAX_MAP_WIDTH, Constants.MAX_MAP_HEIGHT, this);
	public boolean findPathFor(Monster m, Coord to) {
		return pathfinder.findPathBetween(m.rectPosition, to, m.nextPosition);
	}

	@Override
	public boolean isWalkable(CoordRect r) {
		return monsterCanMoveTo(world.model.currentMap, world.model.currentTileMap, r);
	}

	public void moveMonsterToNextPosition(Monster m, PredefinedMap map) {
		CoordRect previousPosition = new CoordRect(new Coord(m.position), m.rectPosition.size);
		m.position.set(m.nextPosition.topLeft);
		monsterMovementListeners.onMonsterMoved(map, m, previousPosition);
	}
}
