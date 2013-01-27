
var model = (function(DataStore, FieldList) {
	
	var model = {
		actorConditions: new DataStore({
			name: 'Actor Conditions'
			,objectTypename: 'actorcondition'
			,iconIDField: 'iconID'
			,newItemTemplate: function() { return {name: "New Condition", id: 'new_condition' }; }
		})
		,quests: new DataStore({
			name: 'Quests'
			,objectTypename: 'quest'
			,newItemTemplate: function() { return {name: "New Quest", id: 'new_quest' }; }
		})
		,items: new DataStore({
			name: 'Items'
			,objectTypename: 'item'
			,iconIDField: 'iconID'
			,newItemTemplate: function() { return {name: "New Item", id: "new_item", category: 'other' }; }
		})
		,droplists: new DataStore({
			name: 'Droplists'
			,objectTypename: 'droplist'
			,nameField: 'id'
			,newItemTemplate: function() { return {id: "new_droplist" }; }
		})
		,dialogue: new DataStore({
			name: 'Dialogue'
			,objectTypename: 'dialogue'
			,nameField: 'id'
			,newItemTemplate: function() { return {id: "new_conversation" }; }
		})
		,monsters: new DataStore({
			name: 'Monsters'
			,objectTypename: 'monster'
			,iconIDField: 'iconID'
			,newItemTemplate: function() { return {id: "new_monster", name: "New Monster", maxAP: 10, attackCost: 5, moveCost: 5 }; }
		})
		,itemCategories: new DataStore({
			name: 'Item Categories'
			,objectTypename: 'itemcategory'
			,newItemTemplate: function() { return {id: "new_itemtype", name: 'ItemType' }; }
		})
	};
	
	model.sections = [ model.actorConditions, model.quests, model.items, model.droplists, model.dialogue, model.monsters, model.itemCategories ];
	
	
	function addExampleModelItems(model) {
		model.actorConditions.add({id: "bless", name: "Bless", isPositive: true, iconID: "actorconditions_1:38", category: 0, hasAbilityEffect: 1, attackChance: 15, blockChance: 5});
		model.actorConditions.add({id: "poison_weak", name: "Weak Poison", iconID: "actorconditions_1:60", category: 3, hasRoundEffect: 1, round_visualEffectID: 2, round_boostHP_Min: -1, round_boostHP_Max: -1});

		model.quests.add({id: "testQuest", name: "Test quest", stages: [ { progress: 10, logText: "Stage 10"} , { progress: 20, logText: "Stage 20", finishesQuest: 1 } ] });

		model.items.add({id: "item0", iconID: "items_weapons:0", name: "Longsword", category: 'lsword', baseMarketCost: 51, hasEquipEffect: 1, equip_attackChance: 10, equip_attackDamage_Min: 2, equip_attackDamage_Max: 4, equip_attackCost: 4});
		model.items.add({id: "dmg_ring1", iconID: "items_jewelry:0", name: "Ring of damage +1", category: 'ring', baseMarketCost: 62, hasEquipEffect: 1, equip_attackDamage_Min: 1, equip_attackDamage_Max: 1});

		model.droplists.add({id: "merchant1", items: [ { itemID: 'dmg_ring1', quantity_Min: 4, quantity_Max: 5, chance: 100 } , { itemID: 'item0', quantity_Min: 1, quantity_Max: 1, chance: 100 } ] } );

		model.dialogue.add({id: "mikhail_default", message: 'Anything else I can help you with?', replies: [ { text: 'Do you have any tasks for me?', nextPhraseID: 'mikhail_tasks' }, { text: 'Is there anything else you can tell me about Andor?', nextPhraseID: 'mikhail_andor1' } ]});
		model.dialogue.add({id: 'mikhail_andor1', message: 'As I said, Andor went out yesterday and hasn\'t been back since. I\'m starting to worry about him. Please go look for your brother, he said he would only be out a short while.'});
		model.dialogue.add({id: 'mikhail_tasks', message: 'Oh yes, there were some things I need help with, bread and rats. Which one would you like to talk about?'});

		model.monsters.add({id: "small_ant", name: "Small ant", iconID: "monsters_insects:2", maxHP: 30, size: ''});
		model.monsters.add({id: "red_ant", name: "Red ant", iconID: "monsters_insects:3", maxHP: 20, size: ''});
		model.monsters.add({id: "wasp", name: "Wasp", iconID: "monsters_insects:1", maxHP: 10, size: ''});
	}
	addExampleModelItems(model);
	
	function addLegacyFieldLists(model) {
		model.actorConditions.legacyFieldList = new FieldList("[id|name|iconID|category|isStacking|isPositive|"
				+ "hasRoundEffect|round_visualEffectID|round_boostHP_Min|round_boostHP_Max|round_boostAP_Min|round_boostAP_Max|"
				+ "hasFullRoundEffect|fullround_visualEffectID|fullround_boostHP_Min|fullround_boostHP_Max|fullround_boostAP_Min|fullround_boostAP_Max|"
				+ "hasAbilityEffect|boostMaxHP|boostMaxAP|moveCostPenalty|attackCost|attackChance|criticalChance|criticalMultiplier|attackDamage_Min|attackDamage_Max|blockChance|damageResistance|"
				+ "];"
			);
		model.quests.legacyFieldList = new FieldList("[id|name|showInLog|stages[progress|logText|rewardExperience|finishesQuest|]|];");
		model.items.legacyFieldList = new FieldList("[id|iconID|name|category|displaytype|hasManualPrice|baseMarketCost|"
				+ "hasEquipEffect|equip_boostMaxHP|equip_boostMaxAP|equip_moveCostPenalty|equip_attackCost|equip_attackChance|equip_criticalChance|equip_criticalMultiplier|equip_attackDamage_Min|equip_attackDamage_Max|equip_blockChance|equip_damageResistance|equip_conditions[condition|magnitude|]|"
				+ "hasUseEffect|use_boostHP_Min|use_boostHP_Max|use_boostAP_Min|use_boostAP_Max|use_conditionsSource[condition|magnitude|duration|chance|]|"
				+ "hasHitEffect|hit_boostHP_Min|hit_boostHP_Max|hit_boostAP_Min|hit_boostAP_Max|hit_conditionsSource[condition|magnitude|duration|chance|]|hit_conditionsTarget[condition|magnitude|duration|chance|]|"
				+ "hasKillEffect|kill_boostHP_Min|kill_boostHP_Max|kill_boostAP_Min|kill_boostAP_Max|kill_conditionsSource[condition|magnitude|duration|chance|]|"
				+ "];"
			);
		model.droplists.legacyFieldList = new FieldList("[id|items[itemID|quantity_Min|quantity_Max|chance|]|];");
		model.dialogue.legacyFieldList = new FieldList("[id|message|rewards[rewardType|rewardID|value|]|replies[text|nextPhraseID|requires_Progress|requires_itemID|requires_Quantity|requires_Type|]|];");
		model.monsters.legacyFieldList = new FieldList("[id|iconID|name|tags|size|monsterClass|unique|faction|maxHP|maxAP|moveCost|attackCost|attackChance|criticalChance|criticalMultiplier|attackDamage_Min|attackDamage_Max|blockChance|damageResistance|droplistID|phraseID|"
				+ "hasHitEffect|onHit_boostHP_Min|onHit_boostHP_Max|onHit_boostAP_Min|onHit_boostAP_Max|onHit_conditionsSource[condition|magnitude|duration|chance|]|onHit_conditionsTarget[condition|magnitude|duration|chance|]|"
				+ "];"
			);
		model.itemCategories.legacyFieldList = new FieldList("[id|name|actionType|inventorySlot|size|];");
	}
	addLegacyFieldLists(model);
	
	
	return model;	
})(DataStore, FieldList.FieldList);
