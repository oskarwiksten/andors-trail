package com.gpl.rpg.AndorsTrail.resource.parsers;

import com.gpl.rpg.AndorsTrail.model.quest.Quest;
import com.gpl.rpg.AndorsTrail.model.quest.QuestLogEntry;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonCollectionParserFor;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonFieldNames;
import com.gpl.rpg.AndorsTrail.resource.parsers.json.JsonParserFor;
import com.gpl.rpg.AndorsTrail.util.Pair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class QuestParser extends JsonCollectionParserFor<Quest> {
	private int sortOrder = 0;
	
	private final JsonParserFor<QuestLogEntry> questLogEntryParser = new JsonParserFor<QuestLogEntry>() {
		@Override
		protected QuestLogEntry parseObject(JSONObject o) throws JSONException {
			return new QuestLogEntry(
					o.getInt(JsonFieldNames.QuestLogEntry.progress)
					,o.getString(JsonFieldNames.QuestLogEntry.logText)
					,o.optInt(JsonFieldNames.QuestLogEntry.rewardExperience, 0)
					,o.optInt(JsonFieldNames.QuestLogEntry.finishesQuest, 0) > 0
			);
		}
	};
	private final Comparator<QuestLogEntry> sortByQuestProgress = new Comparator<QuestLogEntry>() {
		@Override
		public int compare(QuestLogEntry a, QuestLogEntry b) {
			return a.progress - b.progress;
		}
	};

	@Override
	protected Pair<String, Quest> parseObject(JSONObject o) throws JSONException {
		final String id = o.getString(JsonFieldNames.Quest.questID);

		final ArrayList<QuestLogEntry> stages = new ArrayList<QuestLogEntry>();
		questLogEntryParser.parseRows(o.getJSONArray(JsonFieldNames.Quest.stages), stages);
		Collections.sort(stages, sortByQuestProgress);
		final QuestLogEntry[] stages_ = stages.toArray(new QuestLogEntry[stages.size()]);

		++sortOrder;

		return new Pair<String, Quest>(id, new Quest(
				id
				, o.getString(JsonFieldNames.Quest.name)
				, stages_
				, o.optInt(JsonFieldNames.Quest.showInLog, 0) > 0
				, sortOrder
		));
	}
}
