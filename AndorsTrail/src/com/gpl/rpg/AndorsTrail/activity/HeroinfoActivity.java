package com.gpl.rpg.AndorsTrail.activity;

import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.view.ItemContainerAdapter;
import com.gpl.rpg.AndorsTrail.view.ItemEffectsView;
import com.gpl.rpg.AndorsTrail.view.RangeBar;
import com.gpl.rpg.AndorsTrail.view.TraitsInfoView;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public final class HeroinfoActivity extends TabActivity {
	private WorldContext world;
	private ViewContext view;

	private Player player;
	private ItemContainer container;

	private ListView inventoryList;
	private Button levelUpButton;
	private Button questsButton;
    private TextView heroinfo_ap;
    private TextView heroinfo_movecost;
    private TraitsInfoView heroinfo_currenttraits;
    private ItemEffectsView heroinfo_itemeffects;
    private TextView heroinfo_currentconditions_title;
    private LinearLayout heroinfo_currentconditions;
    private TextView heroinfo_level;
    private TextView heroinfo_totalexperience;
    private TextView heroinfo_stats_gold;
    private TextView heroinfo_stats_attack;
    private TextView heroinfo_stats_defense;
    private RangeBar rangebar_hp;
    private RangeBar rangebar_exp;
	
	private final ImageView[] wornItemImage = new ImageView[Inventory.NUM_WORN_SLOTS];
	private final int[] defaultWornItemImageResourceIDs = new int[Inventory.NUM_WORN_SLOTS];
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        this.world = app.world;
        this.view = app.currentView.get();
        
        AndorsTrailApplication.setWindowParameters(this, app.preferences);
        
        this.player = world.model.player;
        
        setContentView(R.layout.heroinfo);
        
        Resources res = getResources();
        TabHost h = getTabHost();
        h.addTab(h.newTabSpec("char")
        		.setIndicator(res.getString(R.string.heroinfo_char), res.getDrawable(R.drawable.char_hero)) //TODO: Should change icon
        		.setContent(R.id.heroinfo_tab1));
        h.addTab(h.newTabSpec("inv")
        		.setIndicator(res.getString(R.string.heroinfo_inv), res.getDrawable(R.drawable.char_hero)) //TODO: Should change icon
        		.setContent(R.id.heroinfo_tab2));
        String t = world.model.uiSelections.selectedTabHeroInfo;
        if (t != null && t.length() > 0) {
        	h.setCurrentTabByTag(t);
        }
        h.setup();
        
        inventoryList = (ListView) h.findViewById(R.id.inventorylist_root);
        registerForContextMenu(inventoryList);
        inventoryList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showInventoryItemInfo((int) id);
			}
		});
        container = player.inventory;
        
        ImageView iv = (ImageView) findViewById(R.id.heroinfo_image);
        iv.setImageBitmap(world.tileStore.bitmaps[player.traits.iconID]);
        
        ((TextView) findViewById(R.id.heroinfo_title)).setText(player.traits.name);
        heroinfo_ap = (TextView) findViewById(R.id.heroinfo_ap);
        heroinfo_movecost = (TextView) findViewById(R.id.heroinfo_movecost);
        heroinfo_currenttraits = (TraitsInfoView) findViewById(R.id.heroinfo_currenttraits);
        heroinfo_itemeffects = (ItemEffectsView) findViewById(R.id.heroinfo_itemeffects);
        heroinfo_currentconditions_title = (TextView) findViewById(R.id.heroinfo_currentconditions_title);
        heroinfo_currentconditions = (LinearLayout) findViewById(R.id.heroinfo_currentconditions);
        heroinfo_stats_gold = (TextView) findViewById(R.id.heroinfo_stats_gold);
        heroinfo_stats_attack = (TextView) findViewById(R.id.heroinfo_stats_attack);
        heroinfo_stats_defense = (TextView) findViewById(R.id.heroinfo_stats_defense);
        heroinfo_level = (TextView) findViewById(R.id.heroinfo_level);
        heroinfo_totalexperience = (TextView) findViewById(R.id.heroinfo_totalexperience);
                
        rangebar_hp = (RangeBar) findViewById(R.id.heroinfo_healthbar);
        rangebar_hp.init(R.drawable.ui_progress_health, R.string.status_hp);
        rangebar_exp = (RangeBar) findViewById(R.id.heroinfo_expbar);
        rangebar_exp.init(R.drawable.ui_progress_exp, R.string.status_exp);
        
        levelUpButton = (Button) findViewById(R.id.heroinfo_levelup);
        levelUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Dialogs.showLevelUp(HeroinfoActivity.this);
				// We disable the button temporarily, so that there is no possibility 
				//  of clicking it again before the levelup activity has started.
				// See issue:
				//  http://code.google.com/p/andors-trail/issues/detail?id=42
				levelUpButton.setEnabled(false);
			}
		});
        questsButton = (Button) findViewById(R.id.heroinfo_quests);
        questsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Dialogs.showQuestLog(HeroinfoActivity.this);
			}
		});
        
        setWearSlot(ItemType.CATEGORY_WEAPON, R.id.heroinfo_worn_weapon, R.drawable.equip_weapon);
        setWearSlot(ItemType.CATEGORY_SHIELD, R.id.heroinfo_worn_shield, R.drawable.equip_shield);
        setWearSlot(ItemType.CATEGORY_WEARABLE_HEAD, R.id.heroinfo_worn_head, R.drawable.equip_head);
        setWearSlot(ItemType.CATEGORY_WEARABLE_BODY, R.id.heroinfo_worn_body, R.drawable.equip_body);
        setWearSlot(ItemType.CATEGORY_WEARABLE_FEET, R.id.heroinfo_worn_feet, R.drawable.equip_feet);
        setWearSlot(ItemType.CATEGORY_WEARABLE_NECK, R.id.heroinfo_worn_neck, R.drawable.equip_neck);
        setWearSlot(ItemType.CATEGORY_WEARABLE_HAND, R.id.heroinfo_worn_hand, R.drawable.equip_hand);
        setWearSlot(ItemType.CATEGORY_WEARABLE_RING, R.id.heroinfo_worn_ringleft, R.drawable.equip_ring);
        setWearSlot(ItemType.CATEGORY_WEARABLE_RING+1, R.id.heroinfo_worn_ringright, R.drawable.equip_ring);
        
        inventoryList.setAdapter(new ItemContainerAdapter(this, world.tileStore, container));
        
        update();
    }

	private void setWearSlot(final int inventorySlot, int viewId, int resourceId) {
    	final ImageView view = (ImageView) findViewById(viewId);
    	wornItemImage[inventorySlot] = view;
    	defaultWornItemImageResourceIDs[inventorySlot] = resourceId;
    	view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (player.inventory.isEmptySlot(inventorySlot)) return;
				view.setClickable(false); // Will be enabled again on update()
				showEquippedItemInfo(player.inventory.wear[inventorySlot], inventorySlot);
			}
    	});
	}
    
	@Override
    protected void onPause() {
        super.onPause();
        world.model.uiSelections.selectedTabHeroInfo = getTabHost().getCurrentTabTag();
    }
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MainActivity.INTENTREQUEST_ITEMINFO:
			if (resultCode != RESULT_OK) break;
			
			ItemType itemType = world.itemTypes.getItemType(data.getExtras().getInt("itemTypeID"));
			int actionType = data.getExtras().getInt("actionType");
			if (actionType == ItemInfoActivity.ITEMACTION_UNEQUIP) {
	        	view.itemController.unequipSlot(itemType, data.getExtras().getInt("inventorySlot"));
	        } else  if (actionType == ItemInfoActivity.ITEMACTION_EQUIP) {
	        	view.itemController.equipItem(itemType);
	        } else  if (actionType == ItemInfoActivity.ITEMACTION_USE) {
				view.itemController.useItem(itemType);	
			}
			break;
		case MainActivity.INTENTREQUEST_LEVELUP:
			break;
		}
		update();
	}

	private void update() {
        updateItemList();
        updateTraits();
        updateWorn();
        updateLevelup();
        updateConditions();
	}

	private void updateLevelup() {
		levelUpButton.setEnabled(player.canLevelup());
    }

	private void updateTraits() {
		heroinfo_level.setText(Integer.toString(player.level));
		heroinfo_totalexperience.setText(Integer.toString(player.totalExperience));
		heroinfo_ap.setText(player.ap.toString());
        heroinfo_movecost.setText(Integer.toString(player.traits.moveCost));
        heroinfo_stats_gold.setText(getResources().getString(R.string.heroinfo_gold, player.inventory.gold));
        heroinfo_stats_attack.setText(ItemType.describeAttackEffect(player.traits));
        heroinfo_stats_defense.setText(ItemType.describeBlockEffect(player.traits));
        rangebar_hp.update(player.health);
        rangebar_exp.update(player.levelExperience);
        
        heroinfo_currenttraits.update(player.traits);
		ArrayList<ItemTraits_OnUse> effects_hit = new ArrayList<ItemTraits_OnUse>();
		ArrayList<ItemTraits_OnUse> effects_kill = new ArrayList<ItemTraits_OnUse>();
		for (int i = 0; i < Inventory.NUM_WORN_SLOTS; ++i) {
			ItemType type = player.inventory.wear[i];
			if (type == null) continue;
			if (type.effects_hit != null) effects_hit.add(type.effects_hit);
			if (type.effects_kill != null) effects_kill.add(type.effects_kill);
		}
		if (effects_hit.isEmpty()) effects_hit = null;
		if (effects_kill.isEmpty()) effects_kill = null;
		heroinfo_itemeffects.update(null, null, effects_hit, effects_kill);
    }

    private void updateWorn() {
    	for(int slot = 0; slot < Inventory.NUM_WORN_SLOTS; ++slot) {
    		updateWornImage(wornItemImage[slot], defaultWornItemImageResourceIDs[slot], player.inventory.wear[slot]);
    	}
    }

    private void updateWornImage(ImageView view, int resourceIDEmptyImage, ItemType type) {
		if (type != null) {
			view.setImageBitmap(world.tileStore.bitmaps[type.iconID]);
		} else {
			view.setImageResource(resourceIDEmptyImage);
		}
		view.setClickable(true);
	}

	private void updateItemList() {
		((ItemContainerAdapter) inventoryList.getAdapter()).notifyDataSetChanged();
    }

	private void updateConditions() {
		if (player.conditions.isEmpty()) {
			heroinfo_currentconditions_title.setVisibility(View.GONE);
			heroinfo_currentconditions.setVisibility(View.GONE);
		} else {
			heroinfo_currentconditions_title.setVisibility(View.VISIBLE);
			heroinfo_currentconditions.setVisibility(View.VISIBLE);
			heroinfo_currentconditions.removeAllViews();
			for (ActorCondition c : player.conditions) {
				View v = View.inflate(this, R.layout.inventoryitemview, null);
				((ImageView) v.findViewById(R.id.inv_image)).setImageBitmap(world.tileStore.bitmaps[c.conditionType.iconID]);
				((TextView) v.findViewById(R.id.inv_text)).setText(c.describeEffect());
				heroinfo_currentconditions.addView(v);
			}
		}
	}
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
    	ItemType type = getSelectedItemType((AdapterContextMenuInfo) menuInfo);
		MenuInflater inflater = getMenuInflater();
		switch (v.getId()) {
		case R.id.inventorylist_root:
			inflater.inflate(R.menu.inventoryitem, menu);
			if (type.isUsable()) menu.findItem(R.id.inv_menu_use).setVisible(true);
			if (type.isEquippable()) menu.findItem(R.id.inv_menu_equip).setVisible(true);
			break;
		}
    }

    private int getSelectedID(AdapterContextMenuInfo info) {
    	return (int) info.id;
    }
    private ItemType getSelectedItemType(AdapterContextMenuInfo info) {
    	return world.itemTypes.getItemType(getSelectedID(info));
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.inv_menu_info:
			showInventoryItemInfo(getSelectedItemType(info));
			//context.controller.itemInfo(this, getSelectedItemType(info));
			break;
		case R.id.inv_menu_drop:
			view.itemController.dropItem(getSelectedItemType(info));
			break;
		case R.id.inv_menu_equip:
			view.itemController.equipItem(getSelectedItemType(info));
			break;
		/*case R.id.inv_menu_unequip:
			context.controller.unequipItem(this, getSelectedItemType(info));
			break;*/
		case R.id.inv_menu_use:
			view.itemController.useItem(getSelectedItemType(info));
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
    		int ap = world.model.player.reequipCost;
    		text = getResources().getString(R.string.iteminfo_action_unequip_ap, ap);
    		if (ap > 0) {
    			if (world.model.player.ap.current < ap) {
        			enabled = false;
        		}
    		}
    	} else {
    		text = getResources().getString(R.string.iteminfo_action_unequip);
    	}
    	Dialogs.showItemInfo(HeroinfoActivity.this, itemType.id, ItemInfoActivity.ITEMACTION_UNEQUIP, text, enabled, inventorySlot);
    }
    private void showInventoryItemInfo(int itemTypeID) { 
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
    			ap = world.model.player.reequipCost;
        		text = getResources().getString(R.string.iteminfo_action_equip_ap, ap);
        	} else {
        		text = getResources().getString(R.string.iteminfo_action_equip);
        	}
    		action = ItemInfoActivity.ITEMACTION_EQUIP;
        } else if (itemType.isUsable()) {
        	if (isInCombat) {
        		ap = world.model.player.useItemCost;
        		text = getResources().getString(R.string.iteminfo_action_use_ap, ap);
        	} else {
        		text = getResources().getString(R.string.iteminfo_action_use);
    		}
    		action = ItemInfoActivity.ITEMACTION_USE;
        }
    	if (isInCombat && ap > 0) {
    		if (world.model.player.ap.current < ap) {
    			enabled = false;
    		}
    	}
    	
    	Dialogs.showItemInfo(HeroinfoActivity.this, itemType.id, action, text, enabled, -1);
    }
    
}
