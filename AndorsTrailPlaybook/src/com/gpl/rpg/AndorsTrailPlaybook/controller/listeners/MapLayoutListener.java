package com.gpl.rpg.AndorsTrailPlaybook.controller.listeners;

import com.gpl.rpg.AndorsTrailPlaybook.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrailPlaybook.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrailPlaybook.util.Coord;

public interface MapLayoutListener {
	void onLootBagCreated(PredefinedMap map, Coord p);
	void onLootBagRemoved(PredefinedMap map, Coord p);
	void onMapTilesChanged(PredefinedMap map, LayeredTileMap tileMap);
}
