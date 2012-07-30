package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.util.TimedMessageTask;
import com.gpl.rpg.AndorsTrail.view.MainView;

public final class GameRoundController implements TimedMessageTask.Callback {
    
    private final ViewContext view;
    private final WorldContext world;
    private final ModelContainer model;
    private final TimedMessageTask roundTimer;
	
	public GameRoundController(ViewContext context) {
    	this.view = context;
    	this.world = context;
    	this.model = world.model;
    	this.roundTimer = new TimedMessageTask(this, Constants.TICK_DELAY, true);
    }
	
    private int ticksUntilNextRound = Constants.TICKS_PER_ROUND;
    private int ticksUntilNextFullRound = Constants.TICKS_PER_FULLROUND;
    public boolean onTick(TimedMessageTask task) {
		if (!model.uiSelections.isMainActivityVisible) return false;
    	if (model.uiSelections.isInCombat) return false;
    	
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
    	
    	return true;
    }
    
    public void resume() {
    	view.mainActivity.updateStatus();
		model.uiSelections.isMainActivityVisible = true;
		roundTimer.start();
    }
    public void pause() {
    	roundTimer.stop();
    	model.uiSelections.isMainActivityVisible = false;
    }
	
    private void onNewFullRound() {
    	view.controller.resetMaps();
    	view.actorStatsController.applyConditionsToMonsters(model.currentMap, true);
    	view.actorStatsController.applyConditionsToPlayer(model.player, true);
    }
    
    public void onNewRound() {
    	onNewMonsterRound();
    	onNewPlayerRound();
    }
    public void onNewPlayerRound() {
    	view.actorStatsController.applyConditionsToPlayer(model.player, false);
    	view.actorStatsController.applySkillEffectsForNewRound(model.player, model.currentMap);
    }
    public void onNewMonsterRound() {
    	view.actorStatsController.applyConditionsToMonsters(model.currentMap, false);
    }
    
	private void onNewTick() {
		boolean hasChanged = false;
		if (view.controller.moveAndSpawnMonsters()) hasChanged = true;
		view.monsterMovementController.attackWithAgressiveMonsters();
		if (VisualEffectController.updateSplatters(model.currentMap)) hasChanged = true;
		
    	if (hasChanged) view.mainActivity.redrawAll(MainView.REDRAW_ALL_MONSTER_MOVED); //TODO: should only redraw spawned tiles
	}
}
