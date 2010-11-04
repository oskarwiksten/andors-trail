package com.gpl.rpg.AndorsTrail.controller;

import java.util.ArrayList;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.EffectCollection;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.AttackResult;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.ActorTraits;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.map.LayeredWorldMap;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.util.Coord;

public final class CombatController {
	private final ViewContext context;
    private final WorldContext world;
    private final ModelContainer model;
    
	private Monster currentActiveMonster = null;
    private final ArrayList<Monster> killedMonsters = new ArrayList<Monster>();
    
	public CombatController(ViewContext context) {
    	this.context = context;
    	this.world = context;
    	this.model = world.model;
    }

    public void enterCombat() {
    	context.mainActivity.combatview.setVisibility(View.VISIBLE);
    	context.mainActivity.combatview.bringToFront();
    	model.uiSelections.isInCombat = true;
    	killedMonsters.clear();
    	context.mainActivity.clearMessages();
    	newPlayerTurn();
    }
    public void exitCombat() {
    	setCombatSelection(null, null);
		context.mainActivity.combatview.setVisibility(View.GONE);
		model.uiSelections.isInCombat = false;
    	context.mainActivity.clearMessages();
    	currentActiveMonster = null;
    	if (!killedMonsters.isEmpty()) {
    		lootMonsters(killedMonsters);
    		killedMonsters.clear();
    	}
    	context.controller.queueAnotherTick();
    }
    
    private void lootMonsters(ArrayList<Monster> killedMonsters) {
    	Loot loot = model.currentMap.getBagOrCreateAt(killedMonsters.get(0).position);
    	for(Monster m : killedMonsters) {
    		m.createLoot(loot);
    		model.statistics.addMonsterKill(m.monsterType);
    	}
    	if (loot.isEmpty()) return;
    	Dialogs.showMonsterLoot(context.mainActivity, context, loot);
    	ItemController.consumeLoot(loot, model.player);
    	context.mainActivity.statusview.update();
	}

	public boolean isMonsterTurn() { 
		return currentActiveMonster != null;
	}

