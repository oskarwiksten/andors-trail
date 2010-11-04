package com.gpl.rpg.AndorsTrail.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;

public final class GameStatistics {
	public int deaths = 0;
	public final HashMap<String, Integer> killedMonsters = new HashMap<String, Integer>();
	
	public GameStatistics() { }
	public void addMonsterKill(MonsterType type) {
		final String n = type.name;
		if (!killedMonsters.containsKey(n)) killedMonsters.put(n, 0);
		killedMonsters.put(n, killedMonsters.get(n) + 1);
	}
	public void addPlayerDeath(int lostExp) {
		++deaths;
	}
	
	
	// ====== PARCELABLE ===================================================================

	public GameStatistics(DataInputStream src, WorldContext world) throws IOException {
		this.deaths = src.readInt();
		final int size = src.readInt();
		for(int i = 0; i < size; ++i) {
			final String name = src.readUTF();
			final int value = src.readInt();
			this.killedMonsters.put(name, value);
		}
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeInt(deaths);
		Set<Entry<String, Integer> > set = killedMonsters.entrySet();
		dest.writeInt(set.size());
		for (Entry<String, Integer> e : set) {
			dest.writeUTF(e.getKey());
			dest.writeInt(e.getValue());
		}
	}
}
