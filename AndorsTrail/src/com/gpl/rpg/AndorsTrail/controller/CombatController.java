package com.gpl.rpg.AndorsTrail.controller;

import java.util.HashSet;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.VisualEffectCollection;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.VisualEffectController.VisualEffectCompletedCallback;
import com.gpl.rpg.AndorsTrail.model.AttackResult;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
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
import com.gpl.rpg.AndorsTrail.view.MainView;

public final class CombatController implements VisualEffectCompletedCallback {
	private final ViewContext context;
    private final WorldContext world;
    private final ModelContainer model;
    
	private Monster currentActiveMonster = null;
    private final HashSet<Loot> killedMonsterBags = new HashSet<Loot>();
    private int totalExpThisFight = 0;
    
	public CombatController(ViewContext context) {
    	this.context = context;
    	this.world = context;
    	this.model = world.model;
    }

	public static final int BEGIN_TURN_PLAYER = 0;
	public static final int BEGIN_TURN_MONSTERS = 1;
	public static final int BEGIN_TURN_CONTINUE = 2;
	
	public void enterCombat(int beginTurnAs) {
    	context.mainActivity.combatview.setVisibility(View.VISIBLE);
    	context.mainActivity.combatview.bringToFront();
    	model.uiSelections.isInCombat = true;
    	killedMonsterBags.clear();
    	context.mainActivity.clearMessages();
    	if (beginTurnAs == BEGIN_TURN_PLAYER) newPlayerTurn();
    	else if (beginTurnAs == BEGIN_TURN_MONSTERS) endPlayerTurn();
    	else maybeAutoEndTurn();
    	updateTurnInfo();
    }
    public void exitCombat(boolean pickupLootBags) {
    	setCombatSelection(null, null);
		context.mainActivity.combatview.setVisibility(View.GONE);
		model.uiSelections.isInCombat = false;
    	context.mainActivity.clearMessages();
    	currentActiveMonster = null;
    	model.uiSelections.selectedPosition = null;
    	model.uiSelections.selectedMonster = null;
    	if (!killedMonsterBags.isEmpty()) {
    		if (pickupLootBags) {
    			lootCurrentMonsterBags();
    		}
    		killedMonsterBags.clear();
    	} else {
    		context.gameRoundController.resume();
    	}
    	totalExpThisFight = 0;
    }
    
    private void lootCurrentMonsterBags() {
    	Dialogs.showMonsterLoot(context.mainActivity, context, killedMonsterBags, totalExpThisFight);
    	ItemController.consumeNonItemLoot(killedMonsterBags, model);
	}

	public boolean isMonsterTurn() { 
		return currentActiveMonster != null;
	}

	public void setCombatSelection(Monster selectedMonster) {
		Coord p = selectedMonster.rectPosition.findPositionAdjacentTo(model.player.position);
		setCombatSelection(selectedMonster, p);
	}
	public void setCombatSelection(Monster selectedMonster, Coord selectedPosition) {
		if (selectedMonster != null) {
			if (!selectedMonster.isAgressive()) return;
		}
		Coord previousSelection = model.uiSelections.selectedPosition;
		if (model.uiSelections.selectedPosition != null) {
			model.uiSelections.selectedPosition = null;
			if (selectedPosition != null && !selectedPosition.equals(previousSelection)) {	
				context.mainActivity.redrawTile(previousSelection, MainView.REDRAW_TILE_SELECTION_REMOVED);
			}
		}
		context.mainActivity.combatview.updateCombatSelection(selectedMonster, selectedPosition);
		model.uiSelections.selectedMonster = selectedMonster;
		if (selectedPosition != null) {
			model.uiSelections.selectedPosition = new Coord(selectedPosition);
			model.uiSelections.isInCombat = true;
			context.mainActivity.redrawTile(selectedPosition, MainView.REDRAW_TILE_SELECTION_ADDED);
		} else {
			model.uiSelections.selectedPosition = null;
		}
	}
	public void setCombatSelection(Coord p) {
		PredefinedMap map = model.currentMap;
		Monster m = map.getMonsterAt(p);
		if (m != null) {
			setCombatSelection(m, p);
		} else if (map.isWalkable(p)) {
			setCombatSelection(null, p);
		}
	}

