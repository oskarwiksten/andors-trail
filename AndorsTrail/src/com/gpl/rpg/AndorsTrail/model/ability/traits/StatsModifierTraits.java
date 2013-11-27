package com.gpl.rpg.AndorsTrail.model.ability.traits;

import com.gpl.rpg.AndorsTrail.resource.VisualEffectCollection;
import com.gpl.rpg.AndorsTrail.util.ConstRange;

public final class StatsModifierTraits {
	public final VisualEffectCollection.VisualEffectID visualEffectID;
	public final ConstRange currentHPBoost;
	public final ConstRange currentAPBoost;
	
	private int calculateViolenceCost(final int hpValue, final int apValue) {
		final float averageHPBoost = currentHPBoost == null ? 0 : currentHPBoost.averagef();
		final float averageAPBoost = currentAPBoost == null ? 0 : currentAPBoost.averagef();
		if (averageHPBoost == 0 && averageAPBoost == 0) return 0;

		final int costBoostHP = (int) (hpValue * Math.pow(Math.max(0, averageHPBoost), 2.5) + 450 * averageHPBoost);
		final int costBoostAP = (int) (apValue * Math.pow(Math.max(0, averageAPBoost), 2.5) + 300 * averageAPBoost);

		return costBoostHP + costBoostAP;
	}
	 
	public StatsModifierTraits(
			VisualEffectCollection.VisualEffectID visualEffectID
			, ConstRange currentHPBoost
			, ConstRange currentAPBoost
	) {
		this.visualEffectID = visualEffectID;
		this.currentHPBoost = currentHPBoost;
		this.currentAPBoost = currentAPBoost;
	}

	public int calculateUseCost() {
		if (currentHPBoost == null) {
			return (0);
		}

		final float averageHPBoost = currentHPBoost.averagef();
		final int costBoostHP = (int) ((((averageHPBoost < 0 ? -1 : +1)
				* averageHPBoost + 30) * averageHPBoost)) / 10;

		return costBoostHP;
	}

	public int calculateHitCost() {
		return(calculateViolenceCost(2770,3100));
	}

	public int calculateKillCost() {
		return(calculateViolenceCost(923,1033));
	}
}
