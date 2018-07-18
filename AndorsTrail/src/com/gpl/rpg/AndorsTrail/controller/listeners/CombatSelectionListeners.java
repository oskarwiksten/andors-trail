package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.ListOfListeners;

public final class CombatSelectionListeners extends ListOfListeners<CombatSelectionListener> implements CombatSelectionListener {

	private final Function3<CombatSelectionListener, Monster, Coord, Coord> onMonsterSelected = new Function3<CombatSelectionListener, Monster, Coord, Coord>() {
		@Override public void call(CombatSelectionListener listener, Monster monster, Coord selectedPosition, Coord previousSelection) { listener.onMonsterSelected(monster, selectedPosition, previousSelection); }
	};

	private final Function2<CombatSelectionListener, Coord, Coord> onMovementDestinationSelected = new Function2<CombatSelectionListener, Coord, Coord>() {
		@Override public void call(CombatSelectionListener listener, Coord selectedPosition, Coord previousSelection) { listener.onMovementDestinationSelected(selectedPosition, previousSelection); }
	};

	private final Function1<CombatSelectionListener, Coord> onCombatSelectionCleared = new Function1<CombatSelectionListener, Coord>() {
		@Override public void call(CombatSelectionListener listener, Coord previousSelection) { listener.onCombatSelectionCleared(previousSelection); }
	};

	@Override
	public void onMonsterSelected(Monster m, Coord selectedPosition, Coord previousSelection) {
		callAllListeners(this.onMonsterSelected, m, selectedPosition, previousSelection);
	}

	@Override
	public void onMovementDestinationSelected(Coord selectedPosition, Coord previousSelection) {
		callAllListeners(this.onMovementDestinationSelected, selectedPosition, previousSelection);
	}

	@Override
	public void onCombatSelectionCleared(Coord previousSelection) {
		callAllListeners(this.onCombatSelectionCleared, previousSelection);
	}
}
