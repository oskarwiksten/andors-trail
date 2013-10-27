package com.gpl.rpg.AndorsTrail.model.conversation;

public final class Phrase {
	private static final Reply[] NO_REPLIES = new Reply[0];

	public final String message;
	public final Reply[] replies;
	public final Reward[] rewards; // If this phrase is reached, player will be awarded all these rewards
	public final String switchToNPC;

	public Phrase(
			String message
			, Reply[] replies
			, Reward[] rewards
			, String switchToNPC
	) {
		this.message = message;
		if (replies == null || replies.length == 0) replies = NO_REPLIES;
		this.replies = replies;
		this.rewards = rewards;
		this.switchToNPC = switchToNPC;
	}
}
