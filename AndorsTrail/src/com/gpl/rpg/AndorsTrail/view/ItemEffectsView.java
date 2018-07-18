package com.gpl.rpg.AndorsTrail.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnEquip;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;

import java.util.Arrays;
import java.util.Collection;

public final class ItemEffectsView extends LinearLayout {
	private final TextView itemeffect_onequip_title;
	private final AbilityModifierInfoView itemeffect_onequip_abilitymodifierinfo;
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
		itemeffect_onequip_abilitymodifierinfo = (AbilityModifierInfoView) findViewById(R.id.itemeffect_onequip_abilitymodifierinfo);
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
			Collection<ItemTraits_OnUse> effects_kill,
			boolean isWeapon
			) {

		itemeffect_onequip_title.setVisibility(View.GONE);
		itemeffect_onequip_abilitymodifierinfo.setVisibility(View.GONE);
		itemeffect_onequip_conditions.update(null);
		if (effects_equip != null) {
			itemeffect_onequip_title.setVisibility(View.VISIBLE);

			if (effects_equip.stats != null) {
				itemeffect_onequip_abilitymodifierinfo.update(effects_equip.stats, isWeapon);
				itemeffect_onequip_abilitymodifierinfo.setVisibility(View.VISIBLE);
			}

			if (effects_equip.addedConditions != null) {
				itemeffect_onequip_conditions.update(Arrays.asList(effects_equip.addedConditions));
			}
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
