package com.gpl.rpg.AndorsTrail.activity;

import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.view.ActorConditionEffectList;
import com.gpl.rpg.AndorsTrail.view.BaseTraitsInfoView;
import com.gpl.rpg.AndorsTrail.view.ItemEffectsView;
import com.gpl.rpg.AndorsTrail.view.RangeBar;
import com.gpl.rpg.AndorsTrail.view.TraitsInfoView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class HeroinfoActivity_Stats extends Activity {
	private WorldContext world;
	private ViewContext view;

	private Player player;
	
	private Button levelUpButton;
    private TextView heroinfo_ap;
    private TextView heroinfo_movecost;
    private TraitsInfoView heroinfo_currenttraits;
    private ItemEffectsView heroinfo_itemeffects;
    private TextView heroinfo_currentconditions_title;
    private LinearLayout heroinfo_currentconditions;
    private TextView heroinfo_level;
    private TextView heroinfo_totalexperience;
    private RangeBar rangebar_hp;
    private RangeBar rangebar_exp;
    private BaseTraitsInfoView heroinfo_basetraits;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        this.world = app.world;
        this.view = app.currentView.get();
        this.player = world.model.player;
        
        setContentView(R.layout.heroinfo_stats);
        
        ImageView iv = (ImageView) findViewById(R.id.heroinfo_image);
        iv.setImageBitmap(world.tileStore.getBitmap(player.traits.iconID));
        
        ((TextView) findViewById(R.id.heroinfo_title)).setText(player.traits.name);
        heroinfo_ap = (TextView) findViewById(R.id.heroinfo_ap);
        heroinfo_movecost = (TextView) findViewById(R.id.heroinfo_movecost);
        heroinfo_currenttraits = (TraitsInfoView) findViewById(R.id.heroinfo_currenttraits);
        heroinfo_itemeffects = (ItemEffectsView) findViewById(R.id.heroinfo_itemeffects);
        heroinfo_currentconditions_title = (TextView) findViewById(R.id.heroinfo_currentconditions_title);
        heroinfo_currentconditions = (LinearLayout) findViewById(R.id.heroinfo_currentconditions);
        heroinfo_level = (TextView) findViewById(R.id.heroinfo_level);
        heroinfo_totalexperience = (TextView) findViewById(R.id.heroinfo_totalexperience);
                
        rangebar_hp = (RangeBar) findViewById(R.id.heroinfo_healthbar);
        rangebar_hp.init(R.drawable.ui_progress_health, R.string.status_hp);
        rangebar_exp = (RangeBar) findViewById(R.id.heroinfo_expbar);
        rangebar_exp.init(R.drawable.ui_progress_exp, R.string.status_exp);
        
        levelUpButton = (Button) findViewById(R.id.heroinfo_levelup);
        levelUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Dialogs.showLevelUp(HeroinfoActivity_Stats.this);
				// We disable the button temporarily, so that there is no possibility 
				//  of clicking it again before the levelup activity has started.
				// See issue:
				//  http://code.google.com/p/andors-trail/issues/detail?id=42
				levelUpButton.setEnabled(false);
			}
		});
        
        heroinfo_basetraits = (BaseTraitsInfoView) findViewById(R.id.heroinfo_basetraits);
    }

    @Override
	protected void onResume() {
    	super.onResume();
    	updateTraits();
        updateLevelup();
        updateConditions();
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ItemType itemType;
		switch (requestCode) {
		case MainActivity.INTENTREQUEST_ITEMINFO:
			if (resultCode != RESULT_OK) break;
			
			itemType = world.itemTypes.getItemType(data.getExtras().getInt("itemTypeID"));
			int actionType = data.getExtras().getInt("actionType");
			if (actionType == ItemInfoActivity.ITEMACTION_UNEQUIP) {
	        	view.itemController.unequipSlot(itemType, data.getExtras().getInt("inventorySlot"));
	        } else  if (actionType == ItemInfoActivity.ITEMACTION_EQUIP) {
	        	view.itemController.equipItem(itemType);
	        } else  if (actionType == ItemInfoActivity.ITEMACTION_USE) {
				view.itemController.useItem(itemType);	
			}
			break;
		case MainActivity.INTENTREQUEST_LEVELUP:
			break;
		}
	}

	private void updateLevelup() {
		levelUpButton.setEnabled(player.canLevelup());
    }

	private void updateTraits() {
		heroinfo_level.setText(Integer.toString(player.level));
		heroinfo_totalexperience.setText(Integer.toString(player.totalExperience));
		heroinfo_ap.setText(player.ap.toString());
        heroinfo_movecost.setText(Integer.toString(player.traits.moveCost));
        rangebar_hp.update(player.health);
        rangebar_exp.update(player.levelExperience);
        
        heroinfo_currenttraits.update(player.traits);
		ArrayList<ItemTraits_OnUse> effects_hit = new ArrayList<ItemTraits_OnUse>();
		ArrayList<ItemTraits_OnUse> effects_kill = new ArrayList<ItemTraits_OnUse>();
		for (int i = 0; i < Inventory.NUM_WORN_SLOTS; ++i) {
			ItemType type = player.inventory.wear[i];
			if (type == null) continue;
			if (type.effects_hit != null) effects_hit.add(type.effects_hit);
			if (type.effects_kill != null) effects_kill.add(type.effects_kill);
		}
		if (effects_hit.isEmpty()) effects_hit = null;
		if (effects_kill.isEmpty()) effects_kill = null;
		heroinfo_itemeffects.update(null, null, effects_hit, effects_kill);
		heroinfo_basetraits.update(player.traits);
    }

	private void updateConditions() {
		if (player.conditions.isEmpty()) {
			heroinfo_currentconditions_title.setVisibility(View.GONE);
			heroinfo_currentconditions.setVisibility(View.GONE);
		} else {
			heroinfo_currentconditions_title.setVisibility(View.VISIBLE);
			heroinfo_currentconditions.setVisibility(View.VISIBLE);
			heroinfo_currentconditions.removeAllViews();
			final Resources res = getResources();
			final Context context = this;
			for (ActorCondition c : player.conditions) {
				View v = View.inflate(this, R.layout.inventoryitemview, null);
				((ImageView) v.findViewById(R.id.inv_image)).setImageBitmap(world.tileStore.getBitmap(c.conditionType.iconID));
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
				heroinfo_currentconditions.addView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			}
		}
	}
    
    private static String describeEffect(Resources res, ActorCondition c) {
    	return ActorConditionEffectList.describeEffect(res, c.conditionType, c.magnitude, c.duration);
	}
}
