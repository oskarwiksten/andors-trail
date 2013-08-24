package com.gpl.rpg.AndorsTrail.model.conversation;

public final class Reward {
	public static enum RewardType {
		questProgress
		,dropList
		,skillIncrease
		,actorCondition
		,alignmentChange
		,giveItem
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
