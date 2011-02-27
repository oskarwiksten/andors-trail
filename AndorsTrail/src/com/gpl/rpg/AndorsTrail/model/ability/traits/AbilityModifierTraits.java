package com.gpl.rpg.AndorsTrail.model.ability.traits;

import com.gpl.rpg.AndorsTrail.model.CombatTraits;

public class AbilityModifierTraits {
	public final int maxHPBoost;
	public final int maxAPBoost;
	public final int moveCostPenalty;
	public final CombatTraits combatProficiency;
	
	public AbilityModifierTraits(int maxHPBoost, int maxAPBoost, int moveCostPenalty, CombatTraits combatProficiency) {
		this.maxHPBoost = maxHPBoost;
		this.maxAPBoost = maxAPBoost;
		this.moveCostPenalty = moveCostPenalty;
		this.combatProficiency = combatProficiency;
	}
}
