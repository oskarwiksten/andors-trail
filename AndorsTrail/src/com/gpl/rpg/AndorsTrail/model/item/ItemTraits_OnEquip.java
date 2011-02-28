package com.gpl.rpg.AndorsTrail.model.item;

import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.traits.AbilityModifierTraits;

public final class ItemTraits_OnEquip extends AbilityModifierTraits {
	public final ActorConditionEffect[] addedConditions;
	
	public ItemTraits_OnEquip(int maxHPBoost, int maxAPBoost, int moveCostPenalty, CombatTraits combatProficiency, ActorConditionEffect[] addedConditions) {
		super(maxHPBoost, maxAPBoost, moveCostPenalty, combatProficiency);
		this.addedConditions = addedConditions;
	}
}
