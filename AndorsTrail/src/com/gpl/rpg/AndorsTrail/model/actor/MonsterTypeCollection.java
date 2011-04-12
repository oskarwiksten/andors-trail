package com.gpl.rpg.AndorsTrail.model.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class MonsterTypeCollection {
	private final ArrayList<MonsterType> monsterTypes = new ArrayList<MonsterType>();
	
	public MonsterType getMonsterType(int id) {
		return monsterTypes.get(id);
	}
	public MonsterType getMonsterTypeFromName(String name) {
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
    	Matcher rowMatcher = ResourceFileParser.rowPattern.matcher(monsterlist);
    	while(rowMatcher.find()) {
    		String[] parts = rowMatcher.group(1).split(ResourceFileParser.columnSeparator, -1);
    		if (parts.length < 17) continue;
    		
    		final String monsterTypeName = parts[1];
        	
    		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    			if (getMonsterTypesFromTags(monsterTypeName).size() > 0) {
    				L.log("OPTIMIZE: Monster " + monsterTypeName + " may be duplicated.");
        		}
    		}
        	
    		final int maxHP = ResourceFileParser.parseInt(parts[5], 1);
    		final int maxAP = ResourceFileParser.parseInt(parts[6], 10);
    		final CombatTraits combatTraits = parseCombatTraits_OLD(parts, 8);
    		final int exp = getExpectedMonsterExperience(combatTraits, maxHP, maxAP);
    		monsterTypes.add(new MonsterType(
				nextId
				, monsterTypeName
				, parts[2]
				, ResourceFileParser.parseImageID(tileLoader, parts[0])
				, ResourceFileParser.parseSize(parts[3], size1x1) //TODO: This could be loaded from the tileset size instead.
				, maxHP 	// HP
				, maxAP		// AP
				, ResourceFileParser.parseInt(parts[7], 10)	// MoveCost
				, combatTraits
		        , null // onHitEffects
				, exp //ResourceLoader.parseInt(parts[4], 0)	// Exp
				, droplists.getDropList(parts[15])
				, parts[16]
			));
    		
        	++nextId;
    	}
    }
	
	public static CombatTraits parseCombatTraits_OLD(String[] parts, int startIndex) {
		String AtkCost = parts[startIndex];
		String AtkPct = parts[startIndex + 1];
		String CritPct = parts[startIndex + 2];
		String CritMult = parts[startIndex + 3];
		String DMG = parts[startIndex + 4];
		String BlkPct = parts[startIndex + 5];
		String DMG_res = parts[startIndex + 6];
		if (       AtkCost.length() <= 0 
				&& AtkPct.length() <= 0
				&& CritPct.length() <= 0
				&& CritMult.length() <= 0
				&& DMG.length() <= 0
				&& BlkPct.length() <= 0
				&& DMG_res.length() <= 0
			) {
			return null;
		} else {
			CombatTraits result = new CombatTraits();
			result.attackCost = ResourceFileParser.parseInt(AtkCost, 0);
			result.attackChance = ResourceFileParser.parseInt(AtkPct, 0);
			result.criticalChance = ResourceFileParser.parseInt(CritPct, 0);
			result.criticalMultiplier = ResourceFileParser.parseInt(CritMult, 0);
			ConstRange r = ResourceFileParser.parseRange_OLD(DMG);
			if (r != null) result.damagePotential.set(r);
			result.blockChance = ResourceFileParser.parseInt(BlkPct, 0);
			result.damageResistance = ResourceFileParser.parseInt(DMG_res, 0);
			return result;
		}
	}
	
	
	public void DEBUG_initializeTestEffectMonsters(WorldContext world) {
		MonsterType t = getMonsterTypeFromName("Forest Snake");
		if (t == null) return;
		t.onHitEffects = new ItemTraits_OnUse[] {
			new ItemTraits_OnUse(null, null, null, new ActorConditionEffect[] { 
				new ActorConditionEffect(world.actorConditionsTypes.getActorConditionType("poison_weak"), 1, 3, new ConstRange(1, 1))
			})
		};
	}

	private static float div100(int v) {
		return (float) v / 100f;
	}
	private static int getExpectedMonsterExperience(final MonsterType t) {
		return getExpectedMonsterExperience(t, t.maxHP, t.maxAP);
	}
	private static int getExpectedMonsterExperience(final CombatTraits t, final int maxHP, final int maxAP) {
		final float avgAttackHP  = t.getAttacksPerTurn(maxAP) * div100(t.attackChance) * t.damagePotential.averagef() * (1 + div100(t.criticalChance) * t.criticalMultiplier);
		final float avgDefenseHP = maxHP * (1 + div100(t.blockChance)) + Constants.EXP_FACTOR_DAMAGERESISTANCE * t.damageResistance;
		return (int) Math.ceil((avgAttackHP * 3 + avgDefenseHP) * Constants.EXP_FACTOR_SCALING);
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
	public void verifyData(ConversationCollection conversations) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		for (MonsterType t : monsterTypes) {
    			if (t.phraseID != null && t.phraseID.length() > 0) {
    				if (conversations.DEBUG_leadsToTradeReply(t.phraseID)) {
    					if (t.dropList == null) {
    						L.log("WARNING: MonsterType \"" + t.name + "\" has conversation \"" + t.phraseID + "\" that leads to a trade, but the monster type does not have a droplist.");
    					}
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
