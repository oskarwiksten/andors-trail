package com.gpl.rpg.AndorsTrail.model.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectFieldParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectTokenizer;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class MonsterTypeCollection {
	private final HashMap<String, MonsterType> monsterTypes = new HashMap<String, MonsterType>();
	
	public MonsterType getMonsterType(String id) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!monsterTypes.containsKey(id)) {
				L.log("WARNING: Cannot find MonsterType for id \"" + id + "\".");
			}
		}
		return monsterTypes.get(id);
	}

	public Collection<? extends MonsterType> getMonsterTypesFromTags(String tagsAndNames) {
		String[] parts = tagsAndNames.toLowerCase().split(",");
		ArrayList<MonsterType> result = new ArrayList<MonsterType>();
		for (MonsterType t : monsterTypes.values()) {
			if (t.matchesAny(parts)) result.add(t);
		}
		return result;
	}
	
	public MonsterType guessMonsterTypeFromName(String name) {
		for (MonsterType t : monsterTypes.values()) {
			if (t.name.equalsIgnoreCase(name)) return t;
		}
		return null;
	}
	
	private static final Size size1x1 = new Size(1, 1);
	private static final ResourceObjectTokenizer monsterResourceTokenizer = new ResourceObjectTokenizer(25);
	public void initialize(final DropListCollection droplists, final ActorConditionTypeCollection actorConditionTypes, final DynamicTileLoader tileLoader, String monsterlist) {
		//[iconID|name|tags|size|maxHP|maxAP|moveCost|attackCost|attackChance|criticalChance|criticalMultiplier|attackDamage_Min|attackDamage_Max|blockChance|damageResistance|droplistID|phraseID|
		// hasHitEffect|onHit_boostHP_Min|onHit_boostHP_Max|onHit_boostAP_Min|onHit_boostAP_Max|onHit_conditionsSource[condition|magnitude|duration|chance|]|onHit_conditionsTarget[condition|magnitude|duration|chance|]|];
		monsterResourceTokenizer.tokenizeRows(monsterlist, new ResourceObjectFieldParser() {
			@Override
			public void matchedRow(String[] parts) {
				final String monsterTypeId = parts[0];
	        	
	    		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
	    			if (monsterTypes.containsKey(monsterTypeId)) {
	    				L.log("WARNING: Monster " + monsterTypeId + " is duplicated.");
	        		}
	    		}
	        	
	    		final int maxHP = ResourceFileParser.parseInt(parts[5], 1);
	    		final int maxAP = ResourceFileParser.parseInt(parts[6], 10);
	    		final CombatTraits combatTraits = ResourceFileParser.parseCombatTraits(parts, 8);
	    		final ItemTraits_OnUse hitEffect = ResourceFileParser.parseItemTraits_OnUse(actorConditionTypes, parts, 18, true);
				final int exp = getExpectedMonsterExperience(combatTraits, hitEffect, maxHP, maxAP);
	    		monsterTypes.put(monsterTypeId, new MonsterType(
					monsterTypeId
					, parts[2]										// Name
					, parts[3] 										// Tags
					, ResourceFileParser.parseImageID(tileLoader, parts[1])
					, ResourceFileParser.parseSize(parts[4], size1x1) //TODO: This could be loaded from the tileset size instead.
					, maxHP 										// HP
					, maxAP											// AP
					, ResourceFileParser.parseInt(parts[7], 10)		// MoveCost
					, combatTraits
			        , hitEffect
					, exp 											// Exp
					, droplists.getDropList(parts[16]) 				// Droplist
					, ResourceFileParser.parseNullableString(parts[17]) // PhraseID
				));
			}
		});
    }

	private static float div100(int v) {
		return (float) v / 100f;
	}
	private static int getExpectedMonsterExperience(final CombatTraits t, ItemTraits_OnUse hitEffect, final int maxHP, final int maxAP) {
		if (t == null) return 0;
		final float avgAttackHP  = t.getAttacksPerTurn(maxAP) * div100(t.attackChance) * t.damagePotential.averagef() * (1 + div100(t.criticalChance) * t.criticalMultiplier);
		final float avgDefenseHP = maxHP * (1 + div100(t.blockChance)) + Constants.EXP_FACTOR_DAMAGERESISTANCE * t.damageResistance;
		int attackConditionBonus = 0;
		if (hitEffect != null && hitEffect.addedConditions_target != null && hitEffect.addedConditions_target.length > 0) {
			attackConditionBonus += 50;
		}
		return (int) Math.ceil((avgAttackHP * 3 + avgDefenseHP) * Constants.EXP_FACTOR_SCALING) + attackConditionBonus;
	}
	
	// Selftest method. Not part of the game logic.
	public void verifyData(WorldContext world) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		for (MonsterType t : monsterTypes.values()) {
    			if (t.phraseID != null && t.phraseID.length() > 0) {
    				if (!world.conversations.isValidPhraseID(t.phraseID)) {
    					L.log("WARNING: Cannot find phrase \"" + t.phraseID + "\" for MonsterType \"" + t.id + "\".");
    				}
    			}
    		}
    	}
	}

	// Selftest method. Not part of the game logic.
	public void verifyData(ConversationCollection conversations) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		for (MonsterType t : monsterTypes.values()) {
    			if (t.phraseID != null && t.phraseID.length() > 0) {
    				if (conversations.DEBUG_leadsToTradeReply(t.phraseID)) {
    					if (t.dropList == null) {
    						L.log("WARNING: MonsterType \"" + t.id + "\" has conversation \"" + t.phraseID + "\" that leads to a trade, but the monster type does not have a droplist.");
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
    		for (MonsterType t : monsterTypes.values()) {
    			if (t.phraseID != null && t.phraseID.length() > 0) {
    				requiredPhrases.add(t.phraseID);
    			}
    		}
    		return requiredPhrases;
    	} else {
    		return null;
    	}
	}
	
	// Selftest method. Not part of the game logic.
	public void DEBUG_getUsedDroplists(HashSet<DropList> usedDroplists) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		for (MonsterType t : monsterTypes.values()) {
    			if (t.dropList != null) usedDroplists.add(t.dropList);
    		}
    	}
	}
}
