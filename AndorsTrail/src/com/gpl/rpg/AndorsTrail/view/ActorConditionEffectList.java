package com.gpl.rpg.AndorsTrail.view;

import java.util.Collection;

import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class ActorConditionEffectList extends LinearLayout {
	
	public ActorConditionEffectList(Context context, AttributeSet attr) {
		super(context, attr);
        setFocusable(false);
        setOrientation(LinearLayout.VERTICAL);
    }

	public void update(Collection<ActorConditionEffect> effects) {
		removeAllViews();
		if (effects == null) return;
		
		final Context context = getContext();
		for (ActorConditionEffect e : effects) {
			String msg;
			if (e.isRemovalEffect()) {
				msg = "Remove all " + e.conditionType.name;
			} else {
				msg = e.describeEffect();
			}
			TextView tv = new TextView(context);
			tv.setText(msg);
			this.addView(tv);
		}
	}
}
