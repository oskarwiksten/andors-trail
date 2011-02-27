package com.gpl.rpg.AndorsTrail.model.quest;

import java.util.HashSet;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.util.L;

public final class QuestCollection {
	public Quest[] quests;
	public QuestLogEntry getQuestLogEntry(final QuestProgress stage) {
		Quest q = getQuest(stage.questID);
		if (q == null) return null;
		
		for (QuestLogEntry s : q.stages) {
			if (s.progress >= stage.progress) return s;
		}
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Cannot find stage " + stage.progress + " in quest \"" + stage.questID + "\".");
		}
		return null;
	}
	
	public Quest getQuest(final String questID) {
		for (Quest q : quests) {
			if (q.questID.equals(questID)) {
				return q;
			}
		}
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Cannot find quest \"" + questID + "\".");
		}
		return null;
	}

	// Selftest method. Not part of the game logic.
	public void verifyData(ConversationCollection conversations) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			final HashSet<String> suppliedStages = new HashSet<String>();
			conversations.DEBUG_getSuppliedQuestStages(suppliedStages);
			for (Quest q : quests) {
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
