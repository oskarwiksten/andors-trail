package com.gpl.rpg.AndorsTrail.model.ability.traits;

import com.gpl.rpg.AndorsTrail.util.ConstRange;

public class StatsModifierTraits {
	public static final int VISUAL_EFFECT_NONE = -1;
	public final int visualEffectID;
	public final ConstRange currentHPBoost;
	public final ConstRange currentAPBoost;
	
	public StatsModifierTraits(ConstRange currentHPBoost, ConstRange currentAPBoost) {
		this.visualEffectID = VISUAL_EFFECT_NONE;
		this.currentHPBoost = currentHPBoost;
		this.currentAPBoost = currentAPBoost;
	}
	public StatsModifierTraits(int visualEffectID, ConstRange currentHPBoost, ConstRange currentAPBoost) {
		this.visualEffectID = visualEffectID;
		this.currentHPBoost = currentHPBoost;
		this.currentAPBoost = currentAPBoost;
	}
	
	public boolean hasVisualEffect() {
		return visualEffectID != VISUAL_EFFECT_NONE;
	}
}
