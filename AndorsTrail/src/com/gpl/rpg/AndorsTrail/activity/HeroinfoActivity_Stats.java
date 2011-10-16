package com.gpl.rpg.AndorsTrail.activity;

import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.view.ActorConditionList;
import com.gpl.rpg.AndorsTrail.view.BaseTraitsInfoView;
import com.gpl.rpg.AndorsTrail.view.ItemEffectsView;
import com.gpl.rpg.AndorsTrail.view.RangeBar;
import com.gpl.rpg.AndorsTrail.view.TraitsInfoView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public final class HeroinfoActivity_Stats extends Activity {
	private WorldContext world;
	
	private Player player;
	
	private Button levelUpButton;
    private TextView heroinfo_ap;
    private TextView heroinfo_movecost;
    private TraitsInfoView heroinfo_currenttraits;
    private ItemEffectsView heroinfo_itemeffects;
    private TextView heroinfo_currentconditions_title;
    private ActorConditionList heroinfo_currentconditions;
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
        this.player = world.model.player;
        
        setContentView(R.layout.heroinfo_stats);
        
        ImageView iv = (ImageView) findViewById(R.id.heroinfo_image);
        world.tileManager.setImageViewTile(iv, player);
        
        ((TextView) findViewById(R.id.heroinfo_title)).setText(player.actorTraits.name);
        heroinfo_ap = (TextView) findViewById(R.id.heroinfo_ap);
        heroinfo_movecost = (TextView) findViewById(R.id.heroinfo_movecost);
        heroinfo_currenttraits = (TraitsInfoView) findViewById(R.id.heroinfo_currenttraits);
        heroinfo_itemeffects = (ItemEffectsView) findViewById(R.id.heroinfo_itemeffects);
        heroinfo_currentconditions_title = (TextView) findViewById(R.id.heroinfo_currentconditions_title);
        heroinfo_currentconditions = (ActorConditionList) findViewById(R.id.heroinfo_currentconditions);
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
		switch (requestCode) {
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
        heroinfo_movecost.setText(Integer.toString(player.actorTraits.moveCost));
        rangebar_hp.update(player.health);
        rangebar_exp.update(player.levelExperience);
        
        heroinfo_currenttraits.update(player.combatTraits);
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
		heroinfo_basetraits.update(player.actorTraits.baseCombatTraits);
    }

	private void updateConditions() {
		if (player.conditions.isEmpty()) {
			heroinfo_currentconditions_title.setVisibility(View.GONE);
			heroinfo_currentconditions.setVisibility(View.GONE);
		} else {
			heroinfo_currentconditions_title.setVisibility(View.VISIBLE);
			heroinfo_currentconditions.setVisibility(View.VISIBLE);
			heroinfo_currentconditions.update(player.conditions);
		}
	}
}
