package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.listeners.ListOfListeners;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public class MonsterMovementListeners extends ListOfListeners<MonsterMovementListener> implements MonsterMovementListener {

	private final Function1<MonsterMovementListener, Monster> onMonsterSteppedOnPlayer = new Function1<MonsterMovementListener, Monster>() {
		@Override public void call(MonsterMovementListener listener, Monster monster) { listener.onMonsterSteppedOnPlayer(monster); }
	};
	
	private final Function2<MonsterMovementListener, Monster, CoordRect> onMonsterMoved = new Function2<MonsterMovementListener, Monster, CoordRect>() {
		@Override public void call(MonsterMovementListener listener, Monster monster, CoordRect previousPosition) { listener.onMonsterMoved(monster, previousPosition); }
	};
	
	@Override
	public void onMonsterSteppedOnPlayer(Monster m) {
		callAllListeners(this.onMonsterSteppedOnPlayer, m);
	}
	
	@Override
	public void onMonsterMoved(Monster m, CoordRect previousPosition) {
		callAllListeners(this.onMonsterMoved, m, previousPosition);
	}
}
