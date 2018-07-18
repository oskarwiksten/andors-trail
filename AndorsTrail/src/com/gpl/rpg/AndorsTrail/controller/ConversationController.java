package com.gpl.rpg.AndorsTrail.controller;

import android.content.res.Resources;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.GameStatistics;
import com.gpl.rpg.AndorsTrail.model.ability.*;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.conversation.*;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.model.quest.QuestLogEntry;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.model.script.Requirement;
import com.gpl.rpg.AndorsTrail.model.script.ScriptEffect;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
import com.gpl.rpg.AndorsTrail.util.L;

import java.util.ArrayList;

public final class ConversationController {

	private final ControllerContext controllers;
	private final WorldContext world;

	public ConversationController(ControllerContext controllers, WorldContext world) {
		this.controllers = controllers;
		this.world = world;
	}

	private static final ConstRange always = new ConstRange(1, 1);

	public static final class ScriptEffectResult {
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

	private ScriptEffectResult applyScriptEffectsForPhrase(final Player player, final Phrase phrase) {
		if (phrase.scriptEffects == null || phrase.scriptEffects.length == 0) return null;

		final ScriptEffectResult result = new ScriptEffectResult();
		for (ScriptEffect effect : phrase.scriptEffects) {
			applyScriptEffect(player, effect, result);
		}

		if (result.isEmpty()) return null;

		player.inventory.add(result.loot);
		controllers.actorStatsController.addExperience(result.loot.exp);

		return result;
	}

	private void applyScriptEffect(Player player, ScriptEffect effect, ScriptEffectResult result) {
		switch (effect.type) {
			case actorCondition:
				addActorConditionReward(player, effect.effectID, effect.value, result);
				break;
			case skillIncrease:
				addSkillReward(player, SkillCollection.SkillID.valueOf(effect.effectID), result);
				break;
			case dropList:
				addDropListReward(player, effect.effectID, result);
				break;
			case questProgress:
				addQuestProgressReward(player, effect.effectID, effect.value, result);
				break;
			case alignmentChange:
				addAlignmentReward(player, effect.effectID, effect.value);
				break;
			case giveItem:
				addItemReward(effect.effectID, effect.value, result);
				break;
			case createTimer:
				world.model.worldData.createTimer(effect.effectID);
				break;
			case spawnAll:
				spawnAll(effect.mapName, effect.effectID);
				break;
			case removeSpawnArea:
				deactivateSpawnArea(effect.mapName, effect.effectID, true);
				break;
			case deactivateSpawnArea:
				deactivateSpawnArea(effect.mapName, effect.effectID, false);
				break;
			case activateMapChangeArea:
				activateMapChangeArea(effect.mapName, effect.effectID);
				break;
			case deactivateMapChangeArea:
				deactivateMapChangeArea(effect.mapName, effect.effectID);
				break;
		}
	}

	private void deactivateMapChangeArea(String mapName, String mapObjectID) {
		PredefinedMap map = findMapForScriptEffect(mapName);
		MapObject o = map.findEventObject(MapObject.MapObjectType.newmap, mapObjectID);
		controllers.mapController.deactivateMapObject(o);
	}

	private PredefinedMap findMapForScriptEffect(String mapName) {
		if (mapName == null) return world.model.currentMap;
		return world.maps.findPredefinedMap(mapName);
	}

	private void activateMapChangeArea(String mapName, String mapObjectID) {
		PredefinedMap map = findMapForScriptEffect(mapName);
		MapObject o = map.findEventObject(MapObject.MapObjectType.newmap, mapObjectID);
		controllers.mapController.activateMapObject(map, o);
	}

	private void spawnAll(String mapName, String monsterTypeSpawnGroup) {
		PredefinedMap map = findMapForScriptEffect(mapName);
		LayeredTileMap tileMap = null;
		if (map == world.model.currentMap) {
			tileMap = world.model.currentTileMap;
		}
		for (MonsterSpawnArea area : map.spawnAreas) {
			if (!area.monsterTypeSpawnGroup.equals(monsterTypeSpawnGroup)) continue;
			controllers.monsterSpawnController.activateSpawnArea(map, tileMap, area, true);
		}
	}

	private void deactivateSpawnArea(String mapName, String monsterTypeSpawnGroup, boolean removeAllMonsters) {
		PredefinedMap map = findMapForScriptEffect(mapName);
		for (MonsterSpawnArea area : map.spawnAreas) {
			if (!area.monsterTypeSpawnGroup.equals(monsterTypeSpawnGroup)) continue;
			controllers.monsterSpawnController.deactivateSpawnArea(area, removeAllMonsters);
		}
	}

