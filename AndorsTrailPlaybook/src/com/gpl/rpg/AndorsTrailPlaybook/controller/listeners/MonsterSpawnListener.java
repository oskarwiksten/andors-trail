package com.gpl.rpg.AndorsTrailPlaybook.controller.listeners;

import com.gpl.rpg.AndorsTrailPlaybook.model.actor.Monster;
import com.gpl.rpg.AndorsTrailPlaybook.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrailPlaybook.util.Coord;
import com.gpl.rpg.AndorsTrailPlaybook.util.CoordRect;

public interface MonsterSpawnListener {
	void onMonsterSpawned(PredefinedMap map, Monster m);
	void onMonsterRemoved(PredefinedMap map, Monster m, CoordRect previousPosition);
	void onSplatterAdded(PredefinedMap map, Coord p);
	void onSplatterChanged(PredefinedMap map, Coord p);
	void onSplatterRemoved(PredefinedMap map, Coord p);
}
