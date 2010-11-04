package com.gpl.rpg.AndorsTrail.model.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceLoader;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class MonsterTypeCollection {
	private final ArrayList<MonsterType> monsterTypes = new ArrayList<MonsterType>();
	
	public MonsterType getMonsterType(int id) {
		return monsterTypes.get(id);
	}
	public MonsterType getMonsterType(String name) {
		for (MonsterType t : monsterTypes) {
			if (t.name.equalsIgnoreCase(name)) return t;
		}
		return null;
	}

	public Collection<? extends MonsterType> getMonsterTypesFromTags(String tagsAndNames) {
		String[] parts = tagsAndNames.toLowerCase().split(",");
		ArrayList<MonsterType> result = new ArrayList<MonsterType>();
		for (MonsterType t : monsterTypes) {
			if (t.matchesAny(parts)) result.add(t);
		}
		//L.log("\"" + tagsAndNames + "\" -> found " + result.size() + " monsters.");
		return result;
	}
	
	private static final Size size1x1 = new Size(1, 1);
	public void initialize(DropListCollection droplists, DynamicTileLoader tileLoader, String monsterlist) {
		int nextId = monsterTypes.size();
    	Matcher rowMatcher = ResourceLoader.rowPattern.matcher(monsterlist);
    	while(rowMatcher.find()) {
    		String[] parts = rowMatcher.group(1).split(ResourceLoader.columnSeparator, -1);
    		if (parts.length < 17) continue;
    		
    		final String monsterTypeName = parts[1];
        	
    		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA && getMonsterTypesFromTags(monsterTypeName).size() > 0) {
    			L.log("OPTIMIZE: Monster " + monsterTypeName + " may be duplicated.");
    		}
        	
    		monsterTypes.add(new MonsterType(
    				nextId
    				, monsterTypeName
					, parts[2]
					, ResourceLoader.parseImage(tileLoader, parts[0])
					, ResourceLoader.parseSize(parts[3], size1x1)
					, ResourceLoader.parseInt(parts[5], 1) 	// HP
					, ResourceLoader.parseInt(parts[6], 10)	// AP
					, ResourceLoader.parseInt(parts[7], 10)	// MoveCost
					, ResourceLoader.parseCombatTraits(parts, 8)
					, ResourceLoader.parseInt(parts[4], 0)	// Exp
					, droplists.getDropList(parts[15])
					, parts[16]
				));
        	++nextId;
    	}
    }
}
