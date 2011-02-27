package com.gpl.rpg.AndorsTrail.context;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.VisualEffectCollection;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.map.MapCollection;
import com.gpl.rpg.AndorsTrail.model.quest.QuestCollection;
import com.gpl.rpg.AndorsTrail.resource.TileStore;

public class WorldContext {
	//Objectcollections
	public final ConversationCollection conversations;
	public final ItemTypeCollection itemTypes;
	public final MonsterTypeCollection monsterTypes;
	public final VisualEffectCollection visualEffectTypes;
	public final DropListCollection dropLists;
	public final QuestCollection quests;
	public final ActorConditionTypeCollection actorConditionsTypes;

	//Objectcollections
	public final TileStore tileStore;

	//Model
	public final MapCollection maps;
	public ModelContainer model;
	
	public WorldContext() {
		this.conversations = new ConversationCollection();
		this.itemTypes = new ItemTypeCollection();
		this.monsterTypes = new MonsterTypeCollection();
		this.visualEffectTypes = new VisualEffectCollection();
		this.dropLists = new DropListCollection();
		this.tileStore = new TileStore();
		this.maps = new MapCollection();
		this.quests = new QuestCollection();
		this.actorConditionsTypes = new ActorConditionTypeCollection();
	}
	public WorldContext(WorldContext copy) {
		this.conversations = copy.conversations;
		this.itemTypes = copy.itemTypes;
		this.monsterTypes = copy.monsterTypes;
		this.visualEffectTypes = copy.visualEffectTypes;
		this.dropLists = copy.dropLists;
		this.tileStore = copy.tileStore;
		this.maps = copy.maps;
		this.quests = copy.quests;
		this.model = copy.model;
		this.actorConditionsTypes = copy.actorConditionsTypes;
	}
	public void reset() {
		maps.reset();
	}
	
	// Selftest method. Not part of the game logic.
	public void verifyData() {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			//Ensure that all phrases that require an item have some droplist that contains them
			conversations.verifyData(dropLists);
			
			//Ensure that all phrases are requested at least once, either by NPCs, mapobjects or by other phrases.
			conversations.verifyData(monsterTypes, maps);
			
			//Ensure that all required quest stages exist
			conversations.verifyData(quests);

			//Ensure that all quest stages are required and supplied.
			conversations.verifyData(maps);

			//Ensure that all quest stages are reachable by phrases
			quests.verifyData(conversations);
			
			//Ensure that all NPCs that have a trading conversation also have a droplist
			monsterTypes.verifyData(conversations);
			
			//TODO: Ensure that all items have at least one corresponding droplist
			//TODO: Ensure that all droplists are used by monsters
			//TODO: Ensure that all monsters are used in spawnareas

			
			//TODO: check for npcs having shop phrases without droplist
		}
	}
}
