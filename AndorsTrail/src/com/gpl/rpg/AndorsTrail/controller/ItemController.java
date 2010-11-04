package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.EffectCollection;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.item.Loot;

public final class ItemController {
	private static final int MARKET_PRICEFACTOR_PERCENT = 15;
	
	private final ViewContext view;
    private final WorldContext world;
    private final ModelContainer model;

	public ItemController(ViewContext context) {
    	this.view = context;
    	this.world = context;
    	this.model = world.model;
    }
	
	public void dropItem(ItemType type) {
    	model.player.inventory.removeItem(type.id, 1);
    	model.currentMap.itemDropped(type, 1, model.player.position);
    	//message(androidContext, androidContext.getResources().getString(R.string.inventory_item_dropped, t.name));
    }

	public void equipItem(ItemType type) {
		if (!type.isEquippable()) return;
		final Player player = model.player;
    	if (model.uiSelections.isInCombat) {
    		if (!player.useAPs(player.reequipCost)) return;
    	}
		
		int slot = type.category;
		if (slot == ItemType.CATEGORY_WEARABLE_RING) {
			if (!player.inventory.isEmptySlot(slot)) {
				++slot;
			}
		}
		
		if (!player.inventory.removeItem(type.id, 1)) return;
		
		if (!player.inventory.isEmptySlot(slot)) {
			player.inventory.addItem(player.inventory.wear[slot]);
		}
		player.inventory.wear[slot] = type;
		player.recalculateCombatTraits();
		
    	//message(androidContext, androidContext.getResources().getString(R.string.inventory_item_equipped, t.name));
    }

    public void unequipSlot(ItemType type, int slot) {
		if (!type.isEquippable()) return;
		final Player player = model.player;
		if (player.inventory.isEmptySlot(slot)) return;
    	
		if (model.uiSelections.isInCombat) {
    		if (!player.useAPs(player.reequipCost)) return;
    	}
    	
		player.inventory.addItem(player.inventory.wear[slot]);
		player.inventory.wear[slot] = null;
		player.recalculateCombatTraits();
		
    	//message(androidContext, androidContext.getResources().getString(R.string.inventory_item_unequipped, t.name));
    }
    
    public void useItem(ItemType type) {
    	if (!type.isUsable()) return;
    	final Player player = model.player;
    	if (model.uiSelections.isInCombat) {
    		if (!player.useAPs(player.useItemCost)) return;
    	}
    	
    	player.inventory.removeItem(type.id, 1);
    	applyUseEffect(player, type);
		
    	//TODO: provide feedback that the item has been used.
    	//context.mainActivity.message(androidContext.getResources().getString(R.string.inventory_item_used, type.name));
    }

	public void handleLootBag(Loot loot) {
    	Dialogs.showGroundLoot(view.mainActivity, view, loot);
    	consumeLoot(loot, model.player);
	}
	
	private void applyUseEffect(Actor actor, ItemType t) {
		if (t.effect_ap != null) {
			int value = ModelContainer.rollValue(t.effect_ap);
			actor.ap.add(value, false);
			view.effectController.startEffect(
					view.mainActivity.mainview
					, model.player.position
					, EffectCollection.EFFECT_RESTORE_AP
					, value);
		}
		if (t.effect_health != null) {
			int value = ModelContainer.rollValue(t.effect_health);
			actor.health.add(value, false);
			view.effectController.startEffect(
					view.mainActivity.mainview
					, model.player.position
					, EffectCollection.EFFECT_RESTORE_HP
					, value);
		}
	}

	public static void consumeLoot(Loot loot, Player player) {
		player.addExperience(loot.exp);
		loot.exp = 0;
		player.inventory.gold += loot.gold;
		loot.gold = 0;
	}
	
	public static int getBuyingPrice(Player player, ItemType itemType) {
		return itemType.baseMarketCost * (100 + MARKET_PRICEFACTOR_PERCENT) / 100;
	}
	public static int getSellingPrice(Player player, ItemType itemType) {
		return itemType.baseMarketCost * (100 - MARKET_PRICEFACTOR_PERCENT) / 100;
	}

	public static boolean canAfford(Player player, ItemType itemType) {
		return player.inventory.gold >= getBuyingPrice(player, itemType);
	}
	public static boolean canAfford(Player player, int price) {
		return player.inventory.gold >= price;
	}
	public static boolean maySellItem(Player player, ItemType itemType) {
		if (itemType.isQuestItem()) return false;
		return true;
	}
	public static void sell(Player player, ItemType itemType, ItemContainer merchant) {
		int price = getSellingPrice(player, itemType);
		player.inventory.gold += price;
		player.inventory.removeItem(itemType.id);
		merchant.addItem(itemType);
	}
	public static void buy(Player player, ItemType itemType, ItemContainer merchant) {
		int price = getBuyingPrice(player, itemType);
		if (!canAfford(player, price)) return;
		player.inventory.gold -= price;
		player.inventory.addItem(itemType);
		merchant.removeItem(itemType.id);
	}
}
