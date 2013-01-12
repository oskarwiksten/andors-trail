package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public interface MonsterSpawnListener {
	void onMonsterSpawned(Monster m);
	void onMonsterRemoved(Monster m, CoordRect previousPosition);
	void onSplatterAdded(Coord p);
	void onSplatterChanged(Coord p);
	void onSplatterRemoved(Coord p);
}
