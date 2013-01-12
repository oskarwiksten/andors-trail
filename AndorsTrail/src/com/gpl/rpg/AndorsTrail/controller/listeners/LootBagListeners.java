package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.listeners.ListOfListeners;
import com.gpl.rpg.AndorsTrail.util.Coord;

public class LootBagListeners extends ListOfListeners<LootBagListener> implements LootBagListener {

	private final Function1<LootBagListener, Coord> onLootBagCreated = new Function1<LootBagListener, Coord>() {
		@Override public void call(LootBagListener listener, Coord p) { listener.onLootBagCreated(p); }
	};
	
	private final Function1<LootBagListener, Coord> onLootBagRemoved = new Function1<LootBagListener, Coord>() {
		@Override public void call(LootBagListener listener, Coord p) { listener.onLootBagRemoved(p); }
	};
	
	@Override
	public void onLootBagCreated(Coord p) {
		callAllListeners(this.onLootBagCreated, p);
	}
	
	@Override
	public void onLootBagRemoved(Coord p) {
		callAllListeners(this.onLootBagRemoved, p);
	}
}
