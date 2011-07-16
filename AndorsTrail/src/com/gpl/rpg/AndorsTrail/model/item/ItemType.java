package com.gpl.rpg.AndorsTrail.model.item;

import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.resource.TileStore;

public final class ItemType {
	public static final int CATEGORY_WEAPON = 0;
	public static final int CATEGORY_SHIELD = 1;
	public static final int CATEGORY_WEARABLE_HEAD = 2;
	public static final int CATEGORY_WEARABLE_BODY = 3;
	public static final int CATEGORY_WEARABLE_HAND = 4;
	public static final int CATEGORY_WEARABLE_FEET = 5;
	//public static final int CATEGORY_WEARABLE_CAPE = 6;
	//public static final int CATEGORY_WEARABLE_LEGS = 7;
	public static final int CATEGORY_WEARABLE_NECK = 6;
	public static final int CATEGORY_WEARABLE_RING = 7;
	public static final int MAX_CATEGORY_WEAR = CATEGORY_WEARABLE_RING;
	public static final int CATEGORY_POTION = 20;
	public static final int CATEGORY_FOOD = 21;
	public static final int MAX_CATEGORY_USE = CATEGORY_FOOD;
	public static final int CATEGORY_MONEY = 30;
	public static final int CATEGORY_OTHER = 31;
	
	public static final int ACTIONTYPE_NONE = 0;
	public static final int ACTIONTYPE_USE = 1;
	public static final int ACTIONTYPE_EQUIP = 2;
	
	public static final int DISPLAYTYPE_ORDINARY = 0;
	public static final int DISPLAYTYPE_QUEST = 1;
	public static final int DISPLAYTYPE_LEGENDARY = 2;
	public static final int DISPLAYTYPE_EXTRAORDINARY = 3;
	public static final int DISPLAYTYPE_UNIQUE = 4;
	
	public final int id;
	public final int iconID;
	public final String name;
	public final int category;
	public final int actionType;
	public final int baseMarketCost;
	public final String searchTag;
	public final int displayType;
	
	public final ItemTraits_OnEquip effects_equip;
	public final ItemTraits_OnUse effects_use;
	public final ItemTraits_OnUse effects_hit;
	public final ItemTraits_OnUse effects_kill;

	public ItemType(int id, int iconID, String name, String searchTag, int category, int displayType, int baseMarketCost, ItemTraits_OnEquip effects_equip, ItemTraits_OnUse effects_use, ItemTraits_OnUse effects_hit, ItemTraits_OnUse effects_kill) {
		this.id = id;
		this.iconID = iconID;
		this.name = name;
		this.searchTag = searchTag;
		this.category = category;
		this.actionType = getActionType(category);
		this.displayType = displayType;
		this.baseMarketCost = baseMarketCost;
		this.effects_equip = effects_equip;
		this.effects_use = effects_use;
		this.effects_hit = effects_hit;
		this.effects_kill = effects_kill;
	}
	
	private static int getActionType(int category) {
		if (category <= MAX_CATEGORY_WEAR) return ACTIONTYPE_EQUIP;
		else if (category <= MAX_CATEGORY_USE) return ACTIONTYPE_USE;
		else return ACTIONTYPE_NONE;
	}
	public boolean isEquippable() { return actionType == ACTIONTYPE_EQUIP; }
	public boolean isUsable() { return actionType == ACTIONTYPE_USE; }
	public boolean isQuestItem() { return baseMarketCost == 0; }
	public boolean isOrdinaryItem() { return displayType == DISPLAYTYPE_ORDINARY; }
	
	public String describeWearEffect(int quantity) {
		StringBuilder sb = new StringBuilder(name);
		if (quantity > 1) {
			sb.append(" (");
			sb.append(quantity);
			sb.append(')'); 
		}
		if (effects_equip != null) {
			if (effects_equip.combatProficiency != null) {
				if (effects_equip.combatProficiency.hasAttackChanceEffect() || effects_equip.combatProficiency.hasAttackDamageEffect()) {
					sb.append(" [");
					describeAttackEffect(effects_equip.combatProficiency, sb);
					sb.append(']');
				}
				if (effects_equip.combatProficiency.hasBlockEffect()) {
					sb.append(" [");
					describeBlockEffect(effects_equip.combatProficiency, sb);
					sb.append(']');
				}
			}
		}
		return sb.toString();
	}
	
	public static void describeAttackEffect(CombatTraits attackEffect, StringBuilder sb) {
		boolean addSpace = false;
		if (attackEffect.hasAttackChanceEffect()) {
			sb.append(attackEffect.attackChance);
			sb.append('%');
			addSpace = true;
		}
		if (attackEffect.hasAttackDamageEffect()) {
			if (addSpace) sb.append(' ');
			sb.append(attackEffect.damagePotential.toMinMaxString());
			addSpace = true;
		}
		if (attackEffect.hasCriticalChanceEffect()) {
			sb.append(" +");
			sb.append(attackEffect.criticalChance);
			sb.append("%x");
			sb.append(attackEffect.criticalMultiplier);	
		}
	}
	public static String describeAttackEffect(CombatTraits attackEffect) {
		StringBuilder sb = new StringBuilder();
		describeAttackEffect(attackEffect, sb);
		return sb.toString();
	}
	
	public static void describeBlockEffect(CombatTraits defenseEffect, StringBuilder sb) {
		sb.append(defenseEffect.blockChance);
		sb.append('%');
		if (defenseEffect.damageResistance != 0) {
			sb.append('/');
			sb.append(defenseEffect.damageResistance);	
		}
	}
	public static String describeBlockEffect(CombatTraits defenseEffect) {
		StringBuilder sb = new StringBuilder();
		describeBlockEffect(defenseEffect, sb);
		return sb.toString();
	}
	
	public int getOverlayTileID() {
		switch (displayType) {
		case ItemType.DISPLAYTYPE_QUEST:
			return TileStore.iconID_selection_yellow;
		case ItemType.DISPLAYTYPE_LEGENDARY:
			return TileStore.iconID_selection_green;
		case ItemType.DISPLAYTYPE_EXTRAORDINARY:
			return TileStore.iconID_selection_blue;
		case ItemType.DISPLAYTYPE_UNIQUE:
			return TileStore.iconID_selection_purple;
		}
		return -1;
	}
}
