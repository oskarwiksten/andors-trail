package com.gpl.rpg.AndorsTrail.controller;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;

import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.VisualEffectCollection;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.VisualEffectController.VisualEffectCompletedCallback;
import com.gpl.rpg.AndorsTrail.controller.listeners.CombatActionListeners;
import com.gpl.rpg.AndorsTrail.controller.listeners.CombatSelectionListeners;
import com.gpl.rpg.AndorsTrail.controller.listeners.CombatTurnListeners;
import com.gpl.rpg.AndorsTrail.model.AttackResult;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.util.Coord;

public final class CombatController implements VisualEffectCompletedCallback {
	private final ViewContext view;
    private final WorldContext world;
    public final CombatSelectionListeners combatSelectionListeners = new CombatSelectionListeners(); 
    public final CombatActionListeners combatActionListeners = new CombatActionListeners();
    public final CombatTurnListeners combatTurnListeners = new CombatTurnListeners();
    
	private Monster currentActiveMonster = null;
    private final ArrayList<Loot> killedMonsterBags = new ArrayList<Loot>();
    private int totalExpThisFight = 0;
    
	public CombatController(ViewContext view, WorldContext world) {
    	this.view = view;
    	this.world = world;
    }

	public static final int BEGIN_TURN_PLAYER = 0;
	public static final int BEGIN_TURN_MONSTERS = 1;
	public static final int BEGIN_TURN_CONTINUE = 2;
	
	public void enterCombat(int beginTurnAs) {
    	world.model.uiSelections.isInCombat = true;
    	killedMonsterBags.clear();
    	combatTurnListeners.onCombatStarted();
    	if (beginTurnAs == BEGIN_TURN_PLAYER) newPlayerTurn(true);
    	else if (beginTurnAs == BEGIN_TURN_MONSTERS) beginMonsterTurn(true);
    	else continueTurn();
    }
    public void exitCombat(boolean pickupLootBags) {
    	setCombatSelection(null, null);
    	world.model.uiSelections.isInCombat = false;
		combatTurnListeners.onCombatEnded();
    	currentActiveMonster = null;
    	world.model.uiSelections.selectedPosition = null;
    	world.model.uiSelections.selectedMonster = null;
    	if (!killedMonsterBags.isEmpty()) {
    		if (pickupLootBags) {
    			view.itemController.lootMonsterBags(killedMonsterBags, totalExpThisFight);
    		}
    		killedMonsterBags.clear();
    	} else {
    		view.gameRoundController.resume();
    	}
    	totalExpThisFight = 0;
    }
    
	public boolean isMonsterTurn() { 
		return currentActiveMonster != null;
	}

	public void setCombatSelection(Monster selectedMonster) {
		Coord p = selectedMonster.rectPosition.findPositionAdjacentTo(world.model.player.position);
		setCombatSelection(selectedMonster, p);
	}
	public void setCombatSelection(Monster selectedMonster, Coord selectedPosition) {
		if (selectedMonster != null) {
			if (!selectedMonster.isAgressive()) return;
		}
		Coord previousSelection = world.model.uiSelections.selectedPosition;
		if (previousSelection != null) {
			world.model.uiSelections.selectedPosition = null;
			if (selectedPosition == null || !selectedPosition.equals(previousSelection)) {	
			} else {
				previousSelection = null;
			}
		}
		world.model.uiSelections.selectedMonster = selectedMonster;
		if (selectedPosition != null) {
			world.model.uiSelections.selectedPosition = new Coord(selectedPosition);
			world.model.uiSelections.isInCombat = true;
		} else {
			world.model.uiSelections.selectedPosition = null;
		}
		
		if (selectedMonster != null) combatSelectionListeners.onMonsterSelected(selectedMonster, selectedPosition, previousSelection);
		else if (selectedPosition != null) combatSelectionListeners.onMovementDestinationSelected(selectedPosition, previousSelection);
		else if (previousSelection != null) combatSelectionListeners.onCombatSelectionCleared(previousSelection);
	}
	
	public void setCombatSelection(Coord p) {
		PredefinedMap map = world.model.currentMap;
		Monster m = map.getMonsterAt(p);
		if (m != null) {
			setCombatSelection(m, p);
		} else if (map.isWalkable(p)) {
			setCombatSelection(null, p);
		}
	}

	private boolean useAPs(int cost) {
		if (view.actorStatsController.useAPs(world.model.player, cost)) {
			return true;
		} else {
			combatActionListeners.onPlayerDoesNotHaveEnoughAP();
			return false;
		}
	}
	