	private void addAlignmentReward(Player player, String faction, int delta) {
		player.addAlignment(faction, delta);
		MovementController.refreshMonsterAggressiveness(world.model.currentMap, world.model.player);
	}

	private void addQuestProgressReward(Player player, String questID, int questProgress, ScriptEffectResult result) {
		QuestProgress progress = new QuestProgress(questID, questProgress);
		boolean added = player.addQuestProgress(progress);
		if (!added) return; // Only apply exp reward if the quest stage was reached just now (and not re-reached)

		QuestLogEntry stage = world.quests.getQuestLogEntry(progress);
		if (stage == null) return;
		result.loot.exp += stage.rewardExperience;
		result.questProgress.add(progress);
	}

	private void addDropListReward(Player player, String droplistID, ScriptEffectResult result) {
		world.dropLists.getDropList(droplistID).createRandomLoot(result.loot, player);
	}

	private void addItemReward(String itemTypeID, int quantity, ScriptEffectResult result) {
		result.loot.add(world.itemTypes.getItemType(itemTypeID), quantity);
	}

	private void addSkillReward(Player player, SkillCollection.SkillID skillID, ScriptEffectResult result) {
		SkillInfo skill = world.skills.getSkill(skillID);
		boolean addedSkill = controllers.skillController.levelUpSkillByQuest(player, skill);
		if (addedSkill) {
			result.skillIncrease.add(skill);
		}
	}

	private void addActorConditionReward(Player player, String conditionTypeID, int value, ScriptEffectResult result) {
		int magnitude = 1;
		int duration = value;
		if (value == ActorCondition.DURATION_FOREVER) duration = ActorCondition.DURATION_FOREVER;
		else if (value == ActorCondition.MAGNITUDE_REMOVE_ALL) magnitude = ActorCondition.MAGNITUDE_REMOVE_ALL;

		ActorConditionType conditionType = world.actorConditionsTypes.getActorConditionType(conditionTypeID);
		ActorConditionEffect e = new ActorConditionEffect(conditionType, magnitude, duration, always);
		controllers.actorStatsController.applyActorCondition(player, e);
		result.actorConditions.add(e);
	}

	private static void applyReplyEffect(final WorldContext world, final Reply reply) {
		if (!reply.hasRequirements()) return;

		for (Requirement requirement : reply.requires) {
			requirementFulfilled(world, requirement);
		}
	}

	private static boolean canSelectReply(final WorldContext world, final Reply reply) {
		if (!reply.hasRequirements()) return true;

		for (Requirement requirement : reply.requires) {
			if (!canFulfillRequirement(world, requirement)) return false;
		}
		return true;
	}

	public static boolean canFulfillRequirement(WorldContext world, Requirement requirement) {
		Player player = world.model.player;
		GameStatistics stats = world.model.statistics;
		boolean result;
		switch (requirement.requireType) {
			case questProgress:
				result = player.hasExactQuestProgress(requirement.requireID, requirement.value);
				break;
			case questLatestProgress:
				result = player.isLatestQuestProgress(requirement.requireID, requirement.value);
				break;
			case wear:
				result =  player.inventory.isWearing(requirement.requireID, requirement.value);
				break;
			case inventoryKeep:
			case inventoryRemove:
				if (ItemTypeCollection.isGoldItemType(requirement.requireID)) {
					result =  player.inventory.gold >= requirement.value;
				} else {
					result =  player.inventory.hasItem(requirement.requireID, requirement.value);
				}
				break;
			case skillLevel:
				result =  player.getSkillLevel(SkillCollection.SkillID.valueOf(requirement.requireID)) >= requirement.value;
				break;
			case killedMonster:
				result =  stats.getNumberOfKillsForMonsterType(requirement.requireID) >= requirement.value;
				break;
			case timerElapsed:
				result =  world.model.worldData.hasTimerElapsed(requirement.requireID, requirement.value);
				break;
			case usedItem:
				result =  stats.getNumberOfTimesItemHasBeenUsed(requirement.requireID) >= requirement.value;
				break;
			case spentGold:
				result =  stats.getSpentGold() >= requirement.value;
				break;
			case consumedBonemeals:
				result =  stats.getNumberOfUsedBonemealPotions() >= requirement.value;
				break;
			case hasActorCondition:
				result =  player.hasCondition(requirement.requireID);
				break;
			default:
				result =  true;
		}
		return requirement.negate ? !result : result;
	}

