package com.gpl.rpg.AndorsTrail.model.ability;

import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectFieldParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectTokenizer;
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
	
	private static final ResourceObjectTokenizer actorConditionResourceTokenizer = new ResourceObjectTokenizer(5);
	public void initialize(final DynamicTileLoader tileLoader, String conditionList) {
		actorConditionResourceTokenizer.tokenizeRows(conditionList, new ResourceObjectFieldParser() {
			@Override
			public void matchedRow(String[] parts) {
    		
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
		});
	}
}