	public boolean canExitCombat() { return getAdjacentMonster() == null; }
	private Monster getAdjacentMonster() { 
		return MovementController.getAdjacentAggressiveMonster(world.model.currentMap, world.model.player); 
	}

	public void executeMoveAttack(int dx, int dy) {
		if (isMonsterTurn()) {
			return;
		} else if (world.model.uiSelections.selectedMonster != null) {
			executePlayerAttack();
		} else if (world.model.uiSelections.selectedPosition != null) {
			executeCombatMove(world.model.uiSelections.selectedPosition);
		} else if (canExitCombat()) {
			exitCombat(true);
		} else if (dx != 0 || dy != 0) {
			executeFlee(dx, dy);
		} else {
			Monster m = getAdjacentMonster();
			if (m == null) return;
			setCombatSelection(m);
			executePlayerAttack();
		}
	}
	
	private void executeFlee(int dx, int dy) {
		// avoid monster fields when fleeing
		if (!view.movementController.findWalkablePosition(dx, dy, AndorsTrailPreferences.MOVEMENTAGGRESSIVENESS_DEFENSIVE)) return;
		Monster m = world.model.currentMap.getMonsterAt(world.model.player.nextPosition);
		if (m != null) return;
		executeCombatMove(world.model.player.nextPosition);
	}
	
	private Monster currentlyAttackedMonster;
	private AttackResult lastAttackResult;
	private void executePlayerAttack() {
		if (view.effectController.isRunningVisualEffect()) return;
		if (!useAPs(world.model.player.getAttackCost())) return;
		final Monster target = world.model.uiSelections.selectedMonster;
		this.currentlyAttackedMonster = target;
		
		final AttackResult attack = playerAttacks(world, target);
		this.lastAttackResult = attack;
		
		if (attack.isHit) {
			combatActionListeners.onPlayerAttackSuccess(target, attack);
			
			if (lastAttackResult.targetDied) {
				playerKilledMonster(currentlyAttackedMonster);
			}
			
			startAttackEffect(attack, world.model.uiSelections.selectedPosition, this, CALLBACK_PLAYERATTACK);
		} else {
			combatActionListeners.onPlayerAttackMissed(target, attack);
			playerAttackCompleted();
		}
	}
	
	private void playerAttackCompleted() {
		if (lastAttackResult.targetDied) {
			Monster nextMonster = getAdjacentMonster();
			if (nextMonster == null) {
				exitCombat(true);
				return;
			} else {
				setCombatSelection(nextMonster);
			}
		}
		
		playerActionCompleted();
	}
	
    public void playerKilledMonster(Monster killedMonster) {
    	final Player player = world.model.player;
    	
    	Loot loot = world.model.currentMap.getBagOrCreateAt(killedMonster.position);
		killedMonster.createLoot(loot, player);
		
		view.monsterSpawnController.remove(world.model.currentMap, killedMonster);
		view.effectController.addSplatter(world.model.currentMap, killedMonster);
		
		view.actorStatsController.addActorAP(player, player.getSkillLevel(SkillCollection.SKILL_CLEAVE) * SkillCollection.PER_SKILLPOINT_INCREASE_CLEAVE_AP);
		view.actorStatsController.addActorHealth(player, player.getSkillLevel(SkillCollection.SKILL_EATER) * SkillCollection.PER_SKILLPOINT_INCREASE_EATER_HEALTH);
		
		world.model.statistics.addMonsterKill(killedMonster.getMonsterTypeID());
		view.actorStatsController.addExperience(loot.exp);
		
		totalExpThisFight += loot.exp;
		loot.exp = 0;
		view.actorStatsController.applyKillEffectsToPlayer(player);
		
		if (!loot.hasItems()) {
			world.model.currentMap.removeGroundLoot(loot);
		} else if (world.model.uiSelections.isInCombat) {
			killedMonsterBags.add(loot);
		}
		
		combatActionListeners.onPlayerKilledMonster(killedMonster);
    }

	private boolean playerHasApLeft() {
		final Player player = world.model.player;
		if (player.hasAPs(player.getUseItemCost())) return true;
		if (player.hasAPs(player.getAttackCost())) return true;
		if (player.hasAPs(player.getMoveCost())) return true;
		return false;
	}
	private void playerActionCompleted() {
		if (!playerHasApLeft()) beginMonsterTurn(false);
	}
	private void continueTurn() {
		if (world.model.uiSelections.isPlayersCombatTurn) return;
		if (playerHasApLeft()) return;
		handleNextMonsterAction();
	}

