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
import com.gpl.rpg.AndorsTrail.view.ItemEffectsView;
import com.gpl.rpg.AndorsTrail.view.RangeBar;
import com.gpl.rpg.AndorsTrail.view.TraitsInfoView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public final class HeroinfoActivity_Stats extends Activity {
	private WorldContext world;
	
	private Player player;
	
	private Button levelUpButton;
    private TextView heroinfo_ap;
    private TextView heroinfo_reequip_cost;
    private TextView heroinfo_useitem_cost;
    private TextView heroinfo_level;
    private TextView heroinfo_totalexperience;
    private TextView basetraitsinfo_max_hp;
    private TextView basetraitsinfo_max_ap;
    private TextView heroinfo_base_reequip_cost;
    private TextView heroinfo_base_useitem_cost;
    private RangeBar rangebar_hp;
    private RangeBar rangebar_exp;
    private ItemEffectsView actorinfo_onhiteffects;
    private TableLayout heroinfo_basestats_table;
    private ViewGroup heroinfo_container;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
        this.world = app.getWorld();
        this.player = world.model.player;
        
        setContentView(R.layout.heroinfo_stats);
        
        TextView tv = (TextView) findViewById(R.id.heroinfo_title);
        tv.setText(player.getName());
        world.tileManager.setImageViewTile(tv, player);
        
        heroinfo_container = (ViewGroup) findViewById(R.id.heroinfo_container);
        heroinfo_ap = (TextView) findViewById(R.id.heroinfo_ap);
        heroinfo_reequip_cost = (TextView) findViewById(R.id.heroinfo_reequip_cost);
        heroinfo_useitem_cost = (TextView) findViewById(R.id.heroinfo_useitem_cost);
        basetraitsinfo_max_hp = (TextView) findViewById(R.id.basetraitsinfo_max_hp);
        basetraitsinfo_max_ap = (TextView) findViewById(R.id.basetraitsinfo_max_ap);
        heroinfo_base_reequip_cost = (TextView) findViewById(R.id.heroinfo_base_reequip_cost);
        heroinfo_base_useitem_cost = (TextView) findViewById(R.id.heroinfo_base_useitem_cost);
        heroinfo_level = (TextView) findViewById(R.id.heroinfo_level);
        heroinfo_totalexperience = (TextView) findViewById(R.id.heroinfo_totalexperience);
		actorinfo_onhiteffects = (ItemEffectsView) findViewById(R.id.actorinfo_onhiteffects);
		heroinfo_basestats_table = (TableLayout) findViewById(R.id.heroinfo_basestats_table);

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
    }

    @Override
	protected void onResume() {
    	super.onResume();
    	updateTraits();
        updateLevelup();
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
		heroinfo_level.setText(Integer.toString(player.getLevel()));
		heroinfo_totalexperience.setText(Integer.toString(player.getTotalExperience()));
		heroinfo_ap.setText(player.getMaxAP() + "/" + player.getCurrentAP());
		heroinfo_reequip_cost.setText(Integer.toString(player.getReequipCost()));
		heroinfo_useitem_cost.setText(Integer.toString(player.getUseItemCost()));
		basetraitsinfo_max_hp.setText(Integer.toString(player.baseTraits.maxHP));
		basetraitsinfo_max_ap.setText(Integer.toString(player.baseTraits.maxAP));
		heroinfo_base_reequip_cost.setText(Integer.toString(player.baseTraits.reequipCost));
		heroinfo_base_useitem_cost.setText(Integer.toString(player.baseTraits.useItemCost));
        rangebar_hp.update(player.getMaxHP(), player.getCurrentHP());
        rangebar_exp.update(player.getMaxLevelExperience(), player.getCurrentLevelExperience());
        
        TraitsInfoView.update(heroinfo_container, player);
        TraitsInfoView.updateTraitsTable(
    		heroinfo_basestats_table
    		, player.baseTraits.moveCost
    		, player.baseTraits.attackCost
    		, player.baseTraits.attackChance
    		, player.baseTraits.damagePotential
    		, player.baseTraits.criticalSkill
    		, player.baseTraits.criticalMultiplier
    		, player.baseTraits.blockChance
    		, player.baseTraits.damageResistance
			, false
		);
        
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
		actorinfo_onhiteffects.update(null, null, effects_hit, effects_kill, false);
    }
}
