package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class MonsterMovementController {
	private final ViewContext view;
    private final WorldContext world;
    private final ModelContainer model;

	public MonsterMovementController(ViewContext context) {
    	this.view = context;
    	this.world = context;
    	this.model = world.model;
    }
	
	public boolean moveMonsters() {
    	boolean hasMoved = false;
    	long currentTime = System.currentTimeMillis();
    	
    	for (MonsterSpawnArea a : model.currentMap.spawnAreas) {
	    	for (Monster m : a.monsters) {
	    		if (m.nextActionTime <= currentTime) {
	    			if (moveMonster(m, a, currentTime)) hasMoved = true;
	    		}
	    	}
    	}
    	
    	return hasMoved;
    }
    
    private boolean moveMonster(final Monster m, final MonsterSpawnArea area, long currentTime) {
		m.nextActionTime += m.millisecondsPerMove;
    	if (m.movementDestination == null) {
    		// Monster has waited and should start to move again.
    		m.movementDestination = new Coord(m.position);
    		if (ModelContainer.rnd.nextBoolean()) {
    			m.movementDestination.x = area.area.topLeft.x + ModelContainer.rnd.nextInt(area.area.size.width);
    		} else {
    			m.movementDestination.y = area.area.topLeft.y + ModelContainer.rnd.nextInt(area.area.size.height);
    		}
    	} else if (m.position.equals(m.movementDestination)) {
    		// Monster has been moving and arrived at the destination.
    		cancelCurrentMonsterMovement(m);
    	} else {
    		// Monster is moving.
    		CoordRect p = new CoordRect(
    				new Coord(
						m.position.x + sgn(m.movementDestination.x - m.position.x)
						,m.position.y + sgn(m.movementDestination.y - m.position.y)
					)
    				,m.monsterType.tileSize
				);
    		
    		if (!monsterCanMoveTo(p)) {
    			cancelCurrentMonsterMovement(m);
    			return false;
    		}
			if (p.contains(model.player.position)) {
				if (!m.monsterType.isAgressive()) {
					cancelCurrentMonsterMovement(m);
					return false;
				}
				view.combatController.monsterSteppedOnPlayer(m);
			} else {
				m.position.set(p.topLeft);
			}
			return true;
    	}
    	return false;
	}
    
    private void cancelCurrentMonsterMovement(final Monster m) {
    	m.movementDestination = null;
		m.nextActionTime += m.millisecondsPerMove * ModelContainer.rollValue(ModelContainer.monsterWaitTurns);
    }

	private boolean monsterCanMoveTo(final CoordRect p) {
		if (!model.currentMap.isWalkable(p)) return false;
		if (model.currentMap.getMonsterAt(p) != null) return false;
    	return true;
	}

	private static int sgn(int i) {
		if (i <= -1) return -1;
		else if (i >= 1) return 1;
		return 0;
	}
}
