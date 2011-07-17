package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.util.Coord;

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
    	boolean atLeastOneMonsterMoved = false;
    	long currentTime = System.currentTimeMillis();
    	
    	for (MonsterSpawnArea a : model.currentMap.spawnAreas) {
	    	for (Monster m : a.monsters) {
	    		if (m.nextActionTime <= currentTime) {
	    			if (moveMonster(m, a, currentTime)) atLeastOneMonsterMoved = true;
	    		}
	    	}
    	}
    	
    	return atLeastOneMonsterMoved;
    }
	
	public void attackWithAgressiveMonsters() {
    	for (MonsterSpawnArea a : model.currentMap.spawnAreas) {
	    	for (Monster m : a.monsters) {
	    		if (!m.isAgressive()) continue;
	    		if (!m.rectPosition.isAdjacentTo(model.player.position)) continue;
	    		
	    		int aggressionChanceBias = model.player.getSkillLevel(SkillCollection.SKILL_EVASION) * SkillCollection.PER_SKILLPOINT_INCREASE_EVASION_MONSTER_ATTACK_CHANCE_PERCENTAGE;
	    		if (Constants.roll100(Constants.MONSTER_AGGRESSION_CHANCE_PERCENT - aggressionChanceBias)) {
	    			view.combatController.monsterSteppedOnPlayer(m);
	    			return;
	    		}
	    	}
    	}
    }
    
	private boolean moveMonster(final Monster m, final MonsterSpawnArea area, long currentTime) {
    	m.nextActionTime += m.millisecondsPerMove;
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
    		
    		if (!model.currentMap.monsterCanMoveTo(m.nextPosition)) {
    			cancelCurrentMonsterMovement(m);
    			return false;
    		}
			if (m.nextPosition.contains(model.player.position)) {
				if (!m.isAgressive()) {
					cancelCurrentMonsterMovement(m);
					return false;
				}
				view.combatController.monsterSteppedOnPlayer(m);
			} else {
				m.position.set(m.nextPosition.topLeft);
			}
			return true;
    	}
    	return false;
	}
    
    private void cancelCurrentMonsterMovement(final Monster m) {
    	m.movementDestination = null;
		m.nextActionTime += m.millisecondsPerMove * Constants.rollValue(Constants.monsterWaitTurns);
    }

	private static int sgn(int i) {
		if (i <= -1) return -1;
		else if (i >= 1) return 1;
		return 0;
	}
}
