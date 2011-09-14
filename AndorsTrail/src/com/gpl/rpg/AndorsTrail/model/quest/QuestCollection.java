package com.gpl.rpg.AndorsTrail.model.quest;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.resource.parsers.QuestParser;
import com.gpl.rpg.AndorsTrail.util.L;

public final class QuestCollection  {
	private final HashMap<String, Quest> quests = new HashMap<String, Quest>();
	
	public Collection<Quest> getAllQuests() {
		return quests.values();
	}

	public QuestLogEntry getQuestLogEntry(final QuestProgress stage) {
		Quest q = getQuest(stage.questID);
		if (q == null) return null;
		
		for (QuestLogEntry s : q.stages) {
			if (s.progress == stage.progress) return s;
		}
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Cannot find stage " + stage.progress + " in quest \"" + stage.questID + "\".");
		}
		return null;
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

	
	// Selftest method. Not part of the game logic.
	public void verifyData() {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			for (Quest q : quests.values()) {
				if (q.name.trim().length() <= 0) {
    				L.log("WARNING: Quest \"" + q.questID + "\" has empty name.");
    			}
    			if (q.stages.length <= 0) {
    				L.log("WARNING: Quest \"" + q.questID + "\" has no log entries.");
    			}
    			boolean hasFinishingEntry = false;
				for (QuestLogEntry entry : q.stages) {
					if (entry.finishesQuest) hasFinishingEntry = true;
					if (entry.rewardExperience == 1) {
						L.log("WARNING: Quest \"" + q.questID + "\" has stage " + entry.progress + " that rewards just 1 exp. Might be malformed resourcefile?");
					}
				}
    			if (q.showInLog) {
    				if (!hasFinishingEntry) {
    					L.log("WARNING: Quest \"" + q.questID + "\" is shown in log, but has no progress stage that finished the quest.");
    				}
    			}
    		}	
		}
	}
	
	// Selftest method. Not part of the game logic.
	public void verifyData(ConversationCollection conversations) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			final HashSet<String> suppliedStages = new HashSet<String>();
			conversations.DEBUG_getSuppliedQuestStages(suppliedStages);
			for (Quest q : quests.values()) {
				for (QuestLogEntry e : q.stages) {
					String s = q.questID + ":" + e.progress;
	    			if (!suppliedStages.contains(s)) {
	    				L.log("OPTIMIZE: Quest stage \"" + s + "\" cannot be reached by any conversation phrase.");
	    			}
				}
    		}	
		}
	}
}
