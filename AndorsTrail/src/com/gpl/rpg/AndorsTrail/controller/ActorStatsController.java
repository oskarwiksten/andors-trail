package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.VisualEffectCollection;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.traits.AbilityModifierTraits;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.ActorTraits;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnEquip;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.map.LayeredWorldMap;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;

public class ActorStatsController {
	private final ViewContext view;
    //private final WorldContext world;

	public ActorStatsController(ViewContext context) {
    	this.view = context;
    	//this.world = context;
    }
	
	public static void removeOrAddConditionsFromEquippedItems(Player player) {
		for(int i = player.conditions.size() - 1; i >= 0; --i) {
			if (!player.conditions.get(i).isTemporaryEffect()) {
				player.conditions.remove(i);
			}
		}
		
		for (int i = 0; i < Inventory.NUM_WORN_SLOTS; ++i) {
			ItemType type = player.inventory.wear[i];
			if (type == null) continue;
		
			ItemTraits_OnEquip equipEffects = type.effects_equip;
			if (equipEffects != null) {
				for (ActorConditionEffect e : equipEffects.addedConditions) {
					player.conditions.add(e.createCondition(ActorCondition.DURATION_FOREVER));
				}
			}
		}
	}

	public static void removeAllTemporaryConditions(final Actor actor) {
		for(int i = actor.conditions.size() - 1; i >= 0; --i) {
			if (!actor.conditions.get(i).isTemporaryEffect()) continue;
			actor.conditions.remove(i);
		}
	}
	
	private static void removeAllConditionsOfType(final Actor actor, final String conditionTypeID) {
		for(int i = actor.conditions.size() - 1; i >= 0; --i) {
			if (actor.conditions.get(i).conditionType.conditionTypeID.equals(conditionTypeID)) {
				actor.conditions.remove(i);
			}
		}
	}
	
	private static void applyConditionEffect(Actor actor, ActorConditionEffect conditionEffect) {
		if (!Constants.rollResult(conditionEffect.chance)) return;
		
		if (conditionEffect.isRemovalEffect()) {
			removeAllConditionsOfType(actor, conditionEffect.conditionType.conditionTypeID);
		} else if (conditionEffect.magnitude > 0) {
			actor.conditions.add(conditionEffect.createCondition());
		} 
	}

	private static void applyEffectsFromCurrentConditions(Player player) {
		for (ActorCondition c : player.conditions) {
			applyAbilityEffects(player, c.conditionType.abilityEffect, false, c.magnitude);
		}
	}
	
	public static void applyAbilityEffects(Actor actor, AbilityModifierTraits effects, boolean isWeapon, int magnitude) {
		if (effects == null) return;
		
		ActorTraits traits = actor.traits;
		
		actor.health.addToMax(effects.maxHPBoost * magnitude);
		actor.ap.addToMax(effects.maxAPBoost * magnitude);
		traits.moveCost += (effects.moveCostPenalty * magnitude);
		
		CombatTraits combatTraits = effects.combatProficiency;
		if (combatTraits != null) {
			if (!isWeapon) { // For weapons, these stats are modified elsewhere (since they are not cumulative)
				traits.attackCost += (combatTraits.attackCost * magnitude);
				traits.criticalMultiplier += (combatTraits.criticalMultiplier * magnitude);
			}
			traits.attackChance += (combatTraits.attackChance * magnitude);
			traits.criticalChance += (combatTraits.criticalChance * magnitude);
			traits.damagePotential.add(combatTraits.damagePotential.current * magnitude, true);
			traits.damagePotential.max += (combatTraits.damagePotential.max * magnitude);
			traits.blockChance += (combatTraits.blockChance * magnitude);
			traits.damageResistance += (combatTraits.damageResistance * magnitude);
		}
		
		if (traits.attackCost <= 0) traits.attackCost = 1;
		if (traits.attackChance < 0) traits.attackChance = 0;
		if (traits.moveCost <= 0) traits.moveCost = 1;
	}
	
	public static void recalculatePlayerCombatTraits(Player player) {
		player.resetStatsToBaseTraits();
		ItemController.applyInventoryEffects(player);
		applyEffectsFromCurrentConditions(player);
		ItemController.recalculateHitEffectsFromWornItems(player);
		
		player.health.capAtMax();
		player.ap.capAtMax();
	}

