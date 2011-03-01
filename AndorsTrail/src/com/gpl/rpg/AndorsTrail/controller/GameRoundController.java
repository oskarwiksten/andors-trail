package com.gpl.rpg.AndorsTrail.controller;

import android.os.Handler;
import android.os.Message;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;

public final class GameRoundController {
    
    private final ViewContext view;
    private final WorldContext world;
    private final ModelContainer model;

	private boolean hasQueuedTick = false;
	//private final int id;

	public GameRoundController(ViewContext context) {
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
        	GameRoundController.this.tick();
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
    
    private int ticksUntilNextRound = Constants.TICKS_PER_ROUND;
    private int ticksUntilNextFullRound = Constants.TICKS_PER_FULLROUND;
    private void tick() {
		//L.log(id + " : Controller::tick()");
    	if (!model.uiSelections.isMainActivityVisible) return;
    	if (model.uiSelections.isInCombat) return;
    	
    	onNewTick();
    	
    	--ticksUntilNextRound;
    	if (ticksUntilNextRound <= 0) {
    		onNewRound();
    		ticksUntilNextRound = Constants.TICKS_PER_ROUND;
    	}
    	
    	--ticksUntilNextFullRound;
    	if (ticksUntilNextFullRound <= 0) {
    		onNewFullRound();
    		ticksUntilNextFullRound = Constants.TICKS_PER_FULLROUND;
    	}
    	
    	queueAnotherTick();
    }
    
    public void resume() {
    	//L.log(id + " : Controller::resume()");
		view.mainActivity.updateStatus();
		model.uiSelections.isMainActivityVisible = true;
    	queueAnotherTick();
    }
    public void pause() {
    	//L.log(id + " : Controller::pause()");
    	hasQueuedTick = false;
    	model.uiSelections.isMainActivityVisible = false;
    }
	
    private void onNewFullRound() {
    	view.controller.resetMaps();
    	view.actorStatsController.applyConditionsToMonsters(model.currentMap, true);
    	view.actorStatsController.applyConditionsToPlayer(model.player, true);
    }
    
    private void onNewRound() {
    	view.actorStatsController.applyConditionsToMonsters(model.currentMap, false);
    	view.actorStatsController.applyConditionsToPlayer(model.player, false);
    }
    
	private void onNewTick() {
		view.controller.moveAndSpawnMonsters();
		view.monsterMovementController.attackWithAgressiveMonsters();
	}
}
