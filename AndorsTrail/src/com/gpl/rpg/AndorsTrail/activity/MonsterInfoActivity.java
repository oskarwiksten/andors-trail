package com.gpl.rpg.AndorsTrail.activity;

import java.util.Arrays;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.CombatController;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.view.ItemEffectsView;
import com.gpl.rpg.AndorsTrail.view.RangeBar;
import com.gpl.rpg.AndorsTrail.view.TraitsInfoView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public final class MonsterInfoActivity extends Activity {
	private WorldContext world;
	private Monster monster;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        this.world = app.world;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        final Intent intent = getIntent();
        final Bundle params = intent.getExtras();
        int x = params.getInt("x");
        int y = params.getInt("y");
        this.monster = world.model.currentMap.getMonsterAt(x, y);
        if (this.monster == null) {
        	finish();
        	return;
        }                          
        
        setContentView(R.layout.monsterinfo);

        ImageView img = (ImageView) findViewById(R.id.monsterinfo_image);
        img.setImageBitmap(world.tileStore.getBitmap(monster.traits.iconID));
        TextView tv = (TextView) findViewById(R.id.monsterinfo_title);
        tv.setText(monster.traits.name);
        tv = (TextView) findViewById(R.id.monsterinfo_difficulty);
        tv.setText(getMonsterDifficultyResource(world, monster));

        Button b = (Button) findViewById(R.id.monsterinfo_close);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MonsterInfoActivity.this.finish();
			}
		});

        ((TraitsInfoView) findViewById(R.id.monsterinfo_currenttraits)).update(monster.traits);
        ((ItemEffectsView) findViewById(R.id.monsterinfo_onhiteffects)).update(
        		null, 
        		null, 
        		monster.traits.onHitEffects == null ? null : Arrays.asList(monster.traits.onHitEffects), 
        		null);
        RangeBar hp = (RangeBar) findViewById(R.id.monsterinfo_healthbar);
        hp.init(R.drawable.ui_progress_health, R.string.status_hp);
        hp.update(monster.health);
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
}