	private void executeCombatMove(final Coord dest) {
		if (world.model.uiSelections.selectedMonster != null) return;
		if (dest == null) return;
		if (!useAPs(world.model.player.getMoveCost())) return;

		int fleeChanceBias = world.model.player.getSkillLevel(SkillCollection.SKILL_EVASION) * SkillCollection.PER_SKILLPOINT_INCREASE_EVASION_FLEE_CHANCE_PERCENTAGE;
		if (Constants.roll100(Constants.FLEE_FAIL_CHANCE_PERCENT - fleeChanceBias)) {
			fleeingFailed();
			return;
		}
		
		world.model.player.nextPosition.set(dest);
		view.movementController.moveToNextIfPossible(false);
		
		if (canExitCombat()) exitCombat(true);
		
		playerActionCompleted();
	}

	private void fleeingFailed() {
		combatActionListeners.onPlayerFailedFleeing();
		beginMonsterTurn(false);
	}
	
	private final Handler monsterTurnHandler = new Handler() {
        public void handleMessage(Message msg) {
        	monsterTurnHandler.removeMessages(0);
            CombatController.this.handleNextMonsterAction();
        }
	};
	
	public void beginMonsterTurn(boolean isFirstRound) {
		view.actorStatsController.setActorMinAP(world.model.player);
		world.model.uiSelections.isPlayersCombatTurn = false;
		for (MonsterSpawnArea a : world.model.currentMap.spawnAreas) {
			for (Monster m : a.monsters) {
				view.actorStatsController.setActorMaxAP(m);
			}
		}
		if (!isFirstRound) view.gameRoundController.onNewMonsterRound();
		handleNextMonsterAction();
	}
	
	private Monster determineNextMonster(Monster previousMonster) {
		if (previousMonster != null) {
			if (previousMonster.hasAPs(previousMonster.getAttackCost())) return previousMonster;
		}
		
		for (MonsterSpawnArea a : world.model.currentMap.spawnAreas) {
			for (Monster m : a.monsters) {
				if (!m.isAgressive()) continue;
				
				if (m.isAdjacentTo(world.model.player)) {
					if (m.hasAPs(m.getAttackCost())) return m;
				}
			}
		}
		return null;
	}
	
	private void handleNextMonsterAction() {
		if (!world.model.uiSelections.isMainActivityVisible) return;
		
		currentActiveMonster = determineNextMonster(currentActiveMonster);
		if (currentActiveMonster == null) {
			endMonsterTurn();
			return;
		}
		view.actorStatsController.useAPs(currentActiveMonster, currentActiveMonster.getAttackCost());
		
		combatTurnListeners.onMonsterIsAttacking(currentActiveMonster);
		AttackResult attack = monsterAttacks(world.model, currentActiveMonster);
		this.lastAttackResult = attack;
		
		if (attack.isHit) {
			combatActionListeners.onMonsterAttackSuccess(currentActiveMonster, attack);
			
			startAttackEffect(attack, world.model.player.position, this, CALLBACK_MONSTERATTACK);
		} else {
			combatActionListeners.onMonsterAttackMissed(currentActiveMonster, attack);
			
			monsterTurnHandler.sendEmptyMessageDelayed(0, view.preferences.attackspeed_milliseconds);
		}
	}
	
	private static final int CALLBACK_MONSTERATTACK = 0;
	private static final int CALLBACK_PLAYERATTACK = 1;
	
	@Override
	public void onVisualEffectCompleted(int callbackValue) {
		if (callbackValue == CALLBACK_MONSTERATTACK) {
			monsterAttackCompleted();
		} else if (callbackValue == CALLBACK_PLAYERATTACK) {
			playerAttackCompleted();
		}
	}

	private void monsterAttackCompleted() {
		if (lastAttackResult.targetDied) {
			view.controller.handlePlayerDeath();
			return;
		}
		handleNextMonsterAction();
	}
	
	private void startAttackEffect(AttackResult attack, final Coord position, VisualEffectCompletedCallback callback, int callbackValue) {
		if (view.preferences.attackspeed_milliseconds <= 0) {
			callback.onVisualEffectCompleted(callbackValue);
			return;
		}
		view.effectController.startEffect(
				position
				, VisualEffectCollection.EFFECT_BLOOD
				, attack.damage
				, callback
				, callbackValue);
	}
	private void endMonsterTurn() {
		currentActiveMonster = null;
		newPlayerTurn(false);
	}
	
	private void newPlayerTurn(boolean isFirstRound) {
		view.actorStatsController.setActorMaxAP(world.model.player);
		if (!isFirstRound) view.gameRoundController.onNewPlayerRound();
		world.model.uiSelections.isPlayersCombatTurn = true;
		combatTurnListeners.onNewPlayerTurn();
	}
	
