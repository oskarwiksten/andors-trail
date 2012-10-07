package com.gpl.rpg.AndorsTrail.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.controller.ItemController;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer.ItemEntry;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCollection;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;

public final class ItemContainerAdapter extends ArrayAdapter<ItemEntry> {
	private final TileManager tileManager;
	private final TileCollection tileCollection;
	
	public ItemContainerAdapter(Context context, TileManager tileManager, ItemContainer items) {
		this(context, tileManager, items, tileManager.loadTilesFor(items, context.getResources()));
	}
	public ItemContainerAdapter(Context context, TileManager tileManager, ItemContainer items, TileCollection tileCollection) {
		super(context, 0, items.items);
		this.tileManager = tileManager;
		this.tileCollection = tileCollection;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ItemEntry item = getItem(position);
		
		View result = convertView;
		if (result == null) {
			result = View.inflate(getContext(), R.layout.inventoryitemview, null);
		}
		
		tileManager.setImageViewTile((ImageView) result.findViewById(R.id.inv_image), item.itemType, tileCollection);
		((TextView) result.findViewById(R.id.inv_text)).setText(ItemController.describeItemForListView(item));
		return result;
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).itemType.id.hashCode();
	}
}
