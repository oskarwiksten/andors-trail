package com.gpl.rpg.AndorsTrail.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public final class ItemEffectsView_OnUse extends LinearLayout {
	private final TableLayout itemeffect_onuse_table;
	private final ActorConditionEffectList itemeffect_onuse_conditions_source;
	private final ActorConditionEffectList itemeffect_onuse_conditions_target;
	private final TextView itemeffect_onuse_conditions_source_title;
	private final TextView itemeffect_onuse_conditions_target_title;
	
	public ItemEffectsView_OnUse(Context context, AttributeSet attr) {
		super(context, attr);
        setFocusable(false);
        setOrientation(LinearLayout.VERTICAL);
        inflate(context, R.layout.itemeffectview_onuse, this);
        
        itemeffect_onuse_table = (TableLayout) findViewById(R.id.itemeffect_onuse_table);
        itemeffect_onuse_conditions_source_title = (TextView) findViewById(R.id.itemeffect_onuse_conditions_source_title);
        itemeffect_onuse_conditions_target_title = (TextView) findViewById(R.id.itemeffect_onuse_conditions_target_title);
        itemeffect_onuse_conditions_source = (ActorConditionEffectList) findViewById(R.id.itemeffect_onuse_conditions_source);
        itemeffect_onuse_conditions_target = (ActorConditionEffectList) findViewById(R.id.itemeffect_onuse_conditions_target);
    }

	public void update(Collection<ItemTraits_OnUse> effects) {
		ArrayList<ActorConditionEffect> sourceEffects = new ArrayList<ActorConditionEffect>();
		ArrayList<ActorConditionEffect> targetEffects = new ArrayList<ActorConditionEffect>();
		
		itemeffect_onuse_table.removeAllViews();
		if (effects != null) {
			final Context context = getContext();
			for (ItemTraits_OnUse t : effects) {
				if (t.addedConditions_source != null) sourceEffects.addAll(Arrays.asList(t.addedConditions_source));
				if (t.addedConditions_target != null) targetEffects.addAll(Arrays.asList(t.addedConditions_target));
				
				if (t.currentAPBoost != null) {
					addTableRow(context, itemeffect_onuse_table, "AP", t.currentAPBoost.toMinMaxString());
				}
				if (t.currentHPBoost != null) {
					addTableRow(context, itemeffect_onuse_table, "HP", t.currentHPBoost.toMinMaxString());
				}
			}
		}
		itemeffect_onuse_conditions_source.update(sourceEffects);
		itemeffect_onuse_conditions_target.update(targetEffects);
		if (sourceEffects.isEmpty()) {
			itemeffect_onuse_conditions_source_title.setVisibility(View.GONE);
		} else {
			itemeffect_onuse_conditions_source_title.setVisibility(View.VISIBLE);
		}
		if (targetEffects.isEmpty()) {
			itemeffect_onuse_conditions_target_title.setVisibility(View.GONE);
		} else {
			itemeffect_onuse_conditions_target_title.setVisibility(View.VISIBLE);
		}
	}
	private static void addTableRow(Context context, TableLayout table, String label, String value) {
		TableRow tr = new TableRow(context);
		TextView tv = new TextView(context);
		tv.setText(label);
		tr.addView(tv);
		tv = new TextView(context);
		tv.setText(value);
		tr.addView(tv);
		table.addView(tr);
	}
}
