package com.gpl.rpg.AndorsTrail.controller;

import android.content.res.Resources;
import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.conversation.Phrase;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reply;
import com.gpl.rpg.AndorsTrail.conversation.Phrase.Reward;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;
import com.gpl.rpg.AndorsTrail.model.ability.SkillInfo;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.quest.QuestLogEntry;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
import com.gpl.rpg.AndorsTrail.util.Coord;

import java.util.ArrayList;

public final class ConversationController {

	private final ControllerContext controllers;
	private final WorldContext world;

	public ConversationController(ControllerContext controllers, WorldContext world) {
		this.controllers = controllers;
		this.world = world;
	}
	
	private static final ConstRange always = new ConstRange(1, 1);

	public static final class PhraseRewards {
		public final Loot loot = new Loot();
		public final ArrayList<ActorConditionEffect> actorConditions = new ArrayList<ActorConditionEffect>();
		public final ArrayList<SkillInfo> skillIncrease = new ArrayList<SkillInfo>();
		public final ArrayList<QuestProgress> questProgress = new ArrayList<QuestProgress>();

        public boolean isEmpty() {
            if (loot.hasItemsOrExp()) return false;
            if (!actorConditions.isEmpty()) return false;
            if (!skillIncrease.isEmpty()) return false;
            if (!questProgress.isEmpty()) return false;
            return true;
        }
    }

	private PhraseRewards applyPhraseRewards(final Player player, final Phrase phrase) {
		if (phrase.rewards == null || phrase.rewards.length == 0) return null;
		
		final PhraseRewards result = new PhraseRewards();
		for (Reward reward : phrase.rewards) {
			switch (reward.rewardType) {
			case Reward.REWARD_TYPE_ACTOR_CONDITION:
				addActorConditionReward(player, reward.rewardID, reward.value, result);
				break;
			case Reward.REWARD_TYPE_SKILL_INCREASE:
				addSkillReward(player, Integer.parseInt(reward.rewardID), result);
				break;
			case Reward.REWARD_TYPE_DROPLIST:
				addDropListReward(player, reward.rewardID, result);
				break;
			case Reward.REWARD_TYPE_QUEST_PROGRESS:
				addQuestProgressReward(player, reward.rewardID, reward.value, result);
				break;
			case Reward.REWARD_TYPE_ALIGNMENT_CHANGE:
				player.addAlignment(reward.rewardID, reward.value);
				break;
			}
		}

        if (result.isEmpty()) return null;

		player.inventory.add(result.loot);
		controllers.actorStatsController.addExperience(result.loot.exp);

		return result;
	}

	private void addQuestProgressReward(Player player, String questID, int questProgress, PhraseRewards result) {
		QuestProgress progress = new QuestProgress(questID, questProgress);
		boolean added = player.addQuestProgress(progress);
		if (!added) return; // Only apply exp reward if the quest stage was reached just now (and not re-reached)

		QuestLogEntry stage = world.quests.getQuestLogEntry(progress);
		if (stage != null) {
			result.loot.exp += stage.rewardExperience;
			result.questProgress.add(progress);
		}
	}

	private void addDropListReward(Player player, String droplistID, PhraseRewards result) {
		world.dropLists.getDropList(droplistID).createRandomLoot(result.loot, player);
	}

	private void addSkillReward(Player player, int skillID, PhraseRewards result) {
		SkillInfo skill = world.skills.getSkill(skillID);
		boolean addedSkill = controllers.skillController.levelUpSkillByQuest(player, skill);
		if (addedSkill) {
			result.skillIncrease.add(skill);
		}
	}

	private void addActorConditionReward(Player player, String conditionTypeID, int value, PhraseRewards result) {
		int magnitude = 1;
		int duration = value;
		if (value == ActorCondition.DURATION_FOREVER) duration = ActorCondition.DURATION_FOREVER;
		else if (value == ActorCondition.MAGNITUDE_REMOVE_ALL) magnitude = ActorCondition.MAGNITUDE_REMOVE_ALL;

		ActorConditionType conditionType = world.actorConditionsTypes.getActorConditionType(conditionTypeID);
		ActorConditionEffect e = new ActorConditionEffect(conditionType, magnitude, duration, always);
		controllers.actorStatsController.applyActorCondition(player, e);
		result.actorConditions.add(e);
	}

	private static void applyReplyEffect(final Player player, final Reply reply) {
		if (!reply.requiresItem()) return;
		
		if (reply.itemRequirementType == Reply.ITEM_REQUIREMENT_TYPE_INVENTORY_REMOVE) {
			if (ItemTypeCollection.isGoldItemType(reply.requiresItemTypeID)) {
				player.inventory.gold -= reply.requiresItemQuantity;
			} else {
				player.inventory.removeItem(reply.requiresItemTypeID, reply.requiresItemQuantity);
			}
		}
	}

