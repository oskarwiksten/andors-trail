package com.gpl.rpg.AndorsTrail.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.WorldMapController;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;

public class ToolboxView extends LinearLayout implements OnClickListener {
	private final WorldContext world;
	private final AndorsTrailPreferences preferences;
	private final Animation showAnimation;
	private final Animation hideAnimation;
	private final ImageButton toolbox_quickitems;
	private final ImageButton toolbox_map;
	private ImageButton toggleVisibility;
	private QuickitemView quickitemView;
	
	public ToolboxView(final Context context, AttributeSet attrs) {
		super(context, attrs);
	    AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
	    this.world = app.getWorld();
	    this.preferences = app.getPreferences();

        inflate(context, R.layout.toolboxview, this);
		
		this.showAnimation = AnimationUtils.loadAnimation(context, R.anim.showtoolbox);
        this.hideAnimation = AnimationUtils.loadAnimation(context, R.anim.hidetoolbox);
        this.hideAnimation.setAnimationListener(new AnimationListener() {
        	@Override public void onAnimationStart(Animation animation) { }
        	@Override public void onAnimationRepeat(Animation animation) { }
        	@Override public void onAnimationEnd(Animation animation) {
        		ToolboxView.this.setVisibility(View.GONE);
        	}
		});
        
        toolbox_quickitems = (ImageButton)findViewById(R.id.toolbox_quickitems);
        toolbox_quickitems.setOnClickListener(this);
        toolbox_map = (ImageButton)findViewById(R.id.toolbox_map);
        toolbox_map.setOnClickListener(this);
	}
	
	public void registerToolboxViews(ImageButton toggleVisibility, QuickitemView quickitemView) {
		this.toggleVisibility = toggleVisibility;
		this.quickitemView = quickitemView;
	}
	
	@Override
	public void onClick(View btn) {
		Context context = getContext();
		if (btn == toolbox_quickitems) {
			toggleQuickItemView();
		} else if (btn == toolbox_map) {
			if (!WorldMapController.displayWorldMap(context, world)) return;
			setVisibility(View.GONE);
		}
	}
	
	private void toggleQuickItemView() {
		if (quickitemView.getVisibility() == View.VISIBLE){
			quickitemView.setVisibility(View.GONE);
		} else {
			quickitemView.setVisibility(View.VISIBLE);
		}
	}

	public void toggleVisibility() {
		if (getVisibility() == View.VISIBLE) {
			if (preferences.enableUiAnimations) {
				startAnimation(hideAnimation);
			} else {
				setVisibility(View.GONE);
			}
			setToolboxIcon(false);
		} else {
			setVisibility(View.VISIBLE);
			if (preferences.enableUiAnimations) {
				startAnimation(showAnimation);
			}
			setToolboxIcon(true);
		}
	}
	
	public void updateIcons() {
		setToolboxIcon(getVisibility() == View.VISIBLE);
	}
	
	private void setToolboxIcon(boolean opened) {
		if (opened) {
			world.tileManager.setImageViewTileForUIIcon(toggleVisibility, TileManager.iconID_boxopened);
		} else {
			world.tileManager.setImageViewTileForUIIcon(toggleVisibility, TileManager.iconID_boxclosed);
		}
	}
}
