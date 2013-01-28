package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.view.SkillListAdapter;

public final class HeroinfoActivity_Skills extends Activity {
	private WorldContext world;
	private ViewContext view;

	private Player player;

	private SkillListAdapter skillListAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
        this.world = app.getWorld();
        this.view = app.getViewContext();
        this.player = world.model.player;
        
        setContentView(R.layout.heroinfo_skill_list);
        
        skillListAdapter = new SkillListAdapter(this, world.skills.getAllSkills(), player);
        ListView skillList = (ListView) findViewById(R.id.heroinfo_listskills_list);
        skillList.setAdapter(skillListAdapter);
        skillList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Dialogs.showSkillInfo(HeroinfoActivity_Skills.this, (int) id);
			}
		});
    }

    @Override
	protected void onResume() {
    	super.onResume();
    	updateSkillList();
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MainActivity.INTENTREQUEST_SKILLINFO:
			if (resultCode != RESULT_OK) break;
			
			int skillID = data.getExtras().getInt("skillID");
			view.skillController.levelUpSkillManually(player, world.skills.getSkill(skillID));
			break;
		}
	}

	private void updateSkillList() {
		TextView listskills_number_of_increases = (TextView) findViewById(R.id.heroinfo_listskills_number_of_increases);
        
        int numberOfSkillIncreases = player.getAvailableSkillIncreases();
		if (numberOfSkillIncreases > 0) {
			if (numberOfSkillIncreases == 1) {
				listskills_number_of_increases.setText(R.string.skill_number_of_increases_one);
			} else {
				listskills_number_of_increases.setText(getResources().getString(R.string.skill_number_of_increases_several, numberOfSkillIncreases));
			}
			listskills_number_of_increases.setVisibility(View.VISIBLE);
		} else {
			listskills_number_of_increases.setVisibility(View.GONE);
		}
		skillListAdapter.notifyDataSetInvalidated();
	}
}