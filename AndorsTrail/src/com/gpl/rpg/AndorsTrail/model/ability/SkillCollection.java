package com.gpl.rpg.AndorsTrail.model.ability;

import java.util.Collection;
import java.util.HashMap;

import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.SkillInfo.SkillLevelRequirement;
import com.gpl.rpg.AndorsTrail.model.actor.ActorTraits;

public final class SkillCollection {
	public static final int SKILL_WEAPON_CHANCE = 0;
	public static final int SKILL_WEAPON_DMG = 1;
	public static final int SKILL_BARTER = 2;
	public static final int SKILL_DODGE = 3;			// + BC
	public static final int SKILL_BARKSKIN = 4;			// Dmg resist
	public static final int SKILL_MORE_CRITICALS = 5;
	public static final int SKILL_BETTER_CRITICALS = 6;
	public static final int SKILL_SPEED = 7; 			// Raises max ap
	public static final int SKILL_COINFINDER = 8;
	public static final int SKILL_MORE_EXP = 9;
	public static final int SKILL_CLEAVE = 10; 			// +10ap on kill
	public static final int SKILL_EATER = 11; 			// +1hp per kill
	public static final int SKILL_FORTITUDE = 12; 		// +N hp per levelup
	public static final int SKILL_EVASION = 13; 		// increase successful flee chance & reduce chance of monster attack
	public static final int SKILL_REGENERATION = 14; 	// +N hp per round
	public static final int SKILL_LOWER_EXPLOSS = 15;
	public static final int SKILL_MAGICFINDER = 16;
	//public static final int SKILL_BERSERKER = 17; 		// <=20%hp increases AC and DMG
	
	public static final int NUM_SKILLS = SKILL_MAGICFINDER+1;
	
	//public static final int BERSERKER_STARTS_AT_HEALTH_PERCENT = 20;
	
