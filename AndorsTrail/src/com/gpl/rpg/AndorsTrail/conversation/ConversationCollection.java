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
import com.gpl.rpg.AndorsTrail.resource.ResourceLoader;
import com.gpl.rpg.AndorsTrail.util.L;

public class ConversationCollection {
	public static final String PHRASE_CLOSE = "X";
	public static final String PHRASE_SHOP = "S";
	public static final String PHRASE_ATTACK = "F";
	
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
	
	public void initialize(ItemTypeCollection itemTypes, String phraselist) {
		Matcher rowMatcher = ResourceLoader.rowPattern.matcher(phraselist);
    	while(rowMatcher.find()) {
    		String[] parts = rowMatcher.group(1).split(ResourceLoader.columnSeparator, -1);
    		if (parts.length < 21) continue;
    		
    		ArrayList<Reply> replies = new ArrayList<Reply>();
    		final int startReplyOffset = 5;
    		final int replyLength = 5;
    		for (int i = 0; i < 3; ++i) {
    			int v = startReplyOffset + i * replyLength;
    			if (parts[v + 1].length() > 0) replies.add(parseReply(parts, v, itemTypes));	
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
    		}
    		
    		phrases.put(phraseID, new Phrase(
        			parts[1]
        			, _replies
        			, parts[2]
		        	, ResourceLoader.parseInt(parts[3], 0)
		        	, ResourceLoader.parseInt(parts[4], 0)
    			));
    	}
	}
	
	private static Reply parseReply(String[] parts, int startIndex, ItemTypeCollection itemTypes) {
		String requiresItemTypeTag = parts[startIndex+3];
		int requiresItemTypeID = 0;
		if (requiresItemTypeTag.length() > 0) {
			ItemType type = itemTypes.getItemTypeByTag(requiresItemTypeTag);
			if (type != null) requiresItemTypeID = type.id;
		}
		return new Reply(
				parts[startIndex]
				,parts[startIndex+1]
				,parts[startIndex+2]
		       	,requiresItemTypeID
       	       	,ResourceLoader.parseInt(parts[startIndex+4], 0)
			);
	}
	
	// Selftest method. Not part of the game logic.
	public void verifyData() {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		HashSet<String> requiredKeys = new HashSet<String>();
    		HashSet<String> suppliedKeys = new HashSet<String>();
			for (Entry<String, Phrase> e : phrases.entrySet()) {
				Phrase p = e.getValue();
				if (p.enablesKey != null && p.enablesKey.length() > 0) {
					suppliedKeys.add(p.enablesKey);	
				}
				if (e.getValue().replies.length <= 0) {
					L.log("WARNING: Phrase \"" + e.getKey() + "\" has no replies.");
				}
				for (Reply r : e.getValue().replies) {
					if (!isValidPhraseID(r.nextPhrase)) {
						L.log("WARNING: Phrase \"" + e.getKey() + "\" has reply to non-existing phrase \"" + r.nextPhrase + "\".");
					}
					if (r.requiresKey != null && r.requiresKey.length() > 0) {
						String s = r.requiresKey;
						if (s.startsWith("!")) s = s.substring(1);
						requiredKeys.add(s);
					}
				}
    		}
			
			for(String s : requiredKeys) {
				if (!suppliedKeys.contains(s)) {
					L.log("WARNING: Key \"" + s + "\" is required but never supplied by any phrases.");
				}
			}

			for(String s : suppliedKeys) {
				if (!requiredKeys.contains(s)) {
					L.log("OPTIMIZE: Key \"" + s + "\" is supplied but never required by any phrases.");
				}
			}
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
	public void verifyData(MonsterTypeCollection monsterTypes) {
    	if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
    		HashSet<String> requiredPhrases = monsterTypes.DEBUG_getRequiredPhrases();
    		for (Entry<String, Phrase> e : phrases.entrySet()) {
				for (Reply r : e.getValue().replies) {
					requiredPhrases.add(r.nextPhrase);
				}
    		}
    		requiredPhrases.remove(PHRASE_ATTACK);
    		requiredPhrases.remove(PHRASE_CLOSE);
    		requiredPhrases.remove(PHRASE_SHOP);
    		for (Entry<String, Phrase> e : phrases.entrySet()) {
    			if (!requiredPhrases.contains(e.getKey())) {
    				L.log("OPTIMIZE: Phrase \"" + e.getKey() + "\" cannot be reached by any monster or other phrase reply.");
    			}
    		}
    	}
    }
}
