package com.gpl.rpg.AndorsTrail.conversation;

public final class Phrase {
	private static final Reply[] NO_REPLIES = new Reply[0];

	public final String message;
	public final Reply[] replies;
	public final Reward[] rewards; // If this phrase is reached, player will be awarded all these rewards
	public final String switchToNPC;

	public Phrase(
			String message
			,Reply[] replies
			,Reward[] rewards
			,String switchToNPC
	) {
		this.message = message;
		if (replies == null || replies.length == 0) replies = NO_REPLIES;
		this.replies = replies;
		this.rewards = rewards;
		this.switchToNPC = switchToNPC;
	}

	public static final class Reply {
		public final String text;
		public final String nextPhrase;
		public final Requirement[] requires;

		public boolean hasRequirements() {
			return requires != null;
		}

		public Reply(String text, String nextPhrase, Requirement[] requires) {
			this.text = text;
			this.nextPhrase = nextPhrase;
			this.requires = requires;
		}
	}

	public static final class Requirement {
		public static enum RequirementType {
			questProgress
			,inventoryRemove	// Player must have item(s) in inventory. Items will be removed when selecting reply.
			,inventoryKeep		// Player must have item(s) in inventory. Items will NOT be removed when selecting reply.
			,wear				// Player must be wearing item(s). Items will NOT be removed when selecting reply.
			,skillLevel			// Player needs to have a specific skill equal to or above a certain level
			,killedMonster
		}

		public final RequirementType requireType;
		public final String requireID;
		public final int value;

		public Requirement(RequirementType requireType, String requireID, int value) {
			this.requireType = requireType;
			this.requireID = requireID;
			this.value = value;
		}
	}

	public static final class Reward {
		public static enum RewardType {
			questProgress
			,dropList
			,skillIncrease
			,actorCondition
			,alignmentChange
		}

		public final RewardType rewardType;
		public final String rewardID;
		public final int value;

		public Reward(RewardType rewardType, String rewardID, int value) {
			this.rewardType = rewardType;
			this.rewardID = rewardID;
			this.value = value;
		}
	}
}
