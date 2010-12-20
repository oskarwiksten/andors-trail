package com.gpl.rpg.AndorsTrail.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.quest.Quest;
import com.gpl.rpg.AndorsTrail.model.quest.QuestCollection;
import com.gpl.rpg.AndorsTrail.model.quest.QuestLogEntry;

public final class QuestLogActivity extends Activity {
	
	private Spinner questlog_includecompleted;
	private SimpleExpandableListAdapter questlog_contents_adapter;

	private QuestCollection questCollection;
	private Player player;
	
	private final List<Map<String, ?>> groupList = new ArrayList<Map<String, ?>>();
	private final List<List<Map<String, ?>>> childList = new ArrayList<List<Map<String,?>>>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        AndorsTrailApplication.setWindowParameters(this, app.preferences);
        this.questCollection = app.world.quests;
        this.player = app.world.model.player;
        
        setContentView(R.layout.questlog);
    	
    	questlog_includecompleted = (Spinner) findViewById(R.id.questlog_includecompleted);
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.questlog_includecompleted, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questlog_includecompleted.setAdapter(adapter);
        questlog_includecompleted.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				app.world.model.uiSelections.selectedQuestFilter = questlog_includecompleted.getSelectedItemPosition();
				reloadQuests();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
    	questlog_includecompleted.setSelection(app.world.model.uiSelections.selectedQuestFilter);
    	
    	ExpandableListView questlog_contents = (ExpandableListView) findViewById(R.id.questlog_contents);
    	questlog_contents_adapter = new SimpleExpandableListAdapter(
    			this
    			, groupList 
    			, android.R.layout.simple_expandable_list_item_2 
                , new String[] { mn_questName, mn_questStatus }
                , new int[] { android.R.id.text1, android.R.id.text2 }
                , childList 
                , R.layout.questlogentry
                , new String[] { mn_logText }
                , new int[] { R.id.questlog_entrytext }
    		);
    	questlog_contents.setAdapter(questlog_contents_adapter);
    	reloadQuests();
    }

	private static final String mn_questName = "questName";
	private static final String mn_questStatus = "questStatus";
	private static final String mn_logText = "logText";
	
	private void reloadQuests() {
		groupList.clear();
		childList.clear();
		
		for (Quest q : questCollection.quests) {
			if (!q.showInLog) continue; // Do not show
			if (player.hasAnyQuestProgress(q.questID)) {
				boolean isCompleted = q.isCompleted(player);
				
				int v = questlog_includecompleted.getSelectedItemPosition();
				if (v == 0) { // Hide completed quests
					if (isCompleted) continue;
				} else if (v == 1) { // Include completed quests
					// Always show.
				} else if (v == 2) { // Only completed quests
					if (!isCompleted) continue;
				}
				
	            int statusResId;
	    		if (isCompleted) {
	    			statusResId = R.string.questlog_queststatus_completed;
	    		} else {
	    			statusResId = R.string.questlog_queststatus_inprogress;
	    		}

				Map<String, Object> item = new HashMap<String, Object>();
	            item.put(mn_questName, q.name);
	            item.put(mn_questStatus, getString(R.string.questlog_queststatus, getString(statusResId)));
                groupList.add(item);

	            List<Map<String, ?>> logItemList = new ArrayList<Map<String, ?>>();
	            for (QuestLogEntry e : q.stages) {
	    			if (e.logtext.length() <= 0) continue; // Do not show if displaytext is empty.
	    			if (player.hasExactQuestProgress(q.questID, e.progress)) {
	    				item = new HashMap<String, Object>();
	                    item.put(mn_logText, e.logtext);
	                    logItemList.add(item);
	    			}
	    		}
	            childList.add(logItemList);
			}
		}
		questlog_contents_adapter.notifyDataSetChanged();
	}
}