	private void message(String s) {
		context.mainActivity.message(s);
	}
	private boolean useAPs(int cost) {
		if (model.player.useAPs(cost)) {
			return true;
		} else {
			message(context.mainActivity.getResources().getString(R.string.combat_not_enough_ap));
			return false;
		}
	}
	
	public boolean canExitCombat() { return getAdjacentMonster() == null; }
	private Monster getAdjacentMonster() {
		for (MonsterSpawnArea a : model.currentMap.spawnAreas) {
			for (Monster m : a.monsters) {
				if (!m.isAgressive()) continue;
				if (m.rectPosition.isAdjacentTo(model.player.position)) {
					return m;
				}
			}
		}
		return null;
	}

	public void executeMoveAttack(int dx, int dy) {
		if (isMonsterTurn()) {
			forceFinishMonsterAction();
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
		if (!context.movementController.findWalkablePosition(dx, dy, AndorsTrailPreferences.MOVEMENTAGGRESSIVENESS_DEFENSIVE)) return;
		Monster m = model.currentMap.getMonsterAt(model.player.nextPosition);
		if (m != null) return;
		executeCombatMove(world.model.player.nextPosition);
	}
	
	private Monster currentlyAttackedMonster;
	private AttackResult lastAttackResult;
	private void executePlayerAttack() {
		if (context.effectController.isRunningVisualEffect()) return;
		if (!useAPs(model.player.combatTraits.attackCost)) return;
		final Monster target = model.uiSelections.selectedMonster;
		this.currentlyAttackedMonster = target;
		
		final AttackResult attack = playerAttacks(model, target);
		this.lastAttackResult = attack;
		
		Resources r = context.mainActivity.getResources();
		if (attack.isHit) {
			String msg;
			
			final String monsterName = target.actorTraits.name;
			if (attack.isCriticalHit) {
				msg = r.getString(R.string.combat_result_herohitcritical, monsterName, attack.damage);
			} else {
				msg = r.getString(R.string.combat_result_herohit, monsterName, attack.damage);
			}
			if (attack.targetDied) {
				msg += " " + r.getString(R.string.combat_result_herokillsmonster, monsterName, attack.damage);
			}
			message(msg);
			
			context.mainActivity.updateStatus();
			if (lastAttackResult.targetDied) {
				playerKilledMonster(currentlyAttackedMonster);
			}
			
			startAttackEffect(attack, model.uiSelections.selectedPosition, this, CALLBACK_PLAYERATTACK);
		} else {
			message(r.getString(R.string.combat_result_heromiss));
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
		
		context.mainActivity.updateStatus();
		maybeAutoEndTurn();
	}
	
    public void playerKilledMonster(Monster killedMonster) {
    	final Player player = model.player;
    	
    	Loot loot = model.currentMap.getBagOrCreateAt(killedMonster.position);
		killedMonster.createLoot(loot, player);
		
		model.currentMap.remove(killedMonster);
		
		player.ap.add(player.getSkillLevel(SkillCollection.SKILL_CLEAVE) * SkillCollection.PER_SKILLPOINT_INCREASE_CLEAVE_AP, false);
		player.health.add(player.getSkillLevel(SkillCollection.SKILL_EATER) * SkillCollection.PER_SKILLPOINT_INCREASE_EATER_HEALTH, false);
		
		model.statistics.addMonsterKill(killedMonster.monsterTypeID);
		model.player.addExperience(loot.exp);
		totalExpThisFight += loot.exp;
		loot.exp = 0;
		context.actorStatsController.applyKillEffectsToPlayer(player);
		
		context.mainActivity.updateStatus();

		if (!loot.hasItems()) {
			model.currentMap.removeGroundLoot(loot);
		} else {
			ItemController.updateLootVisibility(context, loot);
			if (model.uiSelections.isInCombat) killedMonsterBags.add(loot);
		}
		
		context.mainActivity.redrawAll(MainView.REDRAW_ALL_MONSTER_KILLED);
    }

	private void maybeAutoEndTurn() {
		if (model.player.ap.current < model.player.useItemCost
			&& model.player.ap.current < model.player.combatTraits.attackCost
			&& model.player.ap.current < model.player.actorTraits.moveCost) {
			endPlayerTurn();
		}
	}

	private void executeCombatMove(final Coord dest) {
		if (model.uiSelections.selectedMonster != null) return;
		if (dest == null) return;
		if (!useAPs(model.player.actorTraits.moveCost)) return;

		int fleeChanceBias = model.player.getSkillLevel(SkillCollection.SKILL_EVASION) * SkillCollection.PER_SKILLPOINT_INCREASE_EVASION_FLEE_CHANCE_PERCENTAGE;
		if (Constants.roll100(Constants.FLEE_FAIL_CHANCE_PERCENT - fleeChanceBias)) {
			fleeingFailed();
			return;
		}
		
		model.player.nextPosition.set(dest);
		context.movementController.moveToNextIfPossible(false);
		
		if (canExitCombat()) exitCombat(true);
		
		context.mainActivity.updateStatus();
		maybeAutoEndTurn();
	}

	private void fleeingFailed() {
		Resources r = context.mainActivity.getResources();
		message(r.getString(R.string.combat_flee_failed));
		endPlayerTurn();
	}

	private final Handler monsterTurnHandler = new Handler() {
        public void handleMessage(Message msg) {
        	monsterTurnHandler.removeMessages(0);
            CombatController.this.handleNextMonsterAction();
        }
	};
	
	public void endPlayerTurn() {
		model.player.ap.current = 0;
		for (MonsterSpawnArea a : model.currentMap.spawnAreas) {
			for (Monster m : a.monsters) {
				m.setMaxAP();
			}
		}
		handleNextMonsterAction();
	}

	private void forceFinishMonsterAction() {
		//TODO:
		return;
		//waitForEffect = false;
		//monsterTurnHandler.removeMessages(0);
		//monsterTurnHandler.sendEmptyMessage(0);
	}

	private Monster determineNextMonster(Monster previousMonster) {
		if (previousMonster != null) {
			if (previousMonster.useAPs(previousMonster.combatTraits.attackCost)) return previousMonster;
		}
		
		for (MonsterSpawnArea a : model.currentMap.spawnAreas) {
			for (Monster m : a.monsters) {
				if (!m.isAgressive()) continue;
				
				if (m.rectPosition.isAdjacentTo(model.player.position)) {
					if (m.useAPs(m.combatTraits.attackCost)) return m;
				}
			}
		}
		return null;
	}
	
	private void handleNextMonsterAction() {
		if (!context.model.uiSelections.isMainActivityVisible) return;
		
		currentActiveMonster = determineNextMonster(currentActiveMonster);
		if (currentActiveMonster == null) {
			endMonsterTurn();
			return;
		}
		
		context.mainActivity.combatview.updateTurnInfo(currentActiveMonster);
		Resources r = context.mainActivity.getResources();
		AttackResult attack = monsterAttacks(model, currentActiveMonster);
		this.lastAttackResult = attack;
		
		String monsterName = currentActiveMonster.actorTraits.name;
		if (attack.isHit) {
			if (attack.isCriticalHit) {
				message(r.getString(R.string.combat_result_monsterhitcritical, monsterName, attack.damage));
			} else {
				message(r.getString(R.string.combat_result_monsterhit, monsterName, attack.damage));
			}
			context.mainActivity.updateStatus();
			
			startAttackEffect(attack, model.player.position, this, CALLBACK_MONSTERATTACK);
		} else {
			message(r.getString(R.string.combat_result_monstermiss, monsterName));
			context.mainActivity.updateStatus();
			
			monsterTurnHandler.sendEmptyMessageDelayed(0, context.preferences.attackspeed_milliseconds);
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
			context.controller.handlePlayerDeath();
			return;
		}
		handleNextMonsterAction();
	}
	
	private void startAttackEffect(AttackResult attack, final Coord position, VisualEffectCompletedCallback callback, int callbackValue) {
		if (context.preferences.attackspeed_milliseconds <= 0) {
			callback.onVisualEffectCompleted(callbackValue);
			return;
		}
		context.effectController.startEffect(
				context.mainActivity.mainview
				, position
				, VisualEffectCollection.EFFECT_BLOOD
				, attack.damage
				, callback
				, callbackValue);
	}
	private void endMonsterTurn() {
		currentActiveMonster = null;
		newPlayerTurn();
	}
	
	private void newPlayerTurn() {
		model.player.setMaxAP();
    	updateTurnInfo();
	}
	private void updateTurnInfo() {
		context.mainActivity.combatview.updateTurnInfo(currentActiveMonster);
    	context.mainActivity.updateStatus();
	}
	
	private static float getAverageDamagePerHit(Actor attacker, Actor target) {
		float result = (float) (getAttackHitChance(attacker.combatTraits, target.combatTraits)) * attacker.combatTraits.damagePotential.average() / 100;
		result += (float) attacker.combatTraits.criticalChance * result * attacker.combatTraits.criticalMultiplier / 100;
		result -= target.combatTraits.damageResistance;
		return result;
	}
	private static float getAverageDamagePerTurn(Actor attacker, Actor target) {
		return getAverageDamagePerHit(attacker, target) * attacker.getAttacksPerTurn();
	}
	private static int getTurnsToKillTarget(Actor attacker, Actor target) {
		if (attacker.combatTraits.hasCriticalAttacks()) {
			if (attacker.combatTraits.damagePotential.max * attacker.combatTraits.criticalMultiplier <= target.combatTraits.damageResistance) return 999;
		} else {
			if (attacker.combatTraits.damagePotential.max <= target.combatTraits.damageResistance) return 999;
		}
		
		float averageDamagePerTurn = getAverageDamagePerTurn(attacker, target);
		if (averageDamagePerTurn <= 0) return 100;
		return (int) Math.ceil(target.actorTraits.maxHP / averageDamagePerTurn);
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
	
	private AttackResult playerAttacks(ModelContainer model, Monster currentMonster) {
    	return attack(model.player, currentMonster);
	}
	
	private AttackResult monsterAttacks(ModelContainer model, Monster currentMonster) {
		return attack(currentMonster, model.player);
	}
	

	private static final int n = 50;
	private static final int F = 40;
	private static final float two_divided_by_PI = (float) (2f / Math.PI);
	private static int getAttackHitChance(final CombatTraits attacker, final CombatTraits target) {
		final int c = attacker.attackChance - target.blockChance;
		// (2/pi)*atan(..) will vary from -1 to +1 .
		return (int) (50 * (1 + two_divided_by_PI * (float)Math.atan((float)(c-n) / F)));
	}
	
	private AttackResult attack(final Actor attacker, final Actor target) {
		int hitChance = getAttackHitChance(attacker.combatTraits, target.combatTraits);
		if (!Constants.roll100(hitChance)) return AttackResult.MISS;
		
		int damage = Constants.rollValue(attacker.combatTraits.damagePotential);
		boolean isCriticalHit = false;
		if (attacker.combatTraits.hasCriticalAttacks()) {
			isCriticalHit = Constants.roll100(attacker.combatTraits.criticalChance);
			if (isCriticalHit) {
				damage *= attacker.combatTraits.criticalMultiplier;
			}
		}
		damage -= target.combatTraits.damageResistance;
		if (damage < 0) damage = 0;
		target.health.subtract(damage, false);
		
		applyAttackHitStatusEffects(attacker, target);

		return new AttackResult(true, isCriticalHit, damage, target.isDead());
	}

	private void applyAttackHitStatusEffects(Actor attacker, Actor target) {
		if (attacker.actorTraits.onHitEffects == null) return;
		
		for (ItemTraits_OnUse e : attacker.actorTraits.onHitEffects) {
			context.actorStatsController.applyUseEffect(attacker, target, e);
		}
	}
	
	public void monsterSteppedOnPlayer(Monster m) {
		setCombatSelection(m);
		enterCombat(BEGIN_TURN_MONSTERS);
	}
	
	public void startFlee() {
		setCombatSelection(null, null);
		Resources r = context.mainActivity.getResources();
		message(r.getString(R.string.combat_begin_flee));
	}
}
