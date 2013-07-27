package com.gpl.rpg.AndorsTrailPlaybook.controller;

import com.gpl.rpg.AndorsTrailPlaybook.context.ControllerContext;
import com.gpl.rpg.AndorsTrailPlaybook.context.WorldContext;
import com.gpl.rpg.AndorsTrailPlaybook.controller.listeners.GameRoundListeners;
import com.gpl.rpg.AndorsTrailPlaybook.util.TimedMessageTask;

public final class GameRoundController implements TimedMessageTask.Callback {

	private final ControllerContext controllers;
	private final WorldContext world;
	private final TimedMessageTask roundTimer;
	public final GameRoundListeners gameRoundListeners = new GameRoundListeners();

	public GameRoundController(ControllerContext controllers, WorldContext world) {
		this.controllers = controllers;
		this.world = world;
		this.roundTimer = new TimedMessageTask(this, Constants.TICK_DELAY, true);
	}

	private int ticksUntilNextRound = Constants.TICKS_PER_ROUND;
	private int ticksUntilNextFullRound = Constants.TICKS_PER_FULLROUND;

	@Override
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

	public void resetRoundTimers() {
		restartWaitForNextRound();
		restartWaitForNextFullRound();
	}

	public void resume() {
		world.model.uiSelections.isMainActivityVisible = true;
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
		controllers.mapController.resetMapsNotRecentlyVisited();
		controllers.actorStatsController.applyConditionsToMonsters(world.model.currentMap, true);
		controllers.actorStatsController.applyConditionsToPlayer(world.model.player, true);
		gameRoundListeners.onNewFullRound();
	}

	private void onNewRound() {
		onNewMonsterRound();
		onNewPlayerRound();
		gameRoundListeners.onNewRound();
	}
	public void onNewPlayerRound() {
		controllers.actorStatsController.applyConditionsToPlayer(world.model.player, false);
		controllers.actorStatsController.applySkillEffectsForNewRound(world.model.player, world.model.currentMap);
	}
	public void onNewMonsterRound() {
		controllers.actorStatsController.applyConditionsToMonsters(world.model.currentMap, false);
	}

	private void onNewTick() {
		controllers.monsterMovementController.moveMonsters();
		controllers.monsterSpawnController.maybeSpawn(world.model.currentMap, world.model.currentTileMap);
		controllers.monsterMovementController.attackWithAgressiveMonsters();
		controllers.effectController.updateSplatters(world.model.currentMap);
		gameRoundListeners.onNewTick();
	}
}
