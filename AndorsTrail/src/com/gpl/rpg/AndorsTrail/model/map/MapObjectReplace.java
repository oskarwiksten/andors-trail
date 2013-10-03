package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.model.script.Requirement;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class MapObjectReplace {

	public static enum SpawnStrategy {
		do_nothing,
		spawn_all_new,
		clean_up_all
	}
	
	public final CoordRect position;
	public final String sourceGroup;
	public final String targetGroup;
	public final String group;
	public final Requirement replacementRequirement;
	public final SpawnStrategy strategy;
	public boolean isActive = true;
	public boolean isApplied = false;
	
	public MapObjectReplace(CoordRect pos, String src, String target, String group, MapObjectReplace.SpawnStrategy strategy, Requirement replacementRequirement) {
		this.position = pos;
		this.sourceGroup = src;
		this.targetGroup = target;
		this.group = group;
		this.strategy = strategy;
		this.replacementRequirement = replacementRequirement;
	}
	
}
