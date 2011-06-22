package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;

public final class MonsterEncounterActivity extends Activity {
	private WorldContext world;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        this.world = app.world;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Uri uri = getIntent().getData();
        String monsterTypeID = uri.getLastPathSegment().toString();
        final MonsterType monsterType = world.monsterTypes.getMonsterType(monsterTypeID);
        
        setContentView(R.layout.monsterencounter);

        CharSequence difficulty = getText(MonsterInfoActivity.getMonsterDifficultyResource(world, monsterType));

        TextView tv = (TextView) findViewById(R.id.monsterencounter_title);
        tv.setText(monsterType.name);
        
        tv = (TextView) findViewById(R.id.monsterencounter_description);
        tv.setText(getString(R.string.dialog_monsterencounter_message, difficulty));

        ImageView iw = (ImageView) findViewById(R.id.monsterencounter_image);
        iw.setImageBitmap(world.tileStore.getBitmap(monsterType.iconID));
        
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
				Dialogs.showMonsterInfo(MonsterEncounterActivity.this, monsterType.id);
			}
        });
    }
}
