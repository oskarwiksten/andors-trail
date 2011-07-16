package com.gpl.rpg.AndorsTrail.model.actor;

public final class Skills {
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
	//public static final int SKILL_BERSERKER = 12; 		// <=20%hp increases AC and DMG
	public static final int SKILL_FORTITUDE = 13; 		// +N hp per levelup
	public static final int SKILL_EVASION = 14; 		// increase successful flee chance & reduce chance of monster attack
	public static final int SKILL_REGENERATION = 15; 	// +N hp per round
	public static final int SKILL_LOWER_EXPLOSS = 16;
	public static final int SKILL_MAGICFINDER = 17;
	
	public static final int NUM_SKILLS = SKILL_MAGICFINDER+1;
	
	//public static final int BERSERKER_STARTS_AT_HEALTH_PERCENT = 20;
	
	public static final int PER_SKILLPOINT_INCREASE_WEAPON_CHANCE = 15;
	public static final int PER_SKILLPOINT_INCREASE_WEAPON_DAMAGE_MAX = 1;
	public static final int PER_SKILLPOINT_INCREASE_WEAPON_DAMAGE_MIN = 1;
	public static final int PER_SKILLPOINT_INCREASE_DODGE = 9;
	public static final int PER_SKILLPOINT_INCREASE_BARKSKIN = 1;
	public static final int PER_SKILLPOINT_INCREASE_MORE_CRITICALS_PERCENT = 20;
	public static final int PER_SKILLPOINT_INCREASE_BETTER_CRITICALS_PERCENT = 50;
	public static final int PER_SKILLPOINT_INCREASE_SPEED = 1;
	/*public static final int PER_SKILLPOINT_INCREASE_BERSERKER_WEAPON_CHANCE = 15;
	public static final int PER_SKILLPOINT_INCREASE_BERSERKER_WEAPON_DAMAGE_MAX = 1;
	public static final int PER_SKILLPOINT_INCREASE_BERSERKER_WEAPON_DAMAGE_MIN = 1;
	public static final int PER_SKILLPOINT_INCREASE_BERSERKER_DODGE = 9;*/
	public static final int PER_SKILLPOINT_INCREASE_BARTER_PRICEFACTOR_PERCENTAGE = 5;
	public static final int PER_SKILLPOINT_INCREASE_COINFINDER_CHANCE_PERCENT = 50;
	public static final int PER_SKILLPOINT_INCREASE_MAGICFINDER_CHANCE_PERCENT = 100;
	public static final int PER_SKILLPOINT_INCREASE_COINFINDER_QUANTITY_PERCENT = 100;
	public static final int PER_SKILLPOINT_INCREASE_MORE_EXP_PERCENT = 10;
	public static final int PER_SKILLPOINT_INCREASE_EATER_HEALTH = 1;
	public static final int PER_SKILLPOINT_INCREASE_FORTITUDE_HEALTH = 2;
	public static final int PER_SKILLPOINT_INCREASE_EVASION_FLEE_CHANCE_PERCENTAGE = 5;
	public static final int PER_SKILLPOINT_INCREASE_EVASION_MONSTER_ATTACK_CHANCE_PERCENTAGE = 5;
	public static final int PER_SKILLPOINT_INCREASE_REGENERATION = 1;
	public static final int PER_SKILLPOINT_INCREASE_EXPLOSS_PERCENT = 30;
}
