package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;
import com.gpl.rpg.AndorsTrail.view.AbilityModifierInfoView;
import com.gpl.rpg.AndorsTrail.view.ItemEffectsView_OnUse;

public final class ActorConditionInfoActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
		if (!app.isInitialized()) { finish(); return; }
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		final WorldContext world = app.getWorld();

		String conditionTypeID = getIntent().getData().getLastPathSegment();
		ActorConditionType conditionType = world.actorConditionsTypes.getActorConditionType(conditionTypeID);

		setContentView(R.layout.actorconditioninfo);


		TextView tv = (TextView) findViewById(R.id.actorconditioninfo_title);
		tv.setText(conditionType.name);
		world.tileManager.setImageViewTile(getResources(), tv, conditionType);

		Button b = (Button) findViewById(R.id.actorconditioninfo_close);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ActorConditionInfoActivity.this.finish();
			}
		});

		update(conditionType);
	}

	private void update(ActorConditionType conditionType) {
		final Resources res = getResources();
		LinearLayout lv;
		TextView tv;

		tv = (TextView) findViewById(R.id.actorconditioninfo_category);
		final String categoryName = res.getString(getConditionCategoryNameResId(conditionType.conditionCategory));
		tv.setText(res.getString(R.string.actorconditioninfo_category, categoryName));

		((AbilityModifierInfoView) findViewById(R.id.actorconditioninfo_constant_effect_abilitymodifierinfo)).update(conditionType.abilityEffect, false);

		tv = (TextView) findViewById(R.id.actorconditioninfo_constant_effect_title);
		if (conditionType.abilityEffect != null) {
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setVisibility(View.GONE);
		}

		tv = (TextView) findViewById(R.id.actorconditioninfo_everyround_title);
		if (conditionType.statsEffect_everyRound != null) {
			tv.setVisibility(View.VISIBLE);
			lv = (LinearLayout) findViewById(R.id.actorconditioninfo_everyround_list);
			ItemEffectsView_OnUse.describeStatsModifierTraits(conditionType.statsEffect_everyRound, this, res, lv);
		} else {
			tv.setVisibility(View.GONE);
		}

		tv = (TextView) findViewById(R.id.actorconditioninfo_everyfullround_title);
		if (conditionType.statsEffect_everyFullRound != null) {
			tv.setVisibility(View.VISIBLE);
			lv = (LinearLayout) findViewById(R.id.actorconditioninfo_everyfullround_list);
			ItemEffectsView_OnUse.describeStatsModifierTraits(conditionType.statsEffect_everyFullRound, this, res, lv);
		} else {
			tv.setVisibility(View.GONE);
		}
	}

	private int getConditionCategoryNameResId(ActorConditionType.ConditionCategory conditionCategory) {
		switch (conditionCategory) {
			case physical: return R.string.actorcondition_categories_physical;
			case mental: return R.string.actorcondition_categories_mental;
			case blood: return R.string.actorcondition_categories_blood;
			case spiritual: return R.string.actorcondition_categories_spiritual;
		}
		return 0;
	}
}
