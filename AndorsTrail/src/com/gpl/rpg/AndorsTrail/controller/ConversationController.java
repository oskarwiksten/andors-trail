package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.conversation.Phrase;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reward;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.quest.QuestLogEntry;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.util.ConstRange;

public final class ConversationController {

	private static final ConstRange always = new ConstRange(1, 1);
	
	public static Loot applyPhraseRewards(final Player player, final Phrase phrase, final WorldContext world) {
		if (phrase.rewards == null || phrase.rewards.length == 0) return null;
		
		final Loot loot = new Loot();
		for (Reward reward : phrase.rewards) {
			switch (reward.rewardType) {
			case Reward.REWARD_TYPE_ACTOR_CONDITION:
				int magnitude = 1;
				int duration = reward.value;
				if (reward.value == ActorCondition.DURATION_FOREVER) duration = ActorCondition.DURATION_FOREVER;
				else if (reward.value == ActorCondition.MAGNITUDE_REMOVE_ALL) magnitude = ActorCondition.MAGNITUDE_REMOVE_ALL;
				
				ActorConditionType conditionType = world.actorConditionsTypes.getActorConditionType(reward.rewardID);
				ActorConditionEffect e = new ActorConditionEffect(conditionType, magnitude, duration, always);
				ActorStatsController.applyActorCondition(player, e);
				break;
			case Reward.REWARD_TYPE_SKILL_INCREASE:
				player.addSkillLevel(Integer.parseInt(reward.rewardID), false);
				break;
			case Reward.REWARD_TYPE_DROPLIST:
				world.dropLists.getDropList(reward.rewardID).createRandomLoot(loot, player);
				break;
			case Reward.REWARD_TYPE_QUEST_PROGRESS:
				QuestProgress progress = new QuestProgress(reward.rewardID, reward.value);
				boolean added = player.addQuestProgress(progress);
				if (added) {  // Only apply exp reward if the quest stage was reached just now (and not re-reached)
					QuestLogEntry stage = world.quests.getQuestLogEntry(progress);
					if (stage != null) {
						loot.exp += stage.rewardExperience;
					}
				}
				break;
			case Reward.REWARD_TYPE_ALIGNMENT_CHANGE:
				player.addAlignment(reward.rewardID, reward.value);
				break;
			}
		}
		
		player.inventory.add(loot);
		player.addExperience(loot.exp);
		return loot;
	}
	
	public static void applyReplyEffect(final Player player, final Reply reply) {
		if (!reply.requiresItem()) return;
		
		if (reply.itemRequirementType == Reply.ITEM_REQUIREMENT_TYPE_INVENTORY_REMOVE) {
			if (ItemTypeCollection.isGoldItemType(reply.requiresItemTypeID)) {
				player.inventory.gold -= reply.requiresItemQuantity;
			} else {
				player.inventory.removeItem(reply.requiresItemTypeID, reply.requiresItemQuantity);
			}
		}
	}
    
	public static boolean canSelectReply(final Player player, final Reply reply) {
		if (!hasRequiredQuestProgress(player, reply.requiresProgress)) return false;
		if (!hasRequiredItems(player, reply)) return false;
		return true;
    }
	
	private static boolean hasRequiredQuestProgress(final Player player, final QuestProgress progress) {
    	if (progress == null) return true;
    	return player.hasExactQuestProgress(progress);
    }
	
	private static boolean hasRequiredItems(final Player player, Reply reply) {
		if (!reply.requiresItem()) return true;
		
    	if (ItemTypeCollection.isGoldItemType(reply.requiresItemTypeID)) { 
    		return player.inventory.gold >= reply.requiresItemQuantity;
    	} else if (reply.itemRequirementType == Reply.ITEM_REQUIREMENT_TYPE_WEAR_KEEP) {
    		return player.inventory.isWearing(reply.requiresItemTypeID);
    	} else {
    		return player.inventory.hasItem(reply.requiresItemTypeID, reply.requiresItemQuantity);
    	}
    }

	public static String getDisplayMessage(Phrase phrase, Player player) { return replacePlayerName(phrase.message, player); }
	public static String getDisplayMessage(Reply reply, Player player) { return replacePlayerName(reply.text, player); }
	public static String replacePlayerName(String s, Player player) {
		return s.replace("$playername", player.actorTraits.name);
	}
}
