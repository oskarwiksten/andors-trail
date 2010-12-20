package com.gpl.rpg.AndorsTrail.model.item;

import java.util.Collection;

import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.util.ConstRange;

public final class DropList {
	private final DropItem[] items;
	public final DropItem[] DEBUG_items;
	
	public DropList(DropItem[] items) {
		this.items = items;
		this.DEBUG_items = this.items;
	}
	public DropList(Collection<DropItem> items) {
		this.items = items.toArray(new DropItem[items.size()]);
		this.DEBUG_items = this.items;
	}
	public void createRandomLoot(Loot loot) {
		for (DropItem item : items) {
			if (Constants.rollResult(item.chance)) {
				int quantity = Constants.rollValue(item.quantity);
				if (quantity != 0) {
					if (item.itemType.id == ItemTypeCollection.ITEMTYPE_GOLD) {
						loot.gold += quantity;
					} else {
						loot.items.addItem(item.itemType, quantity);
					}
				}
			}
		}
	}
	
	// Selftest metohd. Not part of the game logic.
	public boolean contains(int itemTypeID) {
		for (DropItem item : items) {
			if (item.itemType.id == itemTypeID) return true;
		}
		return false;
	}
	
	public static class DropItem {
		public final ItemType itemType;
		public final ConstRange chance;
		public final ConstRange quantity;
		public DropItem(ItemType itemType, ConstRange chance, ConstRange quantity) {
			this.itemType = itemType;
			this.chance = chance;
			this.quantity = quantity;
		}
	}
}
