package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.util.ListOfListeners;

public final class CombatTurnListeners extends ListOfListeners<CombatTurnListener> implements CombatTurnListener {

	private final Function<CombatTurnListener> onCombatStarted = new Function<CombatTurnListener>() {
		@Override public void call(CombatTurnListener listener) { listener.onCombatStarted(); }
	};

	private final Function<CombatTurnListener> onCombatEnded = new Function<CombatTurnListener>() {
		@Override public void call(CombatTurnListener listener) { listener.onCombatEnded(); }
	};

	private final Function<CombatTurnListener> onNewPlayerTurn = new Function<CombatTurnListener>() {
		@Override public void call(CombatTurnListener listener) { listener.onNewPlayerTurn(); }
	};

	private final Function1<CombatTurnListener, Monster> onMonsterIsAttacking = new Function1<CombatTurnListener, Monster>() {
		@Override public void call(CombatTurnListener listener, Monster m) { listener.onMonsterIsAttacking(m); }
	};

	@Override
	public void onCombatStarted() {
		callAllListeners(this.onCombatStarted);
	}

	@Override
	public void onCombatEnded() {
		callAllListeners(this.onCombatEnded);
	}

	@Override
	public void onNewPlayerTurn() {
		callAllListeners(this.onNewPlayerTurn);
	}

	@Override
	public void onMonsterIsAttacking(Monster m) {
		callAllListeners(this.onMonsterIsAttacking, m);
	}
}
