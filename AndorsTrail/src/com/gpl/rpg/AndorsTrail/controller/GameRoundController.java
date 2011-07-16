package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Skills;
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
    public void onTick(TimedMessageTask task) {
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
    	
    	roundTimer.queueAnotherTick();
    }
    
    public void resume() {
    	//L.log(id + " : Controller::resume()");
		view.mainActivity.updateStatus();
		model.uiSelections.isMainActivityVisible = true;
		roundTimer.start();
    }
    public void pause() {
    	//L.log(id + " : Controller::pause()");
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
    	
    	model.player.health.add(model.player.getSkillLevel(Skills.SKILL_REGENERATION) * Skills.PER_SKILLPOINT_INCREASE_REGENERATION, false);
    }
    
	private void onNewTick() {
		view.controller.moveAndSpawnMonsters();
		view.monsterMovementController.attackWithAgressiveMonsters();
	}
}
