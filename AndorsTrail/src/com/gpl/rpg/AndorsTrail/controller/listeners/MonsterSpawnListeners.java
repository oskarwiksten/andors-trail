package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.ListOfListeners;

public final class MonsterSpawnListeners extends ListOfListeners<MonsterSpawnListener> implements MonsterSpawnListener {

	private final Function2<MonsterSpawnListener, PredefinedMap, Monster> onMonsterSpawned = new Function2<MonsterSpawnListener, PredefinedMap, Monster>() {
		@Override public void call(MonsterSpawnListener listener, PredefinedMap map, Monster monster) { listener.onMonsterSpawned(map, monster); }
	};

	private final Function3<MonsterSpawnListener, PredefinedMap, Monster, CoordRect> onMonsterRemoved = new Function3<MonsterSpawnListener, PredefinedMap, Monster, CoordRect>() {
		@Override public void call(MonsterSpawnListener listener, PredefinedMap map, Monster monster, CoordRect previousPosition) { listener.onMonsterRemoved(map, monster, previousPosition); }
	};

	private final Function2<MonsterSpawnListener, PredefinedMap, Coord> onSplatterAdded = new Function2<MonsterSpawnListener, PredefinedMap, Coord>() {
		@Override public void call(MonsterSpawnListener listener, PredefinedMap map, Coord p) { listener.onSplatterAdded(map, p); }
	};

	private final Function2<MonsterSpawnListener, PredefinedMap, Coord> onSplatterChanged = new Function2<MonsterSpawnListener, PredefinedMap, Coord>() {
		@Override public void call(MonsterSpawnListener listener, PredefinedMap map, Coord p) { listener.onSplatterChanged(map, p); }
	};

	private final Function2<MonsterSpawnListener, PredefinedMap, Coord> onSplatterRemoved = new Function2<MonsterSpawnListener, PredefinedMap, Coord>() {
		@Override public void call(MonsterSpawnListener listener, PredefinedMap map, Coord p) { listener.onSplatterRemoved(map, p); }
	};

	@Override
	public void onMonsterSpawned(PredefinedMap map, Monster m) {
		callAllListeners(this.onMonsterSpawned, map, m);
	}

	@Override
	public void onMonsterRemoved(PredefinedMap map, Monster m, CoordRect previousPosition) {
		callAllListeners(this.onMonsterRemoved, map, m, previousPosition);
	}

	@Override
	public void onSplatterAdded(PredefinedMap map, Coord p) {
		callAllListeners(this.onSplatterAdded, map, p);
	}

	@Override
	public void onSplatterChanged(PredefinedMap map, Coord p) {
		callAllListeners(this.onSplatterChanged, map, p);
	}

	@Override
	public void onSplatterRemoved(PredefinedMap map, Coord p) {
		callAllListeners(this.onSplatterRemoved, map, p);
	}
}
