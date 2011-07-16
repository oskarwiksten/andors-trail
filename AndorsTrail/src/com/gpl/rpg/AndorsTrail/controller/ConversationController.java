package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.conversation.Phrase;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.quest.QuestCollection;
import com.gpl.rpg.AndorsTrail.model.quest.QuestLogEntry;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;

public final class ConversationController {

	public static Loot applyPhraseEffect(final Player player, final Phrase phrase, final QuestCollection questcollection) {
		if (phrase.rewardDropList == null && phrase.progressQuest == null) return null;
		
		final Loot loot = new Loot();
		if (phrase.rewardDropList != null) {
			phrase.rewardDropList.createRandomLoot(loot, player);
			player.inventory.add(loot);
		}
		if (phrase.progressQuest != null) {
			boolean added = player.addQuestProgress(phrase.progressQuest);
			if (added) {  // Only apply exp reward if the quest stage was reached just now (and not re-reached)
				QuestLogEntry stage = questcollection.getQuestLogEntry(phrase.progressQuest);
				if (stage != null) {
					loot.exp = stage.rewardExperience;
					player.addExperience(stage.rewardExperience);
				}
			}
		}
		return loot;
	}
	
	public static void applyReplyEffect(final Player player, final Reply reply) {
		if (reply.requiresItemTypeID < 0) return;
		if (reply.requiresItemQuantity <= 0) return;
		
		if (reply.requiresItemTypeID == ItemTypeCollection.ITEMTYPE_GOLD) {
			player.inventory.gold -= reply.requiresItemQuantity;
		} else {
			player.inventory.removeItem(reply.requiresItemTypeID, reply.requiresItemQuantity);
		}
	}
    
	public static boolean canSelectReply(final Player player, final Reply reply) {
		if (!hasRequiredQuestProgress(player, reply.requiresProgress)) return false;
		if (!hasRequiredItems(player, reply.requiresItemTypeID, reply.requiresItemQuantity)) return false;
		return true;
    }
	
	private static boolean hasRequiredQuestProgress(final Player player, final QuestProgress progress) {
    	if (progress == null) return true;
    	return player.hasExactQuestProgress(progress);
    }
	
	private static boolean hasRequiredItems(final Player player, int requiresItemTypeID, int requiresItemQuantity) {
    	if (requiresItemTypeID < 0) return true;
    	if (requiresItemQuantity <= 0) return true;
    	if (requiresItemTypeID == ItemTypeCollection.ITEMTYPE_GOLD) return player.inventory.gold >= requiresItemQuantity;
    		
    	return player.inventory.hasItem(requiresItemTypeID, requiresItemQuantity);
    }
}