	private static boolean hasCriticalAttack(Actor attacker, Actor target) {
		if (!attacker.hasCriticalAttacks()) return false;
		if (target.isImmuneToCriticalHits()) return false;
		return true;
	}
	private static float getAverageDamagePerHit(Actor attacker, Actor target) {
		float result = (float) (getAttackHitChance(attacker, target)) * attacker.getDamagePotential().average() / 100;
		if (hasCriticalAttack(attacker, target)) {
			result += (float) attacker.getEffectiveCriticalChance() * result * attacker.getCriticalMultiplier() / 100;
		}
		result -= target.getDamageResistance();
		return result;
	}
	private static float getAverageDamagePerTurn(Actor attacker, Actor target) {
		return getAverageDamagePerHit(attacker, target) * attacker.getAttacksPerTurn();
	}
	private static int getTurnsToKillTarget(Actor attacker, Actor target) {
		if (hasCriticalAttack(attacker, target)) {
			if (attacker.getDamagePotential().max * attacker.getCriticalMultiplier() <= target.getDamageResistance()) return 999;
		} else {
			if (attacker.getDamagePotential().max <= target.getDamageResistance()) return 999;
		}
		
		float averageDamagePerTurn = getAverageDamagePerTurn(attacker, target);
		if (averageDamagePerTurn <= 0) return 100;
		return (int) FloatMath.ceil(target.getMaxHP() / averageDamagePerTurn);
	}
	public static int getMonsterDifficulty(WorldContext world, Monster monster) {
		// returns [0..100) . 100 == easy.
		int turnsToKillMonster = getTurnsToKillTarget(world.model.player, monster);
		if (turnsToKillMonster >= 999) return 0;
		int turnsToKillPlayer = getTurnsToKillTarget(monster, world.model.player);
		int result = 50 + (turnsToKillPlayer - turnsToKillMonster) * 2;
		if (result <= 1) return 1;
		else if (result > 100) return 100;
		return result;
	}
	
	private AttackResult playerAttacks(WorldContext world, Monster currentMonster) {
    	AttackResult result = attack(world.model.player, currentMonster);
    	view.skillController.applySkillEffectsFromPlayerAttack(result, world, currentMonster);
    	return result;
	}
	
	private AttackResult monsterAttacks(ModelContainer model, Monster currentMonster) {
		AttackResult result = attack(currentMonster, model.player);
		view.skillController.applySkillEffectsFromMonsterAttack(result, world, currentMonster);
		return result;
	}
	

	private static final int n = 50;
	private static final int F = 40;
	private static final float two_divided_by_PI = (float) (2f / Math.PI);
	private static int getAttackHitChance(final Actor attacker, final Actor target) {
		final int c = attacker.getAttackChance() - target.getBlockChance();
		// (2/pi)*atan(..) will vary from -1 to +1 .
		return (int) (50 * (1 + two_divided_by_PI * (float)Math.atan((float)(c-n) / F)));
	}
	
	private AttackResult attack(final Actor attacker, final Actor target) {
		int hitChance = getAttackHitChance(attacker, target);
		if (!Constants.roll100(hitChance)) return AttackResult.MISS;
		
		int damage = Constants.rollValue(attacker.getDamagePotential());
		boolean isCriticalHit = false;
		if (hasCriticalAttack(attacker, target)) {
			isCriticalHit = Constants.roll100(attacker.getEffectiveCriticalChance());
			if (isCriticalHit) {
				damage *= attacker.getCriticalMultiplier();
			}
		}
		damage -= target.getDamageResistance();
		if (damage < 0) damage = 0;
		view.actorStatsController.removeActorHealth(target, damage);
		
		applyAttackHitStatusEffects(attacker, target);

		return new AttackResult(true, isCriticalHit, damage, target.isDead());
	}

	private void applyAttackHitStatusEffects(Actor attacker, Actor target) {
		ItemTraits_OnUse[] onHitEffects = attacker.getOnHitEffects();
		if (onHitEffects == null) return;
		
		for (ItemTraits_OnUse e : onHitEffects) {
			view.actorStatsController.applyUseEffect(attacker, target, e);
		}
	}
	
	public void monsterSteppedOnPlayer(Monster m) {
		setCombatSelection(m);
		enterCombat(BEGIN_TURN_MONSTERS);
	}
	
	public void startFlee() {
		setCombatSelection(null, null);
		combatActionListeners.onPlayerStartedFleeing();
	}
}
