package com.gpl.rpg.AndorsTrail.model.conversation;

public final class Requirement {
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
