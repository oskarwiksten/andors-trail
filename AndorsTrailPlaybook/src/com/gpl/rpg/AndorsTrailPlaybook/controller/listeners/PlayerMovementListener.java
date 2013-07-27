package com.gpl.rpg.AndorsTrailPlaybook.controller.listeners;

import com.gpl.rpg.AndorsTrailPlaybook.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrailPlaybook.util.Coord;

public interface PlayerMovementListener {
	void onPlayerMoved(Coord newPosition, Coord previousPosition);
	void onPlayerEnteredNewMap(PredefinedMap map, Coord p);
}
