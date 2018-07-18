package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.ListOfListeners;

public final class PlayerMovementListeners extends ListOfListeners<PlayerMovementListener> implements PlayerMovementListener {

	private final Function2<PlayerMovementListener, Coord, Coord> onPlayerMoved = new Function2<PlayerMovementListener, Coord, Coord>() {
		@Override public void call(PlayerMovementListener listener, Coord newPosition, Coord previousPosition) { listener.onPlayerMoved(newPosition, previousPosition); }
	};

	private final Function2<PlayerMovementListener, PredefinedMap, Coord> onPlayerEnteredNewMap = new Function2<PlayerMovementListener, PredefinedMap, Coord>() {
		@Override public void call(PlayerMovementListener listener, PredefinedMap map, Coord p) { listener.onPlayerEnteredNewMap(map, p); }
	};

	@Override
	public void onPlayerMoved(Coord newPosition, Coord previousPosition) {
		callAllListeners(this.onPlayerMoved, newPosition, previousPosition);
	}

	@Override
	public void onPlayerEnteredNewMap(PredefinedMap map, Coord p) {
		callAllListeners(this.onPlayerEnteredNewMap, map, p);
	}
}
