package com.gpl.rpg.AndorsTrail.model.ability;

import java.util.ArrayList;
import java.util.Collection;

import android.util.SparseArray;

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
	public static final int SKILL_RESISTANCE_MENTAL = 17;            // lowers chance to get negative active conditions by monsters (Mental like Dazed)
	public static final int SKILL_RESISTANCE_PHYSICAL_CAPACITY = 18; // lowers chance to get negative active conditions by monsters (Physical Capacity like Minor fatigue)
	public static final int SKILL_RESISTANCE_BLOOD_DISORDER = 19;    // lowers chance to get negative active conditions by monsters (Blood Disorder like Weak Poison)
	public static final int SKILL_SHADOW_BLESS = 20;
	
	public static final int NUM_SKILLS = SKILL_SHADOW_BLESS + 1;
	
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
	public static final int PER_SKILLPOINT_INCREASE_FORTITUDE_HEALTH = 1;
	public static final int PER_SKILLPOINT_INCREASE_EVASION_FLEE_CHANCE_PERCENTAGE = 5;
	public static final int PER_SKILLPOINT_INCREASE_EVASION_MONSTER_ATTACK_CHANCE_PERCENTAGE = 5;
	public static final int PER_SKILLPOINT_INCREASE_REGENERATION = 1;
	public static final int PER_SKILLPOINT_INCREASE_EXPLOSS_PERCENT = 20;
	public static final int PER_SKILLPOINT_INCREASE_RESISTANCE_CHANCE_PERCENT = 10;
	public static final int PER_SKILLPOINT_INCREASE_RESISTANCE_SHADOW_BLESS = 5;

	public static final int MAX_LEVEL_BARTER = (int) Math.floor((float) Constants.MARKET_PRICEFACTOR_PERCENT / PER_SKILLPOINT_INCREASE_BARTER_PRICEFACTOR_PERCENTAGE);
	public static final int MAX_LEVEL_BARKSKIN = 5;
	public static final int MAX_LEVEL_SPEED = 2;
	public static final int MAX_LEVEL_EVASION = Math.max(
			Constants.FLEE_FAIL_CHANCE_PERCENT / PER_SKILLPOINT_INCREASE_EVASION_FLEE_CHANCE_PERCENTAGE
			,Constants.MONSTER_AGGRESSION_CHANCE_PERCENT / PER_SKILLPOINT_INCREASE_EVASION_MONSTER_ATTACK_CHANCE_PERCENTAGE
			);
	public static final int MAX_LEVEL_LOWER_EXPLOSS = 100 / PER_SKILLPOINT_INCREASE_EXPLOSS_PERCENT;
	public static final int MAX_LEVEL_RESISTANCE = 70 / PER_SKILLPOINT_INCREASE_RESISTANCE_CHANCE_PERCENT;
	
	private final SparseArray<SkillInfo> skills = new SparseArray<SkillInfo>();
	private void initializeSkill(SkillInfo skill) {
		skills.put(skill.id, skill);
	}
	public void initialize() {
		initializeSkill(new SkillInfo(SKILL_WEAPON_CHANCE, SkillInfo.MAXLEVEL_NONE, false, null));
		initializeSkill(new SkillInfo(SKILL_WEAPON_DMG, SkillInfo.MAXLEVEL_NONE, false, null));
		initializeSkill(new SkillInfo(SKILL_BARTER, MAX_LEVEL_BARTER, false, null));
		initializeSkill(new SkillInfo(SKILL_DODGE, SkillInfo.MAXLEVEL_NONE, false, null));
		initializeSkill(new SkillInfo(SKILL_BARKSKIN, MAX_LEVEL_BARKSKIN, false, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireExperienceLevels(10, 0) 
			,SkillLevelRequirement.requireCombatStats(CombatTraits.STAT_COMBAT_BLOCK_CHANCE, 15, 0) 
		}));
		initializeSkill(new SkillInfo(SKILL_MORE_CRITICALS, SkillInfo.MAXLEVEL_NONE, false, null));
		initializeSkill(new SkillInfo(SKILL_BETTER_CRITICALS, SkillInfo.MAXLEVEL_NONE, false, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireOtherSkill(SKILL_MORE_CRITICALS, 1)
		}));
		initializeSkill(new SkillInfo(SKILL_SPEED, MAX_LEVEL_SPEED, false, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireExperienceLevels(15, 0) 
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
			SkillLevelRequirement.requireExperienceLevels(15, -10)
		}));
		initializeSkill(new SkillInfo(SKILL_EVASION, MAX_LEVEL_EVASION, false, null));
		initializeSkill(new SkillInfo(SKILL_REGENERATION, SkillInfo.MAXLEVEL_NONE, false, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireActorStats(ActorTraits.STAT_ACTOR_MAX_HP, 30, 0)
			,SkillLevelRequirement.requireOtherSkill(SKILL_FORTITUDE, 1)
		}));
		initializeSkill(new SkillInfo(SKILL_LOWER_EXPLOSS, MAX_LEVEL_LOWER_EXPLOSS, false, null));
		initializeSkill(new SkillInfo(SKILL_MAGICFINDER, SkillInfo.MAXLEVEL_NONE, false, null));
		initializeSkill(new SkillInfo(SKILL_RESISTANCE_MENTAL, MAX_LEVEL_RESISTANCE, false, null));
		initializeSkill(new SkillInfo(SKILL_RESISTANCE_PHYSICAL_CAPACITY, MAX_LEVEL_RESISTANCE, false, null));
		initializeSkill(new SkillInfo(SKILL_RESISTANCE_BLOOD_DISORDER, MAX_LEVEL_RESISTANCE, false, null));
		initializeSkill(new SkillInfo(SKILL_SHADOW_BLESS, 1, true, null));
	}

	public SkillInfo getSkill(int skillID) {
		return skills.get(skillID);
	}
	
	public Collection<SkillInfo> getAllSkills() {
		ArrayList<SkillInfo> result = new ArrayList<SkillInfo>(skills.size());
		for(int i = 0; i < skills.size(); ++i) result.add(skills.valueAt(i));
		return result;
	}
}
