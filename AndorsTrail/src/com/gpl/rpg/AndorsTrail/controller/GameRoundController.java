package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.listeners.GameRoundListeners;
import com.gpl.rpg.AndorsTrail.util.TimedMessageTask;

public final class GameRoundController implements TimedMessageTask.Callback {
    
    private final ViewContext view;
    private final WorldContext world;
    private final TimedMessageTask roundTimer;
	public final GameRoundListeners gameRoundListeners = new GameRoundListeners();
	
	public GameRoundController(ViewContext context, WorldContext world) {
    	this.view = context;
    	this.world = world;
    	this.roundTimer = new TimedMessageTask(this, Constants.TICK_DELAY, true);
    }
	
    private int ticksUntilNextRound = Constants.TICKS_PER_ROUND;
    private int ticksUntilNextFullRound = Constants.TICKS_PER_FULLROUND;
    public boolean onTick(TimedMessageTask task) {
		if (!world.model.uiSelections.isMainActivityVisible) return false;
    	if (world.model.uiSelections.isInCombat) return false;
    	
    	onNewTick();
    	
    	--ticksUntilNextRound;
    	if (ticksUntilNextRound <= 0) {
    		onNewRound();
    		restartWaitForNextRound();
    	}
    	
    	--ticksUntilNextFullRound;
    	if (ticksUntilNextFullRound <= 0) {
    		onNewFullRound();
    		restartWaitForNextFullRound();
    	}
    	
    	return true;
    }
    
    public void resume() {
    	world.model.uiSelections.isMainActivityVisible = true;
		restartWaitForNextRound();
		restartWaitForNextFullRound();
		roundTimer.start();
    }
    
    private void restartWaitForNextFullRound() {
    	ticksUntilNextFullRound = Constants.TICKS_PER_FULLROUND;
	}

	private void restartWaitForNextRound() {
    	ticksUntilNextRound = Constants.TICKS_PER_ROUND;
	}

	public void pause() {
    	roundTimer.stop();
    	world.model.uiSelections.isMainActivityVisible = false;
    }
	
    public void onNewFullRound() {
		Controller.resetMapsNotRecentlyVisited(world);
		view.actorStatsController.applyConditionsToMonsters(world.model.currentMap, true);
    	view.actorStatsController.applyConditionsToPlayer(world.model.player, true);
		gameRoundListeners.onNewFullRound();
    }
    
    public void onNewRound() {
		onNewMonsterRound();
    	onNewPlayerRound();
		gameRoundListeners.onNewRound();
    }
    public void onNewPlayerRound() {
    	view.actorStatsController.applyConditionsToPlayer(world.model.player, false);
    	view.actorStatsController.applySkillEffectsForNewRound(world.model.player, world.model.currentMap);
    }
    public void onNewMonsterRound() {
    	view.actorStatsController.applyConditionsToMonsters(world.model.currentMap, false);
    }
    
	private void onNewTick() {
    	view.monsterMovementController.moveMonsters();
    	view.monsterSpawnController.maybeSpawn(world.model.currentMap);
		view.monsterMovementController.attackWithAgressiveMonsters();
		view.effectController.updateSplatters(world.model.currentMap);
		gameRoundListeners.onNewTick();
	}
}
