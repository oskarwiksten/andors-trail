package com.gpl.rpg.AndorsTrail.model.item;

import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;

public final class ItemTraits_OnUse {
	public final StatsModifierTraits changedStats;
	public final ActorConditionEffect[] addedConditions_source;
	public final ActorConditionEffect[] addedConditions_target;

	public ItemTraits_OnUse(
			StatsModifierTraits changedStats
			, ActorConditionEffect[] addedConditions_source
			, ActorConditionEffect[] addedConditions_target
	) {
		this.changedStats = changedStats;
		this.addedConditions_source = addedConditions_source;
		this.addedConditions_target = addedConditions_target;
	}


	public int calculateUseCost() {
		final int costStats = changedStats == null ? 0 : changedStats.calculateUseCost();
		return costStats;
	}

	public int calculateHitCost() {
		final int costStats = changedStats == null ? 0 : changedStats.calculateHitCost();
		return costStats;
	}

	public int calculateKillCost() {
		final int costStats = changedStats == null ? 0 : changedStats.calculateKillCost();
		return costStats;
	}
	
	public ItemTraits_OnUse clone() {
		int i = addedConditions_source.length;
		ActorConditionEffect[] addedToSource = new ActorConditionEffect[i];
		while (i-- > 0) {
			addedToSource[i] = addedConditions_source[i].clone();
		}
		i = addedConditions_target.length;
		ActorConditionEffect[] addedToTarget = new ActorConditionEffect[i];
		while (i-- > 0) {
			addedToTarget[i] = addedConditions_target[i].clone();
		}
		return new ItemTraits_OnUse(changedStats.clone(), addedToSource, addedToTarget);
	}
}
