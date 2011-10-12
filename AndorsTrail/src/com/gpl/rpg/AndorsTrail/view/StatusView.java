package com.gpl.rpg.AndorsTrail.view;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.activity.MainActivity;
import com.gpl.rpg.AndorsTrail.activity.HeroinfoActivity;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.resource.TileStore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
				new BitmapDrawable(world.tileStore.getBitmap(player.actorTraits.iconID))
				,new BitmapDrawable(world.tileStore.getBitmap(TileStore.iconID_moveselect))
		});
		
		quickToggle = (ImageButton) findViewById(R.id.quickitem_toggle);
		quickToggle.setImageBitmap(world.tileStore.getBitmap(TileStore.iconID_boxclosed));
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
			heroImage.setImageBitmap(world.tileStore.getBitmap(player.actorTraits.iconID));			
		}
	}

	public void updateActiveConditions(Context androidContext, LinearLayout activeConditions) {
		GreedyImageViewAppender t = new GreedyImageViewAppender(androidContext, activeConditions);
		for (ActorCondition condition : player.conditions) {
			t.setCurrentImage(world.tileStore.getBitmap(condition.conditionType.iconID));
		}
		t.removeOtherImages();
	}
	
	public void updateQuickItemImage(boolean visible){
		if(visible){
			quickToggle.setImageBitmap(world.tileStore.getBitmap(TileStore.iconID_boxopened));
		} else {
			quickToggle.setImageBitmap(world.tileStore.getBitmap(TileStore.iconID_boxclosed));
		}
	}
	
	private static class GreedyImageViewAppender {
		private final LinearLayout container;
		private final Context context;
		private int currentChildIndex = 0;
		private final int previousChildCount;
		private final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		public GreedyImageViewAppender(Context context, LinearLayout container) {
			this.container = container;
			this.context = context;
			this.previousChildCount = container.getChildCount();
		}
		public void setCurrentImage(Bitmap b) {
			// Since this is called a lot, we do not want to recreate the view objects every time.
			// Therefore, we reuse existing ImageView:s if they are present, but just change the image on them.
			if (currentChildIndex < previousChildCount) {
				// There already is a create dimage on this position, reuse it.
				ImageView iv = (ImageView) container.getChildAt(currentChildIndex);
				iv.setImageBitmap(b);
				iv.setVisibility(View.VISIBLE);
			} else {
				// The player has never had this many conditions, create a new ImageView to hold the condition image.
				ImageView iv = new ImageView(context);
				iv.setImageBitmap(b);
				container.addView(iv, layoutParams);
			}
			++currentChildIndex;
		}
		public void removeOtherImages() {
			for(int i = previousChildCount - 1; i >= currentChildIndex; --i) {
				//container.removeViewAt(i);
				// Don't actually remove them, just hide them (so we won't have to recreate them next time the player get a condition)
				container.getChildAt(i).setVisibility(View.GONE);
			}
		}
	}
}
