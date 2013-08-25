package com.gpl.rpg.AndorsTrail.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;

import java.util.Collection;

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
		final Resources res = getResources();
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		for (ActorConditionEffect e : effects) {
			String msg;
			final ActorConditionType conditionType = e.conditionType;
			if (e.isRemovalEffect()) {
				msg = res.getString(R.string.actorcondition_info_removes_all, conditionType.name);
			} else {
				msg = describeEffect(res, e);
			}
			TextView tv = new TextView(context);
			tv.setLayoutParams(layoutParams);

			SpannableString content = new SpannableString(msg);
			content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
			tv.setText(content);
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Dialogs.showActorConditionInfo(context, conditionType);
				}
			});
			this.addView(tv, layoutParams);
		}
	}

	private static String describeEffect(Resources res, ActorConditionEffect effect) {
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
