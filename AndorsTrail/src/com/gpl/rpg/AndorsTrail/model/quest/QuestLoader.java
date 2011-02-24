package com.gpl.rpg.AndorsTrail.model.quest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.resource.ResourceLoader;
import com.gpl.rpg.AndorsTrail.util.L;

public final class QuestLoader {
	
	private HashMap<String, ArrayList<QuestLogEntry> > parsedQuestLogEntries = new HashMap<String, ArrayList<QuestLogEntry> >();
	public void parseQuestLogsFromString(String questLogEntrylist) {
		Matcher rowMatcher = ResourceLoader.rowPattern.matcher(questLogEntrylist);
    	while(rowMatcher.find()) {
    		String[] parts = rowMatcher.group(1).split(ResourceLoader.columnSeparator, -1);
    		if (parts.length < 4) continue;
    		
    		final String questID = parts[0];
    		
    		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    			if (questID.trim().length() <= 0) {
    				L.log("WARNING: Quest log phrase with empty quest id.");
    			}
    		}
    		
    		if (!parsedQuestLogEntries.containsKey(questID)) parsedQuestLogEntries.put(questID, new ArrayList<QuestLogEntry>());
    		parsedQuestLogEntries.get(questID).add(new QuestLogEntry(
    				ResourceLoader.parseInt(parts[1], 0)
    				, parts[2]
    				, ResourceLoader.parseInt(parts[3], 0)
    		       	, ResourceLoader.parseInt(parts[4], 0)>0
    			));
    	}
	}
	
	private ArrayList<Quest> parsedQuests = new ArrayList<Quest>();
	public void parseQuestsFromString(String questlist) {
		Matcher rowMatcher = ResourceLoader.rowPattern.matcher(questlist);
    	while(rowMatcher.find()) {
    		String[] parts = rowMatcher.group(1).split(ResourceLoader.columnSeparator, -1);
    		if (parts.length < 4) continue;
    		
    		final String questID = parts[0];
    		
    		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    			if (questID.trim().length() <= 0) {
    				L.log("WARNING: Quest phrase with empty id.");
    			} else if (contains(parsedQuests, questID)) {
    				L.log("WARNING: Quest \"" + questID + "\" may be duplicated.");
    			}
    		}
    		
    		ArrayList<QuestLogEntry> stages;
    		if (parsedQuestLogEntries.containsKey(questID)) {
    			stages = parsedQuestLogEntries.get(questID);
    			Collections.sort(stages, new Comparator<QuestLogEntry>() {
    				@Override
    				public int compare(QuestLogEntry a, QuestLogEntry b) {
    					return a.progress - b.progress;
    				}
    			});
    		} else {
    			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    				L.log("WARNING: Quest \"" + questID + "\" has no log entries.");
    			}
    			stages = new ArrayList<QuestLogEntry>();
    		}
    		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    			parsedQuestLogEntries.remove(questID);
    		}
    		QuestLogEntry[] _stages = new QuestLogEntry[stages.size()];
    		_stages = stages.toArray(_stages);
    		
    		parsedQuests.add(new Quest(
    				questID
        			, parts[1]
        			, _stages
        			, ResourceLoader.parseInt(parts[3], 0)>0
    			));
    	}
	}
	
	public Quest[] getParsedQuests() {
    	Quest[] _result = new Quest[parsedQuests.size()];
    	_result = parsedQuests.toArray(_result);

    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		for (String s : parsedQuestLogEntries.keySet()) {
    			L.log("WARNING: Quest log entries for quest \"" + s + "\" has no corresponding quest.");
    		}
		}
    	
    	return _result;
	}
	
	private boolean contains(ArrayList<Quest> haystack, String needleQuestID) {
		for (Quest q : haystack) {
			if (q.questID.equals(needleQuestID)) return true;
		}
		return false;
	}
}