	public static final int PER_SKILLPOINT_INCREASE_WEAPON_CHANCE = 12;
	public static final int PER_SKILLPOINT_INCREASE_WEAPON_DAMAGE_MAX = 1;
	public static final int PER_SKILLPOINT_INCREASE_WEAPON_DAMAGE_MIN = 1;
	public static final int PER_SKILLPOINT_INCREASE_DODGE = 9;
	public static final int PER_SKILLPOINT_INCREASE_BARKSKIN = 1;
	public static final int PER_SKILLPOINT_INCREASE_MORE_CRITICALS_PERCENT = 20;
	public static final int PER_SKILLPOINT_INCREASE_BETTER_CRITICALS_PERCENT = 25;
	public static final int PER_SKILLPOINT_INCREASE_SPEED = 1;
	public static final int PER_SKILLPOINT_INCREASE_BARTER_PRICEFACTOR_PERCENTAGE = 4;
	public static final int PER_SKILLPOINT_INCREASE_COINFINDER_CHANCE_PERCENT = 30;
	public static final int PER_SKILLPOINT_INCREASE_MAGICFINDER_CHANCE_PERCENT = 50;
	public static final int PER_SKILLPOINT_INCREASE_COINFINDER_QUANTITY_PERCENT = 50;
	public static final int PER_SKILLPOINT_INCREASE_MORE_EXP_PERCENT = 5;
	public static final int PER_SKILLPOINT_INCREASE_CLEAVE_AP = 3;
	public static final int PER_SKILLPOINT_INCREASE_EATER_HEALTH = 1;
	public static final int PER_SKILLPOINT_INCREASE_FORTITUDE_HEALTH = 2;
	public static final int PER_SKILLPOINT_INCREASE_EVASION_FLEE_CHANCE_PERCENTAGE = 5;
	public static final int PER_SKILLPOINT_INCREASE_EVASION_MONSTER_ATTACK_CHANCE_PERCENTAGE = 5;
	public static final int PER_SKILLPOINT_INCREASE_REGENERATION = 1;
	public static final int PER_SKILLPOINT_INCREASE_EXPLOSS_PERCENT = 20;
	/*public static final int PER_SKILLPOINT_INCREASE_BERSERKER_WEAPON_CHANCE = 15;
	public static final int PER_SKILLPOINT_INCREASE_BERSERKER_WEAPON_DAMAGE_MAX = 1;
	public static final int PER_SKILLPOINT_INCREASE_BERSERKER_WEAPON_DAMAGE_MIN = 1;
	public static final int PER_SKILLPOINT_INCREASE_BERSERKER_DODGE = 9;*/
	
	
	private final HashMap<Integer, SkillInfo> skills = new HashMap<Integer, SkillInfo>();
	private void initializeSkill(SkillInfo skill) {
		skills.put(skill.id, skill);
	}
	public void initialize() {
		initializeSkill(new SkillInfo(SKILL_WEAPON_CHANCE, SkillInfo.MAXLEVEL_NONE, false, null));
		initializeSkill(new SkillInfo(SKILL_WEAPON_DMG, SkillInfo.MAXLEVEL_NONE, false, null));
		initializeSkill(new SkillInfo(SKILL_BARTER, Constants.MARKET_PRICEFACTOR_PERCENT / PER_SKILLPOINT_INCREASE_BARTER_PRICEFACTOR_PERCENTAGE, false, null));
		initializeSkill(new SkillInfo(SKILL_DODGE, SkillInfo.MAXLEVEL_NONE, false, null));
		initializeSkill(new SkillInfo(SKILL_BARKSKIN, 5, false, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireExperienceLevels(10) 
			,SkillLevelRequirement.requireCombatStats(CombatTraits.STAT_COMBAT_BLOCK_CHANCE, 15, 0) 
		}));
		initializeSkill(new SkillInfo(SKILL_MORE_CRITICALS, SkillInfo.MAXLEVEL_NONE, false, null));
		initializeSkill(new SkillInfo(SKILL_BETTER_CRITICALS, SkillInfo.MAXLEVEL_NONE, false, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireOtherSkill(SKILL_MORE_CRITICALS, 1)
		}));
		initializeSkill(new SkillInfo(SKILL_SPEED, 2, false, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireExperienceLevels(15) 
		}));
		initializeSkill(new SkillInfo(SKILL_COINFINDER, SkillInfo.MAXLEVEL_NONE, false, null));
		initializeSkill(new SkillInfo(SKILL_MORE_EXP, SkillInfo.MAXLEVEL_NONE, false, null));
		initializeSkill(new SkillInfo(SKILL_CLEAVE, SkillInfo.MAXLEVEL_NONE, false, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireOtherSkill(SKILL_WEAPON_CHANCE, 1) 
			,SkillLevelRequirement.requireOtherSkill(SKILL_WEAPON_DMG, 1)
		}));
		initializeSkill(new SkillInfo(SKILL_EATER, SkillInfo.MAXLEVEL_NONE, false, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireActorStats(ActorTraits.STAT_ACTOR_MAX_HP, 20, 20)
		}));
		initializeSkill(new SkillInfo(SKILL_FORTITUDE, SkillInfo.MAXLEVEL_NONE, false, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireExperienceLevels(5)
		}));
		initializeSkill(new SkillInfo(SKILL_EVASION, Constants.FLEE_FAIL_CHANCE_PERCENT / PER_SKILLPOINT_INCREASE_EVASION_FLEE_CHANCE_PERCENTAGE, false, null));
		initializeSkill(new SkillInfo(SKILL_REGENERATION, SkillInfo.MAXLEVEL_NONE, false, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireActorStats(ActorTraits.STAT_ACTOR_MAX_HP, 30, 0)
			,SkillLevelRequirement.requireOtherSkill(SKILL_FORTITUDE, 1)
		}));
		initializeSkill(new SkillInfo(SKILL_LOWER_EXPLOSS, 100 / PER_SKILLPOINT_INCREASE_EXPLOSS_PERCENT, false, null));
		initializeSkill(new SkillInfo(SKILL_MAGICFINDER, SkillInfo.MAXLEVEL_NONE, false, null));
		//initializeSkill(new SkillInfo(SKILL_BERSERKER, SkillInfo.MAXLEVEL_NONE, false, null));
	}

	public SkillInfo getSkill(int skillID) {
		return skills.get(skillID);
	}
	
	public Collection<SkillInfo> getAllSkills() {
		return skills.values();
	}
}
