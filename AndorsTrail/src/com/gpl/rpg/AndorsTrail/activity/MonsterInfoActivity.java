package com.gpl.rpg.AndorsTrail.activity;

import java.util.Arrays;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.CombatController;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.util.Range;
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
	private WorldContext world;
	private MonsterType monsterType;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        this.world = app.world;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        String monsterTypeID = getIntent().getData().getLastPathSegment().toString();
        this.monsterType = world.monsterTypes.getMonsterType(Integer.parseInt(monsterTypeID));
        
        setContentView(R.layout.monsterinfo);

        ImageView img = (ImageView) findViewById(R.id.monsterinfo_image);
        img.setImageBitmap(world.tileStore.bitmaps[monsterType.iconID]);
        TextView tv = (TextView) findViewById(R.id.monsterinfo_title);
        tv.setText(monsterType.name);
        tv = (TextView) findViewById(R.id.monsterinfo_difficulty);
        tv.setText(getMonsterDifficultyResource(world, monsterType));

        Button b = (Button) findViewById(R.id.monsterinfo_close);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MonsterInfoActivity.this.finish();
			}
		});

        ((TraitsInfoView) findViewById(R.id.monsterinfo_currenttraits)).update(monsterType);
        ((ItemEffectsView) findViewById(R.id.monsterinfo_onhiteffects)).update(
        		null, 
        		null, 
        		monsterType.onHitEffects == null ? null : Arrays.asList(monsterType.onHitEffects), 
        		null);
        RangeBar hp = (RangeBar) findViewById(R.id.monsterinfo_healthbar);
        hp.init(R.drawable.ui_progress_health, R.string.status_hp);
        hp.update(new Range(monsterType.maxHP, monsterType.maxHP)); //TODO: Should show actual monster HP.
    }

	public static int getMonsterDifficultyResource(WorldContext world, MonsterType monsterType) {
		final int difficulty = CombatController.getMonsterDifficulty(world, monsterType);
		if (difficulty >= 80) return R.string.monster_difficulty_veryeasy;
		else if (difficulty >= 60) return R.string.monster_difficulty_easy;
		else if (difficulty >= 40) return R.string.monster_difficulty_normal;
		else if (difficulty >= 20) return R.string.monster_difficulty_hard;
		else if (difficulty == 0) return R.string.monster_difficulty_impossible;
		else return R.string.monster_difficulty_veryhard;
	}
}
