package com.gpl.rpg.AndorsTrail.activity;

import java.util.HashSet;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.ItemController;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCollection;
import com.gpl.rpg.AndorsTrail.view.ShopItemContainerAdapter;
import com.gpl.rpg.AndorsTrail.view.ShopItemContainerAdapter.OnContainerItemClickedListener;

public final class ShopActivity extends TabActivity implements OnContainerItemClickedListener {
	private WorldContext world;
	private Player player;

	private ListView shoplist_buy;
	private ListView shoplist_sell;
	private ItemContainer container_buy;
	private TextView shop_buy_gc;
	private TextView shop_sell_gc;
	private ShopItemContainerAdapter buyListAdapter;
	private ShopItemContainerAdapter sellListAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
        this.world = app.world;
        this.player = world.model.player;
        
        AndorsTrailApplication.setWindowParameters(this, app.preferences);
        
        final Monster npc = Dialogs.getMonsterFromIntent(getIntent(), world);
        final Player player = world.model.player;
        
        setContentView(R.layout.shop);
        
        final Resources res = getResources();
        
        TabHost h = getTabHost();
        h.addTab(h.newTabSpec("buy")
        		.setIndicator(res.getString(R.string.shop_buy))
        		.setContent(R.id.shop_tab1));
        h.addTab(h.newTabSpec("sell")
        		.setIndicator(res.getString(R.string.shop_sell))
        		.setContent(R.id.shop_tab2));
        h.setup();
        shop_buy_gc = (TextView) h.findViewById(R.id.shop_buy_gc);
        shop_sell_gc = (TextView) h.findViewById(R.id.shop_sell_gc);
        
        shoplist_buy = (ListView) h.findViewById(R.id.shop_buy_list);
        shoplist_sell = (ListView) h.findViewById(R.id.shop_sell_list);
        
        container_buy = npc.getShopItems(player);
        
        HashSet<Integer> iconIDs = world.tileManager.getTileIDsFor(container_buy);
        iconIDs.addAll(world.tileManager.getTileIDsFor(player.inventory));
        TileCollection tiles = world.tileManager.loadTilesFor(iconIDs, res);
        buyListAdapter = new ShopItemContainerAdapter(this, tiles, world.tileManager, player, container_buy, this, false);
        sellListAdapter = new ShopItemContainerAdapter(this, tiles, world.tileManager, player, player.inventory, this, true);
		shoplist_buy.setAdapter(buyListAdapter);
        shoplist_sell.setAdapter(sellListAdapter);
        
        update();
    }

	@Override
	public void onItemActionClicked(int position, ItemType itemType, boolean isSelling) {
		if (isSelling) {
			showSellingInterface(itemType);
		} else {
			showBuyingInterface(itemType);
		}
	}

	@Override
	public void onItemInfoClicked(int position, ItemType itemType, boolean isSelling) {
		int price;
		int resid;
		boolean enableButton = true;
		int action;
		if (isSelling) {
			resid = R.string.shop_sellitem;
			action = ItemInfoActivity.ITEMACTION_SELL;
			price = ItemController.getSellingPrice(player, itemType);
			enableButton = ItemController.maySellItem(player, itemType);
		} else {
			resid = R.string.shop_buyitem;
			action = ItemInfoActivity.ITEMACTION_BUY;
			price = ItemController.getBuyingPrice(player, itemType);
			enableButton = ItemController.canAfford(player, price);
		}
		String text = getResources().getString(resid, price);
		Dialogs.showItemInfo(ShopActivity.this, itemType.id, action, text, enableButton, -1);
	}
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MainActivity.INTENTREQUEST_ITEMINFO:
			if (resultCode != RESULT_OK) return;
			
			ItemType itemType = world.itemTypes.getItemType(data.getExtras().getString("itemTypeID"));
			int actionType = data.getExtras().getInt("actionType");
			if (actionType == ItemInfoActivity.ITEMACTION_BUY) {
				showBuyingInterface(itemType);
	        } else  if (actionType == ItemInfoActivity.ITEMACTION_SELL) {
	        	showSellingInterface(itemType);
			}
			break;
		case MainActivity.INTENTREQUEST_BULKSELECT_BUY:
			if (resultCode == Activity.RESULT_OK) {
				int quantity = data.getExtras().getInt("selectedAmount");
				String itemTypeID = data.getExtras().getString("itemTypeID");
				buy(itemTypeID, quantity);
			}
			break;
		case MainActivity.INTENTREQUEST_BULKSELECT_SELL:
			if (resultCode == Activity.RESULT_OK) {
				int quantity = data.getExtras().getInt("selectedAmount");
				String itemTypeID = data.getExtras().getString("itemTypeID");
				sell(itemTypeID, quantity);
			}
			break;
		}
	}

    private void showSellingInterface(ItemType itemType) {
    	Dialogs.showBulkSellingInterface(this, itemType.id, player.inventory.getItemQuantity(itemType.id));
	}

	private void showBuyingInterface(ItemType itemType) {
		Dialogs.showBulkBuyingInterface(this, itemType.id, container_buy.getItemQuantity(itemType.id));
	}
	
	private void buy(String itemTypeID, int quantity) {
		ItemType itemType = world.itemTypes.getItemType(itemTypeID);
		ItemController.buy(world.model, player, itemType, container_buy, quantity);
		final String msg = getResources().getString(R.string.shop_item_bought, itemType.name);
		displayStoreAction(msg);
	}

	private void sell(String itemTypeID, int quantity) {
		ItemType itemType = world.itemTypes.getItemType(itemTypeID);
		ItemController.sell(player, itemType, container_buy, quantity);
		final String msg = getResources().getString(R.string.shop_item_sold, itemType.name);
		displayStoreAction(msg);
	}
	
	private Toast lastToast = null;
	private void displayStoreAction(final String msg) {
		if (lastToast != null) {
			lastToast.setText(msg);
		} else {
			lastToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		}
		lastToast.show();
		update();
	}
	
	@Override
    protected void onPause() {
		super.onPause();
    	lastToast = null;
    }

	private void update() {
        updateBuyItemList();
        updateSellItemList();
        updateGc();
	}

	private void updateGc() {
		String gc = getResources().getString(R.string.shop_yourgold, player.inventory.gold);
		shop_buy_gc.setText(gc);
		shop_sell_gc.setText(gc);		
	}

	private void updateBuyItemList() {
		buyListAdapter.notifyDataSetChanged();
    }
	private void updateSellItemList() {
		sellListAdapter.notifyDataSetChanged();
    }
}
