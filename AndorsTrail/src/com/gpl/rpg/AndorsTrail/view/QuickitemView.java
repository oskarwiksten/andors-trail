package com.gpl.rpg.AndorsTrail.view;

import android.R.color;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.activity.MainActivity;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.resource.TileStore;

public class QuickitemView extends FrameLayout implements OnClickListener{
	public static final int NUM_QUICK_SLOTS = 3;

	private final WorldContext world;
	private final ViewContext view;
	private final QuickButton[] items = new QuickButton[NUM_QUICK_SLOTS];

	public QuickitemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	    AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
	    this.world = app.world;
        this.view = app.currentView.get();
        setFocusable(false);

        inflate(context, R.layout.quickitemview, this);
		Resources res = getResources();
		this.setBackgroundColor(res.getColor(color.transparent));

		TypedArray quickButtons = res.obtainTypedArray(R.array.quick_buttons);
		for(int i = 0; i <items.length ; ++i) {
			items[i] = (QuickButton)findViewById(quickButtons.getResourceId(i, -1));
			QuickButton item = items[i];
			item.setIndex(i);
			item.setImageBitmap(world.tileStore.getBitmap(TileStore.iconID_shop));
			item.setOnClickListener(this);
			item.setEmpty(true);
		}
	}
	
	public boolean isQuickButtonId(int id){
		for(QuickButton item: items){
			if(item.getId()==id)
				return true;
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		QuickButton button = (QuickButton)v;
		if(button.isEmpty())
			return;
		view.itemController.quickitemUse(button.getIndex());
	}
	
	@Override
	public void setVisibility(int visibility) {
		if(visibility==VISIBLE)
			refreshQuickitems();
		super.setVisibility(visibility);
	}
	
	public void refreshQuickitems() {
		for (int i = 0; i < NUM_QUICK_SLOTS; ++i){
			QuickButton item = items[i];
			ItemType type = world.model.player.inventory.quickitem[i];
			if(type==null){
				item.setImageBitmap(world.tileStore.getBitmap(TileStore.iconID_shop));
				item.setEmpty(true);
			} else {
				world.tileStore.setImageViewTile(item, type);
				item.setEmpty(!world.model.player.inventory.hasItem(type.id));
			}
		}
	}
	
	public void registerForContextMenu(MainActivity mainActivity) {
		for(QuickButton item: items)
			mainActivity.registerForContextMenu(item);
	}
}
