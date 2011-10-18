package com.gpl.rpg.AndorsTrail.resource.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.gpl.rpg.AndorsTrail.model.quest.Quest;
import com.gpl.rpg.AndorsTrail.model.quest.QuestLogEntry;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer.ResourceParserFor;
import com.gpl.rpg.AndorsTrail.util.Pair;

public final class QuestParser extends ResourceParserFor<Quest> {
	private int sortOrder = 0;
	
	private final ResourceFileTokenizer questStageResourceTokenizer = new ResourceFileTokenizer(4);
	private final ResourceObjectParser<QuestLogEntry> questLogEntryParser = new ResourceObjectParser<QuestLogEntry>() {
		@Override
		public QuestLogEntry parseRow(String[] parts) {
			return new QuestLogEntry(
					Integer.parseInt(parts[0]) 							// Progress
					, parts[1] 											// Logtext
					, ResourceParserUtils.parseInt(parts[2], 0) 			// RewardExperience
					, ResourceParserUtils.parseBoolean(parts[3], false) 	// FinishesQuest
				);
		}
	};
	private final Comparator<QuestLogEntry> sortByQuestProgress = new Comparator<QuestLogEntry>() {
		@Override
		public int compare(QuestLogEntry a, QuestLogEntry b) {
			return a.progress - b.progress;
		}
	};
	
	public QuestParser() {
		super(4);
	}
	
	@Override
	public Pair<String, Quest> parseRow(String[] parts) {
		// [id|name|showInLog|stages[progress|logText|rewardExperience|finishesQuest|]|];
		
		final ArrayList<QuestLogEntry> stages = new ArrayList<QuestLogEntry>();
		questStageResourceTokenizer.tokenizeArray(parts[3], stages, questLogEntryParser);
		Collections.sort(stages, sortByQuestProgress);				
		final QuestLogEntry[] stages_ = stages.toArray(new QuestLogEntry[stages.size()]);
		
		++sortOrder;
		
		final String questID = parts[0];
		return new Pair<String, Quest>(questID, new Quest(
				questID 	// questID
				, parts[1] 	// name
				, stages_
				, ResourceParserUtils.parseBoolean(parts[2], false) // showInLog
				, sortOrder
			));
	}
}
