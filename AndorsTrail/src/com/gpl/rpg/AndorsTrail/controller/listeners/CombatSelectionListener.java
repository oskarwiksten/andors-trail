package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.util.Coord;

public interface CombatSelectionListener {
	void onMonsterSelected(Monster m, Coord selectedPosition, Coord previousSelection);
	void onMovementDestinationSelected(Coord selectedPosition, Coord previousSelection);
	void onCombatSelectionCleared(Coord previousSelection);
}
