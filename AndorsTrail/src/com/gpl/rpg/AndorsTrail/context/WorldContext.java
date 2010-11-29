package com.gpl.rpg.AndorsTrail.context;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.EffectCollection;
import com.gpl.rpg.AndorsTrail.conversation.ConversationCollection;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.map.MapCollection;
import com.gpl.rpg.AndorsTrail.resource.TileStore;

public class WorldContext {
	//Objectcollections
	public final ConversationCollection conversations;
	public final ItemTypeCollection itemTypes;
	public final MonsterTypeCollection monsterTypes;
	public final EffectCollection effectTypes;
	public final DropListCollection dropLists;

	//Objectcollections
	public final TileStore tileStore;

	//Model
	public final MapCollection maps;
	public ModelContainer model;
	
	public WorldContext() {
		this.conversations = new ConversationCollection();
		this.itemTypes = new ItemTypeCollection();
		this.monsterTypes = new MonsterTypeCollection();
		this.effectTypes = new EffectCollection();
		this.dropLists = new DropListCollection();
		this.tileStore = new TileStore();
		this.maps = new MapCollection();
		//this.model = new ModelContainer();
	}
	public WorldContext(WorldContext copy) {
		this.conversations = copy.conversations;
		this.itemTypes = copy.itemTypes;
		this.monsterTypes = copy.monsterTypes;
		this.effectTypes = copy.effectTypes;
		this.dropLists = copy.dropLists;
		this.tileStore = copy.tileStore;
		this.maps = copy.maps;
		this.model = copy.model;
	}
	public void reset() {
		maps.reset();
	}
	
	// Selftest method. Not part of the game logic.
	public void verifyData() {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			//Ensure that all phrases that require an item have some droplist that contains them
			conversations.verifyData(dropLists);
			
			//Ensure that all phrases are requested at least once, either by NPCs or by other phrases.
			conversations.verifyData(monsterTypes);
			
			//TODO: Ensure that all items have at least one corresponding droplist
			//TODO: Ensure that all droplists are used by monsters
			//TODO: Ensure that all monsters are used in spawnareas
		}
	}
}
