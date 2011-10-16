package com.gpl.rpg.AndorsTrail.view;

import java.util.Collection;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class ActorConditionList extends LinearLayout {
	
	private final WorldContext world;

	public ActorConditionList(Context context, AttributeSet attr) {
		super(context, attr);
        setFocusable(false);
        setOrientation(LinearLayout.VERTICAL);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
        this.world = app.world;
    }

	public void update(Collection<ActorCondition> conditions) {
		removeAllViews();
		if (conditions == null) return;
		
		final Context context = getContext();
		final Resources res = getResources();
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		for (ActorCondition c : conditions) {
			View v = View.inflate(context, R.layout.inventoryitemview, null);
			ImageView iw = (ImageView) v.findViewById(R.id.inv_image);
			world.tileManager.setImageViewTile(iw, c.conditionType);
			SpannableString content = new SpannableString(describeEffect(res, c));
			content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
			((TextView) v.findViewById(R.id.inv_text)).setText(content);
			final ActorConditionType conditionType = c.conditionType;
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Dialogs.showActorConditionInfo(context, conditionType);
				}
			});
			this.addView(v, layoutParams);
		}
	}
    
    private static String describeEffect(Resources res, ActorCondition c) {
    	return ActorConditionEffectList.describeEffect(res, c.conditionType, c.magnitude, c.duration);
	}
}
