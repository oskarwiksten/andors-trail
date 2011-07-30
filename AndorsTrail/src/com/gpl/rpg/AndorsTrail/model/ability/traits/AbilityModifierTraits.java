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
	
	public int calculateCost(boolean isWeapon) {
		final int costCombat = combatProficiency == null ? 0 : combatProficiency.calculateCost(isWeapon);
		final int costMaxHP = (int) (30*Math.pow(Math.max(0,maxHPBoost), 1.2) + 70*maxHPBoost);
		final int costMaxAP = (int) (50*Math.pow(Math.max(0,maxAPBoost), 3) + 750*maxAPBoost);
		final int costMovement = (int) (10*Math.pow(Math.max(0,moveCostPenalty), 2) + 350*moveCostPenalty);
		return costCombat + costMaxHP + costMaxAP + costMovement;
	}
}
