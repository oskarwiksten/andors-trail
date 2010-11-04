package com.gpl.rpg.AndorsTrail.model.actor;

import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class MonsterType extends ActorTraits {
	public final int id;
	private final String searchPattern;
	public final int exp;
	public final DropList dropList;
	public final String phraseID;

	public MonsterType(int id, String name, String tags, int iconID, Size tileSize, int maxHP, int maxAP, int moveCost, CombatTraits baseCombatTraits, int exp, DropList dropList, String phraseID) {
		super(iconID, tileSize, baseCombatTraits);
		this.id = id;
		this.searchPattern = ',' + tags.toLowerCase() + ',';
		this.exp = exp;
		this.name = name;
		this.maxHP = maxHP;
		this.maxAP = maxAP;
		this.moveCost = moveCost;
		this.dropList = dropList;
		if (phraseID != null && phraseID.length() == 0) phraseID = null;
		this.phraseID = phraseID;
	}
	public boolean matchesAny(String[] tagsAndNames) {
		for (String s : tagsAndNames) {
			if (name.equalsIgnoreCase(s)) return true;
			if (searchPattern.contains(',' + s + ',')) return true;
		}
		return false;
	}
	
	public boolean isAgressive() {
		return phraseID == null;
	}
}
