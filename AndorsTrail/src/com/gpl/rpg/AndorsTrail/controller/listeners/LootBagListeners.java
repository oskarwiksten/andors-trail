package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.listeners.ListOfListeners;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.util.Coord;

public final class LootBagListeners extends ListOfListeners<LootBagListener> implements LootBagListener {

	private final Function2<LootBagListener, PredefinedMap, Coord> onLootBagCreated = new Function2<LootBagListener, PredefinedMap, Coord>() {
		@Override public void call(LootBagListener listener, PredefinedMap map, Coord p) { listener.onLootBagCreated(map, p); }
	};
	
	private final Function2<LootBagListener, PredefinedMap, Coord> onLootBagRemoved = new Function2<LootBagListener, PredefinedMap, Coord>() {
		@Override public void call(LootBagListener listener, PredefinedMap map, Coord p) { listener.onLootBagRemoved(map, p); }
	};
	
	@Override
	public void onLootBagCreated(PredefinedMap map, Coord p) {
		callAllListeners(this.onLootBagCreated, map, p);
	}
	
	@Override
	public void onLootBagRemoved(PredefinedMap map, Coord p) {
		callAllListeners(this.onLootBagRemoved, map, p);
	}
}
