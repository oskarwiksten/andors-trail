package com.gpl.rpg.AndorsTrail.model.actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.util.Size;

public class ActorTraits extends CombatTraits {
	public final int iconID;
	public final Size tileSize;
	
	public int maxAP;
	public int maxHP;

	public String name;
	public int moveCost;

	public final CombatTraits baseCombatTraits;
	
	public ActorTraits(
			int iconID
			, Size tileSize
			, CombatTraits baseCombatTraits
			) {
		super(baseCombatTraits);
		this.iconID = iconID;
		this.tileSize = tileSize;
		this.baseCombatTraits = baseCombatTraits;
	}
	public int getAttacksPerTurn() {
		return (int) Math.floor(maxAP / attackCost);
	}
	public int getMovesPerTurn() {
		return (int) Math.floor(maxAP / moveCost);
	}
	
	
	// ====== PARCELABLE ===================================================================

	public ActorTraits(DataInputStream src, WorldContext world) throws IOException {
		super(src);
		this.iconID = src.readInt();
		this.tileSize = new Size(src);
		this.maxAP = src.readInt();
		this.maxHP = src.readInt();
		this.name = src.readUTF();
		this.moveCost = src.readInt();
		this.baseCombatTraits = new CombatTraits(src);
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		super.writeToParcel(dest, flags);
		dest.writeInt(iconID);
		tileSize.writeToParcel(dest, flags);
		dest.writeInt(maxAP);
		dest.writeInt(maxHP);
		dest.writeUTF(name);
		dest.writeInt(moveCost);
		baseCombatTraits.writeToParcel(dest, flags);
	}
}
