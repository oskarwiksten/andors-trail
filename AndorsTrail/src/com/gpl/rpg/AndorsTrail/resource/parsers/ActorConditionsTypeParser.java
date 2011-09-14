package com.gpl.rpg.AndorsTrail.resource.parsers;

import android.util.Pair;

import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer.ResourceParserFor;

public final class ActorConditionsTypeParser extends ResourceParserFor<ActorConditionType> {
	
	private final DynamicTileLoader tileLoader;
		
	public ActorConditionsTypeParser(final DynamicTileLoader tileLoader) {
		super(29);
		this.tileLoader = tileLoader;
	}
	
	@Override
	public Pair<String, ActorConditionType> parseRow(String[] parts) {
		final String conditionTypeID = parts[0];
		return new Pair<String, ActorConditionType>(conditionTypeID, new ActorConditionType(
				conditionTypeID
				, parts[1]
				, ResourceParserUtils.parseImageID(tileLoader, parts[2])
				, Integer.parseInt(parts[3])
				, ResourceParserUtils.parseBoolean(parts[4], false)
				, ResourceParserUtils.parseStatsModifierTraits(parts, 5)
    			, ResourceParserUtils.parseStatsModifierTraits(parts, 11)
    			, ResourceParserUtils.parseAbilityModifierTraits(parts, 17)
			));
	}
}
