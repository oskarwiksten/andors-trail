package com.gpl.rpg.AndorsTrail.controller;

import java.util.ArrayList;

import android.view.View;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
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
	
	public void dropItem(ItemType type, int quantity) {
		if (model.player.inventory.getItemQuantity(type.id) < quantity) return;
    	model.player.inventory.removeItem(type.id, quantity);
    	model.currentMap.itemDropped(type, quantity, model.player.position);
    }

	public void equipItem(ItemType type, int slot) {
		if (!type.isEquippable()) return;
		final Player player = model.player;
    	if (model.uiSelections.isInCombat) {
    		if (!player.useAPs(player.reequipCost)) return;
    	}
		
		if (!player.inventory.removeItem(type.id, 1)) return;
		
		unequipSlot(player, slot);
		if (type.isTwohandWeapon()) unequipSlot(player, Inventory.WEARSLOT_SHIELD);
		else if (slot == Inventory.WEARSLOT_SHIELD) {
			ItemType currentWeapon = player.inventory.wear[Inventory.WEARSLOT_WEAPON];
			if (currentWeapon != null && currentWeapon.isTwohandWeapon()) unequipSlot(player, Inventory.WEARSLOT_WEAPON);
		}
			
		player.inventory.wear[slot] = type;
		ActorStatsController.addConditionsFromEquippedItem(player, type);
		ActorStatsController.recalculatePlayerCombatTraits(player);
    }

   	public void unequipSlot(ItemType type, int slot) {
		if (!type.isEquippable()) return;
		final Player player = model.player;
		if (player.inventory.isEmptySlot(slot)) return;
    	
		if (model.uiSelections.isInCombat) {
    		if (!player.useAPs(player.reequipCost)) return;
    	}
    	
		unequipSlot(player, slot);
		ActorStatsController.recalculatePlayerCombatTraits(player);
    }

   	private static void unequipSlot(Player player, int slot) {
   		ItemType removedItemType = player.inventory.wear[slot];
   		if (removedItemType == null) return;
		player.inventory.addItem(removedItemType);
		player.inventory.wear[slot] = null;
		ActorStatsController.removeConditionsFromUnequippedItem(player, removedItemType);
    }
    
    public void useItem(ItemType type) {
    	if (!type.isUsable()) return;
    	final Player player = model.player;
    	if (model.uiSelections.isInCombat) {
    		if (!player.useAPs(player.useItemCost)) return;
    	}
    	
    	if (!player.inventory.removeItem(type.id, 1)) return;
    	
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
		ItemType weapon = getMainWeapon(player);
		if (weapon != null) {
			player.combatTraits.attackCost = 0;
			player.combatTraits.criticalMultiplier = weapon.effects_equip.combatProficiency.criticalMultiplier;
		}
		
		applyInventoryEffects(player, Inventory.WEARSLOT_WEAPON);
		applyInventoryEffects(player, Inventory.WEARSLOT_SHIELD);
		applyInventoryEffects(player, Inventory.WEARSLOT_HEAD);
		applyInventoryEffects(player, Inventory.WEARSLOT_BODY);
		applyInventoryEffects(player, Inventory.WEARSLOT_HAND);
		applyInventoryEffects(player, Inventory.WEARSLOT_FEET);
		applyInventoryEffects(player, Inventory.WEARSLOT_NECK);
		applyInventoryEffects(player, Inventory.WEARSLOT_LEFTRING);
		applyInventoryEffects(player, Inventory.WEARSLOT_RIGHTRING);
		
		SkillController.applySkillEffectsFromItemProficiencies(player);
		SkillController.applySkillEffectsFromFightingStyles(player);
	}
	public static ItemType getMainWeapon(Player player) {
		ItemType itemType = player.inventory.wear[Inventory.WEARSLOT_WEAPON];
		if (itemType != null) return itemType;
		itemType = player.inventory.wear[Inventory.WEARSLOT_SHIELD];
		if (itemType != null && itemType.isWeapon()) return itemType;
		return null;
	}

	private static void applyInventoryEffects(Player player, int slot) {
		ItemType type = player.inventory.wear[slot];
		if (type == null) return;
		if (slot == Inventory.WEARSLOT_SHIELD) {
			ItemType mainHandItem = player.inventory.wear[Inventory.WEARSLOT_WEAPON];
			// The stats for off-hand weapons will be added later in SkillController.applySkillEffectsFromFightingStyles
			if (SkillController.isDualWielding(mainHandItem, type)) return;
		}
		ActorStatsController.applyAbilityEffects(player, type.effects_equip, 1);
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
			player.actorTraits.onHitEffects = effects_;
		} else {
			player.actorTraits.onHitEffects = null;
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
	
	private static int getMarketPriceFactor(Player player) {
		return Constants.MARKET_PRICEFACTOR_PERCENT 
			- player.getSkillLevel(SkillCollection.SKILL_BARTER) * SkillCollection.PER_SKILLPOINT_INCREASE_BARTER_PRICEFACTOR_PERCENTAGE;
	}
	public static int getBuyingPrice(Player player, ItemType itemType) {
		return itemType.baseMarketCost + itemType.baseMarketCost * getMarketPriceFactor(player) / 100;
	}
	public static int getSellingPrice(Player player, ItemType itemType) {
		return itemType.baseMarketCost - itemType.baseMarketCost * getMarketPriceFactor(player) / 100;
	}

	public static boolean canAfford(Player player, ItemType itemType) {
		return player.inventory.gold >= getBuyingPrice(player, itemType);
	}
	public static boolean canAfford(Player player, int price) {
		return player.inventory.gold >= price;
	}
	public static boolean maySellItem(Player player, ItemType itemType) {
		if (!itemType.isSellable()) return false;
		return true;
	}
	public static void sell(Player player, ItemType itemType, ItemContainer merchant, int quantity) {
		int price = getSellingPrice(player, itemType) * quantity;
		if (player.inventory.removeItem(itemType.id, quantity)) {
			player.inventory.gold += price;
			merchant.addItem(itemType, quantity);
		}
	}
	public static void buy(ModelContainer model, Player player, ItemType itemType, ItemContainer merchant, int quantity) {
		int price = getBuyingPrice(player, itemType) * quantity;
		if (!canAfford(player, price)) return;
		if (merchant.removeItem(itemType.id, quantity)) {
			player.inventory.gold -= price;
			player.inventory.addItem(itemType, quantity);
			model.statistics.addGoldSpent(price);
		}
	}

	public void quickitemUse(int quickSlotId) {
		useItem(model.player.inventory.quickitem[quickSlotId]);
		view.mainActivity.updateStatus();
	}

	public void setQuickItem(ItemType itemType, int quickSlotId) {
		model.player.inventory.quickitem[quickSlotId] = itemType;
		view.mainActivity.updateStatus();
	}
	
	public void toggleQuickItemView() {
		if (view.mainActivity.quickitemview.getVisibility()==View.VISIBLE){
			view.mainActivity.quickitemview.setVisibility(View.GONE);
			view.mainActivity.statusview.updateQuickItemImage(false);
		} else {
	    	view.mainActivity.quickitemview.setVisibility(View.VISIBLE);
	    	view.mainActivity.quickitemview.bringToFront();
			view.mainActivity.statusview.updateQuickItemImage(true);
		}
	}

	public static void correctActorConditionsFromItemsPre0611b1(Player player, String conditionTypeID, WorldContext world, String itemTypeIDWithCondition) {
		if (!player.hasCondition(conditionTypeID)) return;
		boolean hasItemWithCondition = false;
		for (ItemType t : player.inventory.wear) {
			if (t == null) continue;
			if (t.effects_equip == null) continue;
			if (t.effects_equip.addedConditions == null) continue;
			for(ActorConditionEffect e : t.effects_equip.addedConditions) {
				if (!e.conditionType.conditionTypeID.equals(conditionTypeID)) continue;
				hasItemWithCondition = true;
				break;
			}
		}
		if (hasItemWithCondition) return;
		
		ActorStatsController.removeConditionsFromUnequippedItem(player, world.itemTypes.getItemType(itemTypeIDWithCondition));
	}
}
