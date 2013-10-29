package com.gpl.rpg.AndorsTrail.model.item;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.savegames.LegacySavegameFormatReaderForItemContainer;
import com.gpl.rpg.AndorsTrail.util.Coord;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

public final class Loot {
	public int exp = 0;
	public int gold = 0;
	public final ItemContainer items;
	public final Coord position;
	public final boolean isVisible;

	public Loot() {
		this(true);
	}
	public Loot(boolean isVisible) {
		this.items = new ItemContainer();
		this.position = new Coord();
		this.isVisible = isVisible;
	}

	private void add(Loot l) {
		this.exp += l.exp;
		this.gold += l.gold;
		this.items.add(l.items);
	}

	public void add(ItemType itemType, int quantity) {
		if (ItemTypeCollection.isGoldItemType(itemType.id)) {
			gold += quantity;
		} else {
			items.addItem(itemType, quantity);
		}
	}

	public boolean hasItemsOrExp() {
		return exp != 0 || hasItemsOrGold();
	}
	public boolean hasItemsOrGold() {
		return gold != 0 || hasItems();
	}
	public boolean hasItems() {
		return !items.isEmpty();
	}
	public boolean isContainer() {
		return !isVisible;
	}
	public static Loot combine(Iterable<Loot> loot) {
		Loot result = new Loot();
		for (Loot l : loot) {
			result.add(l);
		}
		return result;
	}
	public static boolean hasItems(Collection<Loot> lootBags) {
		if (lootBags == null) return false;
		if (lootBags.isEmpty()) return false;
		for (Loot loot : lootBags) {
			if (loot.hasItems()) return true;
		}
		return false;
	}

	public void clear() {
		exp = 0;
		gold = 0;
		items.items.clear();
	}


	// ====== PARCELABLE ===================================================================

	public Loot(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		this.exp = src.readInt();
		this.gold = src.readInt();
		this.items = new ItemContainer(src, world, fileversion);
		if (fileversion < 23) LegacySavegameFormatReaderForItemContainer.refundUpgradedItems(this);

		this.position = new Coord(src, fileversion);
		if (fileversion <= 15) {
			this.isVisible = true;
			return;
		}
		this.isVisible = src.readBoolean();
	}

	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeInt(exp);
		dest.writeInt(gold);
		items.writeToParcel(dest, flags);
		position.writeToParcel(dest, flags);
		dest.writeBoolean(isVisible);
	}
}
