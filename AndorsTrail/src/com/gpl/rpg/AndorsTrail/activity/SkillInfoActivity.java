package com.gpl.rpg.AndorsTrail.activity;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.SkillController;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.ability.SkillInfo;
import com.gpl.rpg.AndorsTrail.model.actor.Player;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class SkillInfoActivity extends Activity {
	private WorldContext world;
	private Player player;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        this.world = app.world;
        this.player = world.model.player;
        
        AndorsTrailApplication.setWindowParameters(this, app.preferences);
        
        setContentView(R.layout.skill_info_view);

        final Resources res = getResources();
        final Intent intent = getIntent();
        final int skillID = intent.getExtras().getInt("skillID");
        SkillInfo skill = world.skills.getSkill(skillID);
        
        ImageView skillinfo_image = (ImageView) findViewById(R.id.skillinfo_image);
        SkillController.setSkillIcon(skillinfo_image, skillID, res);
        
        TextView skillinfo_title = (TextView) findViewById(R.id.skillinfo_title);
        skillinfo_title.setText(SkillCollection.getSkillTitleResourceID(skillID));
        
        TextView skillinfo_longdescription = (TextView) findViewById(R.id.skillinfo_longdescription);
        skillinfo_longdescription.setText(SkillCollection.getSkillLongDescription(skillID, res));
        
        TextView skillinfo_currentlevel = (TextView) findViewById(R.id.skillinfo_currentlevel);
        if (player.hasSkill(skillID)) {
        	int playerSkillLevel = player.getSkillLevel(skillID);
            if (skill.hasMaxLevel()) {
	            skillinfo_currentlevel.setText(res.getString(R.string.skill_current_level_with_maximum, playerSkillLevel, skill.maxLevel));
	        } else {
	            skillinfo_currentlevel.setText(res.getString(R.string.skill_current_level, playerSkillLevel));
	        }
        } else {
            skillinfo_currentlevel.setVisibility(View.GONE);
        }
        
        
        TextView skillinfo_prerequisites = (TextView) findViewById(R.id.skillinfo_prerequisites);
        LinearLayout parent = (LinearLayout) skillinfo_prerequisites.getParent();
        if (skill.hasLevelupRequirements()) {
        	//skillinfo_prerequisites.setText(SkillInfo.getLongDescription(skillID, res));
        } else {
            parent.removeView(skillinfo_prerequisites);
        }

        Button b = (Button) findViewById(R.id.skillinfoinfo_close);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_CANCELED);
				SkillInfoActivity.this.finish();
			}
		});
        
        b = (Button) findViewById(R.id.skillinfoinfo_action);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent result = new Intent();
				result.putExtras(intent);
				setResult(RESULT_OK, result);
				SkillInfoActivity.this.finish();
			}
		});
        b.setEnabled(SkillController.canLevelupSkill(player, skill));
    }
	
}
