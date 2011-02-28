package com.gpl.rpg.AndorsTrail.model.item;

import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;
import com.gpl.rpg.AndorsTrail.util.ConstRange;

public final class ItemTraits_OnUse extends StatsModifierTraits {
	public final ActorConditionEffect[] addedConditions_source;
	public final ActorConditionEffect[] addedConditions_target;
	
	public ItemTraits_OnUse(ConstRange currentHPBoost, ConstRange currentAPBoost, ActorConditionEffect[] addedConditions_source, ActorConditionEffect[] addedConditions_target) {
		super(currentHPBoost, currentAPBoost);
		this.addedConditions_source = addedConditions_source;
		this.addedConditions_target = addedConditions_target;
	}
}
