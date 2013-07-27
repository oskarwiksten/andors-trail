package com.gpl.rpg.AndorsTrailPlaybook.controller.listeners;

import com.gpl.rpg.AndorsTrailPlaybook.model.actor.Monster;

public interface CombatTurnListener {
	void onCombatStarted();
	void onCombatEnded();
	void onNewPlayerTurn();
	void onMonsterIsAttacking(Monster m);
}
