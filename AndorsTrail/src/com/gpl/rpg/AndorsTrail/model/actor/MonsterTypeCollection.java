package com.gpl.rpg.AndorsTrail.model.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
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
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Cannot find MonsterType for name \"" + name + "\".");
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
        	
    		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    			if (getMonsterTypesFromTags(monsterTypeName).size() > 0) {
    				L.log("OPTIMIZE: Monster " + monsterTypeName + " may be duplicated.");
        		}
    		}
        	
    		final int maxHP = ResourceLoader.parseInt(parts[5], 1);
    		final int maxAP = ResourceLoader.parseInt(parts[6], 10);
    		final CombatTraits combatTraits = ResourceLoader.parseCombatTraits(parts, 8);
    		final int exp = getExpectedMonsterExperience(combatTraits, maxHP, maxAP);
    		monsterTypes.add(new MonsterType(
				nextId
				, monsterTypeName
				, parts[2]
				, ResourceLoader.parseImage(tileLoader, parts[0])
				, ResourceLoader.parseSize(parts[3], size1x1)
				, maxHP 	// HP
				, maxAP		// AP
				, ResourceLoader.parseInt(parts[7], 10)	// MoveCost
				, combatTraits
				, exp //ResourceLoader.parseInt(parts[4], 0)	// Exp
				, droplists.getDropList(parts[15])
				, parts[16]
			));
    		
        	++nextId;
    	}
    }

	private static final int factor_damageresistance = 9;
	private static final float factor_expscaling = 0.7f;

	private static float div100(int v) {
		return (float) v / 100f;
	}
	private static int getExpectedMonsterExperience(final MonsterType t) {
		return getExpectedMonsterExperience(t, t.maxHP, t.maxAP);
	}
	private static int getExpectedMonsterExperience(final CombatTraits t, final int maxHP, final int maxAP) {
		final float avgAttackHP  = t.getAttacksPerTurn(maxAP) * div100(t.attackChance) * t.damagePotential.averagef() * (1 + div100(t.criticalChance) * t.criticalMultiplier);
		final float avgDefenseHP = maxHP * (1 + div100(t.blockChance)) + factor_damageresistance * t.damageResistance;
		return (int) Math.ceil((avgAttackHP * 3 + avgDefenseHP) * factor_expscaling);
	}
	
	// Selftest method. Not part of the game logic.
	public void verifyData(WorldContext world) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		for (MonsterType t : monsterTypes) {
    			if (t.phraseID != null && t.phraseID.length() > 0) {
    				if (!world.conversations.isValidPhraseID(t.phraseID)) {
    					L.log("WARNING: Cannot find phrase \"" + t.phraseID + "\" for MonsterType \"" + t.name + "\".");
    				}
    			}
    			
    			if (t.exp > 0) {
	    			int expected_exp = getExpectedMonsterExperience(t);
	
	    			if (t.exp != expected_exp) {
	    				L.log("WARNING: MonsterType \"" + t.name + "\" has exp=" + t.exp + ", which is different from the suggested exp=" + expected_exp);
	    			}
    			}
    		}
    	}
	}
	
	// Selftest method. Not part of the game logic.
	public HashSet<String> DEBUG_getRequiredPhrases() {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		HashSet<String> requiredPhrases = new HashSet<String>();
    		for (MonsterType t : monsterTypes) {
    			if (t.phraseID != null && t.phraseID.length() > 0) {
    				requiredPhrases.add(t.phraseID);
    			}
    		}
    		return requiredPhrases;
    	} else {
    		return null;
    	}
	}
}
