package com.gpl.rpg.AndorsTrail.view;

import java.util.Collection;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;

import android.content.Context;
import android.content.res.Resources;
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
		
		final Resources res = getResources();
		final Context context = getContext();
		for (ActorConditionEffect e : effects) {
			String msg;
			if (e.isRemovalEffect()) {
				msg = res.getString(R.string.actorcondition_info_removes_all, e.conditionType.name);
			} else {
				msg = describeEffect(res, e); 
			}
			TextView tv = new TextView(context);
			tv.setText(msg);
			this.addView(tv);
		}
	}
	
	public static String describeEffect(Resources res, ActorConditionEffect effect) {
		String msg = describeEffect(res, effect.conditionType, effect.magnitude, effect.duration);
		if (effect.chance.isMax()) return msg;
		
		return res.getString(R.string.iteminfo_effect_chance_of, effect.chance.toPercentString(), msg);
	}
	
	public static String describeEffect(Resources res, ActorConditionType conditionType, int magnitude, int duration) {
		StringBuilder sb = new StringBuilder(conditionType.name);
		if (magnitude > 1) {
			sb.append(" x");
			sb.append(magnitude); 
		}
		if (ActorCondition.isTemporaryEffect(duration)) {
			sb.append(' ');
			sb.append(res.getString(R.string.iteminfo_effect_duration, duration));
		}
		return sb.toString();
	}
}
