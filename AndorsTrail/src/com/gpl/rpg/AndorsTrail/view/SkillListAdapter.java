package com.gpl.rpg.AndorsTrail.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.activity.SkillInfoActivity;
import com.gpl.rpg.AndorsTrail.controller.SkillController;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.ability.SkillInfo;
import com.gpl.rpg.AndorsTrail.model.actor.Player;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public final class SkillListAdapter extends ArrayAdapter<SkillInfo> {
	private final Resources r;
	private final Player player;
	
	public SkillListAdapter(Context context, Collection<SkillInfo> skills, Player player) {
		super(context, 0, filterNondisplayedSkills(skills, player));
		this.r = context.getResources();
		this.player = player;
	}

	private static List<SkillInfo> filterNondisplayedSkills(Collection<SkillInfo> skills, Player player) {
		final ArrayList<SkillInfo> result = new ArrayList<SkillInfo>();
		for (SkillInfo skill : skills) {
			if (shouldDisplaySkill(skill, player)) result.add(skill);
		}
		Collections.sort(result, new Comparator<SkillInfo>() {
    		@Override
			public int compare(SkillInfo a, SkillInfo b) {
				return a.id - b.id;
			}
		});
		return result;
	}

	private static boolean shouldDisplaySkill(SkillInfo skill, Player player) {
		if (player.hasSkill(skill.id)) return true;
		if (skill.levelupVisibility == SkillInfo.LEVELUP_TYPE_ALWAYS_SHOWN) return true;
		return false;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final SkillInfo skill = getItem(position);
		final int skillID = skill.id;
		
		View result = convertView;
		if (result == null) {
			result = View.inflate(getContext(), R.layout.skill_listentry_view, null);
		}
		
		final ImageView icon = (ImageView) result.findViewById(R.id.skillentry_icon);
		final TextView title = (TextView) result.findViewById(R.id.skillentry_title);
		final TextView description = (TextView) result.findViewById(R.id.skillentry_description);
		
		String skillTitle = r.getString(SkillInfoActivity.getSkillTitleResourceID(skillID));
		final int skillLevel = player.getSkillLevel(skillID);
		if (skillLevel > 0) {
			skillTitle += " (" + skillLevel + ')';
		}
		title.setText(skillTitle);
		description.setText(getSkillShortDescriptionResourceID(skillID));
		
		boolean enabled;
		if (player.hasAvailableSkillpoints()) {
			enabled = SkillController.canLevelupSkillManually(player, skill);
		} else {
			enabled = player.hasSkill(skillID);
		}
		icon.setEnabled(enabled);
		title.setEnabled(enabled);
		description.setEnabled(enabled);
		
		return result;
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).id;
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
		case SkillCollection.SKILL_RESISTANCE_MENTAL: return R.string.skill_shortdescription_resistance_mental;
		case SkillCollection.SKILL_RESISTANCE_PHYSICAL_CAPACITY: return R.string.skill_shortdescription_resistance_physical_capacity;
		case SkillCollection.SKILL_RESISTANCE_BLOOD_DISORDER: return R.string.skill_shortdescription_resistance_blood_disorder;
		case SkillCollection.SKILL_SHADOW_BLESS: return R.string.skill_shortdescription_shadow_bless;
		case SkillCollection.SKILL_CRIT1: return R.string.skill_shortdescription_crit1;
		case SkillCollection.SKILL_CRIT2: return R.string.skill_shortdescription_crit2;
		case SkillCollection.SKILL_REJUVENATION: return R.string.skill_shortdescription_rejuvenation;
		case SkillCollection.SKILL_TAUNT: return R.string.skill_shortdescription_taunt;
		case SkillCollection.SKILL_CONCUSSION: return R.string.skill_shortdescription_concussion;
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_DAGGER: return R.string.skill_shortdescription_weapon_prof_dagger;
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_1HSWORD: return R.string.skill_shortdescription_weapon_prof_1hsword;
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_2HSWORD: return R.string.skill_shortdescription_weapon_prof_2hsword;
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_AXE: return R.string.skill_shortdescription_weapon_prof_axe;
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_BLUNT: return R.string.skill_shortdescription_weapon_prof_blunt;
		case SkillCollection.SKILL_WEAPON_PROFICIENCY_UNARMED: return R.string.skill_shortdescription_weapon_prof_unarmed;
		case SkillCollection.SKILL_ARMOR_PROFICIENCY_SHIELD: return R.string.skill_shortdescription_armor_prof_shield;
		case SkillCollection.SKILL_ARMOR_PROFICIENCY_UNARMORED: return R.string.skill_shortdescription_armor_prof_unarmored;
		case SkillCollection.SKILL_ARMOR_PROFICIENCY_LIGHT: return R.string.skill_shortdescription_armor_prof_light;
		case SkillCollection.SKILL_ARMOR_PROFICIENCY_HEAVY: return R.string.skill_shortdescription_armor_prof_heavy;
		case SkillCollection.SKILL_FIGHTSTYLE_DUAL_WIELD: return R.string.skill_shortdescription_fightstyle_dualwield;
		case SkillCollection.SKILL_FIGHTSTYLE_2HAND: return R.string.skill_shortdescription_fightstyle_2hand;
		case SkillCollection.SKILL_FIGHTSTYLE_WEAPON_SHIELD: return R.string.skill_shortdescription_fightstyle_weapon_shield;
		case SkillCollection.SKILL_SPECIALIZATION_DUAL_WIELD: return R.string.skill_shortdescription_specialization_dualwield;
		case SkillCollection.SKILL_SPECIALIZATION_2HAND: return R.string.skill_shortdescription_specialization_2hand;
		case SkillCollection.SKILL_SPECIALIZATION_WEAPON_SHIELD: return R.string.skill_shortdescription_specialization_weapon_shield;
		default:
			return -1;
		}
	}
}
