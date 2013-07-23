package com.gpl.rpg.AndorsTrail.resource.parsers;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.conversation.Phrase;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Requirement;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reward;
import com.gpl.rpg.AndorsTrail.resource.TranslationLoader;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonArrayParserFor;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonCollectionParserFor;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonFieldNames;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Pair;
import org.json.JSONException;
import org.json.JSONObject;

public final class ConversationListParser extends JsonCollectionParserFor<Phrase> {

	private final TranslationLoader translationLoader;

	private final JsonArrayParserFor<Requirement> requirementParser = new JsonArrayParserFor<Requirement>(Requirement.class) {
		@Override
		protected Requirement parseObject(JSONObject o) throws JSONException {
			return new Requirement(
					Requirement.RequirementType.valueOf(o.getString(JsonFieldNames.ReplyRequires.requireType))
					,o.getString(JsonFieldNames.ReplyRequires.requireID)
					,o.optInt(JsonFieldNames.ReplyRequires.value, 0)
			);
		}
	};

	private final JsonArrayParserFor<Reply> replyParser = new JsonArrayParserFor<Reply>(Reply.class) {
		@Override
		protected Reply parseObject(JSONObject o) throws JSONException {
			return new Reply(
					translationLoader.translateConversationReply(o.optString(JsonFieldNames.Reply.text, ""))
					,o.getString(JsonFieldNames.Reply.nextPhraseID)
					,requirementParser.parseArray(o.optJSONArray(JsonFieldNames.Reply.requires))
			);
		}
	};

	private final JsonArrayParserFor<Reward> rewardParser = new JsonArrayParserFor<Reward>(Reward.class) {
		@Override
		protected Reward parseObject(JSONObject o) throws JSONException {
			return new Reward(
					Reward.RewardType.valueOf(o.getString(JsonFieldNames.PhraseReward.rewardType))
					,o.getString(JsonFieldNames.PhraseReward.rewardID)
					,o.optInt(JsonFieldNames.PhraseReward.value, 0)
			);
		}
	};

	public ConversationListParser(TranslationLoader translationLoader) {
		this.translationLoader = translationLoader;
	}

	@Override
	protected Pair<String, Phrase> parseObject(JSONObject o) throws JSONException {
		final String id = o.getString(JsonFieldNames.Phrase.phraseID);

		Reply[] _replies = null;
		Reward[] _rewards = null;
		try {
			_replies = replyParser.parseArray(o.optJSONArray(JsonFieldNames.Phrase.replies));
			_rewards = rewardParser.parseArray(o.optJSONArray(JsonFieldNames.Phrase.rewards));
		} catch (JSONException e) {
			if (AndorsTrailApplication.DEVELOPMENT_DEBUGMESSAGES) {
				L.log("ERROR: parsing phrase " + id + " : " + e.getMessage());
			}
		}

		return new Pair<String, Phrase>(id, new Phrase(
				translationLoader.translateConversationPhrase(o.optString(JsonFieldNames.Phrase.message, null))
				, _replies
				, _rewards
		));
	}
}
