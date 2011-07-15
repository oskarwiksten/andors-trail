package com.gpl.rpg.AndorsTrail.view;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.ability.traits.AbilityModifierTraits;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class AbilityModifierInfoView extends LinearLayout {
	private final TraitsInfoView abilitymodifierinfo_traits;
	private final TextView abilitymodifierinfo_change_maxap;
	private final TextView abilitymodifierinfo_change_maxhp;
	private final TextView abilitymodifierinfo_change_movecost;
	
	public AbilityModifierInfoView(Context context, AttributeSet attr) {
		super(context, attr);
        setFocusable(false);
        setOrientation(LinearLayout.VERTICAL);
        inflate(context, R.layout.abilitymodifierview, this);
        
        abilitymodifierinfo_traits = (TraitsInfoView) findViewById(R.id.abilitymodifierinfo_traits);
        abilitymodifierinfo_change_maxap = (TextView) findViewById(R.id.abilitymodifierinfo_change_maxap);
        abilitymodifierinfo_change_maxhp = (TextView) findViewById(R.id.abilitymodifierinfo_change_maxhp);
        abilitymodifierinfo_change_movecost = (TextView) findViewById(R.id.abilitymodifierinfo_change_movecost);
    }

	public void update(AbilityModifierTraits traits) {
		final Resources res = getResources();
		
		if (traits != null && traits.combatProficiency != null) {
			abilitymodifierinfo_traits.update(traits.combatProficiency);
			abilitymodifierinfo_traits.setVisibility(View.VISIBLE);
		} else {
			abilitymodifierinfo_traits.setVisibility(View.GONE);
		}
		
		if (traits != null && traits.maxAPBoost != 0) {
			final int label = traits.maxAPBoost > 0 ? R.string.iteminfo_effect_increase_max_ap : R.string.iteminfo_effect_decrease_max_ap;
			abilitymodifierinfo_change_maxap.setText(res.getString(label, Math.abs(traits.maxAPBoost)));
			abilitymodifierinfo_change_maxap.setVisibility(View.VISIBLE);
		} else {
			abilitymodifierinfo_change_maxap.setVisibility(View.GONE);
		}
		
		if (traits != null && traits.maxHPBoost != 0) {
			final int label = traits.maxHPBoost > 0 ? R.string.iteminfo_effect_increase_max_hp : R.string.iteminfo_effect_decrease_max_hp;
			abilitymodifierinfo_change_maxhp.setText(res.getString(label, Math.abs(traits.maxHPBoost)));
			abilitymodifierinfo_change_maxhp.setVisibility(View.VISIBLE);
		} else {
			abilitymodifierinfo_change_maxhp.setVisibility(View.GONE);
		}
		
		if (traits != null && traits.moveCostPenalty != 0) {
			final int label = traits.moveCostPenalty > 0 ? R.string.iteminfo_effect_increase_movecost : R.string.iteminfo_effect_decrease_movecost;
			abilitymodifierinfo_change_movecost.setText(res.getString(label, Math.abs(traits.moveCostPenalty)));
			abilitymodifierinfo_change_movecost.setVisibility(View.VISIBLE);
		} else {
			abilitymodifierinfo_change_movecost.setVisibility(View.GONE);
		}
	}
}
