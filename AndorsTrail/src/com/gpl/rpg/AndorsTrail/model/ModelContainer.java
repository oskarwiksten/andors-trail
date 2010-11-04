package com.gpl.rpg.AndorsTrail.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.map.LayeredWorldMap;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
import com.gpl.rpg.AndorsTrail.util.Range;

public final class ModelContainer {
	
	public static final int millisecondsPerTurn = 1200;
	public static final int attackAnimationDuration = 1000;
	public static final int attackAnimationFPS = 10;
	public static final int monsterAttackDelay = 1000;
	public static final int tickDelay = 500;
	public static final ConstRange monsterWaitTurns = new ConstRange(30,4);
	public static final Random rnd = new Random();
	//public static final String PREFERENCE_MODEL_SAVE = "savegame";
	public static final String PREFERENCE_MODEL_QUICKSAVE = "quicksave";
	public static final String PREFERENCE_MODEL_LASTRUNVERSION = "lastversion";
	public static final int CURRENT_VERSION = 4;
	
	public final Player player;
	public final InterfaceData uiSelections;
	public final GameStatistics statistics;
	public LayeredWorldMap currentMap;
	
	public ModelContainer() {
		player = new Player();
		uiSelections = new InterfaceData();
		statistics = new GameStatistics();
	}
    
	public static boolean roll100(final int chance) { return rnd.nextInt(100) < chance; }
	public static int rollValue(final ConstRange r) { 
		if (r.isMax()) return r.max;
		else return rnd.nextInt(r.max - r.current) + r.current;
	}
	public static int rollValue(final Range r) { 
		if (r.isMax()) return r.max;
		else return rnd.nextInt(r.max - r.current + 1) + r.current;
	}
	public static boolean rollResult(final ConstRange r) { return rnd.nextInt(r.max) < r.current; }
	public static boolean rollResult(final Range r) { return rnd.nextInt(r.max) < r.current; }


	public void quicksave(Editor e) {
		e.putString("playername", player.traits.name);
		e.putInt("level", player.level);
	}
	
	public boolean quickload(SharedPreferences p) {
		String name = p.getString("playername", null);
		if (name == null) return false;
		player.traits.name = name;
		player.level = p.getInt("level", -1);
		return true;
	}


	// ====== PARCELABLE ===================================================================

	public ModelContainer(DataInputStream src, WorldContext world) throws IOException {
		this.player = new Player(src, world);
		this.currentMap = world.maps.findPredefinedMap(src.readUTF());
		this.uiSelections = new InterfaceData(src, world);
		this.statistics = new GameStatistics(src, world);
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		player.writeToParcel(dest, flags);
		dest.writeUTF(currentMap.name);
		uiSelections.writeToParcel(dest, flags);
		statistics.writeToParcel(dest, flags);
	}
}
