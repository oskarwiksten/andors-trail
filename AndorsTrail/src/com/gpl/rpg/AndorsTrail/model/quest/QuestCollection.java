package com.gpl.rpg.AndorsTrail.model.quest;

import java.util.HashSet;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.model.map.MapCollection;
import com.gpl.rpg.AndorsTrail.util.L;

public final class QuestCollection {
	public static final int QUEST_PROGRESS_NOT_STARTED = -1;
	
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
	public void verifyData(MapCollection maps, ConversationCollection conversations) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			HashSet<String> requiredStages = new HashSet<String>();
			maps.DEBUG_getRequiredQuestStages(requiredStages);
			conversations.DEBUG_getRequiredQuestStages(requiredStages);
			for (Quest q : quests) {
				for (QuestLogEntry e : q.stages) {
					String s = q.questID + ":" + e.progress;
	    			if (!requiredStages.contains(s)) {
	    				L.log("OPTIMIZE: Quest stage \"" + s + "\" cannot be reached by any maparea or conversation phrase.");
	    			}
				}
    		}	
		}
	}
}
