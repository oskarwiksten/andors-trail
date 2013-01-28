package com.gpl.rpg.AndorsTrail.activity;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;

import android.app.ActivityGroup;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public final class HeroinfoActivity extends ActivityGroup {
	private WorldContext world;

	private TabHost tabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
        this.world = app.getWorld();
        
        app.setWindowParameters(this);
        
        setContentView(R.layout.heroinfo);
        
        Resources res = getResources();
        
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this.getLocalActivityManager());
        
        tabHost.addTab(tabHost.newTabSpec("char")
        		.setIndicator(res.getString(R.string.heroinfo_char), res.getDrawable(R.drawable.char_hero))
        		.setContent(new Intent(this, HeroinfoActivity_Stats.class)));
        tabHost.addTab(tabHost.newTabSpec("quests")
        		.setIndicator(res.getString(R.string.heroinfo_quests), res.getDrawable(R.drawable.ui_icon_quest))
        		.setContent(new Intent(this, HeroinfoActivity_Quests.class)));
        tabHost.addTab(tabHost.newTabSpec("skills")
        		.setIndicator(res.getString(R.string.heroinfo_skill), res.getDrawable(R.drawable.ui_icon_skill))
        		.setContent(new Intent(this, HeroinfoActivity_Skills.class)));
        tabHost.addTab(tabHost.newTabSpec("inv")
        		.setIndicator(res.getString(R.string.heroinfo_inv), res.getDrawable(R.drawable.ui_icon_equipment))
        		.setContent(new Intent(this, HeroinfoActivity_Inventory.class)));
        String t = world.model.uiSelections.selectedTabHeroInfo;
        if (t != null && t.length() > 0) {
        	tabHost.setCurrentTabByTag(t);
        }
    }

	@Override
    protected void onPause() {
        super.onPause();
        world.model.uiSelections.selectedTabHeroInfo = tabHost.getCurrentTabTag();
    }
}