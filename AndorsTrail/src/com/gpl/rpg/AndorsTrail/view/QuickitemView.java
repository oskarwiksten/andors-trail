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
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCollection;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;

public class QuickitemView extends LinearLayout implements OnClickListener {
	public static final int NUM_QUICK_SLOTS = 3;

	private final WorldContext world;
	private final ViewContext view;
	private final QuickButton[] buttons = new QuickButton[NUM_QUICK_SLOTS];
	private final HashSet<Integer> loadedTileIDs = new HashSet<Integer>();
	private TileCollection tiles = null;

	public QuickitemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	    AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
	    this.world = app.world;
        this.view = app.currentView.get();
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
			world.tileManager.setImageViewTileForUIIcon(item, TileManager.iconID_shop);
			item.setOnClickListener(this);
			item.setEmpty(true);
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
			QuickButton item = buttons[i];
			ItemType type = world.model.player.inventory.quickitem[i];
			if (type == null) {
				world.tileManager.setImageViewTileForUIIcon(item, TileManager.iconID_shop);
				item.setEmpty(true);
			} else {
				world.tileManager.setImageViewTile(item, type, tiles);
				item.setEmpty(!world.model.player.inventory.hasItem(type.id));
			}
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
}