	private static boolean canSelectReply(final Player player, final Reply reply) {
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

	private static String getDisplayMessage(Phrase phrase, Player player) { return replacePlayerName(phrase.message, player); }
	private static String getDisplayMessage(Reply reply, Player player) { return replacePlayerName(reply.text, player); }
    private static String replacePlayerName(String s, Player player) {
		return s.replace(Constants.PLACEHOLDER_PLAYERNAME, player.getName());
	}

	public static final class ConversationStatemachine {
		private final ConversationCollection conversationCollection = new ConversationCollection();
		private final WorldContext world;
		private final ControllerContext controllers;
		private final Player player;
		private final Resources res;
		private String phraseID;
		private Phrase currentPhrase;
		private Monster npc;
		public ConversationStateListener listener;

		public ConversationStatemachine(WorldContext world, ControllerContext controllers, Resources res, ConversationStateListener listener) {
			this.world = world;
			this.player = world.model.player;
			this.controllers = controllers;
			this.res = res;
			this.listener = listener;
		}

		public void setCurrentNPC(Monster currentNPC) { this.npc = currentNPC; }
		public Monster getCurrentNPC() { return npc; }
		public String getCurrentPhraseID() { return phraseID; }

		public void playerSelectedReply(Reply r) {
			applyReplyEffect(player, r);
			proceedToPhrase(r.nextPhrase);
		}

		public void playerSelectedNextStep() {
			playerSelectedReply(currentPhrase.replies[0]);
		}

		public interface ConversationStateListener {
			void onTextPhraseReached(String message, Actor actor);
			void onConversationEnded();
			void onConversationEndedWithShop(Monster npc);
			void onConversationEndedWithCombat(Monster npc);
			void onConversationEndedWithRemoval(Monster npc);
			void onPlayerReceivedRewards(ConversationController.PhraseRewards phraseRewards);
			void onConversationCanProceedWithNext();
			void onConversationHasReply(Reply r, String message);
		}

		private void setCurrentPhrase(String phraseID) {
			this.phraseID = phraseID;
			this.currentPhrase = world.conversationLoader.loadPhrase(phraseID, conversationCollection, res);
			if (AndorsTrailApplication.DEVELOPMENT_DEBUGMESSAGES) {
				if (currentPhrase == null) currentPhrase = new Phrase("(phrase \"" + phraseID + "\" not implemented yet)", null, null);
			}
		}

		public void proceedToPhrase(String phraseID) {
			if (phraseID.equalsIgnoreCase(ConversationCollection.PHRASE_CLOSE)) {
				listener.onConversationEnded();
				return;
			} else if (phraseID.equalsIgnoreCase(ConversationCollection.PHRASE_SHOP)) {
				listener.onConversationEndedWithShop(npc);
				return;
			} else if (phraseID.equalsIgnoreCase(ConversationCollection.PHRASE_ATTACK)) {
                endConversationWithCombat();
				return;
			} else if (phraseID.equalsIgnoreCase(ConversationCollection.PHRASE_REMOVE)) {
                endConversationWithRemovingNPC();
				return;
			}

			setCurrentPhrase(phraseID);

			ConversationController.PhraseRewards phraseRewards = controllers.conversationController.applyPhraseRewards(player, currentPhrase);
			if (phraseRewards != null) {
				listener.onPlayerReceivedRewards(phraseRewards);
			}

			if (currentPhrase.message == null) {
				for (Reply r : currentPhrase.replies) {
					if (!canSelectReply(player, r)) continue;
					applyReplyEffect(player, r);
					proceedToPhrase(r.nextPhrase);
					return;
				}
			}

			String message = getDisplayMessage(currentPhrase, player);
			listener.onTextPhraseReached(message, npc);

			requestReplies();
		}

        private void endConversationWithRemovingNPC() {
            controllers.monsterSpawnController.remove(world.model.currentMap, npc);
            listener.onConversationEndedWithRemoval(npc);
        }

        private void endConversationWithCombat() {
            npc.forceAggressive();
            controllers.combatController.setCombatSelection(npc);
            controllers.combatController.enterCombat(CombatController.BEGIN_TURN_PLAYER);
            listener.onConversationEndedWithCombat(npc);
        }

        private void requestReplies() {
			if (hasOnlyOneNextReply()) {
				listener.onConversationCanProceedWithNext();
				return;
			}

			for (Reply r : currentPhrase.replies) {
				if (!canSelectReply(player, r)) continue;
				listener.onConversationHasReply(r, getDisplayMessage(r, player));
			}
		}

		public void proceedToRestoredState(String phraseID) {
			setCurrentPhrase(phraseID);
			requestReplies();
		}

		public boolean hasOnlyOneNextReply() {
			if (currentPhrase.replies == null) return false;
			if (currentPhrase.replies.length != 1) return false;
			if (currentPhrase.replies[0].text.equals(ConversationCollection.REPLY_NEXT)) return true;
			return false;
		}
	}
}
