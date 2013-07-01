package com.gpl.rpg.AndorsTrail.activity;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.SkillController;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.ability.SkillInfo;
import com.gpl.rpg.AndorsTrail.model.ability.SkillInfo.SkillLevelRequirement;
import com.gpl.rpg.AndorsTrail.model.actor.Player;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public final class SkillInfoActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
		final WorldContext world = app.getWorld();
		final Player player = world.model.player;
        
        app.setWindowParameters(this);
        
        setContentView(R.layout.skill_info_view);

        final Resources res = getResources();
        final Intent intent = getIntent();
        final int skillID = intent.getExtras().getInt("skillID");
        SkillInfo skill = world.skills.getSkill(skillID);
        
        TextView skillinfo_title = (TextView) findViewById(R.id.skillinfo_title);
        skillinfo_title.setText(getSkillTitleResourceID(skillID));
        
        TextView skillinfo_longdescription = (TextView) findViewById(R.id.skillinfo_longdescription);
        skillinfo_longdescription.setText(getSkillLongDescription(skillID, res));
        
        TextView skillinfo_currentlevel = (TextView) findViewById(R.id.skillinfo_currentlevel);
        final int playerSkillLevel = player.getSkillLevel(skillID);
        final int nextSkillLevel = playerSkillLevel + 1;
        if (skill.hasMaxLevel()) {
        	skillinfo_currentlevel.setText(res.getString(R.string.skill_current_level_with_maximum, playerSkillLevel, skill.maxLevel));
        } else if (player.hasSkill(skillID)) {
            skillinfo_currentlevel.setText(res.getString(R.string.skill_current_level, playerSkillLevel));
        } else {
            skillinfo_currentlevel.setVisibility(View.GONE);
        }
        
        
        TextView skillinfo_requirement = (TextView) findViewById(R.id.skillinfo_requirement);
        LayoutParams requirementParams = skillinfo_requirement.getLayoutParams();
        ViewGroup requirementList = (ViewGroup) skillinfo_requirement.getParent();
        requirementList.removeView(skillinfo_requirement);
        if (shouldShowSkillRequirements(skill, playerSkillLevel)) {
        	for (SkillLevelRequirement requirement : skill.levelupRequirements) {
        		TextView tv = new TextView(this);
        		tv.setLayoutParams(requirementParams);
        		
        		int requiredValue = requirement.getRequiredValue(nextSkillLevel);
        		tv.setText(getRequirementDescription(requirement, requiredValue, res));
        		boolean satisfiesRequirement = requirement.isSatisfiedByPlayer(player, nextSkillLevel);
        		tv.setEnabled(!satisfiesRequirement);
        		
        		requirementList.addView(tv, requirementParams);
        	}	
        }
        

        Button b = (Button) findViewById(R.id.skillinfoinfo_close);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_CANCELED);
				SkillInfoActivity.this.finish();
			}
		});
        
        b = (Button) findViewById(R.id.skillinfoinfo_action);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent result = new Intent();
				result.putExtras(intent);
				setResult(RESULT_OK, result);
				SkillInfoActivity.this.finish();
			}
		});
        b.setEnabled(SkillController.canLevelupSkillManually(player, skill));
    }
	
	private static boolean shouldShowSkillRequirements(SkillInfo skill, int playerSkillLevel) {
		if (!skill.hasLevelupRequirements()) return false;
		if (!skill.hasMaxLevel()) return true;
		if (playerSkillLevel >= skill.maxLevel) return false;
		return true;
	}
	
	public static int getSkillTitleResourceID(int skill) {
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
		case SkillCollection.SKILL_RESISTANCE_MENTAL: return R.string.skill_title_resistance_mental;
		case SkillCollection.SKILL_RESISTANCE_PHYSICAL_CAPACITY: return R.string.skill_title_resistance_physical_capacity;
		case SkillCollection.SKILL_RESISTANCE_BLOOD_DISORDER: return R.string.skill_title_resistance_blood_disorder;
		case SkillCollection.SKILL_SHADOW_BLESS: return R.string.skill_title_shadow_bless;
		case SkillCollection.SKILL_CRIT1: return R.string.skill_title_crit1;
		case SkillCollection.SKILL_CRIT2: return R.string.skill_title_crit2;
		case SkillCollection.SKILL_REJUVENATION: return R.string.skill_title_rejuvenation;
		case SkillCollection.SKILL_TAUNT: return R.string.skill_title_taunt;
		case SkillCollection.SKILL_CONCUSSION: return R.string.skill_title_concussion;
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_DAGGER: return R.string.skill_title_weapon_prof_dagger;
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_1HSWORD: return R.string.skill_title_weapon_prof_1hsword;
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_2HSWORD: return R.string.skill_title_weapon_prof_2hsword;
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_AXE: return R.string.skill_title_weapon_prof_axe;
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_BLUNT: return R.string.skill_title_weapon_prof_blunt;
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_UNARMED: return R.string.skill_title_weapon_prof_unarmed;
		case SkillCollection.SKILL_ARMOR_PROFICIENCY_SHIELD: return R.string.skill_title_armor_prof_shield;
		case SkillCollection.SKILL_ARMOR_PROFICIENCY_UNARMORED: return R.string.skill_title_armor_prof_unarmored;
		case SkillCollection.SKILL_ARMOR_PROFICIENCY_LIGHT: return R.string.skill_title_armor_prof_light;
		case SkillCollection.SKILL_ARMOR_PROFICIENCY_HEAVY: return R.string.skill_title_armor_prof_heavy;
		case SkillCollection.SKILL_FIGHTSTYLE_DUAL_WIELD: return R.string.skill_title_fightstyle_dualwield;
		case SkillCollection.SKILL_FIGHTSTYLE_2HAND: return R.string.skill_title_fightstyle_2hand;
		case SkillCollection.SKILL_FIGHTSTYLE_WEAPON_SHIELD: return R.string.skill_title_fightstyle_weapon_shield;
		case SkillCollection.SKILL_SPECIALIZATION_DUAL_WIELD: return R.string.skill_title_specialization_dualwield;
		case SkillCollection.SKILL_SPECIALIZATION_2HAND: return R.string.skill_title_specialization_2hand;
		case SkillCollection.SKILL_SPECIALIZATION_WEAPON_SHIELD: return R.string.skill_title_specialization_weapon_shield;
		default:
			return -1;
		}
	}
	
	private static String getSkillLongDescription(final int skill, final Resources res) {
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
		case SkillCollection.SKILL_LOWER_EXPLOSS: return res.getString(R.string.skill_longdescription_lower_exploss, SkillCollection.PER_SKILLPOINT_INCREASE_EXPLOSS_PERCENT, SkillCollection.MAX_LEVEL_LOWER_EXPLOSS);
		case SkillCollection.SKILL_MAGICFINDER: return res.getString(R.string.skill_longdescription_magicfinder, SkillCollection.PER_SKILLPOINT_INCREASE_MAGICFINDER_CHANCE_PERCENT);
		case SkillCollection.SKILL_RESISTANCE_MENTAL: return res.getString(R.string.skill_longdescription_resistance_mental, SkillCollection.PER_SKILLPOINT_INCREASE_RESISTANCE_CHANCE_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_RESISTANCE_CHANCE_PERCENT * SkillCollection.MAX_LEVEL_RESISTANCE);
		case SkillCollection.SKILL_RESISTANCE_PHYSICAL_CAPACITY: return res.getString(R.string.skill_longdescription_resistance_physical_capacity, SkillCollection.PER_SKILLPOINT_INCREASE_RESISTANCE_CHANCE_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_RESISTANCE_CHANCE_PERCENT * SkillCollection.MAX_LEVEL_RESISTANCE);
		case SkillCollection.SKILL_RESISTANCE_BLOOD_DISORDER: return res.getString(R.string.skill_longdescription_resistance_blood_disorder, SkillCollection.PER_SKILLPOINT_INCREASE_RESISTANCE_CHANCE_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_RESISTANCE_CHANCE_PERCENT * SkillCollection.MAX_LEVEL_RESISTANCE);
		case SkillCollection.SKILL_SHADOW_BLESS: return res.getString(R.string.skill_longdescription_shadow_bless, SkillCollection.PER_SKILLPOINT_INCREASE_RESISTANCE_SHADOW_BLESS);
		case SkillCollection.SKILL_CRIT1: return res.getString(R.string.skill_longdescription_crit1, SkillCollection.PER_SKILLPOINT_INCREASE_CRIT1_CHANCE);
		case SkillCollection.SKILL_CRIT2: return res.getString(R.string.skill_longdescription_crit2, SkillCollection.PER_SKILLPOINT_INCREASE_CRIT2_CHANCE);
		case SkillCollection.SKILL_REJUVENATION: return res.getString(R.string.skill_longdescription_rejuvenation, SkillCollection.PER_SKILLPOINT_INCREASE_REJUVENATION_CHANCE);
		case SkillCollection.SKILL_TAUNT: return res.getString(R.string.skill_longdescription_taunt, SkillCollection.PER_SKILLPOINT_INCREASE_TAUNT_CHANCE, SkillCollection.TAUNT_AP_LOSS);
		case SkillCollection.SKILL_CONCUSSION: return res.getString(R.string.skill_longdescription_concussion, SkillCollection.CONCUSSION_THRESHOLD, SkillCollection.PER_SKILLPOINT_INCREASE_CONCUSSION_CHANCE);
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_DAGGER: return res.getString(R.string.skill_longdescription_weapon_prof_dagger, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_AC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_BC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_CS_PERCENT);
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_1HSWORD: return res.getString(R.string.skill_longdescription_weapon_prof_1hsword, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_AC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_BC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_CS_PERCENT);
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_2HSWORD: return res.getString(R.string.skill_longdescription_weapon_prof_2hsword, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_AC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_BC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_CS_PERCENT);
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_AXE: return res.getString(R.string.skill_longdescription_weapon_prof_axe, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_AC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_BC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_CS_PERCENT);
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_BLUNT: return res.getString(R.string.skill_longdescription_weapon_prof_blunt, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_AC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_BC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_WEAPON_PROF_CS_PERCENT);
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_UNARMED: return res.getString(R.string.skill_longdescription_weapon_prof_unarmed, SkillCollection.PER_SKILLPOINT_INCREASE_UNARMED_AC, SkillCollection.PER_SKILLPOINT_INCREASE_UNARMED_DMG, SkillCollection.PER_SKILLPOINT_INCREASE_UNARMED_BC);
		case SkillCollection.SKILL_ARMOR_PROFICIENCY_SHIELD: return res.getString(R.string.skill_longdescription_armor_prof_shield, SkillCollection.PER_SKILLPOINT_INCREASE_SHIELD_PROF_DR);
		case SkillCollection.SKILL_ARMOR_PROFICIENCY_UNARMORED: return res.getString(R.string.skill_longdescription_armor_prof_unarmored, SkillCollection.PER_SKILLPOINT_INCREASE_UNARMORED_BC);
		case SkillCollection.SKILL_ARMOR_PROFICIENCY_LIGHT: return res.getString(R.string.skill_longdescription_armor_prof_light, SkillCollection.PER_SKILLPOINT_INCREASE_LIGHT_ARMOR_BC_PERCENT);
		case SkillCollection.SKILL_ARMOR_PROFICIENCY_HEAVY: return res.getString(R.string.skill_longdescription_armor_prof_heavy, SkillCollection.PER_SKILLPOINT_INCREASE_HEAVY_ARMOR_BC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_HEAVY_ARMOR_MOVECOST_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_HEAVY_ARMOR_ATKCOST_PERCENT);
		case SkillCollection.SKILL_FIGHTSTYLE_DUAL_WIELD: return res.getString(R.string.skill_longdescription_fightstyle_dualwield, SkillCollection.DUALWIELD_EFFICIENCY_LEVEL0, SkillCollection.DUALWIELD_EFFICIENCY_LEVEL1, SkillCollection.DUALWIELD_LEVEL1_OFFHAND_AP_COST_PERCENT, SkillCollection.DUALWIELD_EFFICIENCY_LEVEL2);
		case SkillCollection.SKILL_FIGHTSTYLE_2HAND: return res.getString(R.string.skill_longdescription_fightstyle_2hand, SkillCollection.PER_SKILLPOINT_INCREASE_FIGHTSTYLE_2HAND_DMG_PERCENT);
		case SkillCollection.SKILL_FIGHTSTYLE_WEAPON_SHIELD: return res.getString(R.string.skill_longdescription_fightstyle_weapon_shield, SkillCollection.PER_SKILLPOINT_INCREASE_FIGHTSTYLE_WEAPON_AC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_FIGHTSTYLE_SHIELD_BC_PERCENT);
		case SkillCollection.SKILL_SPECIALIZATION_DUAL_WIELD: return res.getString(R.string.skill_longdescription_specialization_dualwield, SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_DUALWIELD_AC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_DUALWIELD_BC_PERCENT);
		case SkillCollection.SKILL_SPECIALIZATION_2HAND: return res.getString(R.string.skill_longdescription_specialization_2hand, SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_2HAND_DMG_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_2HAND_AC_PERCENT);
		case SkillCollection.SKILL_SPECIALIZATION_WEAPON_SHIELD: return res.getString(R.string.skill_longdescription_specialization_weapon_shield, SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_WEAPON_AC_PERCENT, SkillCollection.PER_SKILLPOINT_INCREASE_SPECIALIZATION_WEAPON_DMG_PERCENT);
		default:
			return "";
		}
	}
	
	private static String getRequirementDescription(SkillLevelRequirement requirement, int requiredValue, final Resources res) {
		switch (requirement.requirementType) {
		case SkillLevelRequirement.REQUIREMENT_TYPE_SKILL_LEVEL: 
			String skillName = res.getString(getSkillTitleResourceID(requirement.skillOrStatID));
			return res.getString(R.string.skill_prerequisite_other_skill, requiredValue, skillName);
		case SkillLevelRequirement.REQUIREMENT_TYPE_EXPERIENCE_LEVEL: 
			return res.getString(R.string.skill_prerequisite_level, requiredValue);
		case SkillLevelRequirement.REQUIREMENT_TYPE_COMBAT_STAT: 
			String combatStatName = res.getString(getRequirementCombatStatsResourceID(requirement.skillOrStatID)).replace(':', ' ').trim();
			return res.getString(R.string.skill_prerequisite_stat, requiredValue, combatStatName);
		case SkillLevelRequirement.REQUIREMENT_TYPE_ACTOR_STAT: 
			String actorStatName = res.getString(getRequirementActorStatsResourceID(requirement.skillOrStatID)).replace(':', ' ').trim();
			return res.getString(R.string.skill_prerequisite_stat, requiredValue, actorStatName);
		}
		return "";
	}
	
	private static int getRequirementActorStatsResourceID(int statID) {
		switch (statID) {
		case Player.STAT_ACTOR_MAX_HP: return R.string.actorinfo_health;
		case Player.STAT_ACTOR_MAX_AP: return R.string.heroinfo_actionpoints;
		case Player.STAT_ACTOR_MOVECOST: return R.string.actorinfo_movecost;
		default:
			return -1;
		}
	}
	
	private static int getRequirementCombatStatsResourceID(int statID) {
		switch (statID) {
		case Player.STAT_COMBAT_ATTACK_COST: return R.string.traitsinfo_attack_cost;
		case Player.STAT_COMBAT_ATTACK_CHANCE: return R.string.traitsinfo_attack_chance;
		case Player.STAT_COMBAT_CRITICAL_SKILL: return R.string.traitsinfo_criticalhit_skill;
		case Player.STAT_COMBAT_CRITICAL_MULTIPLIER: return R.string.traitsinfo_criticalhit_multiplier;
		case Player.STAT_COMBAT_DAMAGE_POTENTIAL_MIN: return R.string.traitsinfo_attack_damage;
		case Player.STAT_COMBAT_DAMAGE_POTENTIAL_MAX: return R.string.traitsinfo_attack_damage;
		case Player.STAT_COMBAT_BLOCK_CHANCE: return R.string.traitsinfo_defense_chance;
		case Player.STAT_COMBAT_DAMAGE_RESISTANCE: return R.string.traitsinfo_defense_damageresist;
		default:
			return -1;
		}
	}
}
