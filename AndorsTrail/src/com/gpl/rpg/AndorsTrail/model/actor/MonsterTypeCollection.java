package com.gpl.rpg.AndorsTrail.model.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectFieldParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectTokenizer;
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
	private static final ResourceObjectTokenizer monsterResourceTokenizer = new ResourceObjectTokenizer(24);
	public void initialize(final DropListCollection droplists, final ActorConditionTypeCollection actorConditionTypes, final DynamicTileLoader tileLoader, String monsterlist) {
		//[iconID|name|tags|size|maxHP|maxAP|moveCost|attackCost|attackChance|criticalChance|criticalMultiplier|attackDamage_Min|attackDamage_Max|blockChance|damageResistance|droplistID|phraseID|
		// hasHitEffect|onHit_boostHP_Min|onHit_boostHP_Max|onHit_boostAP_Min|onHit_boostAP_Max|onHit_conditionsSource[condition|magnitude|duration|chance|]|onHit_conditionsTarget[condition|magnitude|duration|chance|]|];
		monsterResourceTokenizer.tokenizeRows(monsterlist, new ResourceObjectFieldParser() {
			@Override
			public void matchedRow(String[] parts) {
				final int nextId = monsterTypes.size();
				
				final String monsterTypeName = parts[1];
	        	
	    		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
	    			if (getMonsterTypesFromTags(monsterTypeName).size() > 0) {
	    				L.log("OPTIMIZE: Monster " + monsterTypeName + " may be duplicated.");
	        		}
	    		}
	        	
	    		final int maxHP = ResourceFileParser.parseInt(parts[4], 1);
	    		final int maxAP = ResourceFileParser.parseInt(parts[5], 10);
	    		final CombatTraits combatTraits = ResourceFileParser.parseCombatTraits(parts, 7);
	    		final int exp = getExpectedMonsterExperience(combatTraits, maxHP, maxAP);
	    		final ItemTraits_OnUse hitEffect = ResourceFileParser.parseItemTraits_OnUse(actorConditionTypes, parts, 17, true);
				monsterTypes.add(new MonsterType(
					nextId
					, monsterTypeName								// Name
					, parts[2] 										// Tags
					, ResourceFileParser.parseImageID(tileLoader, parts[0])
					, ResourceFileParser.parseSize(parts[3], size1x1) //TODO: This could be loaded from the tileset size instead.
					, maxHP 										// HP
					, maxAP											// AP
					, ResourceFileParser.parseInt(parts[6], 10)		// MoveCost
					, combatTraits
			        , hitEffect
					, exp 											// Exp
					, droplists.getDropList(parts[15]) 				// Droplist
					, parts[16]										// PhraseID
				));
			}
		});
    }

	private static float div100(int v) {
		return (float) v / 100f;
	}
	private static int getExpectedMonsterExperience(final CombatTraits t, final int maxHP, final int maxAP) {
		if (t == null) return 0;
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
