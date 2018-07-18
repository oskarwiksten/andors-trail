package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.util.ListOfListeners;

public final class ActorStatsListeners extends ListOfListeners<ActorStatsListener> implements ActorStatsListener {

	private final Function1<ActorStatsListener, Actor> onActorHealthChanged = new Function1<ActorStatsListener, Actor>() {
		@Override public void call(ActorStatsListener listener, Actor actor) { listener.onActorHealthChanged(actor); }
	};

	private final Function1<ActorStatsListener, Actor> onActorAPChanged = new Function1<ActorStatsListener, Actor>() {
		@Override public void call(ActorStatsListener listener, Actor actor) { listener.onActorAPChanged(actor); }
	};

	private final Function2<ActorStatsListener, Actor, Integer> onActorAttackCostChanged = new Function2<ActorStatsListener, Actor, Integer>() {
		@Override public void call(ActorStatsListener listener, Actor actor, Integer newAttackCost) { listener.onActorAttackCostChanged(actor, newAttackCost); }
	};

	private final Function2<ActorStatsListener, Actor, Integer> onActorMoveCostChanged = new Function2<ActorStatsListener, Actor, Integer>() {
		@Override public void call(ActorStatsListener listener, Actor actor, Integer newMoveCost) { listener.onActorMoveCostChanged(actor, newMoveCost); }
	};

	@Override
	public void onActorHealthChanged(Actor actor) {
		callAllListeners(this.onActorHealthChanged, actor);
	}

	@Override
	public void onActorAPChanged(Actor actor) {
		callAllListeners(this.onActorAPChanged, actor);
	}

	@Override
	public void onActorAttackCostChanged(Actor actor, int newAttackCost) {
		callAllListeners(this.onActorAttackCostChanged, actor, newAttackCost);
	}

	@Override
	public void onActorMoveCostChanged(Actor actor, int newMoveCost) {
		callAllListeners(this.onActorMoveCostChanged, actor, newMoveCost);
	}
}