	public void applyConditionsToPlayer(Player player) {
		applyStatsEffects(player);
		if (player.isDead()) {
			view.controller.handlePlayerDeath();
			return;
		}
		view.mainActivity.combatview.updatePlayerAP(player.ap);
		view.mainActivity.statusview.update();

		boolean removedAnyConditions = decreaseDurationAndRemoveConditions(player);
		if (removedAnyConditions) {
			recalculatePlayerCombatTraits(player);
		}
	}

	public void applyConditionsToMonsters(LayeredWorldMap map) {
		for (MonsterSpawnArea a : map.spawnAreas) {
	    	for (Monster m : a.monsters) {
	    		applyConditionsToMonster(m);
	    	}
		}
	}

	private void applyConditionsToMonster(Monster monster) {
		if (monster.conditions.isEmpty()) return;
		applyStatsEffects(monster);
		if (monster.isDead()) {
			view.combatController.playerKilledMonster(monster);
			return;
		}
		
		decreaseDurationAndRemoveConditions(monster);
	}

	private void applyStatsEffects(Actor actor) {
		for (ActorCondition c : actor.conditions) {
			applyStatsModifierEffect(actor, c.conditionType.statsEffect, c.magnitude);
		}
	}
	
	private static boolean decreaseDurationAndRemoveConditions(Actor actor) {
		boolean removedAnyConditions = false;
		for(int i = actor.conditions.size() - 1; i >= 0; --i) {
			ActorCondition c = actor.conditions.get(i);
			if (c.duration == ActorCondition.DURATION_FOREVER) continue;
			c.duration -= 1;
			if (c.duration <= 0) {
				actor.conditions.remove(i);
				removedAnyConditions = true;
			}
		}
		return removedAnyConditions;
	}
	
	public void applyUseEffect(Actor source, Actor target, ItemTraits_OnUse effect) {
		if (effect == null) return;
		
		applyStatsModifierEffect(source, effect, 1);
		for (ActorConditionEffect e : effect.addedConditions_source) {
			applyConditionEffect(source, e);
		}
		if (target != null) {
			for (ActorConditionEffect e : effect.addedConditions_target) {
				applyConditionEffect(target, e);
			}
		}
	}

	private void applyStatsModifierEffect(Actor actor, StatsModifierTraits effect, int magnitude) {
		if (effect == null) return;
		
		int effectValue = 0;
		int visualEffectID = effect.visualEffectID;
		if (effect.currentAPBoost != null) {
			effectValue = Constants.rollValue(effect.currentAPBoost);
			effectValue *= magnitude;
			boolean changed = actor.ap.change(effectValue, false, false);
			if (!changed) effectValue = 0; // So that the visualeffect doesn't start.
			if (effectValue != 0) {
				if (!effect.hasVisualEffect()) {
					visualEffectID = VisualEffectCollection.EFFECT_RESTORE_AP;
				}
			}
		}
		if (effect.currentHPBoost != null) {
			effectValue = Constants.rollValue(effect.currentHPBoost);
			effectValue *= magnitude;
			boolean changed = actor.health.change(effectValue, false, false);
			if (!changed) effectValue = 0; // So that the visualeffect doesn't start.
			if (effectValue != 0) {
				if (!effect.hasVisualEffect()) {
					if (effectValue > 0) {
						visualEffectID = VisualEffectCollection.EFFECT_RESTORE_HP;
					} else {
						visualEffectID = VisualEffectCollection.EFFECT_BLOOD;
					}
				}
			}
		}
		if (effectValue != 0) {
			view.effectController.startEffect(
				view.mainActivity.mainview
				, actor.position
				, visualEffectID
				, effectValue);
		}
	}
	
	public void applyKillEffectsToPlayer(Player player) {
		for (int i = 0; i < Inventory.NUM_WORN_SLOTS; ++i) {
			ItemType type = player.inventory.wear[i];
			if (type == null) continue;
			
			applyUseEffect(player, null, type.effects_kill);
		}
	}
}
