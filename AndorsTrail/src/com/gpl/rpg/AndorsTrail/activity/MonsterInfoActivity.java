package com.gpl.rpg.AndorsTrail.activity;

import java.util.Arrays;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.CombatController;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.view.ActorConditionList;
import com.gpl.rpg.AndorsTrail.view.ItemEffectsView;
import com.gpl.rpg.AndorsTrail.view.RangeBar;
import com.gpl.rpg.AndorsTrail.view.TraitsInfoView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public final class MonsterInfoActivity extends Activity {
	
	private TextView monsterinfo_title;
	private TextView monsterinfo_difficulty;
	private TraitsInfoView monsterinfo_currenttraits;
	private ItemEffectsView monsterinfo_onhiteffects;
    private TextView monsterinfo_currentconditions_title;
    private TextView monsterinfo_immune_criticals;
    private ActorConditionList monsterinfo_currentconditions;
	private RangeBar hp;
	private WorldContext world;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
        this.world = app.world;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.monsterinfo);

        monsterinfo_title = (TextView) findViewById(R.id.monsterinfo_title);
        monsterinfo_difficulty = (TextView) findViewById(R.id.monsterinfo_difficulty);
        monsterinfo_immune_criticals = (TextView) findViewById(R.id.monsterinfo_immune_criticals);
        
        Button b = (Button) findViewById(R.id.monsterinfo_close);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MonsterInfoActivity.this.finish();
			}
		});

        monsterinfo_currenttraits = (TraitsInfoView) findViewById(R.id.monsterinfo_currenttraits);
        monsterinfo_onhiteffects = (ItemEffectsView) findViewById(R.id.monsterinfo_onhiteffects);
        monsterinfo_currentconditions_title = (TextView) findViewById(R.id.monsterinfo_currentconditions_title);
        monsterinfo_currentconditions = (ActorConditionList) findViewById(R.id.monsterinfo_currentconditions);
        hp = (RangeBar) findViewById(R.id.monsterinfo_healthbar);
        hp.init(R.drawable.ui_progress_health, R.string.status_hp);
    }

    @Override
	protected void onResume() {
    	super.onResume();
    	
    	Monster monster = Dialogs.getMonsterFromIntent(getIntent(), world);
        if (monster == null) {
        	finish();
        	return;
        }  
        
        updateTitle(monster);
    	updateTraits(monster);
        updateConditions(monster);
    }

	private void updateTitle(Monster monster) {
		monsterinfo_title.setText(monster.actorTraits.name);
		world.tileManager.setImageViewTile(monsterinfo_title, monster);
        monsterinfo_difficulty.setText(getMonsterDifficultyResource(world, monster));
	}

	private void updateTraits(Monster monster) {
		monsterinfo_currenttraits.update(monster.combatTraits);
		monsterinfo_onhiteffects.update(
        		null, 
        		null, 
        		monster.actorTraits.onHitEffects == null ? null : Arrays.asList(monster.actorTraits.onHitEffects), 
        		null);
        hp.update(monster.health);
        monsterinfo_immune_criticals.setVisibility(monster.isImmuneToCriticalHits ? View.VISIBLE : View.GONE);
    }

	public static int getMonsterDifficultyResource(WorldContext world, Monster monster) {
		final int difficulty = CombatController.getMonsterDifficulty(world, monster);
		if (difficulty >= 80) return R.string.monster_difficulty_veryeasy;
		else if (difficulty >= 60) return R.string.monster_difficulty_easy;
		else if (difficulty >= 40) return R.string.monster_difficulty_normal;
		else if (difficulty >= 20) return R.string.monster_difficulty_hard;
		else if (difficulty == 0) return R.string.monster_difficulty_impossible;
		else return R.string.monster_difficulty_veryhard;
	}

	private void updateConditions(Monster monster) {
		if (monster.conditions.isEmpty()) {
			monsterinfo_currentconditions_title.setVisibility(View.GONE);
			monsterinfo_currentconditions.setVisibility(View.GONE);
		} else {
			monsterinfo_currentconditions_title.setVisibility(View.VISIBLE);
			monsterinfo_currentconditions.setVisibility(View.VISIBLE);
			monsterinfo_currentconditions.update(monster.conditions);
		}
	}
}
