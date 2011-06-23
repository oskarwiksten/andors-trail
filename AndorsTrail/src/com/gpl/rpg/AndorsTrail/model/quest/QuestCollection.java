package com.gpl.rpg.AndorsTrail.model.quest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectArrayTokenizer;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectFieldParser;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileParser.ResourceObjectTokenizer;
import com.gpl.rpg.AndorsTrail.util.L;

public final class QuestCollection {
	public final ArrayList<Quest> quests = new ArrayList<Quest>();
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

	private static final ResourceObjectTokenizer questResourceTokenizer = new ResourceObjectTokenizer(4);
	private static final ResourceObjectTokenizer questStageResourceTokenizer = new ResourceObjectTokenizer(4);
	public void initialize(String questlist) {
		questResourceTokenizer.tokenizeRows(questlist, new ResourceObjectFieldParser() {
			@Override
			public void matchedRow(String[] parts) {
				// [id|name|showInLog|stages[progress|logText|rewardExperience|finishesQuest|]|];
				
				final ArrayList<QuestLogEntry> stages = new ArrayList<QuestLogEntry>();
				ResourceObjectArrayTokenizer.tokenize(parts[3], questStageResourceTokenizer, new ResourceObjectFieldParser() {
					@Override
					public void matchedRow(String[] parts) {
						stages.add(new QuestLogEntry(
								Integer.parseInt(parts[0]) 							// Progress
								, parts[1] 											// Logtext
								, ResourceFileParser.parseInt(parts[2], 0) 			// RewardExperience
								, ResourceFileParser.parseBoolean(parts[3], false) 	// FinishesQuest
							));
					}
				});
				Collections.sort(stages, new Comparator<QuestLogEntry>() {
    				@Override
    				public int compare(QuestLogEntry a, QuestLogEntry b) {
    					return a.progress - b.progress;
    				}
    			});
				
				final QuestLogEntry[] stages_ = stages.toArray(new QuestLogEntry[stages.size()]);
				final Quest quest = new Quest(
						parts[0] // questID
						, parts[1] // name
						, stages_
						, ResourceFileParser.parseBoolean(parts[2], false) // showInLog
					);
				if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
	    			if (quest.questID.trim().length() <= 0) {
	    				L.log("WARNING: Quest with empty id.");
	    			} else {
	    				for (Quest q : quests) {
	    					if (q.questID.equals(quest.questID)) {
	    						L.log("OPTIMIZE: Quest " + quest.questID + " is duplicated.");
	    						break;
	    					}
	    				}
	    			}
	    			if (quest.name.trim().length() <= 0) {
	    				L.log("WARNING: Quest \"" + quest.questID + "\" has empty name.");
	    			}
	    			if (stages.size() <= 0) {
	    				L.log("WARNING: Quest \"" + quest.questID + "\" has no log entries.");
	    			}
	    			boolean hasFinishingEntry = false;
    				for (QuestLogEntry entry : quest.stages) {
    					if (entry.finishesQuest) hasFinishingEntry = true;
    					if (entry.rewardExperience == 1) {
    						L.log("WARNING: Quest \"" + quest.questID + "\" has stage " + entry.progress + " that rewards just 1 exp. Might be malformed resourcefile?");
    					}
    				}
	    			if (quest.showInLog) {
	    				if (!hasFinishingEntry) {
	    					L.log("WARNING: Quest \"" + quest.questID + "\" is shown in log, but has no progress stage that finished the quest.");
	    				}
	    			}
	    		}
				quests.add(quest);
			}
		});
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
