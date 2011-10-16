package com.gpl.rpg.AndorsTrail.context;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.VisualEffectCollection;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.conversation.ConversationLoader;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.map.MapCollection;
import com.gpl.rpg.AndorsTrail.model.quest.QuestCollection;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;

public class WorldContext {
	//Objectcollections
	//public final ConversationCollection conversations;
	public final ConversationLoader conversationLoader;
	public final ItemTypeCollection itemTypes;
	public final MonsterTypeCollection monsterTypes;
	public final VisualEffectCollection visualEffectTypes;
	public final DropListCollection dropLists;
	public final QuestCollection quests;
	public final ActorConditionTypeCollection actorConditionsTypes;
	public final SkillCollection skills;

	//Objectcollections
	public final TileManager tileManager;

	//Model
	public final MapCollection maps;
	public ModelContainer model;
	
	public WorldContext() {
		this.conversationLoader = new ConversationLoader();
		this.itemTypes = new ItemTypeCollection();
		this.monsterTypes = new MonsterTypeCollection();
		this.visualEffectTypes = new VisualEffectCollection();
		this.dropLists = new DropListCollection();
		this.tileManager = new TileManager();
		this.maps = new MapCollection();
		this.quests = new QuestCollection();
		this.actorConditionsTypes = new ActorConditionTypeCollection();
		this.skills = new SkillCollection();
	}
	public WorldContext(WorldContext copy) {
		this.conversationLoader = copy.conversationLoader;
		this.itemTypes = copy.itemTypes;
		this.monsterTypes = copy.monsterTypes;
		this.visualEffectTypes = copy.visualEffectTypes;
		this.dropLists = copy.dropLists;
		this.tileManager = copy.tileManager;
		this.maps = copy.maps;
		this.quests = copy.quests;
		this.model = copy.model;
		this.actorConditionsTypes = copy.actorConditionsTypes;
		this.skills = copy.skills;
	}
	public void reset() {
		maps.reset();
	}
	
	// Selftest method. Not part of the game logic.
	public void verifyData(ConversationCollection conversations) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			assert(itemTypes.getItemType("gold") != null);

	        //Ensure that all phrases that require an item have some droplist that contains them
			conversations.verifyData(dropLists);
			
			//Ensure that all phrases are requested at least once, either by NPCs, mapobjects or by other phrases.
			conversations.verifyData(monsterTypes, maps);
			
			//Ensure that all required quest stages exist
			conversations.verifyData(quests);

			//Ensure that all quest stages are required and supplied.
			conversations.verifyData(maps);
			
			//Ensure that all conversations that require quest items have quest progress updates
			conversations.verifyData(itemTypes);

			//Ensure that all quest stages are reachable by phrases
			quests.verifyData(conversations);
			
			//Ensure that all NPCs that have a trading conversation also have a droplist
			monsterTypes.verifyData(conversations);
			
			//Ensure that all items have at least one corresponding droplist
			itemTypes.verifyData(dropLists);
			
			//Ensure that all droplists are used by monsters
			dropLists.verifyData(monsterTypes, conversations, maps);
			
			//Ensure that all monsters are used in spawnareas
			monsterTypes.verifyData(maps);

		}
	}
}
