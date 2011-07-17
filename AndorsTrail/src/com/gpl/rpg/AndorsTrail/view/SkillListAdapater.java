package com.gpl.rpg.AndorsTrail.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gpl.rpg.AndorsTrail.R;
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

public final class SkillListAdapater extends ArrayAdapter<SkillInfo> {
	private final Resources r;
	private final Player player;
	
	public SkillListAdapater(Context context, Collection<SkillInfo> skills, Player player) {
		super(context, 0, filterNondisplayedSkills(skills, player));
		this.r = context.getResources();
		this.player = player;
	}

	private static List<SkillInfo> filterNondisplayedSkills(Collection<SkillInfo> skills, Player player) {
		final ArrayList<SkillInfo> result = new ArrayList<SkillInfo>();
		for (SkillInfo skill : skills) {
			if (shouldDisplaySkill(skill, player)) result.add(skill);
		}
		return result;
	}

	private static boolean shouldDisplaySkill(SkillInfo skill, Player player) {
		if (player.hasSkill(skill.id)) return true;
		if (skill.isQuestSkill) return false;
		return true;
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
		
		SkillController.setSkillIcon(icon, skillID, r);
		String skillTitle = SkillCollection.getSkillTitle(skillID, r);
		final int skillLevel = player.getSkillLevel(skillID);
		if (skillLevel > 0) {
			skillTitle += " (" + skillLevel + ")"; 
		}
		title.setText(skillTitle);
		description.setText(SkillCollection.getSkillShortDescription(skillID, r));
		
		boolean enabled = true;
		if (player.hasAvailableSkillpoints()) {
			enabled = SkillController.canLevelupSkill(player, skill);
		} else {
			enabled = player.hasSkill(skillID);
		}
		icon.setEnabled(enabled);
		title.setEnabled(enabled);
		description.setEnabled(enabled);
		
		return result;
	}
}
