package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.listeners.WorldEventListeners;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.util.Coord;

public final class Controller {
    
    private final ViewContext view;
    private final WorldContext world;
    public final WorldEventListeners worldEventListeners = new WorldEventListeners();

	public Controller(ViewContext context, WorldContext world) {
    	this.view = context;
    	this.world = world;
    }
    
    public void handleMapEvent(MapObject o, Coord position) {
		switch (o.type) {
		case MapObject.MAPEVENT_SIGN:
			if (o.id == null || o.id.length() <= 0) return;
			worldEventListeners.onPlayerSteppedOnMapSignArea(o);
			break;
		case MapObject.MAPEVENT_NEWMAP:
			if (o.map == null || o.place == null) return;
			int offset_x = position.x - o.position.topLeft.x;
			int offset_y = position.y - o.position.topLeft.y;
			view.movementController.placePlayerAsyncAt(MapObject.MAPEVENT_NEWMAP, o.map, o.place, offset_x, offset_y);
			break;
		case MapObject.MAPEVENT_REST:
			steppedOnRestArea(o);
			break;
		}
	}
    
    private void steppedOnRestArea(MapObject area) {
    	if (view.preferences.confirmRest) {
			worldEventListeners.onPlayerSteppedOnRestArea(area);
		} else {
			rest(area);
		}
    }

	public void steppedOnMonster(Monster m, Coord p) {
		if (m.isAgressive()) {
			view.combatController.setCombatSelection(m, p);
			if (view.preferences.confirmAttack) {
				worldEventListeners.onPlayerSteppedOnMonster(m);
			} else {
				view.combatController.enterCombat(CombatController.BEGIN_TURN_PLAYER);
			}
		} else {
			worldEventListeners.onPlayerStartedConversation(m, m.getPhraseID());
		}
	}

	public void handlePlayerDeath() {
		view.combatController.exitCombat(false);
		final Player player = world.model.player;
		int lostExp = player.getCurrentLevelExperience() * Constants.PERCENT_EXP_LOST_WHEN_DIED / 100;
		lostExp -= lostExp * player.getSkillLevel(SkillCollection.SKILL_LOWER_EXPLOSS) * SkillCollection.PER_SKILLPOINT_INCREASE_EXPLOSS_PERCENT / 100;
		
		if (lostExp < 0) lostExp = 0;
		view.actorStatsController.addExperience(-lostExp);
		world.model.statistics.addPlayerDeath(lostExp);
		view.movementController.respawnPlayerAsync();
		lotsOfTimePassed();
		worldEventListeners.onPlayerDied(lostExp);
	}
	
	public void lotsOfTimePassed() {
		final Player player = world.model.player;
		view.actorStatsController.removeAllTemporaryConditions(player);
		view.actorStatsController.recalculatePlayerStats(player);
		view.actorStatsController.setActorMaxAP(player);
		view.actorStatsController.setActorMaxHealth(player);
		for (PredefinedMap m : world.maps.predefinedMaps) {
			m.resetTemporaryData();
    	}
		view.monsterSpawnController.spawnAll(world.model.currentMap);
	}

	public void rest(MapObject area) {
		lotsOfTimePassed();
		world.model.player.setSpawnPlace(world.model.currentMap.name, area.id);
		worldEventListeners.onPlayerRested();
	}
	
	public boolean canEnterKeyArea(MapObject area) {
		if (world.model.player.hasExactQuestProgress(area.requireQuestProgress)) return true;
		worldEventListeners.onPlayerSteppedOnKeyArea(area);
		return false;
	}

	public void resetMapsNotRecentlyVisited() {
		for (PredefinedMap m : world.maps.predefinedMaps) {
			if (m == world.model.currentMap) continue;
			if (m.isRecentlyVisited()) continue;
			if (m.hasResetTemporaryData()) continue;
			m.resetTemporaryData();
    	}
	}
}
