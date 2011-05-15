package com.gpl.rpg.AndorsTrail.view;

import android.R.color;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.resource.TileStore;

public class QuickitemView extends FrameLayout{
	public static final int NUM_QUICK_SLOTS = 3;

	private final WorldContext world;
	private final ViewContext view;
	private final ImageButton[] items = new ImageButton[NUM_QUICK_SLOTS];
	private final ColorFilter grayScaleFilter = new ColorMatrixColorFilter(
			new float[] { 0.30f, 0.59f, 0.11f, 0.0f, 0.0f,
                          0.30f, 0.59f, 0.11f, 0.0f, 0.0f,
                          0.30f, 0.59f, 0.11f, 0.0f, 0.0f,
                          0.00f, 0.00f, 0.00f, 1.0f, 0.0f
			});

	public QuickitemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	    AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
	    this.world = app.world;
        this.view = app.currentView.get();
        setFocusable(false);

        inflate(context, R.layout.quickitemview, this);
		Resources res = getResources();
		this.setBackgroundColor(res.getColor(color.transparent));

		items[0] = (ImageButton)findViewById(R.id.quickitemview_item1);
		items[1] = (ImageButton)findViewById(R.id.quickitemview_item2);
		items[2] = (ImageButton)findViewById(R.id.quickitemview_item3);

		for(int i = 0; i < items.length; ++i) {
			final int slotId = i;
			ImageButton item = items[i];
			item.setImageBitmap(world.tileStore.getBitmap(TileStore.iconID_shop));
			item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					view.itemController.quickitemUse(slotId);
				}
			});
			disableButton(item);
		}
	}

	private void disableButton(ImageButton imageButton) {
		imageButton.setEnabled(false);
		imageButton.setColorFilter(grayScaleFilter);
	}

	private void enableButton(ImageButton imageButton) {
		imageButton.setEnabled(true);
		imageButton.setColorFilter(null);
	}

	@Override
	public void setVisibility(int visibility) {
		refreshQuickitems();
		super.setVisibility(visibility);
	}
	
	public void refreshQuickitems() {
		for (int i = 0; i < NUM_QUICK_SLOTS; ++i){
			ImageButton item = items[i];
			ItemType type = world.model.player.inventory.quickitem[i];
			if(type==null){
				item.setImageBitmap(world.tileStore.getBitmap(TileStore.iconID_shop));
				disableButton(item);
			} else {
				item.setImageBitmap(world.tileStore.getBitmap(type.iconID));
				if(world.model.player.inventory.hasItem(type.id)){
					enableButton(item);
				} else {
					disableButton(item);				
				}
			}
		}
	}
}
