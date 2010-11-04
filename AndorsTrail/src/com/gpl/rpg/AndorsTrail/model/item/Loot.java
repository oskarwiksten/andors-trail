package com.gpl.rpg.AndorsTrail.model.item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.util.Coord;

public final class Loot {
	public int exp = 0;
	public int gold = 0;
	public final ItemContainer items;
	public final Coord position;
	
	public Loot() {
		this.items = new ItemContainer();
		this.position = new Coord();
	}
	
	public void add(Loot l) {
		this.exp += l.exp;
		this.gold += l.gold;
		this.items.add(items);
	}
	
	public boolean isEmpty() {
		return exp == 0 && gold == 0 && items.isEmpty();
	}

	public void clear() {
		exp = 0;
		gold = 0;
		items.items.clear();
	}
	
	
	// ====== PARCELABLE ===================================================================

	public Loot(DataInputStream src, WorldContext world) throws IOException {
		this.exp = src.readInt();
		this.gold = src.readInt();
		this.items = new ItemContainer(src, world);
		this.position = new Coord(src);
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeInt(exp);
		dest.writeInt(gold);
		items.writeToParcel(dest, flags);
		position.writeToParcel(dest, flags);
	}
}
