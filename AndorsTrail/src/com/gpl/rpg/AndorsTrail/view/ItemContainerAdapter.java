package com.gpl.rpg.AndorsTrail.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer.ItemEntry;
import com.gpl.rpg.AndorsTrail.resource.TileStore;

public final class ItemContainerAdapter extends ArrayAdapter<ItemEntry> {
	private final TileStore tileStore;
	
	public ItemContainerAdapter(Context context, TileStore tileStore, ItemContainer items) {
		super(context, 0, items.items);
		this.tileStore = tileStore;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//return new InventoryItemView(getContext(), getItem(position));
		final ItemEntry item = getItem(position);
		
		View result = convertView;
		if (result == null) {
			result = View.inflate(getContext(), R.layout.inventoryitemview, null);
		}
		
		((ImageView) result.findViewById(R.id.inv_image)).setImageBitmap(tileStore.getBitmap(item.itemType.iconID));
		((TextView) result.findViewById(R.id.inv_text)).setText(item.itemType.describeWearEffect(item.quantity));
		return result;
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).itemType.id;
	}
}
