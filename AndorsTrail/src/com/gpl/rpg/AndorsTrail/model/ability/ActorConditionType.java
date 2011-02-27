package com.gpl.rpg.AndorsTrail.model.ability;

import com.gpl.rpg.AndorsTrail.model.ability.traits.AbilityModifierTraits;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;

public final class ActorConditionType {
	public final String conditionTypeID;
	public final String name;
	public final int iconID;
	public final StatsModifierTraits statsEffect;
	public final AbilityModifierTraits abilityEffect;
	
	public ActorConditionType(String conditionTypeID, String name, int iconID, StatsModifierTraits statsEffect, AbilityModifierTraits abilityEffect) {
		this.conditionTypeID = conditionTypeID;
		this.name = name;
		this.iconID = iconID;
		this.statsEffect = statsEffect;
		this.abilityEffect = abilityEffect;
	}
}
