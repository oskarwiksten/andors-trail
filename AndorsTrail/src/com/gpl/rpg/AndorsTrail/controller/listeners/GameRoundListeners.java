package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.util.ListOfListeners;

public final class GameRoundListeners extends ListOfListeners<GameRoundListener> implements GameRoundListener {

	private final Function<GameRoundListener> onNewTick = new Function<GameRoundListener>() {
		@Override public void call(GameRoundListener listener) { listener.onNewTick(); }
	};

	private final Function<GameRoundListener> onNewRound = new Function<GameRoundListener>() {
		@Override public void call(GameRoundListener listener) { listener.onNewRound(); }
	};

	private final Function<GameRoundListener> onNewFullRound = new Function<GameRoundListener>() {
		@Override public void call(GameRoundListener listener) { listener.onNewFullRound(); }
	};

	@Override
	public void onNewTick() {
		callAllListeners(this.onNewTick);
	}

	@Override
	public void onNewRound() {
		callAllListeners(this.onNewRound);
	}

	@Override
	public void onNewFullRound() {
		callAllListeners(this.onNewFullRound);
	}
}
