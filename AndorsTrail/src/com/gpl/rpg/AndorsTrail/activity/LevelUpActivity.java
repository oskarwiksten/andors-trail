package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.ActorStatsController;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.actor.Player;

public final class LevelUpActivity extends Activity {
	private WorldContext world;
	private Player player;
	private TextView levelup_description;
	private TextView levelup_title;
	private View levelup_adds_new_skillpoint;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
        this.world = app.world;
        this.player = world.model.player;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.levelup);
    	
        levelup_title = (TextView) findViewById(R.id.levelup_title);
    	levelup_description = (TextView) findViewById(R.id.levelup_description);
        levelup_adds_new_skillpoint = findViewById(R.id.levelup_adds_new_skillpoint);

        Button b;
        
        b = (Button) findViewById(R.id.levelup_add_health);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				levelup(SELECT_HEALTH);
			}
		});
        b.setText(getString(R.string.levelup_add_health, Constants.LEVELUP_EFFECT_HEALTH));
        
        b = (Button) findViewById(R.id.levelup_add_attackchance);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				levelup(SELECT_ATK_CH);
			}
		});
        b.setText(getString(R.string.levelup_add_attackchance, Constants.LEVELUP_EFFECT_ATK_CH));
        
        b = (Button) findViewById(R.id.levelup_add_attackdamage);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				levelup(SELECT_ATK_DMG);
			}
		});
        b.setText(getString(R.string.levelup_add_attackdamage, Constants.LEVELUP_EFFECT_ATK_DMG));
        
        b = (Button) findViewById(R.id.levelup_add_blockchance);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				levelup(SELECT_DEF_CH);
			}
		});
        b.setText(getString(R.string.levelup_add_blockchance, Constants.LEVELUP_EFFECT_DEF_CH));
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	final Resources res = getResources();
    	
    	if (!player.canLevelup()) {
    		this.finish();
    		return;
    	}
    	
    	world.tileManager.setImageViewTile(levelup_title, player);
        levelup_description.setText(res.getString(R.string.levelup_description, player.getLevel() + 1));
        if (player.nextLevelAddsNewSkillpoint()) {
        	levelup_adds_new_skillpoint.setVisibility(View.VISIBLE);
        } else {
        	levelup_adds_new_skillpoint.setVisibility(View.GONE);
        }
    }

    private static final int SELECT_HEALTH = 0;
    private static final int SELECT_ATK_CH = 1;
    private static final int SELECT_ATK_DMG = 2;
    private static final int SELECT_DEF_CH = 3;
    
    public void levelup(int selectionID) {
    	if (LevelUpActivity.this.isFinishing()) return;
    	
    	addLevelupEffect(player, selectionID);
    	LevelUpActivity.this.finish();
    }
    
    public static void addLevelupEffect(Player player, int selectionID) {
    	int hpIncrease = 0;
    	switch (selectionID) {
    	case SELECT_HEALTH:
    		hpIncrease = Constants.LEVELUP_EFFECT_HEALTH;
    		break;
    	case SELECT_ATK_CH:
    		player.baseTraits.attackChance += Constants.LEVELUP_EFFECT_ATK_CH;
    		break;
    	case SELECT_ATK_DMG:
    		player.baseTraits.damagePotential.max += Constants.LEVELUP_EFFECT_ATK_DMG;
    		player.baseTraits.damagePotential.current += Constants.LEVELUP_EFFECT_ATK_DMG;
    		break;
    	case SELECT_DEF_CH:
    		player.baseTraits.blockChance += Constants.LEVELUP_EFFECT_DEF_CH;
    		break;
    	}
    	if (player.nextLevelAddsNewSkillpoint()) {
    		player.availableSkillIncreases++;
    	}
    	player.level++;
    	
    	hpIncrease += player.getSkillLevel(SkillCollection.SKILL_FORTITUDE) * SkillCollection.PER_SKILLPOINT_INCREASE_FORTITUDE_HEALTH;
		player.health.max += hpIncrease;
		player.baseTraits.maxHP += hpIncrease;
		player.health.current += hpIncrease;
    	
    	player.recalculateLevelExperience();
    	ActorStatsController.recalculatePlayerCombatTraits(player);
    }
}
