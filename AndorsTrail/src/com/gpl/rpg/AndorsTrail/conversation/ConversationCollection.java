package com.gpl.rpg.AndorsTrail.conversation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.map.MapCollection;
import com.gpl.rpg.AndorsTrail.model.quest.QuestCollection;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.resource.ResourceLoader;
import com.gpl.rpg.AndorsTrail.util.L;

public final class ConversationCollection {
	public static final String PHRASE_CLOSE = "X";
	public static final String PHRASE_SHOP = "S";
	public static final String PHRASE_ATTACK = "F";
	public static final String REPLY_NEXT = "N";
	
	private final HashMap<String, Phrase> phrases = new HashMap<String, Phrase>();
	
	public boolean isValidPhraseID(String id) {
		if (id.equals(PHRASE_CLOSE)) return true;
		else if (id.equals(PHRASE_SHOP)) return true;
		else if (id.equals(PHRASE_ATTACK)) return true;
		else if (phrases.containsKey(id)) return true;
		else return false;
	}
	
	public Phrase getPhrase(String id) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!phrases.containsKey(id)) {
				L.log("WARNING: Cannot find requested conversation phrase id \"" + id + "\".");
				return null;
			}
		}
		return phrases.get(id);
	}
	
	public void initialize(ItemTypeCollection itemTypes, DropListCollection droplists, String phraselist) {
		Matcher rowMatcher = ResourceLoader.rowPattern.matcher(phraselist);
    	while(rowMatcher.find()) {
    		String[] parts = rowMatcher.group(1).split(ResourceLoader.columnSeparator, -1);
    		if (parts.length < 19) {
    			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    				if (parts.length > 2) {
    					L.log("WARNING: Conversation resource contains row with invalid length: " + rowMatcher.group(1));
    				}
    			}
    			continue;
    		}
    		
    		ArrayList<Reply> replies = new ArrayList<Reply>();
    		final int startReplyOffset = 4;
    		final int replyLength = 5;
    		for (int i = 0; i < 3; ++i) {
    			int v = startReplyOffset + i * replyLength;
    			if (parts[v + 1].length() > 0 || parts[v].length() > 0) replies.add(parseReply(parts, v, itemTypes));	
    		}
    		Reply[] _replies = new Reply[replies.size()];
    		_replies = replies.toArray(_replies);
    		
    		final String phraseID = parts[0];
    		
    		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    			if (phraseID.trim().length() <= 0) {
    				L.log("WARNING: Adding phrase with empty id.");
    			} else if (phrases.get(phraseID) != null) {
    				L.log("WARNING: Phrase \"" + phraseID + "\" may be duplicated.");
    			}
    			
    			boolean hasNextReply = false;
    			boolean hasOtherReply = false;
    			for (Reply r : replies) {
    				if (r.text.equalsIgnoreCase(REPLY_NEXT)) hasNextReply = true;
    				else hasOtherReply = true;
    			}
    			if (hasNextReply && hasOtherReply) {
    				L.log("WARNING: Phrase \"" + phraseID + "\" has both a \"" + REPLY_NEXT + "\" reply and some other reply.");
    			}
    		}
    		
    		phrases.put(phraseID, new Phrase(
        			parts[1]
        			, _replies
        			, QuestProgress.parseQuestProgress(parts[2])
		        	, droplists.getDropList(parts[3])
    			));
    	}
	}
	
	private static Reply parseReply(String[] parts, int startIndex, ItemTypeCollection itemTypes) {
		String requiresItemTypeTag = parts[startIndex+3];
		int requiresItemTypeID = -1;
		if (requiresItemTypeTag.length() > 0) {
			ItemType type = itemTypes.getItemTypeByTag(requiresItemTypeTag);
			if (type != null) requiresItemTypeID = type.id;
		}
		return new Reply(
				parts[startIndex]
				, parts[startIndex+1]
				, QuestProgress.parseQuestProgress(parts[startIndex+2])
		       	, requiresItemTypeID
       	       	, ResourceLoader.parseInt(parts[startIndex+4], 0)
			);
	}
	
	// Selftest method. Not part of the game logic.
	public void verifyData() {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			for (Entry<String, Phrase> e : phrases.entrySet()) {
				for (Reply r : e.getValue().replies) {
					if (!isValidPhraseID(r.nextPhrase)) {
						L.log("WARNING: Phrase \"" + e.getKey() + "\" has reply to non-existing phrase \"" + r.nextPhrase + "\".");
					} else if (r.nextPhrase == null || r.nextPhrase.length() <= 0) {
						L.log("WARNING: Phrase \"" + e.getKey() + "\" has a reply that has no nextPhrase.");
					} else if (r.nextPhrase.equals(e.getKey())) {
						L.log("WARNING: Phrase \"" + e.getKey() + "\" has a reply that points to itself.");
					}
				}
    		}
		}	
	}
	
	// Selftest method. Not part of the game logic.
	public void verifyData(MapCollection maps) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		HashSet<String> requiredQuestStages = new HashSet<String>();
    		HashSet<String> suppliedQuestStages = new HashSet<String>();
    		this.DEBUG_getSuppliedQuestStages(suppliedQuestStages);
    		maps.DEBUG_getRequiredQuestStages(requiredQuestStages);
    		this.DEBUG_getRequiredQuestStages(requiredQuestStages);
    		
			for (String s : requiredQuestStages) {
				if (!suppliedQuestStages.contains(s)) {
					L.log("WARNING: Queststage \"" + s + "\" is required but never supplied by any phrases.");
				}
			}

			/*for (String s : suppliedQuestStages) {
				if (!requiredQuestStages.contains(s)) {
					L.log("OPTIMIZE: Queststage \"" + s + "\" is supplied but never required by any phrases.");
				}
			}*/
		}
    }
	
	// Selftest method. Not part of the game logic.
	public void verifyData(DropListCollection droplists) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		for (Entry<String, Phrase> e : phrases.entrySet()) {
				for (Reply r : e.getValue().replies) {
					if (r.requiresItemTypeID > 0) {
						if (!droplists.verifyExistsDroplist(r.requiresItemTypeID)) {
							L.log("WARNING: Phrase \"" + e.getKey() + "\" has reply that requires an item that is not dropped by any droplist.");
						}
					}
				}
    		}
    	}
    }
	
	// Selftest method. Not part of the game logic.
	public void verifyData(MonsterTypeCollection monsterTypes, MapCollection maps) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		HashSet<String> requiredPhrases = monsterTypes.DEBUG_getRequiredPhrases();
    		maps.DEBUG_getUsedPhrases(requiredPhrases);
    		for (Entry<String, Phrase> e : phrases.entrySet()) {
				for (Reply r : e.getValue().replies) {
					requiredPhrases.add(r.nextPhrase);
				}
    		}
    		requiredPhrases.remove(PHRASE_ATTACK);
    		requiredPhrases.remove(PHRASE_CLOSE);
    		requiredPhrases.remove(PHRASE_SHOP);
    		
    		// Verify that all supplied phrases are required.
    		for (Entry<String, Phrase> e : phrases.entrySet()) {
    			if (!requiredPhrases.contains(e.getKey())) {
    				L.log("OPTIMIZE: Phrase \"" + e.getKey() + "\" cannot be reached by any monster or other phrase reply.");
    			}
    		}
    	}
    }
	
	// Selftest method. Not part of the game logic.
	public void verifyData(QuestCollection quests) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		for (Phrase p : phrases.values()) {
				if (p.progressQuest != null) {
					quests.getQuestLogEntry(p.progressQuest); // Will warn inside if invalid.
    			}
    		}
    	}
    }

	// Selftest method. Not part of the game logic.
	public void DEBUG_getSuppliedQuestStages(HashSet<String> suppliedStages) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			for (Phrase p : phrases.values()) {
				if (p.progressQuest == null) continue;
				suppliedStages.add(p.progressQuest.toString());
			}
		}
	}
	
	// Selftest method. Not part of the game logic.
	public void DEBUG_getRequiredQuestStages(HashSet<String> requiredStages) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			for (Phrase p : phrases.values()) {
				for (Reply r : p.replies) {
					if (r.requiresProgress != null) {
						requiredStages.add(r.requiresProgress.toString());
					}
				}
			}
		}
	}

	// Selftest method. Not part of the game logic.
	public boolean DEBUG_leadsToTradeReply(String phraseID) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			HashSet<String> visited = new HashSet<String>();
			return DEBUG_leadsToTradeReply(phraseID, visited);
		} else {
			return false;
		}
	}
	private boolean DEBUG_leadsToTradeReply(String phraseID, HashSet<String> visited) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (phraseID.equals(PHRASE_SHOP)) return true;
			if (phraseID.equals(PHRASE_ATTACK)) return false;
			if (phraseID.equals(PHRASE_CLOSE)) return false;
			if (visited.contains(phraseID)) return false;
			visited.add(phraseID);
			
			Phrase p = getPhrase(phraseID);
			if (p == null) return false;
			for (Reply r : p.replies) {
				if (DEBUG_leadsToTradeReply(r.nextPhrase, visited)) return true;
			}
		}
		return false;
	}
}
