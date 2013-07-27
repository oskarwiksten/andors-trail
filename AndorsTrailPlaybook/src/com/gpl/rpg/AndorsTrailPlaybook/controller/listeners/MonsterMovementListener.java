package com.gpl.rpg.AndorsTrailPlaybook.controller.listeners;

import com.gpl.rpg.AndorsTrailPlaybook.model.actor.Monster;
import com.gpl.rpg.AndorsTrailPlaybook.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrailPlaybook.util.CoordRect;

public interface MonsterMovementListener {
	void onMonsterSteppedOnPlayer(Monster m);
	void onMonsterMoved(PredefinedMap map, Monster m, CoordRect previousPosition);
}
