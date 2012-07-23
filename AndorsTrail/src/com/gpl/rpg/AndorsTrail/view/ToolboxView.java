package com.gpl.rpg.AndorsTrail.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.activity.HeroinfoActivity;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.WorldMapController;

public class ToolboxView extends LinearLayout implements OnClickListener {
	private final WorldContext world;
	private final Animation slideUpAnimation;
	private final Animation slideDownAnimation;
	private final ImageButton toolbox_playerinfo;
	private final ImageButton toolbox_map;
	
	public ToolboxView(final Context context, AttributeSet attrs) {
		super(context, attrs);
	    AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
	    this.world = app.world;

        inflate(context, R.layout.toolboxview, this);
		
		this.slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slideup);
        this.slideDownAnimation = AnimationUtils.loadAnimation(context, R.anim.slidedown);
        this.slideDownAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) { }
			@Override
			public void onAnimationRepeat(Animation animation) { }
			@Override
			public void onAnimationEnd(Animation animation) {
				ToolboxView.this.setVisibility(View.GONE);
			}
		});
        
        toolbox_playerinfo = (ImageButton)findViewById(R.id.toolbox_playerinfo);
        toolbox_playerinfo.setOnClickListener(this);
        toolbox_map = (ImageButton)findViewById(R.id.toolbox_map);
        toolbox_map.setOnClickListener(this);
        
        updateIcons();
	}
	
	@Override
	public void onClick(View btn) {
		Context context = getContext();
		if (btn == toolbox_playerinfo) {
			context.startActivity(new Intent(context, HeroinfoActivity.class));
		} else if (btn == toolbox_map) {
			if (!WorldMapController.displayWorldMap(context, world)) return;
		}
		ToolboxView.this.setVisibility(View.GONE);
	}

	public void toggleVisibility() {
		if (getVisibility() == View.VISIBLE) {
			startAnimation(slideDownAnimation);
		} else {
			setVisibility(View.VISIBLE);
			bringToFront();
			startAnimation(slideUpAnimation);
		}
	}
	
	public void updateIcons() {
		world.tileManager.setImageViewTile(toolbox_playerinfo, world.model.player);
	}
}
