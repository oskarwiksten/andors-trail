package com.gpl.rpg.AndorsTrail.model.ability;

public class SkillInfo {
	public static final int MAXLEVEL_NONE = -1;
	public final int id;
	public final int maxLevel;
	public final boolean isQuestSkill;
	public SkillInfo(int id, int maxLevel, boolean isQuestSkill) {
		this.id = id;
		this.maxLevel = maxLevel;
		this.isQuestSkill = isQuestSkill;
	}
	
	public boolean hasMaxLevel() {
		if (maxLevel == MAXLEVEL_NONE) return false;
		else return true;
	}
	
	public boolean hasLevelupRequirements() {
		return false;
	}
	
	/*private static final class SkillLevelRequirement {
		public static final int REQUIREMENT_TYPE_SKILL_LEVEL = 0;
		public static final int REQUIREMENT_TYPE_EXPERIENCE_LEVEL = 1;
		public static final int REQUIREMENT_TYPE_COMBAT_STAT = 2;
		public final int requirementType;
		//public final int ;
		public final int value;
	}
	
	public static final class Stats {
		public static final int STAT_MAX_HP = 0;
	}*/
}
