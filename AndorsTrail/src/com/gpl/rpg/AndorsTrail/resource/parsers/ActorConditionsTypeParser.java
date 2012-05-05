package com.gpl.rpg.AndorsTrail.resource.parsers;

import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer.ResourceParserFor;
import com.gpl.rpg.AndorsTrail.util.Pair;

public final class ActorConditionsTypeParser extends ResourceParserFor<ActorConditionType> {
	
	private final DynamicTileLoader tileLoader;
		
	public ActorConditionsTypeParser(final DynamicTileLoader tileLoader) {
		super(30);
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
				, ResourceParserUtils.parseBoolean(parts[5], false)
				, ResourceParserUtils.parseStatsModifierTraits(parts, 6)
    			, ResourceParserUtils.parseStatsModifierTraits(parts, 12)
    			, ResourceParserUtils.parseAbilityModifierTraits(parts, 18)
			));
	}
}
