package com.gpl.rpg.AndorsTrail.model.quest;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.resource.parsers.ResourceParserUtils;
import com.gpl.rpg.AndorsTrail.util.L;

public final class QuestProgress {
	public final String questID;
	public final int progress;
	public QuestProgress(String questID, int progress) {
		this.questID = questID;
		this.progress = progress;
	}

	public static QuestProgress parseQuestProgress(String v) {
		if (v == null || v.length() <= 0) return null;
		String[] parts = v.split(":");
		int requiresQuestProgress = 0;
		if (parts.length >= 2) {
			requiresQuestProgress = ResourceParserUtils.parseInt(parts[1], 0);
		} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Quest progress \"" + v + "\" does not specify any progress stage.");
		}
		final String requiresQuestID = parts[0];
		return new QuestProgress(requiresQuestID, requiresQuestProgress);
	}

	@Override
	public String toString() {
		return questID + ':' + progress;
	}
}
