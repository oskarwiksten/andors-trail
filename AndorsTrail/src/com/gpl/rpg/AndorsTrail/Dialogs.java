package com.gpl.rpg.AndorsTrail;

import java.util.Arrays;
import java.util.HashSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.activity.ActorConditionInfoActivity;
import com.gpl.rpg.AndorsTrail.activity.BulkSelectionInterface;
import com.gpl.rpg.AndorsTrail.activity.ConversationActivity;
import com.gpl.rpg.AndorsTrail.activity.HeroinfoActivity_Inventory;
import com.gpl.rpg.AndorsTrail.activity.HeroinfoActivity_Skills;
import com.gpl.rpg.AndorsTrail.activity.HeroinfoActivity_Stats;
import com.gpl.rpg.AndorsTrail.activity.LoadSaveActivity;
import com.gpl.rpg.AndorsTrail.activity.MainActivity;
import com.gpl.rpg.AndorsTrail.activity.ItemInfoActivity;
import com.gpl.rpg.AndorsTrail.activity.LevelUpActivity;
import com.gpl.rpg.AndorsTrail.activity.MonsterEncounterActivity;
import com.gpl.rpg.AndorsTrail.activity.MonsterInfoActivity;
import com.gpl.rpg.AndorsTrail.activity.Preferences;
import com.gpl.rpg.AndorsTrail.activity.HeroinfoActivity_Quests;
import com.gpl.rpg.AndorsTrail.activity.ShopActivity;
import com.gpl.rpg.AndorsTrail.activity.SkillInfoActivity;
import com.gpl.rpg.AndorsTrail.activity.StartScreenActivity;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.controller.Controller;
import com.gpl.rpg.AndorsTrail.controller.ItemController;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.resource.TileStore;
import com.gpl.rpg.AndorsTrail.view.ItemContainerAdapter;

public final class Dialogs {
	
