package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.listeners.MonsterMovementListeners;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class MonsterMovementController {
	private final ViewContext view;
    private final WorldContext world;
    public final MonsterMovementListeners monsterMovementListeners = new MonsterMovementListeners();

	public MonsterMovementController(ViewContext context, WorldContext world) {
		this.view = context;
    	this.world = world;
    }
	
	public void moveMonsters() {
    	long currentTime = System.currentTimeMillis();
    	
    	for (MonsterSpawnArea a : world.model.currentMap.spawnAreas) {
	    	for (Monster m : a.monsters) {
	    		if (m.nextActionTime <= currentTime) {
	    			moveMonster(m, a, currentTime);
	    		}
	    	}
    	}
    }
	
	public void attackWithAgressiveMonsters() {
    	for (MonsterSpawnArea a : world.model.currentMap.spawnAreas) {
	    	for (Monster m : a.monsters) {
	    		if (!m.isAgressive()) continue;
	    		if (!m.isAdjacentTo(world.model.player)) continue;
	    		
	    		int aggressionChanceBias = world.model.player.getSkillLevel(SkillCollection.SKILL_EVASION) * SkillCollection.PER_SKILLPOINT_INCREASE_EVASION_MONSTER_ATTACK_CHANCE_PERCENTAGE;
	    		if (Constants.roll100(Constants.MONSTER_AGGRESSION_CHANCE_PERCENT - aggressionChanceBias)) {
	    			monsterMovementListeners.onMonsterSteppedOnPlayer(m);
	    			view.combatController.monsterSteppedOnPlayer(m);
	    			return;
	    		}
	    	}
    	}
    }
	
	public static boolean monsterCanMoveTo(final PredefinedMap map, final CoordRect p) {
		if (!map.isWalkable(p)) return false;
		if (map.getMonsterAt(p) != null) return false;
		MapObject m = map.getEventObjectAt(p.topLeft);
		if (m != null) {
			if (m.type == MapObject.MAPEVENT_NEWMAP) return false;
		}
    	return true;
	}
    
	private void moveMonster(final Monster m, final MonsterSpawnArea area, long currentTime) {
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
    		// Monster is moving.
    		m.nextPosition.topLeft.set(
    				m.position.x + sgn(m.movementDestination.x - m.position.x)
					,m.position.y + sgn(m.movementDestination.y - m.position.y)
				);
    		
    		if (!monsterCanMoveTo(world.model.currentMap, m.nextPosition)) {
    			cancelCurrentMonsterMovement(m);
    			return;
    		}
			if (m.nextPosition.contains(world.model.player.position)) {
				if (!m.isAgressive()) {
					cancelCurrentMonsterMovement(m);
					return;
				}
				monsterMovementListeners.onMonsterSteppedOnPlayer(m);
				view.combatController.monsterSteppedOnPlayer(m);
			} else {
				CoordRect previousPosition = new CoordRect(m.position, m.rectPosition.size);
				m.position.set(m.nextPosition.topLeft);
				monsterMovementListeners.onMonsterMoved(m, previousPosition);
			}
    	}
	}
    
    private void cancelCurrentMonsterMovement(final Monster m) {
    	m.movementDestination = null;
		m.nextActionTime += getMillisecondsPerMove(m) * Constants.rollValue(Constants.monsterWaitTurns);
    }
    
    private static int getMillisecondsPerMove(Monster m) {
    	return Constants.MONSTER_MOVEMENT_TURN_DURATION_MS * m.getMoveCost() / m.getMaxAP();
    }

	private static int sgn(int i) {
		if (i <= -1) return -1;
		else if (i >= 1) return 1;
		return 0;
	}
}
