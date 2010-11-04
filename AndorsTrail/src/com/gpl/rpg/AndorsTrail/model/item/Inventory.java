package com.gpl.rpg.AndorsTrail.model.item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;

public final class Inventory extends ItemContainer {

	public int gold = 0;
	public static final int NUM_WORN_SLOTS = ItemType.MAX_CATEGORY_WEAR+1+1; // +1 for 0 based index. +1 for left+right rings.
	public final ItemType[] wear = new ItemType[NUM_WORN_SLOTS];
	
	public Inventory() { }

	public void add(final Loot loot) {
		this.gold += loot.gold;
		this.add(loot.items);
	}
	
	public boolean isEmptySlot(int slot) {
		return wear[slot] == null;
	}

	public void apply(CombatTraits traits) {
		ItemType weapon = wear[ItemType.CATEGORY_WEAPON];
		if (weapon != null) {
			CombatTraits weaponTraits = weapon.effect_combat;
			if (weaponTraits != null) {
				traits.attackCost = weaponTraits.attackCost;
				traits.criticalMultiplier = weaponTraits.criticalMultiplier;
			}
		}
		
		for (int i = 0; i < NUM_WORN_SLOTS; ++i) {
			ItemType type = wear[i];
			if (type != null) {
				CombatTraits itemTraits = type.effect_combat;
				if (itemTraits != null) {
					if (i != ItemType.CATEGORY_WEAPON) {
						// For weapons, these attributes should be SET, not added.
						traits.attackCost += itemTraits.attackCost;
						traits.criticalMultiplier += itemTraits.criticalMultiplier;
					}
					traits.attackChance += itemTraits.attackChance;
					traits.criticalChance += itemTraits.criticalChance;
					traits.damagePotential.add(itemTraits.damagePotential.current, true);
					traits.damagePotential.max += itemTraits.damagePotential.max;
					traits.blockChance += itemTraits.blockChance;
					traits.damageResistance += itemTraits.damageResistance;
				}
			}
		}
		
		if (traits.attackCost <= 0) traits.attackCost = 1;
		if (traits.attackChance < 0) traits.attackChance = 0;
	}
	
	
	// ====== PARCELABLE ===================================================================

	public Inventory(DataInputStream src, WorldContext world) throws IOException {
		super(src, world);
		gold = src.readInt();
		final int size = src.readInt();
		for(int i = 0; i < size; ++i) {
			if (src.readBoolean()) {
				wear[i] = world.itemTypes.getItemTypeByTag(src.readUTF());
			} else {
				wear[i] = null;
			}
		}
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		super.writeToParcel(dest, flags);
		dest.writeInt(gold);
		dest.writeInt(NUM_WORN_SLOTS);
		for(int i = 0; i < NUM_WORN_SLOTS; ++i) {
			if (wear[i] != null) {
				dest.writeBoolean(true);
				dest.writeUTF(wear[i].searchTag);
			} else {
				dest.writeBoolean(false);
			}
		}
	}
}
