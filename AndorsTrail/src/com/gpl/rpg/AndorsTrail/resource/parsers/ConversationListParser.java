package com.gpl.rpg.AndorsTrail.resource.parsers;

import com.gpl.rpg.AndorsTrail.conversation.Phrase;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reward;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonCollectionParserFor;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonFieldNames;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonParserFor;
import com.gpl.rpg.AndorsTrail.util.Pair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class ConversationListParser extends JsonCollectionParserFor<Phrase> {
	
	private final JsonParserFor<Reply> replyParser = new JsonParserFor<Reply>() {
		@Override
		protected Reply parseObject(JSONObject o) throws JSONException {
			JSONObject requires = o.optJSONObject(JsonFieldNames.Reply.requires);
			String requiresProgress = null;
			String requiresItemTypeID = null;
			int requiresItemQuantity = 0;
			int itemRequirementType = Reply.ITEM_REQUIREMENT_TYPE_INVENTORY_REMOVE;
			if (requires != null) {
				requiresProgress = requires.optString(JsonFieldNames.ReplyRequires.progress, null);
				JSONObject requiresItem = requires.optJSONObject(JsonFieldNames.ReplyRequires.item);
				if (requiresItem != null) {
					requiresItemTypeID = requiresItem.getString(JsonFieldNames.ReplyRequiresItem.itemID);
					requiresItemQuantity = requiresItem.getInt(JsonFieldNames.ReplyRequiresItem.quantity);
					itemRequirementType = requiresItem.getInt(JsonFieldNames.ReplyRequiresItem.requireType);
				}
			}
			return new Reply(
					o.optString(JsonFieldNames.Reply.text, "")
					,o.getString(JsonFieldNames.Reply.nextPhraseID)
					,QuestProgress.parseQuestProgress(requiresProgress)
					,requiresItemTypeID
					,requiresItemQuantity
					,itemRequirementType
			);
		}
	};
	
	private final JsonParserFor<Reward> rewardParser = new JsonParserFor<Reward>() {
		@Override
		protected Reward parseObject(JSONObject o) throws JSONException {
			return new Reward(
					o.getInt(JsonFieldNames.PhraseReward.rewardType)
					,o.getString(JsonFieldNames.PhraseReward.rewardID)
					,o.optInt(JsonFieldNames.PhraseReward.value, 0)
			);
		}
	};
	
	@Override
	protected Pair<String, Phrase> parseObject(JSONObject o) throws JSONException {
		final String id = o.getString(JsonFieldNames.Phrase.phraseID);

		final ArrayList<Reply> replies = new ArrayList<Reply>();
		replyParser.parseRows(o.optJSONArray(JsonFieldNames.Phrase.replies), replies);
		final Reply[] _replies = replies.toArray(new Reply[replies.size()]);

		final ArrayList<Reward> rewards = new ArrayList<Reward>();
		rewardParser.parseRows(o.optJSONArray(JsonFieldNames.Phrase.rewards), rewards);
		Reward[] _rewards = rewards.toArray(new Reward[rewards.size()]);
		if (_rewards.length == 0) _rewards = null;

		return new Pair<String, Phrase>(id, new Phrase(
				o.optString(JsonFieldNames.Phrase.message, null)
				, _replies
				, _rewards
		));
	}
}
