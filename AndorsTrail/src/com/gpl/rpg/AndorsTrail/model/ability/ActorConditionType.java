package com.gpl.rpg.AndorsTrail.model.ability;

import com.gpl.rpg.AndorsTrail.model.ability.traits.AbilityModifierTraits;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;

public final class ActorConditionType {
	public static final int STACKINGTYPE_ONLE_ONE = 0;
	public static final int STACKINGTYPE_ALLOW_MULTIPLE = 1;
	
	public final String conditionTypeID;
	public final String name;
	public final int iconID;
	public final boolean isStacking;
	public final StatsModifierTraits statsEffect_everyRound;
	public final StatsModifierTraits statsEffect_everyFullRound;
	public final AbilityModifierTraits abilityEffect;
	
	public ActorConditionType(
			String conditionTypeID, 
			String name, 
			int iconID, 
			boolean isStacking, 
			StatsModifierTraits statsEffect_everyRound, 
			StatsModifierTraits statsEffect_everyFullRound,
			AbilityModifierTraits abilityEffect) {
		this.conditionTypeID = conditionTypeID;
		this.name = name;
		this.iconID = iconID;
		this.isStacking = isStacking;
		this.statsEffect_everyRound = statsEffect_everyRound;
		this.statsEffect_everyFullRound = statsEffect_everyFullRound;
		this.abilityEffect = abilityEffect;
	}
}
