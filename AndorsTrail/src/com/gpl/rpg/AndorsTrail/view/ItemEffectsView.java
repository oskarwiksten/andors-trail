package com.gpl.rpg.AndorsTrail.view;

import java.util.Arrays;
import java.util.Collection;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnEquip;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class ItemEffectsView extends LinearLayout {
	private final TraitsInfoView itemeffect_onequip_traits;
	private final TextView itemeffect_onequip_title;
	private final TextView itemeffect_onequip_change_maxap;
	private final TextView itemeffect_onequip_change_maxhp;
	private final TextView itemeffect_onequip_change_movecost;
	private final ActorConditionEffectList itemeffect_onequip_conditions;
	private final ItemEffectsView_OnUse itemeffect_onuse;
	private final ItemEffectsView_OnUse itemeffect_onhit;
	private final ItemEffectsView_OnUse itemeffect_onkill;
	private final TextView itemeffect_onuse_title;
	private final TextView itemeffect_onhit_title;
	private final TextView itemeffect_onkill_title;
	
	public ItemEffectsView(Context context, AttributeSet attr) {
		super(context, attr);
        setFocusable(false);
        setOrientation(LinearLayout.VERTICAL);
        inflate(context, R.layout.itemeffectview, this);
        
        itemeffect_onequip_title = (TextView) findViewById(R.id.itemeffect_onequip_title);
        itemeffect_onequip_traits = (TraitsInfoView) findViewById(R.id.itemeffect_onequip_traits);
        
        itemeffect_onequip_change_maxap = (TextView) findViewById(R.id.itemeffect_onequip_change_maxap);
        itemeffect_onequip_change_maxhp = (TextView) findViewById(R.id.itemeffect_onequip_change_maxhp);
        itemeffect_onequip_change_movecost = (TextView) findViewById(R.id.itemeffect_onequip_change_movecost);
        itemeffect_onequip_conditions = (ActorConditionEffectList) findViewById(R.id.itemeffect_onequip_conditions);
        
        itemeffect_onuse = (ItemEffectsView_OnUse) findViewById(R.id.itemeffect_onuse);
        itemeffect_onhit = (ItemEffectsView_OnUse) findViewById(R.id.itemeffect_onhit);
        itemeffect_onkill = (ItemEffectsView_OnUse) findViewById(R.id.itemeffect_onkill);
        itemeffect_onuse_title = (TextView) findViewById(R.id.itemeffect_onuse_title);
        itemeffect_onhit_title = (TextView) findViewById(R.id.itemeffect_onhit_title);
        itemeffect_onkill_title = (TextView) findViewById(R.id.itemeffect_onkill_title);
    }

	public void update(
			ItemTraits_OnEquip effects_equip, 
			Collection<ItemTraits_OnUse> effects_use,
			Collection<ItemTraits_OnUse> effects_hit,
			Collection<ItemTraits_OnUse> effects_kill
			) {
		
		final Resources res = getResources();
		
		if (effects_equip != null) {
			itemeffect_onequip_title.setVisibility(View.VISIBLE);
		} else {
			itemeffect_onequip_title.setVisibility(View.GONE);
		}
		
		if (effects_equip != null && effects_equip.combatProficiency != null) {
			itemeffect_onequip_traits.update(effects_equip.combatProficiency);
			itemeffect_onequip_traits.setVisibility(View.VISIBLE);
		} else {
			itemeffect_onequip_traits.setVisibility(View.GONE);
		}
		
		if (effects_equip != null && effects_equip.maxAPBoost != 0) {
			final int label = effects_equip.maxAPBoost > 0 ? R.string.iteminfo_effect_increase_max_ap : R.string.iteminfo_effect_decrease_max_ap;
			itemeffect_onequip_change_maxap.setText(res.getString(label, Math.abs(effects_equip.maxAPBoost)));
			itemeffect_onequip_change_maxap.setVisibility(View.VISIBLE);
		} else {
			itemeffect_onequip_change_maxap.setVisibility(View.GONE);
		}
		
		if (effects_equip != null && effects_equip.maxHPBoost != 0) {
			final int label = effects_equip.maxHPBoost > 0 ? R.string.iteminfo_effect_increase_max_hp : R.string.iteminfo_effect_decrease_max_hp;
			itemeffect_onequip_change_maxhp.setText(res.getString(label, Math.abs(effects_equip.maxHPBoost)));
			itemeffect_onequip_change_maxhp.setVisibility(View.VISIBLE);
		} else {
			itemeffect_onequip_change_maxhp.setVisibility(View.GONE);
		}
		
		if (effects_equip != null && effects_equip.moveCostPenalty != 0) {
			final int label = effects_equip.moveCostPenalty > 0 ? R.string.iteminfo_effect_increase_movecost : R.string.iteminfo_effect_decrease_movecost;
			itemeffect_onequip_change_movecost.setText(res.getString(label, Math.abs(effects_equip.moveCostPenalty)));
			itemeffect_onequip_change_movecost.setVisibility(View.VISIBLE);
		} else {
			itemeffect_onequip_change_movecost.setVisibility(View.GONE);
		}

		if (effects_equip != null && effects_equip.addedConditions != null) {
			itemeffect_onequip_conditions.update(Arrays.asList(effects_equip.addedConditions));
		} else {
			itemeffect_onequip_conditions.update(null);
		}
		
		itemeffect_onuse.update(effects_use);
		if (effects_use != null) {
			itemeffect_onuse_title.setVisibility(View.VISIBLE);
		} else {
			itemeffect_onuse_title.setVisibility(View.GONE);
		}
		
		itemeffect_onhit.update(effects_hit);
		if (effects_hit != null) {
			itemeffect_onhit_title.setVisibility(View.VISIBLE);
		} else {
			itemeffect_onhit_title.setVisibility(View.GONE);
		}
		
		itemeffect_onkill.update(effects_kill);
		if (effects_kill != null) {
			itemeffect_onkill_title.setVisibility(View.VISIBLE);
		} else {
			itemeffect_onkill_title.setVisibility(View.GONE);
		}
	}
}
