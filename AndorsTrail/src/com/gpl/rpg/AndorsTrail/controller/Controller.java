package com.gpl.rpg.AndorsTrail.controller;

import android.app.Activity;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.activity.MainActivity;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.util.Coord;

public final class Controller {
    
    private final ViewContext view;
    private final WorldContext world;
    private final ModelContainer model;

	public Controller(ViewContext context) {
    	this.view = context;
    	this.world = context;
    	this.model = world.model;
    }
    
    public void handleMapEvent(MapObject o, Coord position) {
		switch (o.type) {
		case MapObject.MAPEVENT_SIGN:
			if (o.id == null || o.id.length() <= 0) return;
			Dialogs.showMapSign(view.mainActivity, view, o.id);
			break;
		case MapObject.MAPEVENT_NEWMAP:
			if (o.map == null || o.place == null) return;
			int offset_x = position.x - o.position.topLeft.x;
			int offset_y = position.y - o.position.topLeft.y;
			view.movementController.placePlayerAt(MapObject.MAPEVENT_NEWMAP, o.map, o.place, offset_x, offset_y);
			break;
		case MapObject.MAPEVENT_REST:
			Dialogs.showRest(view.mainActivity, view, o);
			break;
		}
	}

	public void steppedOnMonster(Monster m, Coord p) {
		if (m.isAgressive()) {
			view.combatController.setCombatSelection(m, p);
			if (!view.preferences.confirmAttack) {
				view.combatController.enterCombat(CombatController.BEGIN_TURN_PLAYER);
			} else {
				Dialogs.showMonsterEncounter(view.mainActivity, view, m);
			}
		} else {
			Dialogs.showConversation(view.mainActivity, view, m.getPhraseID(), m);
		}
	}

	public void handlePlayerDeath() {
		view.combatController.exitCombat(false);
		final Player player = model.player;
		int lostExp = player.levelExperience.current * Constants.PERCENT_EXP_LOST_WHEN_DIED / 100;
		lostExp -= lostExp * player.getSkillLevel(SkillCollection.SKILL_LOWER_EXPLOSS) * SkillCollection.PER_SKILLPOINT_INCREASE_EXPLOSS_PERCENT / 100;
		
		if (lostExp < 0) lostExp = 0;
		player.addExperience(-lostExp);
		model.statistics.addPlayerDeath(lostExp);
		final MainActivity act = view.mainActivity;
		MovementController.respawnPlayer(act.getResources(), world);
		playerRested(world, null);
		act.updateStatus();
		act.mainview.notifyMapChanged(world.model);
		act.message(act.getResources().getString(R.string.combat_hero_dies, lostExp));
	}
	
	public static void playerRested(final WorldContext world, MapObject area) {
		final Player player = world.model.player;
		ActorStatsController.removeAllTemporaryConditions(player);
		ActorStatsController.recalculatePlayerCombatTraits(player);
		player.setMaxAP();
		player.setMaxHP();
		if (area != null) {
			player.setSpawnPlace(world.model.currentMap.name, area.id);
		}
		for (PredefinedMap m : world.maps.predefinedMaps) {
			m.resetTemporaryData();
    	}
		world.model.currentMap.spawnAll(world);
	}

	public static void ui_playerRested(final Activity currentActivity, final ViewContext viewContext, MapObject area) {
		playerRested(viewContext, area);
		viewContext.mainActivity.updateStatus();
    	Dialogs.showRested(currentActivity, viewContext);
	}
	
	public boolean handleKeyArea(MapObject area) {
		if (view.model.player.hasExactQuestProgress(area.requireQuestProgress)) return true;
		Dialogs.showKeyArea(view.mainActivity, view, area.id);
		return false;
	}

	public static void resetMapsNotRecentlyVisited(final WorldContext world) {
		for (PredefinedMap m : world.maps.predefinedMaps) {
			if (m == world.model.currentMap) continue;
			if (m.isRecentlyVisited()) continue;
			if (m.hasResetTemporaryData()) continue;
			m.resetTemporaryData();
    	}
	}

	public boolean moveAndSpawnMonsters() {
    	boolean hasChanged = false;
    	if (view.monsterMovementController.moveMonsters()) hasChanged = true;
    	if (model.currentMap.maybeSpawn(world)) hasChanged = true;
    	return hasChanged;
	}
}
