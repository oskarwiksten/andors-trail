package com.gpl.rpg.AndorsTrail.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.activity.ItemInfoActivity;
import com.gpl.rpg.AndorsTrail.controller.ItemController;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;

public final class ShopActivity_Sell extends ShopActivityFragment {

	@Override
	protected boolean isSellingInterface() {
		return true;
	}

	@Override
	public void onItemActionClicked(int position, ItemType itemType) {
		showSellingInterface(itemType);
	}

	@Override
	public void onItemInfoClicked(int position, ItemType itemType) {
		int price = ItemController.getSellingPrice(player, itemType);
		boolean enableButton = ItemController.maySellItem(player, itemType);
		String text = getResources().getString(R.string.shop_sellitem, price);
		Intent intent = Dialogs.getIntentForItemInfo(getActivity(), itemType.id, ItemInfoActivity.ItemInfoAction.sell, text, enableButton, null);
		startActivityForResult(intent, INTENTREQUEST_ITEMINFO);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) return;

		ItemType itemType = world.itemTypes.getItemType(data.getExtras().getString("itemTypeID"));
		switch (requestCode) {
		case INTENTREQUEST_ITEMINFO:
			showSellingInterface(itemType);
			break;
		case INTENTREQUEST_BULKSELECT:
			int quantity = data.getExtras().getInt("selectedAmount");
			sell(itemType, quantity);
			break;
		}
		update();
	}

	private void showSellingInterface(ItemType itemType) {
		Intent intent = Dialogs.getIntentForBulkSellingInterface(getActivity(), itemType.id, player.inventory.getItemQuantity(itemType.id));
		startActivityForResult(intent, INTENTREQUEST_BULKSELECT);
	}

	private void sell(ItemType itemType, int quantity) {
		if (!ItemController.sell(player, itemType, shopInventory, quantity)) return;
		final String msg = getResources().getString(R.string.shop_item_sold, itemType.getName(player));
		displayStoreAction(msg);
	}
}
