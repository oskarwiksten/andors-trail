package com.gpl.rpg.AndorsTrail.model.item;

import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;

public final class ItemType {
	
	public static final int DISPLAYTYPE_ORDINARY = 0;
	public static final int DISPLAYTYPE_QUEST = 1;
	public static final int DISPLAYTYPE_LEGENDARY = 2;
	public static final int DISPLAYTYPE_EXTRAORDINARY = 3;
	public static final int DISPLAYTYPE_RARE = 4;
	
	public final String id;
	public final int iconID;
	private final String name;
	private final String description;
	private final boolean hasPersonalizedName;
	public final ItemCategory category;
	public final boolean hasManualPrice;
	public final int baseMarketCost;
	public final int fixedBaseMarketCost;
	public final int displayType;
	
	public final ItemTraits_OnEquip effects_equip;
	public final ItemTraits_OnUse effects_use;
	public final ItemTraits_OnUse effects_hit;
	public final ItemTraits_OnUse effects_kill;

	public ItemType(
			String id, 
			int iconID, 
			String name, 
			String description,
			ItemCategory category,
			int displayType, 
			boolean hasManualPrice, 
			int fixedBaseMarketCost, 
			ItemTraits_OnEquip effects_equip, 
			ItemTraits_OnUse effects_use, 
			ItemTraits_OnUse effects_hit, 
			ItemTraits_OnUse effects_kill) {
		this.id = id;
		this.iconID = iconID;
		this.name = name;
		this.description = description;
		this.category = category;
		this.displayType = displayType;
		this.hasManualPrice = hasManualPrice;
		this.baseMarketCost = hasManualPrice ? fixedBaseMarketCost : calculateCost(category, effects_equip, effects_use);
		this.fixedBaseMarketCost = fixedBaseMarketCost;
		this.effects_equip = effects_equip;
		this.effects_use = effects_use;
		this.effects_hit = effects_hit;
		this.effects_kill = effects_kill;
		this.hasPersonalizedName = name.contains(Constants.PLACEHOLDER_PLAYERNAME);
	}
	
	public boolean isEquippable() { return category.isEquippable(); }
	public boolean isUsable() { return category.isUsable(); }
	public boolean isQuestItem() { return displayType == DISPLAYTYPE_QUEST; }
	public boolean isOrdinaryItem() { return displayType == DISPLAYTYPE_ORDINARY; }
	public boolean isWeapon() { return category.isWeapon(); }
	public boolean isArmor() { return category.isArmor(); }
	public boolean isShield() { return category.isShield(); }
	public boolean isTwohandWeapon() { return category.isTwohandWeapon(); }
	public boolean isOffhandCapableWeapon() { return category.isOffhandCapableWeapon(); }
	public boolean isSellable() {
		if (isQuestItem()) return false;
		if (baseMarketCost == 0) return false;
		return true;
	}

    public String getDescription() { return description; }
    public String getName(Player p) {
		if (!hasPersonalizedName) return name;
		else return name.replace(Constants.PLACEHOLDER_PLAYERNAME, p.getName());		
	}

    public int getOverlayTileID() {
		switch (displayType) {
		case ItemType.DISPLAYTYPE_QUEST:
			return TileManager.iconID_selection_yellow;
		case ItemType.DISPLAYTYPE_LEGENDARY:
			return TileManager.iconID_selection_green;
		case ItemType.DISPLAYTYPE_EXTRAORDINARY:
			return TileManager.iconID_selection_blue;
		case ItemType.DISPLAYTYPE_RARE:
			return TileManager.iconID_selection_purple;
		}
		return -1;
	}
	
	public int calculateCost() { return calculateCost(category, effects_equip, effects_use); }
	public static int calculateCost(ItemCategory category, ItemTraits_OnEquip effects_equip, ItemTraits_OnUse effects_use) {
		final int costEquipStats = effects_equip == null ? 0 : effects_equip.calculateCost(category.isWeapon());
		final int costUse = effects_use == null ? 0 : effects_use.calculateCost();
		//final int costHit = effects_hit == null ? 0 : effects_hit.calculateCost();
		//final int costKill = effects_kill == null ? 0 : effects_kill.calculateCost();
		return Math.max(1, costEquipStats + costUse);
	}
}
