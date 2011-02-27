package com.gpl.rpg.AndorsTrail.model.item;

import java.util.ArrayList;
import java.util.Collection;

import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.traits.AbilityModifierTraits;

public final class ItemTraits_OnEquip extends AbilityModifierTraits {
	public final ArrayList<ActorConditionEffect> addedConditions = new ArrayList<ActorConditionEffect>();
	
	public ItemTraits_OnEquip(int maxHPBoost, int maxAPBoost, int moveCostPenalty, CombatTraits combatProficiency, Collection<ActorConditionEffect> addedConditions) {
		super(maxHPBoost, maxAPBoost, moveCostPenalty, combatProficiency);
		if (addedConditions != null) this.addedConditions.addAll(addedConditions);
	}
}
