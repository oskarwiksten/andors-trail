package com.gpl.rpg.AndorsTrail.controller;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.activity.MainActivity;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.map.LayeredWorldMap;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.view.MainView;

public final class Controller {
    
    private final ViewContext view;
    private final WorldContext world;
    private final ModelContainer model;

	private boolean hasQueuedTick = false;
	//private final int id;

	public Controller(ViewContext context) {
    	this.view = context;
    	this.world = context;
    	this.model = world.model;
    	//this.id = ModelContainer.rnd.nextInt();
    }
    
	private final RefreshHandler mTickHandler = new RefreshHandler();
	private class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
        	if (!hasQueuedTick) return;
        	hasQueuedTick = false;
            Controller.this.tick();
        }

        public void sleep(long delayMillis) {
        	this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };

    public void queueAnotherTick() {
    	if (hasQueuedTick) return;
    	hasQueuedTick = true;
    	mTickHandler.sleep(Constants.TICK_DELAY);
    }
    
    private int tickCount = 0;
	private void tick() {
		//L.log(id + " : Controller::tick()");
    	if (!model.uiSelections.isMainActivityVisible) return;
    	if (model.uiSelections.isInCombat) return;
    	
    	++tickCount;
    	if (tickCount >= 20) {
    		tickCount = 0;
    		for (LayeredWorldMap m : world.maps.predefinedMaps) {
	    		if (m == model.currentMap) continue;
    			m.resetIfNotRecentlyVisited();
	    	}
	    }
    		
    	boolean hasChanged = false;
    	if (view.monsterMovementController.moveMonsters()) hasChanged = true;
    	if (model.currentMap.maybeSpawn(world)) hasChanged = true;
    	
    	if (hasChanged) view.mainActivity.redrawAll(MainView.REDRAW_ALL_MONSTER_MOVED); //TODO: should only redraw spawned tiles
    	
    	view.monsterMovementController.attackWithAgressiveMonsters();
    	
    	queueAnotherTick();
    }
    
    public void resume() {
    	//L.log(id + " : Controller::resume()");
		view.mainActivity.statusview.update();
		model.uiSelections.isMainActivityVisible = true;
    	queueAnotherTick();
    }
    public void pause() {
    	//L.log(id + " : Controller::pause()");
    	hasQueuedTick = false;
    	model.uiSelections.isMainActivityVisible = false;
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
			view.movementController.placePlayerAt(o.map, o.place, offset_x, offset_y);
			break;
		case MapObject.MAPEVENT_REST:
			Dialogs.showRest(view.mainActivity, view);
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
			Dialogs.showConversation(view.mainActivity, view, m.monsterType.phraseID, m);
		}
	}

	public void handlePlayerDeath() {
		view.effectController.waitForCurrentEffect();		
		final Player player = model.player;
		int lostExp = player.levelExperience.current / (100 / Constants.PERCENT_EXP_LOST_WHEN_DIED);
		if (lostExp < 0) lostExp = 0; // Shouldn't happen, but just to be sure.
		player.addExperience(-lostExp);
		model.statistics.addPlayerDeath(lostExp);
		playerRested(world);
		MovementController.respawnPlayer(world);
		final MainActivity act = view.mainActivity;
		act.statusview.update();
		act.mainview.notifyMapChanged();
		act.message(act.getResources().getString(R.string.combat_hero_dies, lostExp));
	}
	
	public static void playerRested(final WorldContext world) {
		final Player player = world.model.player;
		player.setMaxAP();
		player.setMaxHP();
		for (LayeredWorldMap m : world.maps.predefinedMaps) {
        	if (m.visited) m.spawnAll(world);
        }
	}

	public static void ui_playerRested(final Activity currentActivity, final ViewContext viewContext) {
		playerRested(viewContext);
		viewContext.mainActivity.statusview.update();
    	Dialogs.showRested(currentActivity, viewContext);
	}
	
	public boolean handleKeyArea(MapObject area) {
		if (view.model.player.hasExactQuestProgress(area.requireQuestProgress)) return true;
		Dialogs.showKeyArea(view.mainActivity, view, area.id);
		return false;
	}
}
