package com.gpl.rpg.AndorsTrail.controller;

import android.content.Context;
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
import com.gpl.rpg.AndorsTrail.model.map.KeyArea;
import com.gpl.rpg.AndorsTrail.model.map.LayeredWorldMap;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.view.MainView;

public final class Controller {
	private static final int PERCENT_EXP_LOST_WHEN_DIED = 30;
	public static final int LEVELUP_EFFECT_HEALTH = 5;
	public static final int LEVELUP_EFFECT_ATK_CH = 5;
	public static final int LEVELUP_EFFECT_ATK_DMG = 1;
	public static final int LEVELUP_EFFECT_DEF_CH = 3;
    
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
    	mTickHandler.sleep(ModelContainer.tickDelay);
    }
    
	private void tick() {
		//L.log(id + " : Controller::tick()");
    	if (!model.uiSelections.isTicking) return;
    	if (model.uiSelections.isInCombat) return;
    	
    	boolean hasChanged = false;
    	if (view.monsterMovementController.moveMonsters()) hasChanged = true;
    	if (model.currentMap.maybeSpawn(world)) hasChanged = true;
    	
    	if (hasChanged) view.mainActivity.redrawAll(MainView.REDRAW_ALL_MONSTER_MOVED); //TODO: should only redraw spawned tiles
    	
    	queueAnotherTick();
    }
    
    public void resume() {
    	//L.log(id + " : Controller::resume()");
    	model.uiSelections.isTicking = true;
    	queueAnotherTick();
    }
    public void pause() {
    	//L.log(id + " : Controller::pause()");
    	hasQueuedTick = false;
    	model.uiSelections.isTicking = false;
    }
    
    public void handleMapEvent(MapObject o) {
		switch (o.type) {
		case MapObject.MAPEVENT_SIGN:
			if (o.text == null) return;
			Dialogs.showMapSign(view.mainActivity, view, o.title, o.text);
			break;
		case MapObject.MAPEVENT_NEWMAP:
			if (o.map == null || o.place == null) return;
			view.movementController.placePlayerAt(o.map, o.place);
			break;
		case MapObject.MAPEVENT_REST:
			Dialogs.showRest(view.mainActivity, view);
			break;
		}
	}

	public void steppedOnMonster(Monster m, Coord p) {
		if (m.monsterType.isAgressive()) {
			view.combatController.setCombatSelection(m, p);
			Dialogs.showMonsterEncounter(view.mainActivity, m);
		} else {
			Dialogs.showConversation(view.mainActivity, m.monsterType.phraseID, m);
		}
	}

	public void handlePlayerDeath() {
		view.effectController.waitForCurrentEffect();		
		final Player player = model.player;
		int lostExp = player.levelExperience.current / (100 / PERCENT_EXP_LOST_WHEN_DIED);
		player.addExperience(-lostExp);
		model.statistics.addPlayerDeath(lostExp);
		playerRested(world, false);
		MovementController.respawnPlayer(world);
		final MainActivity act = view.mainActivity;
		act.mainview.notifyMapChanged();
		act.message(act.getResources().getString(R.string.combat_hero_dies, lostExp));
		act.statusview.update();
	}
	
	public static void playerRested(final WorldContext world, boolean respawnUniqueMonsters) {
		final Player player = world.model.player;
		player.setMaxAP();
		player.setMaxHP();
		for (LayeredWorldMap m : world.maps.predefinedMaps) {
        	m.spawnAll(world, respawnUniqueMonsters);
        }
	}
	
	public boolean handleKeyArea(KeyArea area) {
		if (view.model.player.hasKey(area.requiredKey)) return true;
		final Context androidContext = view.mainActivity;

		String message = area.message;
		if (message == null || message.length() == 0) {
			message = androidContext.getResources().getString(R.string.key_required);
		}
		view.mainActivity.message(message);
		return false;
	}
}
