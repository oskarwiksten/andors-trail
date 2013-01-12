package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.util.Coord;

public interface LootBagListener {
	void onLootBagCreated(Coord p);
	void onLootBagRemoved(Coord p);
}