	public static void requirementFulfilled(WorldContext world, Requirement requirement) {
		Player p = world.model.player;
		switch (requirement.requireType) {
			case inventoryRemove:
				if (ItemTypeCollection.isGoldItemType(requirement.requireID)) {
					p.inventory.gold -= requirement.value;
					world.model.statistics.addGoldSpent(requirement.value);
				} else {
					p.inventory.removeItem(requirement.requireID, requirement.value);
				}
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
		private String phraseID;
		private Phrase currentPhrase;
		private Monster npc;
		public final ConversationStateListener listener;

		public ConversationStatemachine(WorldContext world, ControllerContext controllers, ConversationStateListener listener) {
			this.world = world;
			this.player = world.model.player;
			this.controllers = controllers;
			this.listener = listener;
		}

		public void setCurrentNPC(Monster currentNPC) { this.npc = currentNPC; }
		public Monster getCurrentNPC() { return npc; }
		public String getCurrentPhraseID() { return phraseID; }

		public void playerSelectedReply(final Resources res, Reply r) {
			applyReplyEffect(world, r);
			proceedToPhrase(res, r.nextPhrase, true, true);
		}

		public void playerSelectedNextStep(final Resources res) {
			playerSelectedReply(res, currentPhrase.replies[0]);
		}

		public interface ConversationStateListener {
			void onTextPhraseReached(String message, Actor actor, String phraseID);
			void onConversationEnded();
			void onConversationEndedWithShop(Monster npc);
			void onConversationEndedWithCombat(Monster npc);
			void onConversationEndedWithRemoval(Monster npc);
			void onScriptEffectsApplied(ScriptEffectResult scriptEffectResult);
			void onConversationCanProceedWithNext();
			void onConversationHasReply(Reply r, String message);
		}

		private void setCurrentPhrase(final Resources res, String phraseID) {
			this.phraseID = phraseID;
			this.currentPhrase = world.conversationLoader.loadPhrase(phraseID, conversationCollection, res);
			if (AndorsTrailApplication.DEVELOPMENT_DEBUGMESSAGES) {
				if (currentPhrase == null) currentPhrase = new Phrase("(phrase \"" + phraseID + "\" not implemented yet)", null, null, null);
			}
			if (this.currentPhrase.switchToNPC != null) {
				setCurrentNPC(world.model.currentMap.findSpawnedMonster(this.currentPhrase.switchToNPC));
			}
		}

		public void proceedToPhrase(final Resources res, String phraseID, boolean applyScriptEffects, boolean displayPhraseMessage) {
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

			setCurrentPhrase(res, phraseID);

			if (applyScriptEffects) {
				ScriptEffectResult scriptEffectResult = controllers.conversationController.applyScriptEffectsForPhrase(player, currentPhrase);
				if (scriptEffectResult != null) {
					listener.onScriptEffectsApplied(scriptEffectResult);
				}
			}

			if (currentPhrase.message == null) {
				for (Reply r : currentPhrase.replies) {
					if (!canSelectReply(world, r)) continue;
					applyReplyEffect(world, r);
					proceedToPhrase(res, r.nextPhrase, applyScriptEffects, displayPhraseMessage);
					return;
				}
			} else if (displayPhraseMessage) {
				String message = getDisplayMessage(currentPhrase, player);
				listener.onTextPhraseReached(message, npc, phraseID);
			}

			if (hasOnlyOneNextReply()) {
				listener.onConversationCanProceedWithNext();
				return;
			}

			for (Reply r : currentPhrase.replies) {
				if (!canSelectReply(world, r)) continue;
				listener.onConversationHasReply(r, getDisplayMessage(r, player));
			}
		}

		private void endConversationWithRemovingNPC() {
			if (npc == null) {
				if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) L.log("Tried to remove NPC from conversation without having a valid npc target!");
				listener.onConversationEnded();
				return;
			}
			controllers.monsterSpawnController.remove(world.model.currentMap, npc);
			listener.onConversationEndedWithRemoval(npc);
		}

		private void endConversationWithCombat() {
			if (npc == null) {
				if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) L.log("Tried to enter combat from conversation without having a valid npc target!");
				listener.onConversationEnded();
				return;
			}
			npc.forceAggressive();
			controllers.combatController.setCombatSelection(npc);
			controllers.combatController.enterCombat(CombatController.BeginTurnAs.player);
			listener.onConversationEndedWithCombat(npc);
		}

		public boolean hasOnlyOneNextReply() {
			if (currentPhrase.replies == null) return false;
			if (currentPhrase.replies.length != 1) return false;
			final Reply singleReply = currentPhrase.replies[0];
			if (!singleReply.text.equals(ConversationCollection.REPLY_NEXT)) return false;
			if (!canSelectReply(world, singleReply)) return false;
			return true;
		}
	}
}
