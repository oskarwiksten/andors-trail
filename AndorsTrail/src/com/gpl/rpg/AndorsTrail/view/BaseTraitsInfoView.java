package com.gpl.rpg.AndorsTrail.view;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.actor.Player.PlayerBaseTraits;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public final class BaseTraitsInfoView extends TraitsInfoView {
	private final TextView basetraitsinfo_max_hp;
	private final TextView basetraitsinfo_max_ap;
	
	public BaseTraitsInfoView(Context context, AttributeSet attr) {
		super(context, attr, R.layout.basetraitsinfoview);
        
		basetraitsinfo_max_hp = (TextView) findViewById(R.id.basetraitsinfo_max_hp);
		basetraitsinfo_max_ap = (TextView) findViewById(R.id.basetraitsinfo_max_ap);
    }

	public void update(PlayerBaseTraits traits) {
		super.update(
			traits.attackCost
			,traits.attackChance
			,traits.damagePotential
			,traits.criticalSkill
			,traits.criticalMultiplier
			,traits.blockChance
			,traits.damageResistance);
		basetraitsinfo_max_hp.setText(Integer.toString(traits.maxHP));
		basetraitsinfo_max_ap.setText(Integer.toString(traits.maxAP));
	}
}
