package com.gpl.rpg.AndorsTrail.conversation;

import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;

public final class Phrase {
	public static final Reply[] NO_REPLIES = new Reply[0];
	
	public final String message;
	public final Reply[] replies;
	public final QuestProgress progressQuest; 	// If this phrase is reached, this quest will be updated.
	public final DropList rewardDropList; 	// If this phrase is reached, player will be awarded all these items
	
	public Phrase(String message, Reply[] replies, QuestProgress progressQuest, DropList rewardDropList) {
		this.message = message;
		if (replies == null) replies = NO_REPLIES;
		this.replies = replies;
		this.progressQuest = progressQuest;
		this.rewardDropList = rewardDropList;
	}

	public static final class Reply {
		public final String text;
		public final String nextPhrase;
		public final QuestProgress requiresProgress;
		public final int requiresItemTypeID;
		public final int requiresItemQuantity;
		
		public Reply(String text, String nextPhrase, QuestProgress requiresProgress, int requiresItemTypeID, int requiresItemQuantity) {
			this.text = text;
			this.nextPhrase = nextPhrase;
			this.requiresProgress = requiresProgress;
			this.requiresItemTypeID = requiresItemTypeID;
			this.requiresItemQuantity = requiresItemQuantity;
		}
	}
}
