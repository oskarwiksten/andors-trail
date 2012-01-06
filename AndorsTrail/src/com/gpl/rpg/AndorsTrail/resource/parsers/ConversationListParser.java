package com.gpl.rpg.AndorsTrail.resource.parsers;

import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.conversation.Phrase;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reward;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer.ResourceParserFor;
import com.gpl.rpg.AndorsTrail.util.Pair;

public final class ConversationListParser extends ResourceParserFor<Phrase> {
	
	public ConversationListParser() { 
		super(5); 
	}

	private final ResourceFileTokenizer replyResourceTokenizer = new ResourceFileTokenizer(5);
	private final ResourceObjectParser<Reply> replyParser = new ResourceObjectParser<Reply>() {
		@Override
		public Reply parseRow(String[] parts) {
			return new Reply(
					parts[0]											// text
					, parts[1]											// nextPhrase
					, QuestProgress.parseQuestProgress(parts[2])		// requiresProgress
			       	, ResourceParserUtils.parseNullableString(parts[3])	// requiresItemType
			       	, ResourceParserUtils.parseInt(parts[4], 0)			// requiresItemQuantity
			       	, Reply.ITEM_REQUIREMENT_TYPE_INVENTORY_REMOVE		// itemRequirementType
				);
		}
	};
	
	@Override
	public Pair<String, Phrase> parseRow(String[] parts) {
		// [id|message|progressQuest|rewardDropListID|replies[text|nextPhraseID|requires_Progress|requires_itemID|requires_Quantity|]|];
		
		final ArrayList<Reply> replies = new ArrayList<Reply>();
		replyResourceTokenizer.tokenizeArray(parts[4], replies, replyParser);
		final Reply[] _replies = replies.toArray(new Reply[replies.size()]);
		
		final ArrayList<Reward> rewards = new ArrayList<Reward>();
		QuestProgress questProgress = QuestProgress.parseQuestProgress(parts[2]);
		if (questProgress != null) rewards.add(new Reward(Reward.REWARD_TYPE_QUEST_PROGRESS, questProgress.questID, questProgress.progress));
		String rewardDroplist = ResourceParserUtils.parseNullableString(parts[3]);
		if (rewardDroplist != null) rewards.add(new Reward(Reward.REWARD_TYPE_DROPLIST, rewardDroplist, 0));
		Reward[] _rewards = rewards.toArray(new Reward[rewards.size()]);
		if (_rewards.length == 0) _rewards = null;
		
		return new Pair<String, Phrase>(parts[0], new Phrase(
				ResourceParserUtils.parseNullableString(parts[1])	// message
    			, _replies											// replies
				, _rewards 											// rewards
			));
	}
}
