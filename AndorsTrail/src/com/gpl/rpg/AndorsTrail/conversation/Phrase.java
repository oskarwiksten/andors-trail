package com.gpl.rpg.AndorsTrail.conversation;

import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;

public final class Phrase {
	public static final Reply[] NO_REPLIES = new Reply[0];
	
	public final String message;
	public final Reply[] replies;
	public final QuestProgress progressQuest; 	// If this phrase is reached, this quest will be updated.
	public final String rewardDropListID; 		// If this phrase is reached, player will be awarded all these items
	
	public Phrase(String message, Reply[] replies, QuestProgress progressQuest, String rewardDropListID) {
		this.message = message;
		if (replies == null) replies = NO_REPLIES;
		this.replies = replies;
		this.progressQuest = progressQuest;
		this.rewardDropListID = rewardDropListID;
	}

	public static final class Reply {
		public final String text;
		public final String nextPhrase;
		public final QuestProgress requiresProgress;
		public final String requiresItemTypeID;
		public final int requiresItemQuantity;
		
		public boolean requiresItem() {
			if (requiresItemTypeID == null) return false;
			if (requiresItemQuantity <= 0) return false;
	    	return true;
		}
		
		public Reply(String text, String nextPhrase, QuestProgress requiresProgress, String requiresItemTypeID, int requiresItemQuantity) {
			this.text = text;
			this.nextPhrase = nextPhrase;
			this.requiresProgress = requiresProgress;
			this.requiresItemTypeID = requiresItemTypeID;
			this.requiresItemQuantity = requiresItemQuantity;
		}
	}
}
