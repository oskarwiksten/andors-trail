package com.gpl.rpg.AndorsTrail.conversation;

public final class Phrase {
	public static final Reply[] NO_REPLIES = new Reply[0];
	
	public final String message;
	public final Reply[] replies;
	public final String enablesKey;
	public final int rewardExperience;
	public final int rewardGold;
	
	public Phrase(String message, Reply[] replies, String enablesKey, int rewardExperience, int rewardGold) {
		this.message = message;
		if (replies == null) replies = NO_REPLIES;
		this.replies = replies;
		if (enablesKey != null && enablesKey.length() <= 0) enablesKey = null;
		this.enablesKey = enablesKey;
		this.rewardExperience = rewardExperience;
		this.rewardGold = rewardGold;
	}

	public static final class Reply {
		public final String text;
		public final String nextPhrase;
		public final String requiresKey;
		public final int requiresItemTypeID;
		public final int requiresItemQuantity;
		
		public Reply(String text, String nextPhrase, String requiresKey, int requiresItemTypeID, int requiresItemQuantity) {
			this.text = text;
			this.nextPhrase = nextPhrase;
			if (requiresKey != null && requiresKey.length() <= 0) requiresKey = null;
			this.requiresKey = requiresKey;
			this.requiresItemTypeID = requiresItemTypeID;
			this.requiresItemQuantity = requiresItemQuantity;
		}
	}
}
