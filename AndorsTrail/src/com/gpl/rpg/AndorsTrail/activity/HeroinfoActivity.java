package com.gpl.rpg.AndorsTrail.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.activity.fragment.HeroinfoActivity_Inventory;
import com.gpl.rpg.AndorsTrail.activity.fragment.HeroinfoActivity_Quests;
import com.gpl.rpg.AndorsTrail.activity.fragment.HeroinfoActivity_Skills;
import com.gpl.rpg.AndorsTrail.activity.fragment.HeroinfoActivity_Stats;
import com.gpl.rpg.AndorsTrail.context.WorldContext;

public final class HeroinfoActivity extends FragmentActivity {
	private WorldContext world;

	private FragmentTabHost tabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
		if (!app.isInitialized()) { finish(); return; }
		this.world = app.getWorld();

		app.setWindowParameters(this);

		setContentView(R.layout.tabbedlayout);

		Resources res = getResources();

		tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		tabHost.addTab(tabHost.newTabSpec("char")
				.setIndicator(res.getString(R.string.heroinfo_char), res.getDrawable(R.drawable.char_hero))
				,HeroinfoActivity_Stats.class, null);
		tabHost.addTab(tabHost.newTabSpec("quests")
				.setIndicator(res.getString(R.string.heroinfo_quests), res.getDrawable(R.drawable.ui_icon_quest))
				,HeroinfoActivity_Quests.class, null);
		tabHost.addTab(tabHost.newTabSpec("skills")
				.setIndicator(res.getString(R.string.heroinfo_skill), res.getDrawable(R.drawable.ui_icon_skill))
				,HeroinfoActivity_Skills.class, null);
		tabHost.addTab(tabHost.newTabSpec("inv")
				.setIndicator(res.getString(R.string.heroinfo_inv), res.getDrawable(R.drawable.ui_icon_equipment))
				,HeroinfoActivity_Inventory.class, null);
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