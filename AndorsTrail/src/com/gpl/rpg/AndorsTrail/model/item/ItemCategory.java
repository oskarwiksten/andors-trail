package com.gpl.rpg.AndorsTrail.model.item;

public final class ItemCategory {
	public static final int SIZE_NONE = 0;
	public static final int SIZE_LIGHT = 1;
	public static final int SIZE_STD = 2;
	public static final int SIZE_LARGE = 3;
	
	public final String id;
	public final String displayName;
	public final int inventorySlot;
	private final int actionType;
	public final int size;
	
	public ItemCategory(String id, String displayName, int actionType, int inventorySlot, int size) {
		this.id = id;
		this.displayName = displayName;
		this.inventorySlot = inventorySlot;
		this.size = size;
		this.actionType = actionType;
	}
	
	private static final int ACTIONTYPE_NONE = 0;
	private static final int ACTIONTYPE_USE = 1;
	private static final int ACTIONTYPE_EQUIP = 2;
	public boolean isEquippable() { return actionType == ACTIONTYPE_EQUIP; }
	public boolean isUsable() { return actionType == ACTIONTYPE_USE; }
	public boolean isWeapon() { return inventorySlot == Inventory.WEARSLOT_WEAPON; }
	public boolean isShield() { return inventorySlot == Inventory.WEARSLOT_SHIELD; }
	public boolean isArmor() { return Inventory.isArmorSlot(inventorySlot); }
	public boolean isTwohandWeapon() {
		/*if (!isWeapon()) return false;
		else if (size == SIZE_LARGE) return true;
		else*/ return false;
	}
	public boolean isOffhandCapableWeapon() {
		/*if (!isWeapon()) return false;
		else if (size == SIZE_LIGHT) return true;
		else if (size == SIZE_STD) return true;
		else*/ return false;
	}
}
