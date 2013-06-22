package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.util.Coord;

public interface MapLayoutListener {
	void onLootBagCreated(PredefinedMap map, Coord p);
	void onLootBagRemoved(PredefinedMap map, Coord p);
	void onMapTilesChanged(PredefinedMap map, LayeredTileMap tileMap);
}
