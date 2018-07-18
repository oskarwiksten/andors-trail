package com.gpl.rpg.AndorsTrail.model;

import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ModelContainer {

	public final Player player;
	public final InterfaceData uiSelections;
	public final CombatLog combatLog = new CombatLog();
	public final GameStatistics statistics;
	public final WorldData worldData;
	public PredefinedMap currentMap;
	public LayeredTileMap currentTileMap;

	public ModelContainer() {
		player = new Player();
		uiSelections = new InterfaceData();
		statistics = new GameStatistics();
		worldData = new WorldData();
	}

	// ====== PARCELABLE ===================================================================

	public ModelContainer(DataInputStream src, WorldContext world, ControllerContext controllers, int fileversion) throws IOException {
		this.player = Player.readFromParcel(src, world, controllers, fileversion);
		this.currentMap = world.maps.findPredefinedMap(src.readUTF());
		this.uiSelections = new InterfaceData(src, world, fileversion);
		if (uiSelections.selectedPosition != null) {
			this.uiSelections.selectedMonster = currentMap.getMonsterAt(uiSelections.selectedPosition);
		}
		this.statistics = new GameStatistics(src, world, fileversion);
		this.currentTileMap = null;
		if (fileversion >= 40) {
			this.worldData = new WorldData(src, fileversion);
		} else {
			this.worldData = new WorldData();
		}
	}

	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		player.writeToParcel(dest, flags);
		dest.writeUTF(currentMap.name);
		uiSelections.writeToParcel(dest, flags);
		statistics.writeToParcel(dest, flags);
		worldData.writeToParcel(dest, flags);
	}
}
