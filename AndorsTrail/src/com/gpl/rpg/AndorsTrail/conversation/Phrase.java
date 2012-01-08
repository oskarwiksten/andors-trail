package com.gpl.rpg.AndorsTrail.conversation;

import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;

public final class Phrase {
	public static final Reply[] NO_REPLIES = new Reply[0];
	
	public final String message;
	public final Reply[] replies;
	public final Reward[] rewards; // If this phrase is reached, player will be awarded all these rewards
	
	public Phrase(String message, Reply[] replies, Reward[] rewards) {
		this.message = message;
		if (replies == null || replies.length == 0) replies = NO_REPLIES;
		this.replies = replies;
		this.rewards = rewards;
	}

	public static final class Reply {
		public static final int ITEM_REQUIREMENT_TYPE_INVENTORY_REMOVE = 0; // Player must have item(s) in inventory. Items will be removed when selecting reply.
		public static final int ITEM_REQUIREMENT_TYPE_INVENTORY_KEEP = 1; // Player must have item(s) in inventory. Items will NOT be removed when selecting reply.
		public static final int ITEM_REQUIREMENT_TYPE_WEAR_KEEP = 2; // Player must be wearing item(s). Items will NOT be removed when selecting reply.
		
		public final String text;
		public final String nextPhrase;
		public final QuestProgress requiresProgress;
		public final String requiresItemTypeID;
		public final int requiresItemQuantity;
		public final int itemRequirementType;
		
		public boolean requiresItem() {
			if (requiresItemTypeID == null) return false;
			if (requiresItemQuantity <= 0) return false;
	    	return true;
		}
		
		public Reply(String text, String nextPhrase, QuestProgress requiresProgress, String requiresItemTypeID, int requiresItemQuantity, int itemRequirementType) {
			this.text = text;
			this.nextPhrase = nextPhrase;
			this.requiresProgress = requiresProgress;
			this.requiresItemTypeID = requiresItemTypeID;
			this.requiresItemQuantity = requiresItemQuantity;
			this.itemRequirementType = itemRequirementType;
		}
	}
	
	public static final class Reward {
		public static final int REWARD_TYPE_QUEST_PROGRESS = 0;
		public static final int REWARD_TYPE_DROPLIST = 1;
		public static final int REWARD_TYPE_SKILL_INCREASE = 2;
		public static final int REWARD_TYPE_ACTOR_CONDITION = 3;
		public static final int REWARD_TYPE_ALIGNMENT_CHANGE = 4;
		
		public final int rewardType;
		public final String rewardID;
		public final int value;
		
		public Reward(int rewardType, String rewardID, int value) {
			this.rewardType = rewardType;
			this.rewardID = rewardID;
			this.value = value;
		}
	}
}
