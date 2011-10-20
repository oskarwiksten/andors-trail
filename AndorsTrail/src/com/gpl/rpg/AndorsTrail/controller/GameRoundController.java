package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.util.TimedMessageTask;

public final class GameRoundController implements TimedMessageTask.Callback {
    
    private final ViewContext view;
    private final WorldContext world;
    private final ModelContainer model;
    private final TimedMessageTask roundTimer;
	
	public GameRoundController(ViewContext context) {
    	this.view = context;
    	this.world = context;
    	this.model = world.model;
    	//this.id = ModelContainer.rnd.nextInt();
    	this.roundTimer = new TimedMessageTask(this, Constants.TICK_DELAY, true);
    }
	
    private int ticksUntilNextRound = Constants.TICKS_PER_ROUND;
    private int ticksUntilNextFullRound = Constants.TICKS_PER_FULLROUND;
    public boolean onTick(TimedMessageTask task) {
		//L.log(id + " : Controller::tick()");
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
    	//L.log("GameRoundController::resume() from " + from);
    	view.mainActivity.updateStatus();
		model.uiSelections.isMainActivityVisible = true;
		roundTimer.start();
    }
    public void pause() {
    	//L.log("GameRoundController::pause() from " + from);
    	roundTimer.stop();
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
    	
    	model.player.health.add(model.player.getSkillLevel(SkillCollection.SKILL_REGENERATION) * SkillCollection.PER_SKILLPOINT_INCREASE_REGENERATION, false);
    }
    
	private void onNewTick() {
		view.controller.moveAndSpawnMonsters();
		view.monsterMovementController.attackWithAgressiveMonsters();
	}
}
