package com.gpl.rpg.AndorsTrail.model.item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.savegames.LegacySavegameFormatReaderForItemContainer;

public final class Inventory extends ItemContainer {

	public static final int WEARSLOT_WEAPON = 0;
	public static final int WEARSLOT_SHIELD = 1;
	public static final int WEARSLOT_HEAD = 2;
	public static final int WEARSLOT_BODY = 3;
	public static final int WEARSLOT_HAND = 4;
	public static final int WEARSLOT_FEET = 5;
	public static final int WEARSLOT_NECK = 6;
	public static final int WEARSLOT_LEFTRING = 7;
	public static final int WEARSLOT_RIGHTRING = 8;
	
	public int gold = 0;
	public static final int NUM_WORN_SLOTS = WEARSLOT_RIGHTRING+1; // +1 for 0 based index.
	public static final int NUM_QUICK_SLOTS = 3;
	public final ItemType[] wear = new ItemType[NUM_WORN_SLOTS];
	public final ItemType[] quickitem = new ItemType[NUM_QUICK_SLOTS];
	
	public Inventory() { }

	public void clear() {
		for(int i = 0; i < NUM_WORN_SLOTS; ++i) wear[i] = null;
		for(int i = 0; i < NUM_QUICK_SLOTS; ++i) quickitem[i] = null;
		gold = 0;
		items.clear();
	}
	
	public void add(final Loot loot) {
		this.gold += loot.gold;
		this.add(loot.items);
	}
	
	public boolean isEmptySlot(int slot) {
		return wear[slot] == null;
	}

	public boolean isWearing(String itemTypeID) {
		for(int i = 0; i < NUM_WORN_SLOTS; ++i) {
			if (wear[i] == null) continue;
			if (wear[i].id.equals(itemTypeID)) return true;
		}
		return false;
	}
	
	public static boolean isArmorSlot(int slot) {
		if (slot == WEARSLOT_HEAD) return true;
		else if (slot == WEARSLOT_BODY) return true;
		else if (slot == WEARSLOT_HAND) return true;
		else if (slot == WEARSLOT_FEET) return true;
		else return false;
	}
	
	
	// ====== PARCELABLE ===================================================================

	public Inventory(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		this.readFromParcel(src, world, fileversion);
	}
	public void readFromParcel(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		super.readFromParcel(src, world, fileversion);
		gold = src.readInt();
		
		if (fileversion < 23) LegacySavegameFormatReaderForItemContainer.refundUpgradedItems(this);
		
		for(int i = 0; i < NUM_WORN_SLOTS; ++i) {
			wear[i] = null;
		}
		final int numWornSlots = src.readInt();
		for(int i = 0; i < numWornSlots; ++i) {
			if (src.readBoolean()) {
				wear[i] = world.itemTypes.getItemType(src.readUTF());
			}
		}
		for(int i = 0; i < NUM_QUICK_SLOTS; ++i) {
			quickitem[i] = null;
		}
		if (fileversion >= 19) {
			final int quickSlots = src.readInt();
			for(int i = 0; i < quickSlots; ++i) {
				if (src.readBoolean()) {
					quickitem[i] = world.itemTypes.getItemType(src.readUTF());
				}
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
				dest.writeUTF(wear[i].id);
			} else {
				dest.writeBoolean(false);
			}
		}
		dest.writeInt(NUM_QUICK_SLOTS);
		for(int i = 0; i < NUM_QUICK_SLOTS; ++i) {
			if (quickitem[i] != null) {
				dest.writeBoolean(true);
				dest.writeUTF(quickitem[i].id);
			} else {
				dest.writeBoolean(false);
			}
		}
	}
}
