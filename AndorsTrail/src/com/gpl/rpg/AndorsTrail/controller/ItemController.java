package com.gpl.rpg.AndorsTrail.controller;

import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.view.MainView;

public final class ItemController {
	
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
		
		if (type.effects_equip != null && type.effects_equip.addedConditions != null) {
			ActorStatsController.removeOrAddConditionsFromEquippedItems(player);
		}
		ActorStatsController.recalculatePlayerCombatTraits(player);
		
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
		
		if (type.effects_equip != null && type.effects_equip.addedConditions != null) {
			ActorStatsController.removeOrAddConditionsFromEquippedItems(player);
		}
		ActorStatsController.recalculatePlayerCombatTraits(player);
		
    	//message(androidContext, androidContext.getResources().getString(R.string.inventory_item_unequipped, t.name));
    }
    
    public void useItem(ItemType type) {
    	if (!type.isUsable()) return;
    	final Player player = model.player;
    	if (model.uiSelections.isInCombat) {
    		if (!player.useAPs(player.useItemCost)) return;
    	}
    	
    	player.inventory.removeItem(type.id, 1);
    	view.actorStatsController.applyUseEffect(player, null, type.effects_use);
    	model.statistics.addItemUsage(type);
		
    	//TODO: provide feedback that the item has been used.
    	//context.mainActivity.message(androidContext.getResources().getString(R.string.inventory_item_used, type.name));
    }
    
	public void handleLootBag(Loot loot) {
    	Dialogs.showGroundLoot(view.mainActivity, view, loot);
    	consumeNonItemLoot(loot, model);
	}
	
	public static void applyInventoryEffects(Player player) {
		ItemType weapon = player.inventory.wear[ItemType.CATEGORY_WEAPON];
		if (weapon != null) {
			if (weapon.effects_equip != null) {
				CombatTraits weaponTraits = weapon.effects_equip.combatProficiency;
				if (weaponTraits != null) {
					player.traits.attackCost = weaponTraits.attackCost;
					player.traits.criticalMultiplier = weaponTraits.criticalMultiplier;
				}
			}
		}
		
		for (int i = 0; i < Inventory.NUM_WORN_SLOTS; ++i) {
			ItemType type = player.inventory.wear[i];
			if (type == null) continue;
			
			final boolean isWeapon = (i == ItemType.CATEGORY_WEAPON);
			ActorStatsController.applyAbilityEffects(player, type.effects_equip, isWeapon, 1);
		}
	}
	
	public static void recalculateHitEffectsFromWornItems(Player player) {
		ArrayList<ItemTraits_OnUse> effects = null;
		for (int i = 0; i < Inventory.NUM_WORN_SLOTS; ++i) {
			ItemType type = player.inventory.wear[i];
			if (type == null) continue;
			ItemTraits_OnUse e = type.effects_hit;
			if (e == null) continue;
			
			if (effects == null) effects = new ArrayList<ItemTraits_OnUse>();
			effects.add(e);
		}
		
		if (effects != null) {
			ItemTraits_OnUse[] effects_ = new ItemTraits_OnUse[effects.size()];
			effects_ = effects.toArray(effects_);
			player.traits.onHitEffects = effects_;
		} else {
			player.traits.onHitEffects = null;
		}
	}
	
	public static void consumeNonItemLoot(Loot loot, ModelContainer model) {
		// Experience will be given as soon as the monster is killed.
		model.player.inventory.gold += loot.gold;
		loot.gold = 0;
		removeEmptyLoot(loot, model);
	}
	public static void consumeNonItemLoot(Iterable<Loot> lootBags, ModelContainer model) {
		for(Loot l : lootBags) {
			consumeNonItemLoot(l, model);
		}
	}

	public static void pickupAll(Loot loot, ModelContainer model) {
		model.player.inventory.add(loot.items);
		consumeNonItemLoot(loot, model);
    	loot.clear();
	}
	public static void pickupAll(Iterable<Loot> lootBags, ModelContainer model) {
		for(Loot l : lootBags) {
			pickupAll(l, model);
		}
	}
	public static boolean removeEmptyLoot(Loot loot, ModelContainer model) {
		if (!loot.hasItems()) {
			model.currentMap.removeGroundLoot(loot);
			return true; // The bag was removed.
		} else {
			return false;
		}
	}
	
	public static boolean updateLootVisibility(final ViewContext context, final Iterable<Loot> lootBags) {
		boolean isEmpty = true;
		for (Loot l : lootBags) {
			if (!updateLootVisibility(context, l)) isEmpty = false;
		}
		return isEmpty;
	}
	
	public static boolean updateLootVisibility(final ViewContext context, final Loot loot) {
		final boolean isBagRemoved = removeEmptyLoot(loot, context.model);
		context.mainActivity.redrawTile(loot.position, MainView.REDRAW_TILE_BAG);
		return isBagRemoved;
	}
	
	public static int getBuyingPrice(Player player, ItemType itemType) {
		return itemType.baseMarketCost * (100 + Constants.MARKET_PRICEFACTOR_PERCENT) / 100;
	}
	public static int getSellingPrice(Player player, ItemType itemType) {
		return itemType.baseMarketCost * (100 - Constants.MARKET_PRICEFACTOR_PERCENT) / 100;
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
	public static void buy(ModelContainer model, Player player, ItemType itemType, ItemContainer merchant) {
		int price = getBuyingPrice(player, itemType);
		if (!canAfford(player, price)) return;
		player.inventory.gold -= price;
		player.inventory.addItem(itemType);
		merchant.removeItem(itemType.id);
		model.statistics.addGoldSpent(price);
	}

	public void quickitemUse(int quickSlotId) {
		useItem(model.player.inventory.quickitem[quickSlotId]);
		view.mainActivity.statusview.updateStatus();
		view.mainActivity.combatview.updatePlayerAP(model.player.ap);
		view.mainActivity.refreshQuickitems();
	}

	public void setQuickItem(ItemType itemType, int quickSlotId) {
		model.player.inventory.quickitem[quickSlotId] = itemType;
		view.mainActivity.refreshQuickitems();
	}
}
