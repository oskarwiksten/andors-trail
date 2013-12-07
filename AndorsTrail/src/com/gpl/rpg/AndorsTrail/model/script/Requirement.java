package com.gpl.rpg.AndorsTrail.model.script;

public final class Requirement {
	public static enum RequirementType {
		questProgress
		,questLatestProgress // Highest quest stage reached must match.
		,inventoryRemove	// Player must have item(s) in inventory. Items will be removed when selecting reply.
		,inventoryKeep		// Player must have item(s) in inventory. Items will NOT be removed when selecting reply.
		,wear				// Player must be wearing item(s). Items will NOT be removed when selecting reply.
		,skillLevel			// Player needs to have a specific skill equal to or above a certain level
		,killedMonster
		,timerElapsed
		,usedItem
		,spentGold
		,consumedBonemeals
		,hasActorCondition
	}

	public final RequirementType requireType;
	public final String requireID;
	public final int value;
	public final boolean negate;

	public Requirement(
			RequirementType requireType
			, String requireID
			, int value
			, boolean negate
	) {
		this.requireType = requireType;
		this.requireID = requireID;
		this.value = value;
		this.negate = negate;
	}

	public String toString() {
		StringBuilder buf = new StringBuilder(requireType.toString());
		buf.append("--");
		buf.append(requireID);
		buf.append("--");
		if (negate) buf.append('!');
		buf.append(value);
		return buf.toString();
	}
}
