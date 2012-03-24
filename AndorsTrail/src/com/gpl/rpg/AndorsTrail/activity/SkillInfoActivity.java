package com.gpl.rpg.AndorsTrail.activity;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.SkillController;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.ability.SkillInfo;
import com.gpl.rpg.AndorsTrail.model.ability.SkillInfo.SkillLevelRequirement;
import com.gpl.rpg.AndorsTrail.model.actor.ActorTraits;
import com.gpl.rpg.AndorsTrail.model.actor.Player;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public final class SkillInfoActivity extends Activity {
	private WorldContext world;
	private Player player;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        if (!app.isInitialized()) { finish(); return; }
        this.world = app.world;
        this.player = world.model.player;
        
        AndorsTrailApplication.setWindowParameters(this, app.preferences);
        
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
        b.setEnabled(SkillController.canLevelupSkill(player, skill));
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
		case ActorTraits.STAT_ACTOR_MAX_HP: return R.string.actorinfo_health;
		case ActorTraits.STAT_ACTOR_MAX_AP: return R.string.heroinfo_actionpoints;
		case ActorTraits.STAT_ACTOR_MOVECOST: return R.string.actorinfo_movecost;
		default:
			return -1;
		}
	}
	
	private static int getRequirementCombatStatsResourceID(int statID) {
		switch (statID) {
		case CombatTraits.STAT_COMBAT_ATTACK_COST: return R.string.traitsinfo_attack_cost;
		case CombatTraits.STAT_COMBAT_ATTACK_CHANCE: return R.string.traitsinfo_attack_chance;
		case CombatTraits.STAT_COMBAT_CRITICAL_CHANCE: return R.string.traitsinfo_criticalhit_chance;
		case CombatTraits.STAT_COMBAT_CRITICAL_MULTIPLIER: return R.string.traitsinfo_criticalhit_multiplier;
		case CombatTraits.STAT_COMBAT_DAMAGE_POTENTIAL_MIN: return R.string.traitsinfo_attack_damage;
		case CombatTraits.STAT_COMBAT_DAMAGE_POTENTIAL_MAX: return R.string.traitsinfo_attack_damage;
		case CombatTraits.STAT_COMBAT_BLOCK_CHANCE: return R.string.traitsinfo_defense_chance;
		case CombatTraits.STAT_COMBAT_DAMAGE_RESISTANCE: return R.string.traitsinfo_defense_damageresist;
		default:
			return -1;
		}
	}
}
