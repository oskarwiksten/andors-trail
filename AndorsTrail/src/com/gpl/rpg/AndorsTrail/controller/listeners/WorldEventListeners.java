package com.gpl.rpg.AndorsTrail.controller.listeners;

import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.util.ListOfListeners;

import java.util.Collection;

public final class WorldEventListeners extends ListOfListeners<WorldEventListener> implements WorldEventListener {

	private final Function2<WorldEventListener, Monster, String> onPlayerStartedConversation = new Function2<WorldEventListener, Monster, String>() {
		@Override public void call(WorldEventListener listener, Monster m, String phraseID) { listener.onPlayerStartedConversation(m, phraseID); }
	};

	private final Function1<WorldEventListener, String> onScriptAreaStartedConversation = new Function1<WorldEventListener, String>() {
		@Override public void call(WorldEventListener listener, String phraseID) { listener.onScriptAreaStartedConversation(phraseID); }
	};

	private final Function1<WorldEventListener, Monster> onPlayerSteppedOnMonster = new Function1<WorldEventListener, Monster>() {
		@Override public void call(WorldEventListener listener, Monster m) { listener.onPlayerSteppedOnMonster(m); }
	};

	private final Function1<WorldEventListener, MapObject> onPlayerSteppedOnMapSignArea = new Function1<WorldEventListener, MapObject>() {
		@Override public void call(WorldEventListener listener, MapObject area) { listener.onPlayerSteppedOnMapSignArea(area); }
	};

	private final Function1<WorldEventListener, MapObject> onPlayerSteppedOnKeyArea = new Function1<WorldEventListener, MapObject>() {
		@Override public void call(WorldEventListener listener, MapObject area) { listener.onPlayerSteppedOnKeyArea(area); }
	};

	private final Function1<WorldEventListener, MapObject> onPlayerSteppedOnRestArea = new Function1<WorldEventListener, MapObject>() {
		@Override public void call(WorldEventListener listener, MapObject area) { listener.onPlayerSteppedOnRestArea(area); }
	};

	private final Function1<WorldEventListener, Loot> onPlayerSteppedOnGroundLoot = new Function1<WorldEventListener, Loot>() {
		@Override public void call(WorldEventListener listener, Loot loot) { listener.onPlayerSteppedOnGroundLoot(loot); }
	};

	private final Function1<WorldEventListener, Loot> onPlayerPickedUpGroundLoot = new Function1<WorldEventListener, Loot>() {
		@Override public void call(WorldEventListener listener, Loot loot) { listener.onPlayerPickedUpGroundLoot(loot); }
	};

	private final Function2<WorldEventListener, Collection<Loot>, Integer> onPlayerFoundMonsterLoot = new Function2<WorldEventListener, Collection<Loot>, Integer>() {
		@Override public void call(WorldEventListener listener, Collection<Loot> loot, Integer exp) { listener.onPlayerFoundMonsterLoot(loot, exp); }
	};

	private final Function2<WorldEventListener, Collection<Loot>, Integer> onPlayerPickedUpMonsterLoot = new Function2<WorldEventListener, Collection<Loot>, Integer>() {
		@Override public void call(WorldEventListener listener, Collection<Loot> loot, Integer exp) { listener.onPlayerPickedUpMonsterLoot(loot, exp); }
	};

	private final Function<WorldEventListener> onPlayerRested = new Function<WorldEventListener>() {
		@Override public void call(WorldEventListener listener) { listener.onPlayerRested(); }
	};

	private final Function1<WorldEventListener, Integer> onPlayerDied = new Function1<WorldEventListener, Integer>() {
		@Override public void call(WorldEventListener listener, Integer lostExp) { listener.onPlayerDied(lostExp); }
	};

	@Override
	public void onPlayerStartedConversation(Monster m, String phraseID) {
		callAllListeners(this.onPlayerStartedConversation, m, phraseID);
	}

	@Override
	public void onScriptAreaStartedConversation(String phraseID) {
		callAllListeners(this.onScriptAreaStartedConversation, phraseID);
	}

	@Override
	public void onPlayerSteppedOnMonster(Monster m) {
		callAllListeners(this.onPlayerSteppedOnMonster, m);
	}

	@Override
	public void onPlayerSteppedOnMapSignArea(MapObject area) {
		callAllListeners(this.onPlayerSteppedOnMapSignArea, area);
	}

	@Override
	public void onPlayerSteppedOnKeyArea(MapObject area) {
		callAllListeners(this.onPlayerSteppedOnKeyArea, area);
	}

	@Override
	public void onPlayerSteppedOnRestArea(MapObject area) {
		callAllListeners(this.onPlayerSteppedOnRestArea, area);
	}

	@Override
	public void onPlayerSteppedOnGroundLoot(Loot loot) {
		callAllListeners(this.onPlayerSteppedOnGroundLoot, loot);
	}

	@Override
	public void onPlayerPickedUpGroundLoot(Loot loot) {
		callAllListeners(this.onPlayerPickedUpGroundLoot, loot);
	}

	@Override
	public void onPlayerFoundMonsterLoot(Collection<Loot> loot, int exp) {
		callAllListeners(this.onPlayerFoundMonsterLoot, loot, exp);
	}

	@Override
	public void onPlayerPickedUpMonsterLoot(Collection<Loot> loot, int exp) {
		callAllListeners(this.onPlayerPickedUpMonsterLoot, loot, exp);
	}

	@Override
	public void onPlayerRested() {
		callAllListeners(this.onPlayerRested);
	}

	@Override
	public void onPlayerDied(int lostExp) {
		callAllListeners(this.onPlayerDied, lostExp);
	}
}
