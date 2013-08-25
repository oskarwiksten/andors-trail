package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.ListOfListeners;

public final class MapLayoutListeners extends ListOfListeners<MapLayoutListener> implements MapLayoutListener {

	private final Function2<MapLayoutListener, PredefinedMap, Coord> onLootBagCreated = new Function2<MapLayoutListener, PredefinedMap, Coord>() {
		@Override public void call(MapLayoutListener listener, PredefinedMap map, Coord p) { listener.onLootBagCreated(map, p); }
	};

	private final Function2<MapLayoutListener, PredefinedMap, Coord> onLootBagRemoved = new Function2<MapLayoutListener, PredefinedMap, Coord>() {
		@Override public void call(MapLayoutListener listener, PredefinedMap map, Coord p) { listener.onLootBagRemoved(map, p); }
	};

	private final Function2<MapLayoutListener, PredefinedMap, LayeredTileMap> onMapTilesChanged = new Function2<MapLayoutListener, PredefinedMap, LayeredTileMap>() {
		@Override public void call(MapLayoutListener listener, PredefinedMap map, LayeredTileMap tileMap) { listener.onMapTilesChanged(map, tileMap); }
	};

	@Override
	public void onLootBagCreated(PredefinedMap map, Coord p) {
		callAllListeners(this.onLootBagCreated, map, p);
	}

	@Override
	public void onLootBagRemoved(PredefinedMap map, Coord p) {
		callAllListeners(this.onLootBagRemoved, map, p);
	}

	@Override
	public void onMapTilesChanged(PredefinedMap map, LayeredTileMap tileMap) {
		callAllListeners(this.onMapTilesChanged, map, tileMap);
	}
}
