package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.actor.Monster;

public interface CombatTurnListener {
	void onCombatStarted();
	void onCombatEnded();
	void onNewPlayerTurn();
	void onMonsterIsAttacking(Monster m);
}
