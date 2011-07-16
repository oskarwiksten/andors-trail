package com.gpl.rpg.AndorsTrail.model.item;

import java.util.Collection;

import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.controller.SkillController;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
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
	public void createRandomLoot(Loot loot, Player player) {
		for (DropItem item : items) {
			
			final int chanceRollBias = SkillController.getChanceRollBias(item, player);
			if (Constants.rollResult(item.chance, chanceRollBias)) {
				
				final int quantityRollBias = SkillController.getQuantityRollBias(item, player);
				int quantity = Constants.rollValue(item.quantity, quantityRollBias);
				
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
	
	// Selftest method. Not part of the game logic.
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
