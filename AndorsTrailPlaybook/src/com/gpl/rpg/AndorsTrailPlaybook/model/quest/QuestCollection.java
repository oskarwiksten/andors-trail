package com.gpl.rpg.AndorsTrailPlaybook.model.quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.gpl.rpg.AndorsTrailPlaybook.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrailPlaybook.resource.parsers.QuestParser;
import com.gpl.rpg.AndorsTrailPlaybook.util.L;

public final class QuestCollection {
	private final HashMap<String, Quest> quests = new HashMap<String, Quest>();

	public Collection<Quest> getAllQuests() {
		ArrayList<Quest> quests = new ArrayList<Quest>(this.quests.values());
		Collections.sort(quests);
		return quests;
	}

	public QuestLogEntry getQuestLogEntry(final QuestProgress stage) {
		Quest q = getQuest(stage.questID);
		if (q == null) return null;
		return q.getQuestLogEntry(stage.progress);
	}

	public Quest getQuest(final String questID) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!quests.containsKey(questID)) {
				L.log("WARNING: Cannot find quest \"" + questID + "\".");
			}
		}
		return quests.get(questID);
	}

	public void initialize(QuestParser parser, String input) {
		parser.parseRows(input, quests);
	}
}
