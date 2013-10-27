package com.gpl.rpg.AndorsTrail.model.ability.traits;

import com.gpl.rpg.AndorsTrail.resource.VisualEffectCollection;
import com.gpl.rpg.AndorsTrail.util.ConstRange;

public final class StatsModifierTraits {
	public final VisualEffectCollection.VisualEffectID visualEffectID;
	public final ConstRange currentHPBoost;
	public final ConstRange currentAPBoost;

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
		final float averageHPBoost = currentHPBoost == null ? 0 : currentHPBoost.averagef();
		if (averageHPBoost == 0) return 0;

		final int costBoostHP = (int) (0.1*Math.signum(averageHPBoost)*Math.pow(Math.abs(averageHPBoost), 2) + 3*averageHPBoost);
		return costBoostHP;
	}

	public int calculateHitCost() {
		final float averageHPBoost = currentHPBoost == null ? 0 : currentHPBoost.averagef();
		final float averageAPBoost = currentAPBoost == null ? 0 : currentAPBoost.averagef();
		if (averageHPBoost == 0 && averageAPBoost == 0) return 0;

		final int costBoostHP = (int)(2770*Math.pow(Math.max(0,averageHPBoost), 2.5) + 450*averageHPBoost);
		final int costBoostAP = (int)(3100*Math.pow(Math.max(0,averageAPBoost), 2.5) + 300*averageAPBoost);
		return costBoostHP + costBoostAP;
	}

	public int calculateKillCost() {
		final float averageHPBoost = currentHPBoost == null ? 0 : currentHPBoost.averagef();
		final float averageAPBoost = currentAPBoost == null ? 0 : currentAPBoost.averagef();
		if (averageHPBoost == 0 && averageAPBoost == 0) return 0;

		final int costBoostHP = (int)(923*Math.pow(Math.max(0,averageHPBoost), 2.5) + 450*averageHPBoost);
		final int costBoostAP = (int)(1033*Math.pow(Math.max(0,averageAPBoost), 2.5) + 300*averageAPBoost);
		return costBoostHP + costBoostAP;
	}
}
