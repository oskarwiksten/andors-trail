package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.listeners.ListOfListeners;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public class MonsterSpawnListeners extends ListOfListeners<MonsterSpawnListener> implements MonsterSpawnListener {

	private final Function1<MonsterSpawnListener, Monster> onMonsterSpawned = new Function1<MonsterSpawnListener, Monster>() {
		@Override public void call(MonsterSpawnListener listener, Monster monster) { listener.onMonsterSpawned(monster); }
	};
	
	private final Function2<MonsterSpawnListener, Monster, CoordRect> onMonsterRemoved = new Function2<MonsterSpawnListener, Monster, CoordRect>() {
		@Override public void call(MonsterSpawnListener listener, Monster monster, CoordRect previousPosition) { listener.onMonsterRemoved(monster, previousPosition); }
	};
	
	private final Function1<MonsterSpawnListener, Coord> onSplatterAdded = new Function1<MonsterSpawnListener, Coord>() {
		@Override public void call(MonsterSpawnListener listener, Coord p) { listener.onSplatterAdded(p); }
	};
	
	private final Function1<MonsterSpawnListener, Coord> onSplatterChanged = new Function1<MonsterSpawnListener, Coord>() {
		@Override public void call(MonsterSpawnListener listener, Coord p) { listener.onSplatterChanged(p); }
	};
	
	private final Function1<MonsterSpawnListener, Coord> onSplatterRemoved = new Function1<MonsterSpawnListener, Coord>() {
		@Override public void call(MonsterSpawnListener listener, Coord p) { listener.onSplatterRemoved(p); }
	};
	
	@Override
	public void onMonsterSpawned(Monster m) {
		callAllListeners(this.onMonsterSpawned, m);
	}

	@Override
	public void onMonsterRemoved(Monster m, CoordRect previousPosition) {
		callAllListeners(this.onMonsterRemoved, m, previousPosition);
	}

	@Override
	public void onSplatterAdded(Coord p) {
		callAllListeners(this.onSplatterAdded, p);
	}

	@Override
	public void onSplatterChanged(Coord p) {
		callAllListeners(this.onSplatterChanged, p);
	}

	@Override
	public void onSplatterRemoved(Coord p) {
		callAllListeners(this.onSplatterRemoved, p);
	}
}
