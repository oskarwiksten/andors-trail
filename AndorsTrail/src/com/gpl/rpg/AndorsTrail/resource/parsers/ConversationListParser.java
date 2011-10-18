package com.gpl.rpg.AndorsTrail.resource.parsers;

import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.conversation.Phrase;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
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
				);
		}
	};
	
	@Override
	public Pair<String, Phrase> parseRow(String[] parts) {
		// [id|message|progressQuest|rewardDropListID|replies[text|nextPhraseID|requires_Progress|requires_itemID|requires_Quantity|]|];
		
		final ArrayList<Reply> replies = new ArrayList<Reply>();
		replyResourceTokenizer.tokenizeArray(parts[4], replies, replyParser);
		final Reply[] _replies = replies.toArray(new Reply[replies.size()]);
		
		return new Pair<String, Phrase>(parts[0], new Phrase(
				ResourceParserUtils.parseNullableString(parts[1])	// message
    			, _replies											// replies
    			, QuestProgress.parseQuestProgress(parts[2])		// questProgress
	        	, ResourceParserUtils.parseNullableString(parts[3])	// rewardDroplist
			));
	}
}
