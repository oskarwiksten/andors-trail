package com.gpl.rpg.AndorsTrail.model.ability;

import java.util.ArrayList;
import java.util.regex.Matcher;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser;
import com.gpl.rpg.AndorsTrail.util.L;

public class ActorConditionTypeCollection {
	private final ArrayList<ActorConditionType> conditionTypes = new ArrayList<ActorConditionType>();
	
	public ActorConditionType getActorConditionType(String conditionTypeID) {
		for (ActorConditionType t : conditionTypes) {
			if (t.conditionTypeID.equals(conditionTypeID)) return t;
		}
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Cannot find ActorConditionType \"" + conditionTypeID + "\".");
		}
		return null;
	}
	
	public void initialize(DynamicTileLoader tileLoader, String conditionList) {
		Matcher rowMatcher = ResourceFileParser.rowPattern.matcher(conditionList);
    	while(rowMatcher.find()) {
    		String[] parts = rowMatcher.group(1).split(ResourceFileParser.columnSeparator, -1);
    		if (parts.length < 28) continue;
    		
    		final String conditionTypeID = parts[0];
    		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				if (conditionTypeID == null || conditionTypeID.length() <= 0) {
					L.log("OPTIMIZE: ActorConditionType \"" + parts[1] + "\" has empty searchtag.");
				}
				for (ActorConditionType t : conditionTypes) {
					if (t.conditionTypeID.equals(conditionTypeID)) {
						L.log("OPTIMIZE: ActorConditionType " + conditionTypeID + " is duplicated.");
						break;
					}
				}
			}
			
			final ActorConditionType actorConditionType = new ActorConditionType(
					conditionTypeID
					, parts[1]
					, ResourceFileParser.parseImageID(tileLoader, parts[2])
					, ResourceFileParser.parseBoolean(parts[3], false)
					, ResourceFileParser.parseStatsModifierTraits(parts, 4)
        			, ResourceFileParser.parseStatsModifierTraits(parts, 10)
        			, ResourceFileParser.parseAbilityModifierTraits(parts, 16)
    			);
			conditionTypes.add(actorConditionType);
    	}
		/*
		CombatTraits t = new CombatTraits();
		t.attackChance = 5;
		conditionTypes.add(new ActorConditionType("bless", "Bless", tileLoader.prepareTileID("items_tiles", 13+22*14), false, null, null, new AbilityModifierTraits(0, 0, 0, t)));
		
		t = new CombatTraits();
		t.damagePotential.set(2, 2);
		conditionTypes.add(new ActorConditionType("str", "Strength", tileLoader.prepareTileID("items_tiles", 0+25*14), false, null, null, new AbilityModifierTraits(0, 0, 0, t)));
		
		conditionTypes.add(new ActorConditionType("regen", "Regeneration", tileLoader.prepareTileID("items_tiles", 7+22*14), false, new StatsModifierTraits(VisualEffectCollection.EFFECT_RESTORE_HP, new ConstRange(1, 1), null), null, null));
		conditionTypes.add(new ActorConditionType("poison", "Poison", tileLoader.prepareTileID("items_tiles", 4+24*14), true, new StatsModifierTraits(VisualEffectCollection.EFFECT_POISON, new ConstRange(-1, -1), null), null, null));
		*/
	}
}
