package com.gpl.rpg.AndorsTrail.view;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.activity.MainActivity;
import com.gpl.rpg.AndorsTrail.activity.HeroinfoActivity;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ImageButton;

public final class StatusView extends RelativeLayout {
	
	private final WorldContext world;
	private final Player player;
	private final ViewContext view;
	
	private final RangeBar healthBar;
	private final RangeBar expBar;
	private final ImageButton heroImage;
	private final ImageButton quickToggle;
	private boolean showingLevelup;
	private final Drawable levelupDrawable;
	
	public StatusView(final Context context, AttributeSet attr) {
		super(context, attr);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
        this.world = app.world;
        this.player = app.world.model.player;
        this.view = app.currentView.get();
        
        setFocusable(false);
        inflate(context, R.layout.statusview, this);
        this.setBackgroundResource(R.drawable.ui_gradientshape);
        
        heroImage = (ImageButton) findViewById(R.id.status_image);
        showingLevelup = true;
        
        heroImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, HeroinfoActivity.class);
				AndorsTrailApplication.getActivityFromActivityContext(context).startActivityForResult(intent, MainActivity.INTENTREQUEST_HEROINFO);
			}
		});
		healthBar = (RangeBar) findViewById(R.id.statusview_health);
		healthBar.init(R.drawable.ui_progress_health, R.string.status_hp);
        
		expBar = (RangeBar) findViewById(R.id.statusview_exp);
		expBar.init(R.drawable.ui_progress_exp, R.string.status_exp);
        
		levelupDrawable = new LayerDrawable(new Drawable[] {
				new BitmapDrawable(world.tileManager.preloadedTiles.getBitmap(player.actorTraits.iconID))
				,new BitmapDrawable(world.tileManager.preloadedTiles.getBitmap(TileManager.iconID_moveselect))
		});
		
		quickToggle = (ImageButton) findViewById(R.id.quickitem_toggle);
		world.tileManager.setImageViewTileForUIIcon(quickToggle, TileManager.iconID_boxclosed);
		quickToggle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				view.itemController.toggleQuickItemView();
			}
		});
		
		updateStatus();
        updateIcon(player.canLevelup());
    }

	public void updateStatus() {
		healthBar.update(player.health);
		expBar.update(player.levelExperience);
		boolean canLevelUp = player.canLevelup();
		if (showingLevelup != canLevelUp) {
			updateIcon(canLevelUp);
		}
	}

	private void updateIcon(boolean canLevelUp) {
		showingLevelup = canLevelUp;
		if (canLevelUp) {
			heroImage.setImageDrawable(levelupDrawable);
		} else {
			world.tileManager.setImageViewTile(heroImage, player);			
		}
	}
	
	public void updateQuickItemImage(boolean visible){
		if(visible){
			world.tileManager.setImageViewTileForUIIcon(quickToggle, TileManager.iconID_boxopened);
		} else {
			world.tileManager.setImageViewTileForUIIcon(quickToggle, TileManager.iconID_boxclosed);
		}
	}
}
