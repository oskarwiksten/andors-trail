package com.gpl.rpg.AndorsTrail.activity;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.ItemController;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCollection;
import com.gpl.rpg.AndorsTrail.view.ItemContainerAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public final class HeroinfoActivity_Inventory extends Activity {
	private WorldContext world;
	private ControllerContext controllers;
	private TileCollection wornTiles;

	private Player player;
	private ItemContainerAdapter inventoryListAdapter;

	private TextView heroinfo_stats_gold;
    private TextView heroinfo_stats_attack;
    private TextView heroinfo_stats_defense;
    
    private ItemType lastSelectedItem; // Workaround android bug #7139
	
	private final ImageView[] wornItemImage = new ImageView[Inventory.NUM_WORN_SLOTS];
	private final int[] defaultWornItemImageResourceIDs = new int[Inventory.NUM_WORN_SLOTS];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
        this.world = app.getWorld();
        this.controllers = app.getControllerContext();
        this.player = world.model.player;
        
        setContentView(R.layout.heroinfo_inventory);

		ListView inventoryList = (ListView) findViewById(R.id.inventorylist_root);
        registerForContextMenu(inventoryList);
        inventoryList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				ItemType itemType = inventoryListAdapter.getItem(position).itemType;
				showInventoryItemInfo(itemType.id);
			}
		});
		ItemContainer container = player.inventory;
        wornTiles = world.tileManager.loadTilesFor(player.inventory, getResources());
        inventoryListAdapter = new ItemContainerAdapter(this, world.tileManager, container, player, wornTiles);
        inventoryList.setAdapter(inventoryListAdapter);
        
        heroinfo_stats_gold = (TextView) findViewById(R.id.heroinfo_stats_gold);
        heroinfo_stats_attack = (TextView) findViewById(R.id.heroinfo_stats_attack);
        heroinfo_stats_defense = (TextView) findViewById(R.id.heroinfo_stats_defense);
        
        setWearSlot(Inventory.WEARSLOT_WEAPON, R.id.heroinfo_worn_weapon, R.drawable.equip_weapon);
        setWearSlot(Inventory.WEARSLOT_SHIELD, R.id.heroinfo_worn_shield, R.drawable.equip_shield);
        setWearSlot(Inventory.WEARSLOT_HEAD, R.id.heroinfo_worn_head, R.drawable.equip_head);
        setWearSlot(Inventory.WEARSLOT_BODY, R.id.heroinfo_worn_body, R.drawable.equip_body);
        setWearSlot(Inventory.WEARSLOT_FEET, R.id.heroinfo_worn_feet, R.drawable.equip_feet);
        setWearSlot(Inventory.WEARSLOT_NECK, R.id.heroinfo_worn_neck, R.drawable.equip_neck);
        setWearSlot(Inventory.WEARSLOT_HAND, R.id.heroinfo_worn_hand, R.drawable.equip_hand);
        setWearSlot(Inventory.WEARSLOT_LEFTRING, R.id.heroinfo_worn_ringleft, R.drawable.equip_ring);
        setWearSlot(Inventory.WEARSLOT_RIGHTRING, R.id.heroinfo_worn_ringright, R.drawable.equip_ring);
    }

    @Override
	protected void onResume() {
    	super.onResume();
    	update();
    }

	private void setWearSlot(final int inventorySlot, int viewId, int resourceId) {
    	final ImageView imageView = (ImageView) findViewById(viewId);
    	wornItemImage[inventorySlot] = imageView;
    	defaultWornItemImageResourceIDs[inventorySlot] = resourceId;
    	imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (player.inventory.isEmptySlot(inventorySlot)) return;
				imageView.setClickable(false); // Will be enabled again on update()
				showEquippedItemInfo(player.inventory.wear[inventorySlot], inventorySlot);
			}
		});
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MainActivity.INTENTREQUEST_ITEMINFO:
			if (resultCode != RESULT_OK) break;
			
			ItemType itemType = world.itemTypes.getItemType(data.getExtras().getString("itemTypeID"));
			int actionType = data.getExtras().getInt("actionType");
			if (actionType == ItemInfoActivity.ITEMACTION_UNEQUIP) {
	        	controllers.itemController.unequipSlot(itemType, data.getExtras().getInt("inventorySlot"));
	        } else  if (actionType == ItemInfoActivity.ITEMACTION_EQUIP) {
	    		int slot = suggestInventorySlot(itemType);
	        	controllers.itemController.equipItem(itemType, slot);
	        } else  if (actionType == ItemInfoActivity.ITEMACTION_USE) {
				controllers.itemController.useItem(itemType);
			}
			break;
		case MainActivity.INTENTREQUEST_BULKSELECT_DROP:
			if (resultCode != RESULT_OK) break;
			
			int quantity = data.getExtras().getInt("selectedAmount");
			String itemTypeID = data.getExtras().getString("itemTypeID");
			dropItem(itemTypeID, quantity);
			break;
		}
	}

	private int suggestInventorySlot(ItemType itemType) {
		int slot = itemType.category.inventorySlot;
		if (player.inventory.isEmptySlot(slot)) return slot;
		
		if (slot == Inventory.WEARSLOT_LEFTRING) return Inventory.WEARSLOT_RIGHTRING;
		if (itemType.isOffhandCapableWeapon()) {
			ItemType mainWeapon = player.inventory.wear[Inventory.WEARSLOT_WEAPON];
			if (mainWeapon != null && mainWeapon.isTwohandWeapon()) return slot;
			else if (player.inventory.isEmptySlot(Inventory.WEARSLOT_SHIELD)) return Inventory.WEARSLOT_SHIELD;
		}
		return slot;
	}

	private void dropItem(String itemTypeID, int quantity) {
		ItemType itemType = world.itemTypes.getItemType(itemTypeID);
		controllers.itemController.dropItem(itemType, quantity);
	}

	private void update() {
		updateTraits();
		updateWorn();
		updateItemList();
	}

	private void updateTraits() {
        heroinfo_stats_gold.setText(getResources().getString(R.string.heroinfo_gold, player.inventory.gold));
        
        StringBuilder sb = new StringBuilder(10);
        ItemController.describeAttackEffect(
        		player.getAttackChance(), 
        		player.getDamagePotential().current, 
        		player.getDamagePotential().max, 
        		player.getCriticalSkill(), 
        		player.getCriticalMultiplier(), 
        		sb);
        heroinfo_stats_attack.setText(sb.toString());
        
        sb = new StringBuilder(10);
        ItemController.describeBlockEffect(player.getBlockChance(), player.getDamageResistance(), sb);
        heroinfo_stats_defense.setText(sb.toString());
    }

    private void updateWorn() {
    	for(int slot = 0; slot < Inventory.NUM_WORN_SLOTS; ++slot) {
    		updateWornImage(wornItemImage[slot], defaultWornItemImageResourceIDs[slot], player.inventory.wear[slot]);
    	}
    }

    private void updateWornImage(ImageView imageView, int resourceIDEmptyImage, ItemType type) {
		if (type != null) {
			world.tileManager.setImageViewTile(getResources(), imageView, type, wornTiles);
		} else {
			imageView.setImageResource(resourceIDEmptyImage);
		}
		imageView.setClickable(true);
	}

	private void updateItemList() {
		inventoryListAdapter.notifyDataSetChanged();
    }

	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
    	ItemType type = getSelectedItemType((AdapterContextMenuInfo) menuInfo);
		MenuInflater inflater = getMenuInflater();
		switch (v.getId()) {
		case R.id.inventorylist_root:
			inflater.inflate(R.menu.inventoryitem, menu);
			if (type.isUsable()){
				menu.findItem(R.id.inv_menu_use).setVisible(true);
				menu.findItem(R.id.inv_menu_assign).setVisible(true);
			}
			if (type.isEquippable()) {
				menu.findItem(R.id.inv_menu_equip).setVisible(true);
				if (type.isOffhandCapableWeapon()) menu.findItem(R.id.inv_menu_equip_offhand).setVisible(true);
				else if (type.category.inventorySlot == Inventory.WEARSLOT_LEFTRING) menu.findItem(R.id.inv_menu_equip_offhand).setVisible(true);
			}
			break;
		}
		lastSelectedItem = null;
    }

    private ItemType getSelectedItemType(AdapterContextMenuInfo info) {
    	return inventoryListAdapter.getItem(info.position).itemType;
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	ItemType itemType;
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.inv_menu_info:
			showInventoryItemInfo(getSelectedItemType(info));
			//context.mapController.itemInfo(this, getSelectedItemType(info));
			break;
		case R.id.inv_menu_drop:
			String itemTypeID = getSelectedItemType(info).id;
			int quantity = player.inventory.getItemQuantity(itemTypeID);
			if (quantity > 1) {
				Dialogs.showBulkDroppingInterface(this, itemTypeID, quantity);
			} else {
				dropItem(itemTypeID, quantity);
			}
			break;
		case R.id.inv_menu_equip:
			itemType = getSelectedItemType(info);
			controllers.itemController.equipItem(itemType, itemType.category.inventorySlot);
			break;
		case R.id.inv_menu_equip_offhand:
			itemType = getSelectedItemType(info);
			controllers.itemController.equipItem(itemType, itemType.category.inventorySlot + 1);
			break;
		/*case R.id.inv_menu_unequip:
			context.mapController.unequipItem(this, getSelectedItemType(info));
			break;*/
		case R.id.inv_menu_use:
			controllers.itemController.useItem(getSelectedItemType(info));
			break;
		case R.id.inv_menu_assign:
			lastSelectedItem = getSelectedItemType(info);
			break;
		case R.id.inv_assign_slot1:
			controllers.itemController.setQuickItem(lastSelectedItem, 0);
			break;
		case R.id.inv_assign_slot2:
			controllers.itemController.setQuickItem(lastSelectedItem, 1);
			break;
		case R.id.inv_assign_slot3:
			controllers.itemController.setQuickItem(lastSelectedItem, 2);
			break;
		case R.id.inv_menu_movetop:
			player.inventory.sortToTop(getSelectedItemType(info).id);
			break;
		case R.id.inv_menu_movebottom:
			player.inventory.sortToBottom(getSelectedItemType(info).id);
			break;
		default:
			return super.onContextItemSelected(item);
		}
		update();
		return true;
    }

	private void showEquippedItemInfo(ItemType itemType, int inventorySlot) {
    	String text;
    	boolean enabled = true;
    	
    	if (world.model.uiSelections.isInCombat) {
    		int ap = world.model.player.getReequipCost();
    		text = getResources().getString(R.string.iteminfo_action_unequip_ap, ap);
    		if (ap > 0) {
    			enabled = world.model.player.hasAPs(ap);
    		}
    	} else {
    		text = getResources().getString(R.string.iteminfo_action_unequip);
    	}
    	Dialogs.showItemInfo(HeroinfoActivity_Inventory.this, itemType.id, ItemInfoActivity.ITEMACTION_UNEQUIP, text, enabled, inventorySlot);
    }
    private void showInventoryItemInfo(String itemTypeID) { 
    	showInventoryItemInfo(world.itemTypes.getItemType(itemTypeID)); 
    }
    private void showInventoryItemInfo(ItemType itemType) {
    	String text = "";
        int ap = 0;
        boolean enabled = true;
        int action = ItemInfoActivity.ITEMACTION_NONE;
        final boolean isInCombat = world.model.uiSelections.isInCombat;
    	if (itemType.isEquippable()) {
    		if (isInCombat) {
    			ap = world.model.player.getReequipCost();
        		text = getResources().getString(R.string.iteminfo_action_equip_ap, ap);
        	} else {
        		text = getResources().getString(R.string.iteminfo_action_equip);
        	}
    		action = ItemInfoActivity.ITEMACTION_EQUIP;
        } else if (itemType.isUsable()) {
        	if (isInCombat) {
        		ap = world.model.player.getUseItemCost();
        		text = getResources().getString(R.string.iteminfo_action_use_ap, ap);
        	} else {
        		text = getResources().getString(R.string.iteminfo_action_use);
    		}
    		action = ItemInfoActivity.ITEMACTION_USE;
        }
    	if (isInCombat && ap > 0) {
    		enabled = world.model.player.hasAPs(ap);
    	}
    	
    	Dialogs.showItemInfo(HeroinfoActivity_Inventory.this, itemType.id, action, text, enabled, -1);
    }
    
}