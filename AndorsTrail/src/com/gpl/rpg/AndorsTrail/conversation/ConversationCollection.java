package com.gpl.rpg.AndorsTrail.conversation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.resource.ResourceLoader;

public class ConversationCollection {
	public static final String PHRASE_CLOSE = "X";
	public static final String PHRASE_SHOP = "S";
	
	private final HashMap<String, Phrase> phrases = new HashMap<String, Phrase>();
	
	public Phrase getPhrase(String id) {
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
    		
    		phrases.put(parts[0], new Phrase(
        			parts[1]
        			, _replies
        			, parts[2]
		        	, ResourceLoader.parseInt(parts[3], -1)
		        	, ResourceLoader.parseInt(parts[4], -1)
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
}
