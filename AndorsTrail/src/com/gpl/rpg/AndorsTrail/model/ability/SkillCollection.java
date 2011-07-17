package com.gpl.rpg.AndorsTrail.model.ability;

import java.util.Collection;
import java.util.HashMap;

import android.content.res.Resources;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.controller.Constants;

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
	public static final int PER_SKILLPOINT_INCREASE_BETTER_CRITICALS_PERCENT = 50;
	public static final int PER_SKILLPOINT_INCREASE_SPEED = 1;
	public static final int PER_SKILLPOINT_INCREASE_BARTER_PRICEFACTOR_PERCENTAGE = 5;
	public static final int PER_SKILLPOINT_INCREASE_COINFINDER_CHANCE_PERCENT = 30;
	public static final int PER_SKILLPOINT_INCREASE_MAGICFINDER_CHANCE_PERCENT = 100;
	public static final int PER_SKILLPOINT_INCREASE_COINFINDER_QUANTITY_PERCENT = 50;
	public static final int PER_SKILLPOINT_INCREASE_MORE_EXP_PERCENT = 10;
	public static final int PER_SKILLPOINT_INCREASE_CLEAVE_AP = 10;
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
		initializeSkill(new SkillInfo(SKILL_WEAPON_CHANCE, SkillInfo.MAXLEVEL_NONE, false));
		initializeSkill(new SkillInfo(SKILL_WEAPON_DMG, SkillInfo.MAXLEVEL_NONE, false));
		initializeSkill(new SkillInfo(SKILL_BARTER, Constants.MARKET_PRICEFACTOR_PERCENT / PER_SKILLPOINT_INCREASE_BARTER_PRICEFACTOR_PERCENTAGE, false));
		initializeSkill(new SkillInfo(SKILL_DODGE, SkillInfo.MAXLEVEL_NONE, false));
		initializeSkill(new SkillInfo(SKILL_BARKSKIN, SkillInfo.MAXLEVEL_NONE, false));
		initializeSkill(new SkillInfo(SKILL_MORE_CRITICALS, SkillInfo.MAXLEVEL_NONE, false));
		initializeSkill(new SkillInfo(SKILL_BETTER_CRITICALS, SkillInfo.MAXLEVEL_NONE, false));
		initializeSkill(new SkillInfo(SKILL_SPEED, SkillInfo.MAXLEVEL_NONE, false));
		initializeSkill(new SkillInfo(SKILL_COINFINDER, SkillInfo.MAXLEVEL_NONE, false));
		initializeSkill(new SkillInfo(SKILL_MORE_EXP, SkillInfo.MAXLEVEL_NONE, false));
		initializeSkill(new SkillInfo(SKILL_CLEAVE, SkillInfo.MAXLEVEL_NONE, false));
		initializeSkill(new SkillInfo(SKILL_EATER, SkillInfo.MAXLEVEL_NONE, false));
		initializeSkill(new SkillInfo(SKILL_FORTITUDE, SkillInfo.MAXLEVEL_NONE, false));
		initializeSkill(new SkillInfo(SKILL_EVASION, Constants.FLEE_FAIL_CHANCE_PERCENT / PER_SKILLPOINT_INCREASE_EVASION_FLEE_CHANCE_PERCENTAGE, false));
		initializeSkill(new SkillInfo(SKILL_REGENERATION, SkillInfo.MAXLEVEL_NONE, false));
		initializeSkill(new SkillInfo(SKILL_LOWER_EXPLOSS, 100 / PER_SKILLPOINT_INCREASE_EXPLOSS_PERCENT, false));
		initializeSkill(new SkillInfo(SKILL_MAGICFINDER, SkillInfo.MAXLEVEL_NONE, false));
		//initializeSkill(new SkillInfo(SKILL_BERSERKER, SkillInfo.MAXLEVEL_NONE);
	}
	public SkillInfo getSkill(int skillID) {
		return skills.get(skillID);
	}
	
	public Collection<SkillInfo> getAllSkills() {
		return skills.values();
	}
	
	public static String getSkillTitle(final int skill, final Resources res) {
		return res.getString(getSkillTitleResourceID(skill));
	}
	
	private static int getSkillTitleResourceID(int skill) {
		switch (skill) {
		case SkillCollection.SKILL_WEAPON_CHANCE: return R.string.skill_title_weapon_chance;
		case SkillCollection.SKILL_WEAPON_DMG: return R.string.skill_title_weapon_dmg;
		case SkillCollection.SKILL_BARTER: return R.string.skill_title_barter;
		case SkillCollection.SKILL_DODGE: return R.string.skill_title_dodge;
		case SkillCollection.SKILL_BARKSKIN: return R.string.skill_title_barkskin;
		case SkillCollection.SKILL_MORE_CRITICALS: return R.string.skill_title_more_criticals;
		case SkillCollection.SKILL_BETTER_CRITICALS: return R.string.skill_title_better_criticals;
		case SkillCollection.SKILL_SPEED: return R.string.skill_title_speed;
		case SkillCollection.SKILL_COINFINDER: return R.string.skill_title_coinfinder;
		case SkillCollection.SKILL_MORE_EXP: return R.string.skill_title_more_exp;
		case SkillCollection.SKILL_CLEAVE: return R.string.skill_title_cleave;
		case SkillCollection.SKILL_EATER: return R.string.skill_title_eater;
		case SkillCollection.SKILL_FORTITUDE: return R.string.skill_title_fortitude;
		case SkillCollection.SKILL_EVASION: return R.string.skill_title_evasion;
		case SkillCollection.SKILL_REGENERATION: return R.string.skill_title_regeneration;
		case SkillCollection.SKILL_LOWER_EXPLOSS: return R.string.skill_title_lower_exploss;
		case SkillCollection.SKILL_MAGICFINDER: return R.string.skill_title_magicfinder;
		default:
			return -1;
		}
	}
	
	public static String getSkillShortDescription(final int skill, final Resources res) {
		return res.getString(getSkillShortDescriptionResourceID(skill));
	}
	
	private static int getSkillShortDescriptionResourceID(int skill) {
		switch (skill) {
		case SkillCollection.SKILL_WEAPON_CHANCE: return R.string.skill_shortdescription_weapon_chance;
		case SkillCollection.SKILL_WEAPON_DMG: return R.string.skill_shortdescription_weapon_dmg;
		case SkillCollection.SKILL_BARTER: return R.string.skill_shortdescription_barter;
		case SkillCollection.SKILL_DODGE: return R.string.skill_shortdescription_dodge;
		case SkillCollection.SKILL_BARKSKIN: return R.string.skill_shortdescription_barkskin;
		case SkillCollection.SKILL_MORE_CRITICALS: return R.string.skill_shortdescription_more_criticals;
		case SkillCollection.SKILL_BETTER_CRITICALS: return R.string.skill_shortdescription_better_criticals;
		case SkillCollection.SKILL_SPEED: return R.string.skill_shortdescription_speed;
		case SkillCollection.SKILL_COINFINDER: return R.string.skill_shortdescription_coinfinder;
		case SkillCollection.SKILL_MORE_EXP: return R.string.skill_shortdescription_more_exp;
		case SkillCollection.SKILL_CLEAVE: return R.string.skill_shortdescription_cleave;
		case SkillCollection.SKILL_EATER: return R.string.skill_shortdescription_eater;
		case SkillCollection.SKILL_FORTITUDE: return R.string.skill_shortdescription_fortitude;
		case SkillCollection.SKILL_EVASION: return R.string.skill_shortdescription_evasion;
		case SkillCollection.SKILL_REGENERATION: return R.string.skill_shortdescription_regeneration;
		case SkillCollection.SKILL_LOWER_EXPLOSS: return R.string.skill_shortdescription_lower_exploss;
		case SkillCollection.SKILL_MAGICFINDER: return R.string.skill_shortdescription_magicfinder;
		default:
			return -1;
		}
	}
	
	public static String getSkillLongDescription(final int skill, final Resources res) {
		switch (skill) {
		case SkillCollection.SKILL_WEAPON_CHANCE: return res.getString(R.string.skill_longdescription_weapon_chance, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_CHANCE);
		case SkillCollection.SKILL_WEAPON_DMG: return res.getString(R.string.skill_longdescription_weapon_dmg, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_DAMAGE_MIN);
		case SkillCollection.SKILL_BARTER: return res.getString(R.string.skill_longdescription_barter, SkillCollection.PER_SKILLPOINT_INCREASE_BARTER_PRICEFACTOR_PERCENTAGE);
		case SkillCollection.SKILL_DODGE: return res.getString(R.string.skill_longdescription_dodge, SkillCollection.PER_SKILLPOINT_INCREASE_DODGE);
		case SkillCollection.SKILL_BARKSKIN: return res.getString(R.string.skill_longdescription_barkskin, SkillCollection.PER_SKILLPOINT_INCREASE_BARKSKIN);
		case SkillCollection.SKILL_MORE_CRITICALS: return res.getString(R.string.skill_longdescription_more_criticals, SkillCollection.PER_SKILLPOINT_INCREASE_MORE_CRITICALS_PERCENT);
		case SkillCollection.SKILL_BETTER_CRITICALS: return res.getString(R.string.skill_longdescription_better_criticals, SkillCollection.PER_SKILLPOINT_INCREASE_BETTER_CRITICALS_PERCENT);
		case SkillCollection.SKILL_SPEED: return res.getString(R.string.skill_longdescription_speed, SkillCollection.PER_SKILLPOINT_INCREASE_SPEED);
		case SkillCollection.SKILL_COINFINDER: return res.getString(R.string.skill_longdescription_coinfinder, SkillCollection.PER_SKILLPOINT_INCREASE_COINFINDER_CHANCE_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_COINFINDER_QUANTITY_PERCENT);
		case SkillCollection.SKILL_MORE_EXP: return res.getString(R.string.skill_longdescription_more_exp, SkillCollection.PER_SKILLPOINT_INCREASE_MORE_EXP_PERCENT);
		case SkillCollection.SKILL_CLEAVE: return res.getString(R.string.skill_longdescription_cleave, SkillCollection.PER_SKILLPOINT_INCREASE_CLEAVE_AP);
		case SkillCollection.SKILL_EATER: return res.getString(R.string.skill_longdescription_eater, SkillCollection.PER_SKILLPOINT_INCREASE_EATER_HEALTH);
		case SkillCollection.SKILL_FORTITUDE: return res.getString(R.string.skill_longdescription_fortitude, SkillCollection.PER_SKILLPOINT_INCREASE_FORTITUDE_HEALTH);
		case SkillCollection.SKILL_EVASION: return res.getString(R.string.skill_longdescription_evasion, SkillCollection.PER_SKILLPOINT_INCREASE_EVASION_FLEE_CHANCE_PERCENTAGE, SkillCollection.PER_SKILLPOINT_INCREASE_EVASION_MONSTER_ATTACK_CHANCE_PERCENTAGE);
		case SkillCollection.SKILL_REGENERATION: return res.getString(R.string.skill_longdescription_regeneration, SkillCollection.PER_SKILLPOINT_INCREASE_REGENERATION);
		case SkillCollection.SKILL_LOWER_EXPLOSS: return res.getString(R.string.skill_longdescription_lower_exploss, SkillCollection.PER_SKILLPOINT_INCREASE_EXPLOSS_PERCENT);
		case SkillCollection.SKILL_MAGICFINDER: return res.getString(R.string.skill_longdescription_magicfinder, SkillCollection.PER_SKILLPOINT_INCREASE_MAGICFINDER_CHANCE_PERCENT);
		default:
			return "";
		}
	}
}
