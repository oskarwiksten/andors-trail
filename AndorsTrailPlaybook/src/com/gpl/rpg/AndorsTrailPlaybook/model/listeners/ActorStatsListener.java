package com.gpl.rpg.AndorsTrailPlaybook.model.listeners;

import com.gpl.rpg.AndorsTrailPlaybook.model.actor.Actor;

public interface ActorStatsListener {
	void onActorHealthChanged(Actor actor);
	void onActorAPChanged(Actor actor);
	void onActorAttackCostChanged(Actor actor, int newAttackCost);
	void onActorMoveCostChanged(Actor actor, int newMoveCost);
}
