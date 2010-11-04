package com.gpl.rpg.AndorsTrail.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.util.Coord;

public final class InterfaceData {
	public boolean isTicking = false;
	public boolean isInCombat = false;
    public Monster selectedMonster;
	public Coord selectedPosition;
	public String selectedTabHeroInfo = "";
	
	public InterfaceData() { }
	
	
	// ====== PARCELABLE ===================================================================

	public InterfaceData(DataInputStream src, WorldContext world) throws IOException {
		this.isTicking = src.readBoolean();
		this.isInCombat = src.readBoolean();
		final boolean hasSelectedPosition = src.readBoolean();
		if (hasSelectedPosition) {
			this.selectedPosition = new Coord(src);
			this.selectedMonster = world.model.currentMap.getMonsterAt(selectedPosition);
		}
		this.selectedTabHeroInfo = src.readUTF();
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeBoolean(isTicking);
		dest.writeBoolean(isInCombat);
		if (selectedPosition != null) {
			dest.writeBoolean(true);
			selectedPosition.writeToParcel(dest, flags);
		} else {
			dest.writeBoolean(false);
		}
		dest.writeUTF(selectedTabHeroInfo);
	}
}
