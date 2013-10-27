package com.gpl.rpg.AndorsTrail.model.script;

public final class ScriptEffect {
	public static enum ScriptEffectType {
		questProgress
		, dropList
		, skillIncrease
		, actorCondition
		, alignmentChange
		, giveItem
		, createTimer
	}

	public final ScriptEffectType type;
	public final String effectID;
	public final int value;

	public ScriptEffect(
			ScriptEffectType type
			, String effectID
			, int value
	) {
		this.type = type;
		this.effectID = effectID;
		this.value = value;
	}
}
