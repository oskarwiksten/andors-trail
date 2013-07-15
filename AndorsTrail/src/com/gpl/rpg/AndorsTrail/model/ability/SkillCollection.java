package com.gpl.rpg.AndorsTrail.model.ability;

import java.util.ArrayList;
import java.util.Collection;

import android.util.SparseArray;

import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.ability.SkillInfo.SkillLevelRequirement;
import com.gpl.rpg.AndorsTrail.model.actor.Player;

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
	public static final int SKILL_CRIT1 = 21;			// lowers atk ability
	public static final int SKILL_CRIT2 = 22;			// lowers def ability
	public static final int SKILL_REJUVENATION = 23;	// Reduces magnitudes of conditions
	public static final int SKILL_TAUNT = 24;			// Causes AP loss of attackers that miss
	public static final int SKILL_CONCUSSION = 25;		// AC loss for monsters with (AC-BC)>N
	public static final int SKILL_WEAPON_PROFICIENCY_DAGGER = 26;
	public static final int SKILL_WEAPON_PROFICIENCY_1HSWORD = 27;
	public static final int SKILL_WEAPON_PROFICIENCY_2HSWORD = 28;
	public static final int SKILL_WEAPON_PROFICIENCY_AXE = 29;
	public static final int SKILL_WEAPON_PROFICIENCY_BLUNT = 30;
	public static final int SKILL_WEAPON_PROFICIENCY_UNARMED = 31;
	public static final int SKILL_ARMOR_PROFICIENCY_SHIELD = 32;
	public static final int SKILL_ARMOR_PROFICIENCY_UNARMORED = 33;
	public static final int SKILL_ARMOR_PROFICIENCY_LIGHT = 34;
	public static final int SKILL_ARMOR_PROFICIENCY_HEAVY = 35;
	public static final int SKILL_FIGHTSTYLE_DUAL_WIELD = 36;
	public static final int SKILL_FIGHTSTYLE_2HAND = 37;
	public static final int SKILL_FIGHTSTYLE_WEAPON_SHIELD = 38;
	public static final int SKILL_SPECIALIZATION_DUAL_WIELD = 39;
	public static final int SKILL_SPECIALIZATION_2HAND = 40;
	public static final int SKILL_SPECIALIZATION_WEAPON_SHIELD = 41;
	
	public static final int NUM_SKILLS = SKILL_SPECIALIZATION_WEAPON_SHIELD + 1;
	
	public static final int PER_SKILLPOINT_INCREASE_WEAPON_CHANCE = 12;
	public static final int PER_SKILLPOINT_INCREASE_WEAPON_DAMAGE_MAX = 2;
	public static final int PER_SKILLPOINT_INCREASE_WEAPON_DAMAGE_MIN = 0;
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
	public static final int PER_SKILLPOINT_INCREASE_CRIT1_CHANCE = 50;
	public static final int PER_SKILLPOINT_INCREASE_CRIT2_CHANCE = 50;
	public static final int PER_SKILLPOINT_INCREASE_REJUVENATION_CHANCE = 20;
	public static final int PER_SKILLPOINT_INCREASE_TAUNT_CHANCE = 75;
	public static final int TAUNT_AP_LOSS = 2;
	public static final int CONCUSSION_THRESHOLD = 50;
	public static final int PER_SKILLPOINT_INCREASE_CONCUSSION_CHANCE = 15;
	public static final int PER_SKILLPOINT_INCREASE_WEAPON_PROF_AC_PERCENT = 30;
	public static final int PER_SKILLPOINT_INCREASE_WEAPON_PROF_CS_PERCENT = 10;
	public static final int PER_SKILLPOINT_INCREASE_WEAPON_PROF_BC_PERCENT = 30;
	public static final int PER_SKILLPOINT_INCREASE_UNARMED_AC = 20;
	public static final int PER_SKILLPOINT_INCREASE_UNARMED_DMG = 2;
	public static final int PER_SKILLPOINT_INCREASE_UNARMED_BC = 5;
	public static final int PER_SKILLPOINT_INCREASE_SHIELD_PROF_DR = 1;
	public static final int PER_SKILLPOINT_INCREASE_UNARMORED_BC = 10;
	public static final int PER_SKILLPOINT_INCREASE_LIGHT_ARMOR_BC_PERCENT = 30;
	public static final int PER_SKILLPOINT_INCREASE_HEAVY_ARMOR_BC_PERCENT = 20;
	public static final int PER_SKILLPOINT_INCREASE_HEAVY_ARMOR_MOVECOST_PERCENT = 25;
	public static final int PER_SKILLPOINT_INCREASE_HEAVY_ARMOR_ATKCOST_PERCENT = 25;
	public static final int PER_SKILLPOINT_INCREASE_FIGHTSTYLE_2HAND_DMG_PERCENT = 30;
	public static final int PER_SKILLPOINT_INCREASE_SPECIALIZATION_2HAND_DMG_PERCENT = 50;
	public static final int PER_SKILLPOINT_INCREASE_SPECIALIZATION_2HAND_AC_PERCENT = 20;
	public static final int PER_SKILLPOINT_INCREASE_FIGHTSTYLE_WEAPON_AC_PERCENT = 25;
	public static final int PER_SKILLPOINT_INCREASE_FIGHTSTYLE_SHIELD_BC_PERCENT = 25;
	public static final int PER_SKILLPOINT_INCREASE_SPECIALIZATION_WEAPON_AC_PERCENT = 50;
	public static final int PER_SKILLPOINT_INCREASE_SPECIALIZATION_WEAPON_DMG_PERCENT = 20;
	public static final int DUALWIELD_EFFICIENCY_LEVEL2 = 100;
	public static final int DUALWIELD_EFFICIENCY_LEVEL1 = 50;
	public static final int DUALWIELD_EFFICIENCY_LEVEL0 = 25;
	public static final int DUALWIELD_LEVEL1_OFFHAND_AP_COST_PERCENT = 50;
	public static final int PER_SKILLPOINT_INCREASE_SPECIALIZATION_DUALWIELD_AC_PERCENT = 50;
	public static final int PER_SKILLPOINT_INCREASE_SPECIALIZATION_DUALWIELD_BC_PERCENT = 50;

	private static final int MAX_LEVEL_BARTER = (int) Math.floor((float) Constants.MARKET_PRICEFACTOR_PERCENT / PER_SKILLPOINT_INCREASE_BARTER_PRICEFACTOR_PERCENTAGE);
	private static final int MAX_LEVEL_BARKSKIN = 5;
	private static final int MAX_LEVEL_SPEED = 2;
	private static final int MAX_LEVEL_EVASION = Math.max(
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
		initializeSkill(new SkillInfo(SKILL_WEAPON_CHANCE, SkillInfo.MAXLEVEL_NONE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, null));
		initializeSkill(new SkillInfo(SKILL_WEAPON_DMG, SkillInfo.MAXLEVEL_NONE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, null));
		initializeSkill(new SkillInfo(SKILL_BARTER, MAX_LEVEL_BARTER, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, null));
		initializeSkill(new SkillInfo(SKILL_DODGE, SkillInfo.MAXLEVEL_NONE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, null));
		initializeSkill(new SkillInfo(SKILL_BARKSKIN, MAX_LEVEL_BARKSKIN, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireExperienceLevels(10, 0) 
			,SkillLevelRequirement.requireCombatStats(Player.STAT_COMBAT_BLOCK_CHANCE, 15, 0) 
		}));
		initializeSkill(new SkillInfo(SKILL_MORE_CRITICALS, SkillInfo.MAXLEVEL_NONE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, null));
		initializeSkill(new SkillInfo(SKILL_BETTER_CRITICALS, SkillInfo.MAXLEVEL_NONE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireOtherSkill(SKILL_MORE_CRITICALS, 1)
		}));
		initializeSkill(new SkillInfo(SKILL_SPEED, MAX_LEVEL_SPEED, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireExperienceLevels(15, 0) 
		}));
		initializeSkill(new SkillInfo(SKILL_COINFINDER, SkillInfo.MAXLEVEL_NONE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, null));
		initializeSkill(new SkillInfo(SKILL_MORE_EXP, SkillInfo.MAXLEVEL_NONE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, null));
		initializeSkill(new SkillInfo(SKILL_CLEAVE, SkillInfo.MAXLEVEL_NONE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireOtherSkill(SKILL_WEAPON_CHANCE, 1) 
			,SkillLevelRequirement.requireOtherSkill(SKILL_WEAPON_DMG, 1)
		}));
		initializeSkill(new SkillInfo(SKILL_EATER, SkillInfo.MAXLEVEL_NONE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireActorStats(Player.STAT_ACTOR_MAX_HP, 20, 20)
		}));
		initializeSkill(new SkillInfo(SKILL_FORTITUDE, SkillInfo.MAXLEVEL_NONE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireExperienceLevels(15, -10)
		}));
		initializeSkill(new SkillInfo(SKILL_EVASION, MAX_LEVEL_EVASION, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, null));
		initializeSkill(new SkillInfo(SKILL_REGENERATION, SkillInfo.MAXLEVEL_NONE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireActorStats(Player.STAT_ACTOR_MAX_HP, 30, 0)
			,SkillLevelRequirement.requireOtherSkill(SKILL_FORTITUDE, 1)
		}));
		initializeSkill(new SkillInfo(SKILL_LOWER_EXPLOSS, MAX_LEVEL_LOWER_EXPLOSS, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, null));
		initializeSkill(new SkillInfo(SKILL_MAGICFINDER, SkillInfo.MAXLEVEL_NONE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, null));
		initializeSkill(new SkillInfo(SKILL_RESISTANCE_MENTAL, MAX_LEVEL_RESISTANCE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, null));
		initializeSkill(new SkillInfo(SKILL_RESISTANCE_PHYSICAL_CAPACITY, MAX_LEVEL_RESISTANCE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, null));
		initializeSkill(new SkillInfo(SKILL_RESISTANCE_BLOOD_DISORDER, MAX_LEVEL_RESISTANCE, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, null));
		initializeSkill(new SkillInfo(SKILL_SHADOW_BLESS, 1, SkillInfo.LEVELUP_TYPE_ONLY_BY_QUESTS, null));
		initializeSkill(new SkillInfo(SKILL_CRIT1, 1, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireOtherSkill(SKILL_MORE_CRITICALS, 3)
			,SkillLevelRequirement.requireOtherSkill(SKILL_BETTER_CRITICALS, 3)
		}));
		initializeSkill(new SkillInfo(SKILL_CRIT2, 1, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireOtherSkill(SKILL_MORE_CRITICALS, 6)
			,SkillLevelRequirement.requireOtherSkill(SKILL_BETTER_CRITICALS, 6)
			,SkillLevelRequirement.requireOtherSkill(SKILL_CRIT1, 1)
		}));
		initializeSkill(new SkillInfo(SKILL_REJUVENATION, 1, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireOtherSkill(SKILL_RESISTANCE_BLOOD_DISORDER, 3)
			,SkillLevelRequirement.requireOtherSkill(SKILL_RESISTANCE_MENTAL, 3)
			,SkillLevelRequirement.requireOtherSkill(SKILL_RESISTANCE_PHYSICAL_CAPACITY, 3)
		}));
		initializeSkill(new SkillInfo(SKILL_TAUNT, 1, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireOtherSkill(SKILL_EVASION, 2)
			,SkillLevelRequirement.requireOtherSkill(SKILL_DODGE, 4)
		}));
		initializeSkill(new SkillInfo(SKILL_CONCUSSION, 1, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] { 
			SkillLevelRequirement.requireOtherSkill(SKILL_SPEED, 2)
			,SkillLevelRequirement.requireOtherSkill(SKILL_WEAPON_CHANCE, 3)
			,SkillLevelRequirement.requireOtherSkill(SKILL_WEAPON_DMG, 5)
		}));
		initializeSkill(new SkillInfo(SKILL_WEAPON_PROFICIENCY_DAGGER, 3, SkillInfo.LEVELUP_TYPE_FIRST_LEVEL_REQUIRES_QUEST, null));
		initializeSkill(new SkillInfo(SKILL_WEAPON_PROFICIENCY_1HSWORD, 3, SkillInfo.LEVELUP_TYPE_FIRST_LEVEL_REQUIRES_QUEST, null));
		initializeSkill(new SkillInfo(SKILL_WEAPON_PROFICIENCY_2HSWORD, 3, SkillInfo.LEVELUP_TYPE_FIRST_LEVEL_REQUIRES_QUEST, null));
		initializeSkill(new SkillInfo(SKILL_WEAPON_PROFICIENCY_AXE, 3, SkillInfo.LEVELUP_TYPE_FIRST_LEVEL_REQUIRES_QUEST, null));
		initializeSkill(new SkillInfo(SKILL_WEAPON_PROFICIENCY_BLUNT, 3, SkillInfo.LEVELUP_TYPE_FIRST_LEVEL_REQUIRES_QUEST, null));
		initializeSkill(new SkillInfo(SKILL_WEAPON_PROFICIENCY_UNARMED, 3, SkillInfo.LEVELUP_TYPE_FIRST_LEVEL_REQUIRES_QUEST, null));
		initializeSkill(new SkillInfo(SKILL_ARMOR_PROFICIENCY_SHIELD, 2, SkillInfo.LEVELUP_TYPE_FIRST_LEVEL_REQUIRES_QUEST, null));
		initializeSkill(new SkillInfo(SKILL_ARMOR_PROFICIENCY_UNARMORED, 3, SkillInfo.LEVELUP_TYPE_FIRST_LEVEL_REQUIRES_QUEST, null));
		initializeSkill(new SkillInfo(SKILL_ARMOR_PROFICIENCY_LIGHT, 3, SkillInfo.LEVELUP_TYPE_FIRST_LEVEL_REQUIRES_QUEST, null));
		initializeSkill(new SkillInfo(SKILL_ARMOR_PROFICIENCY_HEAVY, 4, SkillInfo.LEVELUP_TYPE_FIRST_LEVEL_REQUIRES_QUEST, null));
		initializeSkill(new SkillInfo(SKILL_FIGHTSTYLE_DUAL_WIELD, 2, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] {
				SkillLevelRequirement.requireExperienceLevels(15, 0) 
			}));
		initializeSkill(new SkillInfo(SKILL_FIGHTSTYLE_2HAND, 2, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] {
				SkillLevelRequirement.requireExperienceLevels(15, 0) 
			}));
		initializeSkill(new SkillInfo(SKILL_FIGHTSTYLE_WEAPON_SHIELD, 2, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] {
				SkillLevelRequirement.requireExperienceLevels(15, 0) 
			}));
		initializeSkill(new SkillInfo(SKILL_SPECIALIZATION_DUAL_WIELD, 1, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] {
				SkillLevelRequirement.requireExperienceLevels(45, 0)
				,SkillLevelRequirement.requireOtherSkill(SKILL_FIGHTSTYLE_DUAL_WIELD, 2)
			}));
		initializeSkill(new SkillInfo(SKILL_SPECIALIZATION_2HAND, 1, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] {
				SkillLevelRequirement.requireExperienceLevels(45, 0)
				,SkillLevelRequirement.requireOtherSkill(SKILL_FIGHTSTYLE_2HAND, 2)
			}));
		initializeSkill(new SkillInfo(SKILL_SPECIALIZATION_WEAPON_SHIELD, 1, SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN, new SkillLevelRequirement[] {
				SkillLevelRequirement.requireExperienceLevels(45, 0)
				,SkillLevelRequirement.requireOtherSkill(SKILL_FIGHTSTYLE_WEAPON_SHIELD, 2)
			}));
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
