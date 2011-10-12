package com.gpl.rpg.AndorsTrail.model.actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.util.Size;

public class ActorTraits {
	public static final int STAT_ACTOR_MAX_HP = 0;
	public static final int STAT_ACTOR_MAX_AP = 1;
	public static final int STAT_ACTOR_MOVECOST = 2;

	public final int iconID;
	public final Size tileSize;
	
	public int maxAP;
	public int maxHP;

	public String name;
	public int moveCost;
	public final int baseMoveCost;

	public final CombatTraits baseCombatTraits;
	public ItemTraits_OnUse[] onHitEffects;
	
	public ActorTraits(
			int iconID
			, Size tileSize
			, CombatTraits baseCombatTraits
			, int standardMoveCost
			, ItemTraits_OnUse[] onHitEffects
			) {
		this.iconID = iconID;
		this.tileSize = tileSize;
		this.baseCombatTraits = baseCombatTraits;
		this.baseMoveCost = standardMoveCost;
		this.onHitEffects = onHitEffects;
	}
	public int getMovesPerTurn() {
		return (int) Math.floor(maxAP / moveCost);
	}
	
	public int getActorStats(int statID) {
		switch (statID) {
		case STAT_ACTOR_MAX_HP: return maxHP;
		case STAT_ACTOR_MAX_AP: return maxAP;
		case STAT_ACTOR_MOVECOST: return moveCost;
		}
		return 0;
	}
	
	
	// ====== PARCELABLE ===================================================================

	public ActorTraits(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		this.iconID = src.readInt();
		this.tileSize = new Size(src, fileversion);
		this.maxAP = src.readInt();
		this.maxHP = src.readInt();
		this.name = src.readUTF();
		this.moveCost = src.readInt();
		this.baseCombatTraits = new CombatTraits(src, fileversion);
		if (fileversion <= 16) {
			this.baseMoveCost = this.moveCost;
		} else {
			this.baseMoveCost = src.readInt();
		}
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeInt(iconID);
		tileSize.writeToParcel(dest, flags);
		dest.writeInt(maxAP);
		dest.writeInt(maxHP);
		dest.writeUTF(name);
		dest.writeInt(moveCost);
		baseCombatTraits.writeToParcel(dest, flags);
		dest.writeInt(baseMoveCost);
	}
}
