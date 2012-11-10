package com.gpl.rpg.AndorsTrail.model.ability;

import com.gpl.rpg.AndorsTrail.model.actor.Player;

public class SkillInfo {
	public static final int MAXLEVEL_NONE = -1;
	public static final int LEVELUP_TYPE_ALWAYS_SHOWN = 0; 
	public static final int LEVELUP_TYPE_ONLY_BY_QUESTS = 1;
	public static final int LEVELUP_TYPE_FIRST_LEVEL_REQUIRES_QUEST = 2;
	
	public final int id;
	public final int maxLevel;
	public final int levelupVisibility;
	public final SkillLevelRequirement[] levelupRequirements;
	public SkillInfo(int id, int maxLevel, int levelupVisibility, SkillLevelRequirement[] levelupRequirements) {
		this.id = id;
		this.maxLevel = maxLevel;
		this.levelupVisibility = levelupVisibility;
		this.levelupRequirements = levelupRequirements;
	}
	
	public boolean hasMaxLevel() {
		if (maxLevel == MAXLEVEL_NONE) return false;
		else return true;
	}
	
	public boolean hasLevelupRequirements() {
		return levelupRequirements != null;
	}
	
	public boolean canLevelUpSkillTo(Player player, int requestedSkillLevel) {
		if (!hasLevelupRequirements()) return true;
		
		for (SkillLevelRequirement requirement : levelupRequirements) {
			if (!requirement.isSatisfiedByPlayer(player, requestedSkillLevel)) return false;
		}
		return true;
	}
	
	public static final class SkillLevelRequirement {
		public static final int REQUIREMENT_TYPE_SKILL_LEVEL = 0;
		public static final int REQUIREMENT_TYPE_EXPERIENCE_LEVEL = 1;
		public static final int REQUIREMENT_TYPE_COMBAT_STAT = 2;
		public static final int REQUIREMENT_TYPE_ACTOR_STAT = 3;
		public final int requirementType;
		public final int skillOrStatID;
		public final int everySkillLevelRequiresThisAmount;
		public final int initialRequiredAmount;
		
		private SkillLevelRequirement(int requirementType, int everySkillLevelRequiresThisAmount, int initialRequiredAmount, int skillOrStatID) {
			this.requirementType = requirementType;
			this.skillOrStatID = skillOrStatID;
			this.everySkillLevelRequiresThisAmount = everySkillLevelRequiresThisAmount;
			this.initialRequiredAmount = initialRequiredAmount;
		}
		
		public static SkillLevelRequirement requireOtherSkill(int skillID, int everySkillLevelRequiresThisAmount) {
			return new SkillLevelRequirement(REQUIREMENT_TYPE_SKILL_LEVEL, everySkillLevelRequiresThisAmount, 0, skillID);
		}
		public static SkillLevelRequirement requireExperienceLevels(int everySkillLevelRequiresThisAmount, int initialRequiredAmount) {
			return new SkillLevelRequirement(REQUIREMENT_TYPE_EXPERIENCE_LEVEL, everySkillLevelRequiresThisAmount, initialRequiredAmount, 0);
		}
		public static SkillLevelRequirement requireCombatStats(int statID, int everySkillLevelRequiresThisAmount, int initialRequiredAmount) {
			return new SkillLevelRequirement(REQUIREMENT_TYPE_COMBAT_STAT, everySkillLevelRequiresThisAmount, initialRequiredAmount, statID);
		}
		public static SkillLevelRequirement requireActorStats(int statID, int everySkillLevelRequiresThisAmount, int initialRequiredAmount) {
			return new SkillLevelRequirement(REQUIREMENT_TYPE_ACTOR_STAT, everySkillLevelRequiresThisAmount, initialRequiredAmount, statID);
		}
		
		public boolean isSatisfiedByPlayer(Player player, int requestedSkillLevel) {
			final int minimumValueRequired = getRequiredValue(requestedSkillLevel);
			final int playerValue = getRequirementActualValue(player);
			if (playerValue >= minimumValueRequired) return true;
			return false;
		}
		
		public int getRequiredValue(int requestedSkillLevel) {
			return requestedSkillLevel * everySkillLevelRequiresThisAmount + initialRequiredAmount;
		}
		
		private int getRequirementActualValue(Player player) {
			switch (requirementType) {
			case REQUIREMENT_TYPE_SKILL_LEVEL: return player.getSkillLevel(skillOrStatID);
			case REQUIREMENT_TYPE_EXPERIENCE_LEVEL: return player.level;
			case REQUIREMENT_TYPE_COMBAT_STAT: return player.actorTraits.baseCombatTraits.getCombatStats(skillOrStatID);
			case REQUIREMENT_TYPE_ACTOR_STAT: return player.actorTraits.getActorStats(skillOrStatID);
			default: return 0;
			}
		}
	}
}
