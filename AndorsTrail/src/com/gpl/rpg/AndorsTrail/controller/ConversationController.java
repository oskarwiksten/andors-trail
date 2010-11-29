package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.conversation.Phrase;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
import com.gpl.rpg.AndorsTrail.model.actor.Player;

public class ConversationController {

	public static void applyPhraseEffect(final Player player, final Phrase phrase) {
		if (phrase.enablesKey != null) player.addKey(phrase.enablesKey);
		if (phrase.rewardExperience > 0) player.addExperience(phrase.rewardExperience);
		if (phrase.rewardGold != 0) player.inventory.gold += phrase.rewardGold;
	}
	
	public static void applyReplyEffect(final Player player, final Reply reply) {
		if (reply.requiresItemTypeID > 0 && reply.requiresItemQuantity > 0) {
			player.inventory.removeItem(reply.requiresItemTypeID, reply.requiresItemQuantity);
		}
	}
    
	public static boolean canSelectReply(final Player player, final Reply reply) {
		if (!hasRequiredKey(player, reply.requiresKey)) return false;
		if (!hasRequiredItems(player, reply.requiresItemTypeID, reply.requiresItemQuantity)) return false;
		return true;
    }
	
	private static boolean hasRequiredKey(final Player player, final String key) {
    	if (key == null) return true;
    	if (key.startsWith("!")) {
    		return !player.hasKey(key.substring(1));
    	} else {
    		return player.hasKey(key);
    	}
    }
	
	private static boolean hasRequiredItems(final Player player, int requiresItemTypeID, int requiresItemQuantity) {
    	if (requiresItemTypeID <= 0) return true;
    	if (requiresItemQuantity <= 0) return true;
    	return player.inventory.hasItem(requiresItemTypeID, requiresItemQuantity);
    }
}
