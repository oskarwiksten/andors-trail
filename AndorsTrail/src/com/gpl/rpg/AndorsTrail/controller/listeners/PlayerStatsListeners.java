package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.util.ListOfListeners;

public final class PlayerStatsListeners extends ListOfListeners<PlayerStatsListener> implements PlayerStatsListener {

	private final Function1<PlayerStatsListener, Player> onPlayerExperienceChanged = new Function1<PlayerStatsListener, Player>() {
		@Override public void call(PlayerStatsListener listener, Player p) { listener.onPlayerExperienceChanged(p); }
	};

	@Override
	public void onPlayerExperienceChanged(Player p) {
		callAllListeners(this.onPlayerExperienceChanged, p);
	}
}