	private static void showDialogAndPause(Dialog d, final ViewContext context) { 
		showDialogAndPause(d, context, new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				context.gameRoundController.resume();
			}
		});
	}
	private static void showDialogAndPause(Dialog d, ViewContext context, OnDismissListener onDismiss) {
		context.gameRoundController.pause();
    	d.setOnDismissListener(onDismiss);
    	//setBlurrywindow(d);
		d.show();
	}

	/*
	private static void setBlurrywindow(Dialog d) {
		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}
	*/
	
	public static void showKeyArea(final MainActivity currentActivity, final ViewContext context, String phraseID) {
		showConversation(currentActivity, context, phraseID, "");
	}
	
	public static void showMapSign(final MainActivity currentActivity, final ViewContext context, String phraseID) {
		showConversation(currentActivity, context, phraseID, "");
	}
	
	public static void showConversation(final MainActivity currentActivity, final ViewContext context, final String phraseID, final Monster npc) {
		showConversation(currentActivity, context, phraseID, npc.monsterType.id);
	}
	
	private static void showConversation(final MainActivity currentActivity, final ViewContext context, final String phraseID, String monsterTypeID) {
		context.gameRoundController.pause();
		Intent intent = new Intent(currentActivity, ConversationActivity.class);
		Uri.Builder b = Uri.parse("content://com.gpl.rpg.AndorsTrail/conversation/" + phraseID).buildUpon();
		b.appendQueryParameter("monsterTypeID", monsterTypeID);
		intent.setData(b.build());
		currentActivity.startActivityForResult(intent, MainActivity.INTENTREQUEST_CONVERSATION);
	}
	
	public static void showMonsterEncounter(final MainActivity currentActivity, final ViewContext context, final Monster m) {
		context.gameRoundController.pause();
		Intent intent = new Intent(currentActivity, MonsterEncounterActivity.class);
		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/monsterencounter/" + m.monsterType.id));
		currentActivity.startActivityForResult(intent, MainActivity.INTENTREQUEST_MONSTERENCOUNTER);
	}

	public static void showMonsterInfo(final Activity currentActivity, String monsterTypeID) {
		Intent intent = new Intent(currentActivity, MonsterInfoActivity.class);
		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/monsterinfo/" + monsterTypeID));
		currentActivity.startActivity(intent);
	}
	
	public static void showMonsterLoot(final MainActivity mainActivity, final ViewContext context, final HashSet<Loot> lootBags, int totalExpThisFight) {
		// The real object of lootBags will get clear():ed by the caller after we have reached this function.
		// Therefore, we make a shallow copy of it to remember the Loot objects that should be modified.
		HashSet<Loot> copy = new HashSet<Loot>(lootBags);
		
		String msg = mainActivity.getString(R.string.dialog_monsterloot_message);
		showLoot(mainActivity, context, copy, totalExpThisFight, R.string.dialog_monsterloot_title, msg, false);
	}

	public static void showGroundLoot(final MainActivity mainActivity, final ViewContext context, final Loot loot) {
		String msg = "";
		if (!loot.items.isEmpty()) msg = mainActivity.getString(R.string.dialog_groundloot_message);
		showLoot(mainActivity, context, Arrays.asList(loot), 0, R.string.dialog_groundloot_title, msg, !loot.isVisible);
	}
	
	private static void showLoot(final MainActivity mainActivity, final ViewContext context, final Iterable<Loot> lootBags, final int exp, final int title, String msg, boolean isContainer) {
		//if (ItemController.updateLootVisibility(context, lootBags)) return;
		
		final Loot combinedLoot = new Loot();
		for (Loot l : lootBags) {
			combinedLoot.add(l);
		}
		
		if (exp > 0) {
			msg += mainActivity.getString(R.string.dialog_monsterloot_gainedexp, exp);
		}
		if (combinedLoot.gold > 0) {
			msg += mainActivity.getString(R.string.dialog_loot_foundgold, combinedLoot.gold);
		}
		
		if (!isContainer) {
			if (context.preferences.displayLoot != AndorsTrailPreferences.DISPLAYLOOT_DIALOG) {
				if (context.preferences.displayLoot == AndorsTrailPreferences.DISPLAYLOOT_TOAST) {
					int numItems = combinedLoot.items.countItems();
					if (numItems > 0) {
						msg += mainActivity.getString(R.string.dialog_loot_pickedupitems, numItems);
					}
					mainActivity.showToast(msg, Toast.LENGTH_LONG);
				}
				ItemController.pickupAll(lootBags, context.model);
	        	ItemController.updateLootVisibility(context, lootBags);
				context.gameRoundController.resume();
				return;
			}
		}
		
		final ListView itemList = new ListView(mainActivity);
		itemList.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));
		itemList.setPadding(20, 0, 20, 20);
		itemList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final int itemTypeID = (int) id;
				combinedLoot.items.removeItem(itemTypeID);
				for (Loot l : lootBags) {
					if (l.items.removeItem(itemTypeID)) break;
				}
				ItemType type = context.itemTypes.getItemType(itemTypeID);
				context.model.player.inventory.addItem(type);
				((ItemContainerAdapter) itemList.getAdapter()).notifyDataSetChanged();
			}
		});
		itemList.setAdapter(new ItemContainerAdapter(mainActivity, context.tileStore, combinedLoot.items));
		
		AlertDialog.Builder db = new AlertDialog.Builder(mainActivity)
        .setTitle(title)
        .setMessage(msg)
        .setIcon(new BitmapDrawable(context.tileStore.getBitmap(TileStore.iconID_groundbag)))
        .setNegativeButton(R.string.dialog_close, null)
        .setView(itemList);
		
		if (!combinedLoot.items.isEmpty()) {
			db.setPositiveButton(R.string.dialog_loot_pickall, new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	ItemController.pickupAll(lootBags, context.model);
	            }
	        });
		}
		
		final Dialog d = db.create();
		
		showDialogAndPause(d, context, new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				ItemController.updateLootVisibility(context, lootBags);
				context.gameRoundController.resume();
			}
		});
	}

	public static void showItemInfo(final Activity currentActivity, int itemTypeID, int actionType, String buttonText, boolean buttonEnabled, int inventorySlot) {
		Intent intent = new Intent(currentActivity, ItemInfoActivity.class);
		intent.putExtra("buttonText", buttonText);
		intent.putExtra("buttonEnabled", buttonEnabled);
		intent.putExtra("itemTypeID", itemTypeID);
		intent.putExtra("actionType", actionType);
		intent.putExtra("inventorySlot", inventorySlot);
		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/iteminfo/" + itemTypeID));
		currentActivity.startActivityForResult(intent, MainActivity.INTENTREQUEST_ITEMINFO);
	}
	public static void showLevelUp(final HeroinfoActivity_Stats currentActivity) {
		Intent intent = new Intent(currentActivity, LevelUpActivity.class);
		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/levelup"));
		currentActivity.startActivityForResult(intent, MainActivity.INTENTREQUEST_LEVELUP);
	}

	public static void showRest(final Activity currentActivity, final ViewContext viewContext, final MapObject area) {
		if (!viewContext.preferences.confirmRest) {
			Controller.ui_playerRested(currentActivity, viewContext, area);
			return;
		}
		Dialog d = new AlertDialog.Builder(currentActivity)
        .setTitle(R.string.dialog_rest_title)
        .setMessage(R.string.dialog_rest_confirm_message)
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
        		Controller.ui_playerRested(currentActivity, viewContext, area);
            }
        })
        .setNegativeButton(android.R.string.no, null)
        .create();

    	showDialogAndPause(d, viewContext);
	}
	public static void showRested(final Activity currentActivity, final ViewContext viewContext) {
		Dialog d = new AlertDialog.Builder(currentActivity)
        .setTitle(R.string.dialog_rest_title)
        .setMessage(R.string.dialog_rest_message)
        .setNeutralButton(android.R.string.ok, null)
        .create();

    	showDialogAndPause(d, viewContext);
	}

	public static void showNewVersion(final Activity currentActivity) {
		new AlertDialog.Builder(currentActivity)
        .setTitle(R.string.dialog_newversion_title)
        .setMessage(R.string.dialog_newversion_message)
        .setNeutralButton(android.R.string.ok, null)
        .show();
	}
	
	public static void showPreferences(final Activity currentActivity) {
		Intent intent = new Intent(currentActivity, Preferences.class);
		currentActivity.startActivityForResult(intent, MainActivity.INTENTREQUEST_PREFERENCES);
	}
	
	public static void showSave(final Activity currentActivity, final ViewContext viewContext) {
		viewContext.gameRoundController.pause();
    	Intent intent = new Intent(currentActivity, LoadSaveActivity.class);
    	intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/save"));
		currentActivity.startActivityForResult(intent, MainActivity.INTENTREQUEST_SAVEGAME);
	}
	
	public static void showLoad(final Activity currentActivity) {
		Intent intent = new Intent(currentActivity, LoadSaveActivity.class);
		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/load"));
		currentActivity.startActivityForResult(intent, StartScreenActivity.INTENTREQUEST_LOADGAME);
	}
	
	public static void showQuestLog(final Activity currentActivity) {
		Intent intent = new Intent(currentActivity, HeroinfoActivity_Quests.class);
		currentActivity.startActivity(intent);
	}
	
	public static void showActorConditionInfo(final Context context, ActorConditionType conditionType) {
		Intent intent = new Intent(context, ActorConditionInfoActivity.class);
		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/actorconditioninfo/" + conditionType.conditionTypeID));
		context.startActivity(intent);
	}
	
	public static void showBulkBuyingInterface(ShopActivity currentActivity, int itemTypeID, int totalAvailableAmount) {
		showBulkSelectionInterface(currentActivity, itemTypeID, totalAvailableAmount, BulkSelectionInterface.BULK_INTERFACE_BUY, MainActivity.INTENTREQUEST_BULKSELECT_BUY);
	}
	
	public static void showBulkSellingInterface(ShopActivity currentActivity, int itemTypeID, int totalAvailableAmount) {
		showBulkSelectionInterface(currentActivity, itemTypeID, totalAvailableAmount, BulkSelectionInterface.BULK_INTERFACE_SELL, MainActivity.INTENTREQUEST_BULKSELECT_SELL);
	}
	
	public static void showBulkDroppingInterface(HeroinfoActivity_Inventory currentActivity, int itemTypeID, int totalAvailableAmount) {
		showBulkSelectionInterface(currentActivity, itemTypeID, totalAvailableAmount, BulkSelectionInterface.BULK_INTERFACE_DROP, MainActivity.INTENTREQUEST_BULKSELECT_DROP);
	}
	
	public static void showBulkSelectionInterface(Activity currentActivity, int itemTypeID, int totalAvailableAmount, int interfaceType, int requestCode) {
		Intent intent = new Intent(currentActivity, BulkSelectionInterface.class);
		intent.putExtra("itemTypeID", itemTypeID);
		intent.putExtra("totalAvailableAmount", totalAvailableAmount);
		intent.putExtra("interfaceType", interfaceType);
		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/bulkselection/" + itemTypeID));
		currentActivity.startActivityForResult(intent, requestCode);
	}
	public static void showSkillInfo(HeroinfoActivity_Skills currentActivity, int skillID) {
		Intent intent = new Intent(currentActivity, SkillInfoActivity.class);
		intent.putExtra("skillID", skillID);
		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/showskillinfo/" + skillID));
		currentActivity.startActivityForResult(intent, MainActivity.INTENTREQUEST_SKILLINFO);
	}
}
