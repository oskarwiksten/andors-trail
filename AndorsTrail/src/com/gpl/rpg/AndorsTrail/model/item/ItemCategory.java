package com.gpl.rpg.AndorsTrail.model.item;

public final class ItemCategory {

	public static enum ItemCategorySize {
		none, light, std, large;

		public static ItemCategorySize fromString(String s, ItemCategorySize default_) {
			if (s == null) return default_;
			return valueOf(s);
		}
	}

	public final String id;
	public final String displayName;
	public final Inventory.WearSlot inventorySlot;
	private final ActionType actionType;
	private final ItemCategorySize size;

	public ItemCategory(
			String id
			, String displayName
			, ActionType actionType
			, Inventory.WearSlot inventorySlot
			, ItemCategorySize size
	) {
		this.id = id;
		this.displayName = displayName;
		this.inventorySlot = inventorySlot;
		this.size = size;
		this.actionType = actionType;
	}

	public static enum ActionType {
		none, use, equip;

		public static ActionType fromString(String s, ActionType default_) {
			if (s == null) return default_;
			return valueOf(s);
		}
	}
	public ItemCategorySize getSize() { return size; }
	public boolean isEquippable() { return actionType == ActionType.equip; }
	public boolean isUsable() { return actionType == ActionType.use; }
	public boolean isWeapon() { return inventorySlot == Inventory.WearSlot.weapon; }
	public boolean isShield() { return inventorySlot == Inventory.WearSlot.shield; }
	public boolean isArmor() { return Inventory.isArmorSlot(inventorySlot); }
	public boolean isTwohandWeapon() {
		if (!isWeapon()) return false;
		else if (size == ItemCategorySize.large) return true;
		else return false;
	}
	public boolean isOffhandCapableWeapon() {
		if (!isWeapon()) return false;
		else if (size == ItemCategorySize.light) return true;
		else if (size == ItemCategorySize.std) return true;
		else return false;
	}
}
