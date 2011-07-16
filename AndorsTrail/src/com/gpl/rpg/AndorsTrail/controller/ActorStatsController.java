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
import com.gpl.rpg.AndorsTrail.model.actor.ActorTraits;
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

		boolean removedAnyConditions = decreaseDurationAndRemoveConditions(player);
		if (removedAnyConditions) {
			recalculatePlayerCombatTraits(player);
		}
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
	}

	private static void rollForConditionEffect(Actor actor, ActorConditionEffect conditionEffect) {
		if (!Constants.rollResult(conditionEffect.chance)) return;
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
