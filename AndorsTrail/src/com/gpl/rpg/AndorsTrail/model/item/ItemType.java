package com.gpl.rpg.AndorsTrail.model.item;

import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;

public final class ItemType {
	
	public static final int DISPLAYTYPE_ORDINARY = 0;
	public static final int DISPLAYTYPE_QUEST = 1;
	public static final int DISPLAYTYPE_LEGENDARY = 2;
	public static final int DISPLAYTYPE_EXTRAORDINARY = 3;
	public static final int DISPLAYTYPE_RARE = 4;
	
	public final String id;
	public final int iconID;
	public final String name;
	public final ItemCategory category;
	public final boolean hasManualPrice;
	public final int baseMarketCost;
	public final int fixedBaseMarketCost;
	public final int displayType;
	
	public final ItemTraits_OnEquip effects_equip;
	public final ItemTraits_OnUse effects_use;
	public final ItemTraits_OnUse effects_hit;
	public final ItemTraits_OnUse effects_kill;

	public ItemType(String id, int iconID, String name, ItemCategory category, int displayType, boolean hasManualPrice, int fixedBaseMarketCost, ItemTraits_OnEquip effects_equip, ItemTraits_OnUse effects_use, ItemTraits_OnUse effects_hit, ItemTraits_OnUse effects_kill) {
		this.id = id;
		this.iconID = iconID;
		this.name = name;
		this.category = category;
		this.displayType = displayType;
		this.hasManualPrice = hasManualPrice;
		this.baseMarketCost = hasManualPrice ? fixedBaseMarketCost : calculateCost(category, effects_equip, effects_use);
		this.fixedBaseMarketCost = fixedBaseMarketCost;
		this.effects_equip = effects_equip;
		this.effects_use = effects_use;
		this.effects_hit = effects_hit;
		this.effects_kill = effects_kill;
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
		if (attackEffect.hasCriticalSkillEffect()) {
			if (addSpace) sb.append(' ');
			if (attackEffect.criticalSkill >= 0) {
				sb.append('+');
			}
			sb.append(attackEffect.criticalSkill);
			sb.append("x");
			if (attackEffect.hasCriticalMultiplierEffect()) {
				sb.append(attackEffect.criticalMultiplier);				
			}
			addSpace = true;
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
