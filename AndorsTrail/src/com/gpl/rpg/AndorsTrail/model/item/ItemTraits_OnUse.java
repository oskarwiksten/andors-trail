package com.gpl.rpg.AndorsTrail.model.item;

import java.util.ArrayList;
import java.util.Collection;

import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;
import com.gpl.rpg.AndorsTrail.util.ConstRange;

public final class ItemTraits_OnUse extends StatsModifierTraits {
	public final ArrayList<ActorConditionEffect> addedConditions_source = new ArrayList<ActorConditionEffect>();
	public final ArrayList<ActorConditionEffect> addedConditions_target = new ArrayList<ActorConditionEffect>();
	
	public ItemTraits_OnUse(ConstRange currentHPBoost, ConstRange currentAPBoost, Collection<ActorConditionEffect> addedConditions_source, Collection<ActorConditionEffect> addedConditions_target) {
		super(currentHPBoost, currentAPBoost);
		if (addedConditions_source != null) this.addedConditions_source.addAll(addedConditions_source);
		if (addedConditions_target != null) this.addedConditions_target.addAll(addedConditions_target);
	}
}
