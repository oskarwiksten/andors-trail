package com.gpl.rpg.AndorsTrail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

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
import com.gpl.rpg.AndorsTrail.activity.ShopActivity;
import com.gpl.rpg.AndorsTrail.activity.SkillInfoActivity;
import com.gpl.rpg.AndorsTrail.activity.StartScreenActivity;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;
import com.gpl.rpg.AndorsTrail.view.ItemContainerAdapter;

public final class Dialogs {
	
	private static void showDialogAndPause(Dialog d, final ViewContext context) { 
		showDialogAndPause(d, context, null);
	}
	private static void showDialogAndPause(Dialog d, final ViewContext context, final OnDismissListener onDismiss) {
		context.gameRoundController.pause();
    	d.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				if (onDismiss != null) onDismiss.onDismiss(arg0);
				context.gameRoundController.resume();
			}
		});
    	//setBlurrywindow(d);
		d.show();
	}

	/*
	private static void setBlurrywindow(Dialog d) {
		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}
	*/
	
	public static void showKeyArea(final MainActivity currentActivity, final ViewContext context, String phraseID) {
		showConversation(currentActivity, context, phraseID, null);
	}
	
	public static void showMapSign(final MainActivity currentActivity, final ViewContext context, String phraseID) {
		showConversation(currentActivity, context, phraseID, null);
	}
	
	public static void showConversation(final MainActivity currentActivity, final ViewContext context, final String phraseID, final Monster npc) {
		context.gameRoundController.pause();
		Intent intent = new Intent(currentActivity, ConversationActivity.class);
		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/conversation/" + phraseID));
		addMonsterIdentifiers(intent, npc);
		currentActivity.startActivityForResult(intent, MainActivity.INTENTREQUEST_CONVERSATION);
	}
	
	public static void addMonsterIdentifiers(Intent intent, Monster monster) {
		if (monster == null) return;
		intent.putExtra("x", monster.position.x);
		intent.putExtra("y", monster.position.y);
	}
	
	public static Monster getMonsterFromIntent(Intent intent, final WorldContext world) {
		Bundle params = intent.getExtras();
		if (params == null) return null;
		if (!params.containsKey("x")) return null;
		int x = params.getInt("x");
        int y = params.getInt("y");
        return world.model.currentMap.getMonsterAt(x, y);
	}
	
	public static void showMonsterEncounter(final MainActivity currentActivity, final ViewContext context, final Monster monster) {
		context.gameRoundController.pause();
		Intent intent = new Intent(currentActivity, MonsterEncounterActivity.class);
		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/monsterencounter"));
		addMonsterIdentifiers(intent, monster);
		currentActivity.startActivityForResult(intent, MainActivity.INTENTREQUEST_MONSTERENCOUNTER);
	}

	public static void showMonsterInfo(final Context context, final Monster monster) {
		Intent intent = new Intent(context, MonsterInfoActivity.class);
		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/monsterinfo"));
		addMonsterIdentifiers(intent, monster);
		context.startActivity(intent);
	}
	
	public static String getGroundLootMessage(final Context ctx, final Loot loot) {
		StringBuilder sb = new StringBuilder(60);
		if (!loot.items.isEmpty()) {
			sb.append(ctx.getString(R.string.dialog_groundloot_message));
		}
		if (loot.gold > 0) {
			sb.append(' ');
			sb.append(ctx.getString(R.string.dialog_loot_foundgold, loot.gold));
		}
		appendLootMessage(ctx, loot, sb);
		return sb.toString();
	}
	public static String getMonsterLootMessage(final Context ctx, final Loot combinedLoot, final int exp) {
		StringBuilder sb = new StringBuilder(60);
		sb.append(ctx.getString(R.string.dialog_monsterloot_message));
		
		if (exp > 0) {
			sb.append(' ');
			sb.append(ctx.getString(R.string.dialog_monsterloot_gainedexp, exp));
		}
		appendLootMessage(ctx, combinedLoot, sb);
		return sb.toString();
	}
	private static void appendLootMessage(final Context ctx, final Loot loot, final StringBuilder sb) {
		if (loot.gold > 0) {
			sb.append(' ');
			sb.append(ctx.getString(R.string.dialog_loot_foundgold, loot.gold));
		}
		int numItems = loot.items.countItems();
		if (numItems == 1) {
			sb.append(' ');
			sb.append(ctx.getString(R.string.dialog_loot_pickedupitem));
		} else if (numItems > 1){
			sb.append(' ');
			sb.append(ctx.getString(R.string.dialog_loot_pickedupitems, numItems));
		}
	}
	
	public static void showMonsterLoot(final MainActivity mainActivity, final ViewContext view, final WorldContext world, final Collection<Loot> lootBags, final Loot combinedLoot, final String msg) {
		// CombatController will do killedMonsterBags.clear() after this method has been called,
		// so we need to keep the list of objects. Therefore, we create a shallow copy of the list of bags.
		ArrayList<Loot> bags = new ArrayList<Loot>(lootBags);
		showLoot(mainActivity, view, world, combinedLoot, bags, R.string.dialog_monsterloot_title, msg);
	}

	public static void showGroundLoot(final MainActivity mainActivity, final ViewContext view, final WorldContext world, final Loot loot, final String msg) {
		showLoot(mainActivity, view, world, loot, Collections.singletonList(loot), R.string.dialog_groundloot_title, msg);
 	}
	
	private static void showLoot(final MainActivity mainActivity, final ViewContext view, final WorldContext world, final Loot combinedLoot, final Iterable<Loot> lootBags, final int title, final String msg) {
		final ListView itemList = new ListView(mainActivity);
		itemList.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
		itemList.setPadding(20, 0, 20, 20);
		itemList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				
				final String itemTypeID = ((ItemContainerAdapter) parent.getAdapter()).getItem(position).itemType.id;
				for (Loot l : lootBags) {
					if (l.items.removeItem(itemTypeID)) {
						view.itemController.removeLootBagIfEmpty(l);
						break;
					}
				}
				combinedLoot.items.removeItem(itemTypeID);
				ItemType type = world.itemTypes.getItemType(itemTypeID);
				world.model.player.inventory.addItem(type);
				((ItemContainerAdapter) itemList.getAdapter()).notifyDataSetChanged();
			}
		});
		itemList.setAdapter(new ItemContainerAdapter(mainActivity, world.tileManager, combinedLoot.items, world.model.player));
		
		AlertDialog.Builder db = new AlertDialog.Builder(mainActivity)
        .setTitle(title)
        .setMessage(msg)
        .setIcon(new BitmapDrawable(mainActivity.getResources(), world.tileManager.preloadedTiles.getBitmap(TileManager.iconID_groundbag)))
        .setNegativeButton(R.string.dialog_close, null)
        .setView(itemList);
		
		if (!combinedLoot.items.isEmpty()) {
			db.setPositiveButton(R.string.dialog_loot_pickall, new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	view.itemController.pickupAll(lootBags);
	            }
	        });
		}
		
		final Dialog d = db.create();
		
		showDialogAndPause(d, view, new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				view.itemController.removeLootBagIfEmpty(lootBags);
			}
		});
	}

	public static void showItemInfo(final Activity currentActivity, String itemTypeID, int actionType, String buttonText, boolean buttonEnabled, int inventorySlot) {
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

	public static void showConfirmRest(final Activity currentActivity, final ViewContext viewContext, final MapObject area) {
		Dialog d = new AlertDialog.Builder(currentActivity)
        .setTitle(R.string.dialog_rest_title)
        .setMessage(R.string.dialog_rest_confirm_message)
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	viewContext.controller.rest(area);
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
	
	public static void showSave(final MainActivity mainActivity, final ViewContext viewContext, final WorldContext world) {
		if (world.model.uiSelections.isInCombat) {
			mainActivity.showToast(mainActivity.getResources().getString(R.string.menu_save_saving_not_allowed_in_combat), Toast.LENGTH_SHORT);
			return;
		}
		viewContext.gameRoundController.pause();
    	Intent intent = new Intent(mainActivity, LoadSaveActivity.class);
    	intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/save"));
    	mainActivity.startActivityForResult(intent, MainActivity.INTENTREQUEST_SAVEGAME);
	}
	
	public static void showLoad(final Activity currentActivity) {
		Intent intent = new Intent(currentActivity, LoadSaveActivity.class);
		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/load"));
		currentActivity.startActivityForResult(intent, StartScreenActivity.INTENTREQUEST_LOADGAME);
	}
	
	public static void showActorConditionInfo(final Context context, ActorConditionType conditionType) {
		Intent intent = new Intent(context, ActorConditionInfoActivity.class);
		intent.setData(Uri.parse("content://com.gpl.rpg.AndorsTrail/actorconditioninfo/" + conditionType.conditionTypeID));
		context.startActivity(intent);
	}
	
	public static void showBulkBuyingInterface(ShopActivity currentActivity, String itemTypeID, int totalAvailableAmount) {
		showBulkSelectionInterface(currentActivity, itemTypeID, totalAvailableAmount, BulkSelectionInterface.BULK_INTERFACE_BUY, MainActivity.INTENTREQUEST_BULKSELECT_BUY);
	}
	
	public static void showBulkSellingInterface(ShopActivity currentActivity, String itemTypeID, int totalAvailableAmount) {
		showBulkSelectionInterface(currentActivity, itemTypeID, totalAvailableAmount, BulkSelectionInterface.BULK_INTERFACE_SELL, MainActivity.INTENTREQUEST_BULKSELECT_SELL);
	}
	
	public static void showBulkDroppingInterface(HeroinfoActivity_Inventory currentActivity, String itemTypeID, int totalAvailableAmount) {
		showBulkSelectionInterface(currentActivity, itemTypeID, totalAvailableAmount, BulkSelectionInterface.BULK_INTERFACE_DROP, MainActivity.INTENTREQUEST_BULKSELECT_DROP);
	}

    private static void showBulkSelectionInterface(Activity currentActivity, String itemTypeID, int totalAvailableAmount, int interfaceType, int requestCode) {
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
