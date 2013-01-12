package com.gpl.rpg.AndorsTrail.view;

import java.util.HashSet;

import android.R.color;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.activity.MainActivity;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.listeners.QuickSlotListener;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCollection;

public class QuickitemView extends LinearLayout implements OnClickListener, QuickSlotListener {
	private static final int NUM_QUICK_SLOTS = Inventory.NUM_QUICK_SLOTS;

	private final WorldContext world;
	private final ViewContext view;
	private final QuickButton[] buttons = new QuickButton[NUM_QUICK_SLOTS];
	private final HashSet<Integer> loadedTileIDs = new HashSet<Integer>();
	private TileCollection tiles = null;

	public QuickitemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	    AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
	    this.world = app.getWorld();
        this.view = app.getViewContext();
        setFocusable(false);
        setOrientation(LinearLayout.HORIZONTAL);

        inflate(context, R.layout.quickitemview, this);
		Resources res = getResources();
		this.setBackgroundColor(res.getColor(color.transparent));

		TypedArray quickButtons = res.obtainTypedArray(R.array.quick_buttons);
		for(int i = 0; i < buttons.length; ++i) {
			buttons[i] = (QuickButton)findViewById(quickButtons.getResourceId(i, -1));
			QuickButton item = buttons[i];
			item.setIndex(i);
			item.setItemType(null, world, tiles);
			item.setOnClickListener(this);
		}
	}
	
	public boolean isQuickButtonId(int id){
		for(QuickButton item: buttons){
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
		loadItemTypeImages();
		
		for (int i = 0; i < NUM_QUICK_SLOTS; ++i){
			ItemType type = world.model.player.inventory.quickitem[i];
			buttons[i].setItemType(type, world, tiles);
		}
	}
	
	private void loadItemTypeImages() {
		boolean shouldLoadImages = false;
		for (ItemType type : world.model.player.inventory.quickitem) {
			if (type == null) continue;
			if (!loadedTileIDs.contains(type.iconID)) {
				shouldLoadImages = true;
				break;
			}
		}
		if (!shouldLoadImages) return;
		
		HashSet<Integer> iconIDs = new HashSet<Integer>();
		
		for (ItemType type : world.model.player.inventory.quickitem) {
			if (type == null) continue;
			iconIDs.add(type.iconID);
		}
		
		loadedTileIDs.clear();
		loadedTileIDs.addAll(iconIDs);
		tiles = world.tileManager.loadTilesFor(iconIDs, getResources());
	}

	public void registerForContextMenu(MainActivity mainActivity) {
		for(QuickButton item: buttons)
			mainActivity.registerForContextMenu(item);
	}

	@Override
	public void onQuickSlotChanged(int slotId) {
		refreshQuickitems();
	}

	@Override
	public void onQuickSlotUsed(int slotId) {
		refreshQuickitems();
	}

	public void subscribe() {
		view.itemController.quickSlotListeners.add(this);
	}
	public void unsubscribe() {
		view.itemController.quickSlotListeners.remove(this);
	}
}
