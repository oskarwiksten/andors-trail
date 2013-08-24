package com.gpl.rpg.AndorsTrail.savegames;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer.ItemEntry;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.util.L;

public final class LegacySavegameFormatReaderForItemContainer {
	public static void refundUpgradedItems(Inventory inventory) {
		inventory.gold += getRefundForUpgradedItems(inventory);
	}
	public static void refundUpgradedItems(Loot loot) {
		loot.gold += getRefundForUpgradedItems(loot.items);
	}

	private static int getRefundForUpgradedItems(ItemContainer container) {
		int removedCost = 0;
		for (ItemEntry e : container.items) {
			if (e.quantity >= 2 && isRefundableItem(e.itemType)) {
				if (AndorsTrailApplication.DEVELOPMENT_DEBUGMESSAGES) {
					L.log("INFO: Refunding " + (e.quantity-1) + " items of type \"" + e.itemType.id + "\" for a total of " + ((e.quantity-1) * e.itemType.fixedBaseMarketCost) + "gc.");
				}
				removedCost += (e.quantity-1) * e.itemType.fixedBaseMarketCost;
				e.quantity = 1;
			}
		}
		return removedCost;
	}

	private static boolean isRefundableItem(ItemType itemType) {
		if (itemType.hasManualPrice) return false;
		if (itemType.isQuestItem()) return false;
		if (itemType.displayType == ItemType.DisplayType.extraordinary) return false;
		if (itemType.displayType == ItemType.DisplayType.legendary) return false;
		return itemType.baseMarketCost > itemType.fixedBaseMarketCost;
	}
}
