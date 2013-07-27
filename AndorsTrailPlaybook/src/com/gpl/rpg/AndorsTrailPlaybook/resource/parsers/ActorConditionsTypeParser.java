package com.gpl.rpg.AndorsTrailPlaybook.resource.parsers;

import com.gpl.rpg.AndorsTrailPlaybook.model.ability.ActorConditionType;
import com.gpl.rpg.AndorsTrailPlaybook.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrailPlaybook.resource.TranslationLoader;
import com.gpl.rpg.AndorsTrailPlaybook.resource.parsers.json.JsonCollectionParserFor;
import com.gpl.rpg.AndorsTrailPlaybook.resource.parsers.json.JsonFieldNames;
import com.gpl.rpg.AndorsTrailPlaybook.util.Pair;
import org.json.JSONException;
import org.json.JSONObject;

public final class ActorConditionsTypeParser extends JsonCollectionParserFor<ActorConditionType> {

	private final DynamicTileLoader tileLoader;
	private final TranslationLoader translationLoader;

	public ActorConditionsTypeParser(final DynamicTileLoader tileLoader, TranslationLoader translationLoader) {
		this.tileLoader = tileLoader;
		this.translationLoader = translationLoader;
	}

	@Override
	protected Pair<String, ActorConditionType> parseObject(JSONObject o) throws JSONException {
		final String conditionTypeID = o.getString(JsonFieldNames.ActorCondition.conditionTypeID);
		ActorConditionType result = new ActorConditionType(
				conditionTypeID
				,translationLoader.translateActorConditionName(o.getString(JsonFieldNames.ActorCondition.name))
				,ResourceParserUtils.parseImageID(tileLoader, o.getString(JsonFieldNames.ActorCondition.iconID))
				,ActorConditionType.ConditionCategory.valueOf(o.getString(JsonFieldNames.ActorCondition.category))
				,o.optInt(JsonFieldNames.ActorCondition.isStacking) > 0
				,o.optInt(JsonFieldNames.ActorCondition.isPositive) > 0
				,ResourceParserUtils.parseStatsModifierTraits(o.optJSONObject(JsonFieldNames.ActorCondition.roundEffect))
				,ResourceParserUtils.parseStatsModifierTraits(o.optJSONObject(JsonFieldNames.ActorCondition.fullRoundEffect))
				,ResourceParserUtils.parseAbilityModifierTraits(o.optJSONObject(JsonFieldNames.ActorCondition.abilityEffect))
		);
		return new Pair<String, ActorConditionType>(conditionTypeID, result);
	}
}
