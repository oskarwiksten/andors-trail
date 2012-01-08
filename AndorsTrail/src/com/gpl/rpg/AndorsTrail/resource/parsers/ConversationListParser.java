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
		super(4); 
	}

	private final ResourceFileTokenizer replyResourceTokenizer = new ResourceFileTokenizer(6);
	private final ResourceObjectParser<Reply> replyParser = new ResourceObjectParser<Reply>() {
		@Override
		public Reply parseRow(String[] parts) {
			return new Reply(
					parts[0]											// text
					, parts[1]											// nextPhrase
					, QuestProgress.parseQuestProgress(parts[2])		// requiresProgress
			       	, ResourceParserUtils.parseNullableString(parts[3])	// requiresItemType
			       	, ResourceParserUtils.parseInt(parts[4], 0)			// requiresItemQuantity
			       	, ResourceParserUtils.parseInt(parts[5], Reply.ITEM_REQUIREMENT_TYPE_INVENTORY_REMOVE)	// itemRequirementType
				);
		}
	};
	
	private final ResourceFileTokenizer rewardResourceTokenizer = new ResourceFileTokenizer(3);
	private final ResourceObjectParser<Reward> rewardParser = new ResourceObjectParser<Reward>() {
		@Override
		public Reward parseRow(String[] parts) {
			return new Reward(
					Integer.parseInt(parts[0]) 					// rewardType
					, parts[1]									// rewardID
					, ResourceParserUtils.parseInt(parts[2], 0)	// value
				);
		}
	};
	
	@Override
	public Pair<String, Phrase> parseRow(String[] parts) {
		// [id|message|rewards[rewardType|rewardID|value|]|replies[text|nextPhraseID|requires_Progress|requires_itemID|requires_Quantity|requires_Type|]|];
		
		final ArrayList<Reply> replies = new ArrayList<Reply>();
		replyResourceTokenizer.tokenizeArray(parts[3], replies, replyParser);
		final Reply[] _replies = replies.toArray(new Reply[replies.size()]);
		
		final ArrayList<Reward> rewards = new ArrayList<Reward>();
		rewardResourceTokenizer.tokenizeArray(parts[2], rewards, rewardParser);
		Reward[] _rewards = rewards.toArray(new Reward[rewards.size()]);
		if (_rewards.length == 0) _rewards = null;
		
		return new Pair<String, Phrase>(parts[0], new Phrase(
				ResourceParserUtils.parseNullableString(parts[1])	// message
    			, _replies											// replies
				, _rewards 											// rewards
			));
	}
}
