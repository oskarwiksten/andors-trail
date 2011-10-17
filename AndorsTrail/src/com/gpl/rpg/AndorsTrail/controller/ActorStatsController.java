package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.VisualEffectCollection;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;
import com.gpl.rpg.AndorsTrail.model.ability.traits.AbilityModifierTraits;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnEquip;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
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
			if (equipEffects != null && equipEffects.addedConditions != null) {
				for (ActorConditionEffect e : equipEffects.addedConditions) {
					addActorCondition(player, e, ActorCondition.DURATION_FOREVER);
				}
			}
		}
	}
	
	private static void addActorCondition(Actor actor, ActorConditionEffect e) { addActorCondition(actor, e, e.duration); }
	private static void addActorCondition(Actor actor, ActorConditionEffect e, int duration) {
		final ActorConditionType type = e.conditionType;
		if (e.isRemovalEffect()) {
			removeAllConditionsOfType(actor, type.conditionTypeID);
		} else if (e.magnitude > 0) {
			if (!e.conditionType.isStacking) {
				if (actor.hasCondition(type.conditionTypeID)) return;
				//TODO: Maybe only keep the one with the highest magnitude?
			}
			actor.conditions.add(e.createCondition(duration));
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
	
	private static void applyEffectsFromCurrentConditions(Actor actor) {
		for (ActorCondition c : actor.conditions) {
			applyAbilityEffects(actor, c.conditionType.abilityEffect, false, c.magnitude);
		}
		actor.health.capAtMax();
		actor.ap.capAtMax();
	}
	
	public static void applyAbilityEffects(Actor actor, AbilityModifierTraits effects, boolean isWeapon, int magnitude) {
		if (effects == null) return;
		
		CombatTraits actorCombatTraits = actor.combatTraits;
		
		actor.health.addToMax(effects.maxHPBoost * magnitude);
		actor.ap.addToMax(effects.maxAPBoost * magnitude);
		actor.actorTraits.moveCost += (effects.moveCostPenalty * magnitude);
		
		CombatTraits combatTraits = effects.combatProficiency;
		if (combatTraits != null) {
			if (!isWeapon) { // For weapons, these stats are modified elsewhere (since they are not cumulative)
				actorCombatTraits.attackCost += (combatTraits.attackCost * magnitude);
				actorCombatTraits.criticalMultiplier += (combatTraits.criticalMultiplier * magnitude);
			}
			actorCombatTraits.attackChance += (combatTraits.attackChance * magnitude);
			actorCombatTraits.criticalChance += (combatTraits.criticalChance * magnitude);
			actorCombatTraits.damagePotential.add(combatTraits.damagePotential.current * magnitude, true);
			actorCombatTraits.damagePotential.max += (combatTraits.damagePotential.max * magnitude);
			actorCombatTraits.blockChance += (combatTraits.blockChance * magnitude);
			actorCombatTraits.damageResistance += (combatTraits.damageResistance * magnitude);
		}
		
		if (actorCombatTraits.attackCost <= 0) actorCombatTraits.attackCost = 1;
		if (actorCombatTraits.attackChance < 0) actorCombatTraits.attackChance = 0;
		if (actor.actorTraits.moveCost <= 0) actor.actorTraits.moveCost = 1;
		if (actorCombatTraits.damagePotential.max < 0) actorCombatTraits.damagePotential.set(0, 0);
	}
	
	public static void recalculatePlayerCombatTraits(Player player) { recalculateActorCombatTraits(player); }
	public static void recalculateMonsterCombatTraits(Monster monster) { recalculateActorCombatTraits(monster); }
	private static void recalculateActorCombatTraits(Actor actor) {
		actor.resetStatsToBaseTraits();
		if (actor.isPlayer) ItemController.applyInventoryEffects((Player) actor);
		if (actor.isPlayer) SkillController.applySkillEffects((Player) actor);
		applyEffectsFromCurrentConditions(actor);
		if (actor.isPlayer) ItemController.recalculateHitEffectsFromWornItems((Player) actor);
	}

	public void applyConditionsToPlayer(Player player, boolean isFullRound) {
		applyStatsEffects(player, isFullRound);
		if (player.isDead()) {
			view.controller.handlePlayerDeath();
			return;
		}
		view.mainActivity.updateStatus();

		decreaseDurationAndRemoveConditions(player);
	}

	public void applyConditionsToMonsters(PredefinedMap map, boolean isFullRound) {
		for (MonsterSpawnArea a : map.spawnAreas) {
	    	for (Monster m : a.monsters) {
	    		applyConditionsToMonster(m, isFullRound);
	    	}
		}
	}

	private void applyConditionsToMonster(Monster monster, boolean isFullRound) {
		if (monster.conditions.isEmpty()) return;
		applyStatsEffects(monster, isFullRound);
		if (monster.isDead()) {
			view.combatController.playerKilledMonster(monster);
			return;
		}
		
		decreaseDurationAndRemoveConditions(monster);
	}

	private void applyStatsEffects(Actor actor, boolean isFullRound) {
		for (ActorCondition c : actor.conditions) {
			StatsModifierTraits effect = isFullRound ? c.conditionType.statsEffect_everyFullRound : c.conditionType.statsEffect_everyRound;
			applyStatsModifierEffect(actor, effect, c.magnitude);
		}
	}
	
	private static void decreaseDurationAndRemoveConditions(Actor actor) {
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
		if (removedAnyConditions) {
			recalculateActorCombatTraits(actor);
		}
	}
	
	public void applyUseEffect(Actor source, Actor target, ItemTraits_OnUse effect) {
		if (effect == null) return;
		
		if (effect.addedConditions_source != null) {
			for (ActorConditionEffect e : effect.addedConditions_source) {
				rollForConditionEffect(source, e);
			}
		}
		if (target != null) {
			if (effect.addedConditions_target != null) {
				for (ActorConditionEffect e : effect.addedConditions_target) {
					rollForConditionEffect(target, e);
				}
			}
		}
		applyStatsModifierEffect(source, effect, 1);
	}

	private static void rollForConditionEffect(Actor actor, ActorConditionEffect conditionEffect) {
		int chanceRollBias = 0;
		if (actor.isPlayer) chanceRollBias = SkillController.getActorConditionEffectChanceRollBias(conditionEffect, (Player) actor);
		
		if (!Constants.rollResult(conditionEffect.chance, chanceRollBias)) return;
		addActorCondition(actor, conditionEffect);
		recalculateActorCombatTraits(actor);
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
				, effectValue
				, null
				, 0);
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
