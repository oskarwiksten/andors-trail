package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;

public final class MonsterEncounterActivity extends Activity {
	private WorldContext world;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
        this.world = app.world;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        final Monster monster = Dialogs.getMonsterFromIntent(getIntent(), world);
        if (monster == null) {
        	finish();
        	return;
        }
        
        setContentView(R.layout.monsterencounter);

        CharSequence difficulty = getText(MonsterInfoActivity.getMonsterDifficultyResource(world, monster));

        TextView tv = (TextView) findViewById(R.id.monsterencounter_title);
        tv.setText(monster.getName());
        world.tileManager.setImageViewTile(tv, monster);
        
        tv = (TextView) findViewById(R.id.monsterencounter_description);
        tv.setText(getString(R.string.dialog_monsterencounter_message, difficulty));

        Button b = (Button) findViewById(R.id.monsterencounter_attack);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_OK);
				MonsterEncounterActivity.this.finish();
			}
        });
        b = (Button) findViewById(R.id.monsterencounter_cancel);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_CANCELED);
				MonsterEncounterActivity.this.finish();
			}
        });
        b = (Button) findViewById(R.id.monsterencounter_info);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Dialogs.showMonsterInfo(MonsterEncounterActivity.this, monster);
			}
        });
    }
}
