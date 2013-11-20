package com.gpl.rpg.AndorsTrail.scripting.proxyobjects;

import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.traits.AbilityModifierTraits;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnEquip;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.resource.VisualEffectCollection.VisualEffectID;
import com.gpl.rpg.AndorsTrail.util.ConstRange;

public class ItemReward {

	private VisualEffectID visualEffectID = null;

	public int hpMax = 0;
	public int hpMin = 0;
	public int apMax = 0;
	public int apMin = 0;
	
	public int increaseMaxHP = 0;
	public int increaseMaxAP = 0;
	public int increaseMoveCost = 0;
	public int increaseUseItemCost = 0;
	public int increaseReequipCost = 0;
	public int increaseAttackCost = 0;
	public int increaseAttackChance = 0;
	public int increaseBlockChance = 0;
	public int increaseMinDamage = 0;
	public int increaseMaxDamage = 0;
	public int increaseCriticalSkill = 0;
	public float setCriticalMultiplier = 0;
	public int increaseDamageResistance = 0;
	
	public ActorConditionEffect[] effectsToSource;
	public ActorConditionEffect[] effectsToTarget;

	public ItemReward(ItemTraits_OnUse useEffect) {
		if (useEffect.changedStats != null) {
			this.visualEffectID = useEffect.changedStats.visualEffectID;
			if (useEffect.changedStats.currentHPBoost != null) {
				hpMax = useEffect.changedStats.currentHPBoost.max;
				hpMin = useEffect.changedStats.currentHPBoost.current;
			}
			if (useEffect.changedStats.currentAPBoost != null) {
				apMax = useEffect.changedStats.currentAPBoost.max;
				apMin = useEffect.changedStats.currentAPBoost.current;
			}
		}
		if (useEffect.addedConditions_source != null) {
			int i = useEffect.addedConditions_source.length;
			effectsToSource = new ActorConditionEffect[i];
			while (i-- > 0) {
				effectsToSource[i] = useEffect.addedConditions_source[i].clone();
			}
		}
		if (useEffect.addedConditions_target != null) {
			int i = useEffect.addedConditions_target.length;
			effectsToTarget = new ActorConditionEffect[i];
			while (i-- > 0) {
				effectsToTarget[i] = useEffect.addedConditions_target[i].clone();
			}
		}
	}
	
	public ItemTraits_OnUse toUseEffect() {
		ConstRange hpBoost = null;
		if (hpMax != 0 || hpMin != 0) {
			hpBoost = new ConstRange(hpMax, hpMin);
		}
		ConstRange apBoost = null;
		if (apMax != 0 && apMin != 0) {
			apBoost = new ConstRange(apMax, apMin);
		}
		StatsModifierTraits changedStats = null;
		if (hpBoost != null || apBoost != null) {
			changedStats = new StatsModifierTraits(visualEffectID, hpBoost, apBoost);
		}
		return new ItemTraits_OnUse(changedStats, effectsToSource, effectsToTarget);
	}
	
	
	public ItemReward(ItemTraits_OnEquip equipEffect) {
		AbilityModifierTraits statsEffect = equipEffect.stats;
		if (statsEffect != null) {
			this.increaseAttackChance = statsEffect.increaseAttackChance;
			this.increaseAttackCost = statsEffect.increaseAttackCost;
			this.increaseBlockChance = statsEffect.increaseBlockChance;
			this.increaseCriticalSkill = statsEffect.increaseCriticalSkill;
			this.increaseDamageResistance = statsEffect.increaseDamageResistance;
			this.increaseMaxAP = statsEffect.increaseMaxAP;
			this.increaseMaxHP = statsEffect.increaseMaxHP;
			this.increaseMinDamage = statsEffect.increaseMinDamage;
			this.increaseMaxDamage = statsEffect.increaseMaxDamage;
			this.increaseMoveCost = statsEffect.increaseMoveCost;
			this.increaseReequipCost = statsEffect.increaseReequipCost;
			this.increaseUseItemCost = statsEffect.increaseUseItemCost;
			this.setCriticalMultiplier = statsEffect.setCriticalMultiplier;
		}
		if (equipEffect.addedConditions != null) {
			int i = equipEffect.addedConditions.length;
			effectsToSource = new ActorConditionEffect[i];
			while (i-- > 0) {
				effectsToSource[i] = equipEffect.addedConditions[i].clone();
			}
		}
	}
	
	public ItemTraits_OnEquip toEquipEffect() {
		return new ItemTraits_OnEquip(
				new AbilityModifierTraits(increaseMaxHP, 
						increaseMaxAP, 
						increaseMoveCost, 
						increaseUseItemCost, 
						increaseReequipCost, 
						increaseAttackCost, 
						increaseAttackChance, 
						increaseBlockChance, 
						increaseMinDamage, 
						increaseMaxDamage, 
						increaseCriticalSkill, 
						setCriticalMultiplier, 
						increaseDamageResistance), 
					effectsToSource);
	}
	
}