	public void setCombatSelection(Monster selectedMonster, Coord selectedPosition) {
		if (selectedMonster != null) {
			if (!selectedMonster.monsterType.isAgressive()) return;
		}
		Coord previousSelection = model.uiSelections.selectedPosition;
		if (model.uiSelections.selectedPosition != null) {
			model.uiSelections.selectedPosition = null;
			context.mainActivity.redrawTile(previousSelection);
		}
		model.uiSelections.selectedMonster = selectedMonster;
		model.uiSelections.selectedPosition = selectedPosition;
		context.mainActivity.combatview.updateCombatSelection(selectedMonster, selectedPosition);
		if (selectedPosition != null) {
			model.uiSelections.isInCombat = true;
			context.mainActivity.redrawTile(selectedPosition);
		}
	}
	public void setCombatSelection(Coord p) {
		LayeredWorldMap map = model.currentMap;
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
			context.mainActivity.combatview.updatePlayerAP(model.player.ap);
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
				if (!m.monsterType.isAgressive()) continue;
				if (m.rectPosition.isAdjacentTo(model.player.position)) {
					return m;
				}
			}
		}
		return null;
	}

	public void executeMoveAttack() {
		if (world.model.uiSelections.selectedMonster != null) {
			executeAttack();
		} else if (world.model.uiSelections.selectedPosition != null) {
			executeMove();
		} else if (canExitCombat()) {
			exitCombat();
		}
	}
	
	private void executeAttack() {
		if (!useAPs(model.player.traits.attackCost)) return;
		Monster target = model.uiSelections.selectedMonster;
			
		AttackResult attack = playerAttacks(model, target);
		Resources r = context.mainActivity.getResources();
		if (attack.isHit) {
			String msg;
			
			final String monsterName = target.traits.name;
			if (attack.isCriticalHit) {
				msg = r.getString(R.string.combat_result_herohitcritical, monsterName, attack.damage);
			} else {
				msg = r.getString(R.string.combat_result_herohit, monsterName, attack.damage);
			}
			if (attack.targetDied) {
				msg += " " + r.getString(R.string.combat_result_herokillsmonster, monsterName, attack.damage);
			}
			message(msg);
			context.effectController.startEffect(
					context.mainActivity.mainview
					, model.uiSelections.selectedPosition
					, EffectCollection.EFFECT_BLOOD
					, attack.damage);

			if (!attack.targetDied) {
				context.mainActivity.combatview.updateMonsterHealth(target.health);
			} else {
				killedMonsters.add(target);
				Monster nextMonster = getAdjacentMonster();
				if (nextMonster == null) {
					exitCombat();
				} else {
					setCombatSelection(nextMonster, nextMonster.position);
				}
			}
		} else {
			message(r.getString(R.string.combat_result_heromiss));
		}
		
		if (!attack.isHit || !attack.targetDied) {
			maybeAutoEndTurn();
		}
	}

	private void maybeAutoEndTurn() {
		if (model.player.ap.current < model.player.useItemCost
			&& model.player.ap.current < model.player.traits.attackCost
			&& model.player.ap.current < model.player.traits.moveCost) {
			endPlayerTurn();
		}
	}

	private void executeMove() {
		if (model.uiSelections.selectedMonster != null) return;
		if (model.uiSelections.selectedPosition == null) return;
		if (!useAPs(model.player.traits.moveCost)) return;

		model.player.nextPosition.set(model.uiSelections.selectedPosition);
		context.movementController.moveToNextIfPossible(false);
		
		maybeAutoEndTurn();
	}

	private final Handler monsterTurnHandler = new Handler() {
        public void handleMessage(Message msg) {
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

	private Monster determineNextMonster(Monster previousMonster) {
		if (previousMonster != null) {
			if (previousMonster.useAPs(previousMonster.traits.attackCost)) return previousMonster;
		}
		
		for (MonsterSpawnArea a : model.currentMap.spawnAreas) {
			for (Monster m : a.monsters) {
				if (!m.monsterType.isAgressive()) continue;
				
				if (m.rectPosition.isAdjacentTo(model.player.position)) {
					if (m.useAPs(m.traits.attackCost)) return m;
				}
			}
		}
		return null;
	}
	
	private void handleNextMonsterAction() {
		currentActiveMonster = determineNextMonster(currentActiveMonster);
		if (currentActiveMonster == null) {
			endMonsterTurn();
			return;
		}
		context.mainActivity.combatview.updateTurnInfo(currentActiveMonster);
		Resources r = context.mainActivity.getResources();
		AttackResult attack = monsterAttacks(model, currentActiveMonster);
		String monsterName = currentActiveMonster.traits.name;
		if (attack.isHit) {
			context.effectController.startEffect(
					context.mainActivity.mainview
					, model.player.position
					, EffectCollection.EFFECT_BLOOD
					, attack.damage);
			if (attack.isCriticalHit) {
				message(r.getString(R.string.combat_result_monsterhitcritical, monsterName, attack.damage));
			} else {
				message(r.getString(R.string.combat_result_monsterhit, monsterName, attack.damage));
			}
			if (attack.targetDied) {
				exitCombat();
				context.controller.handlePlayerDeath();
				return;
			}
		} else {
			message(r.getString(R.string.combat_result_monstermiss, monsterName));
		}
		context.mainActivity.statusview.update();
		monsterTurnHandler.sendEmptyMessageDelayed(0, ModelContainer.monsterAttackDelay);
	}

	private void endMonsterTurn() {
		newPlayerTurn();
	}
	
	private void newPlayerTurn() {
		currentActiveMonster = null;
    	model.player.ap.setMax();
    	context.mainActivity.combatview.updateTurnInfo(null);
    	context.mainActivity.combatview.updatePlayerAP(model.player.ap);
	}
	
	private static float getAverageDamagePerHit(ActorTraits attacker, ActorTraits target) {
		float result = (float) (attacker.attackChance - target.blockChance) * attacker.damagePotential.average() / 100;
		result += (float) attacker.criticalChance * result * attacker.criticalMultiplier / 100;
		result -= target.damageResistance;
		return result;
	}
	private static float getAverageDamagePerTurn(ActorTraits attacker, ActorTraits target) {
		return getAverageDamagePerHit(attacker, target) * attacker.getAttacksPerTurn();
	}
	private static int getTurnsToKillTarget(ActorTraits attacker, ActorTraits target) {
		float averageDamagePerTurn = getAverageDamagePerTurn(attacker, target);
		if (averageDamagePerTurn == 0) return 100;
		return (int) Math.ceil(target.maxHP / averageDamagePerTurn);
	}
	public static int getMonsterDifficulty(WorldContext world, MonsterType monsterType) {
		// returns [0..100) . 100 == easy.
		int turnsToKillMonster = getTurnsToKillTarget(world.model.player.traits, monsterType);
		int turnsToKillPlayer = getTurnsToKillTarget(monsterType, world.model.player.traits);
		int result = 50 + (turnsToKillPlayer - turnsToKillMonster) * 2;
		if (result < 0) result = 0;
		else if (result > 100) result = 100;
		return result;
	}
	
	public AttackResult playerAttacks(ModelContainer model, Monster currentMonster) {
    	AttackResult result = attack(model.player, currentMonster);
    	
    	if (result.targetDied) {
    		model.currentMap.remove(currentMonster);
		}

		return result;
	}
	
	public AttackResult monsterAttacks(ModelContainer model, Monster currentMonster) {
		return attack(currentMonster, model.player);
	}
	

	private static AttackResult attack(final Actor attacker, final Actor target) {
		int hitChance = attacker.traits.attackChance - target.traits.blockChance;
		if (!ModelContainer.roll100(hitChance)) return AttackResult.MISS;
		
		int damage = ModelContainer.rollValue(attacker.traits.damagePotential);
		boolean isCriticalHit = false;
		if (attacker.traits.hasCriticalEffect()) {
			isCriticalHit = ModelContainer.roll100(attacker.traits.criticalChance);
			if (isCriticalHit) {
				damage *= attacker.traits.criticalMultiplier;
			}
		}
		damage -= target.traits.damageResistance;
		target.health.subtract(damage, false);
			
		return new AttackResult(true, isCriticalHit, damage, target.health.current <= 0);
	}

	public void monsterSteppedOnPlayer(Monster m) {
		enterCombat();
		endPlayerTurn();
		setCombatSelection(m, m.position);
	}
}
