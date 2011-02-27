package com.gpl.rpg.AndorsTrail.model.ability.traits;

import com.gpl.rpg.AndorsTrail.util.ConstRange;

public class StatsModifierTraits {
	public final ConstRange currentHPBoost;
	public final ConstRange currentAPBoost;
	
	public StatsModifierTraits(ConstRange currentHPBoost, ConstRange currentAPBoost) {
		this.currentHPBoost = currentHPBoost;
		this.currentAPBoost = currentAPBoost;
	}
}
