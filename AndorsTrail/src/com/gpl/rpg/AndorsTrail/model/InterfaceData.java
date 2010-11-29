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
	
	//Loaded from shared preferences, should not be parceled.
	public boolean confirmRest = true;
	public boolean confirmAttack = true;
	public int displayLoot = DISPLAYLOOT_DIALOG;
	public static final int DISPLAYLOOT_DIALOG = 0;
	public static final int DISPLAYLOOT_TOAST = 1;
	public static final int DISPLAYLOOT_NONE = 2;
	public boolean fullscreen = true;
	
	
	public InterfaceData() { }
	
	
	// ====== PARCELABLE ===================================================================

	public InterfaceData(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		this.isTicking = src.readBoolean();
		this.isInCombat = src.readBoolean();
		final boolean hasSelectedPosition = src.readBoolean();
		if (hasSelectedPosition) {
			this.selectedPosition = new Coord(src, fileversion);
		} else {
			this.selectedPosition = null;
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
