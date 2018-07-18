package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;

public interface ActorConditionListener {
	public void onActorConditionAdded(Actor actor, ActorCondition condition);
	public void onActorConditionRemoved(Actor actor, ActorCondition condition);
	public void onActorConditionDurationChanged(Actor actor, ActorCondition condition);
	public void onActorConditionMagnitudeChanged(Actor actor, ActorCondition condition);
	public void onActorConditionRoundEffectApplied(Actor actor, ActorCondition condition);
}
