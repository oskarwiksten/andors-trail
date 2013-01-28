var ATEditor = (function(ATEditor, DataStore, FieldList, _) {
	
	var model = {
		actorConditions: new DataStore({
			name: 'Actor Conditions'
			,id: 'actorcondition'
			,iconIDField: 'iconID'
			,newItemTemplate: function() { return {name: "New Condition", id: 'new_condition' }; }
		})
		,quests: new DataStore({
			name: 'Quests'
			,id: 'quest'
			,newItemTemplate: function() { return {name: "New Quest", id: 'new_quest' }; }
		})
		,items: new DataStore({
			name: 'Items'
			,id: 'item'
			,iconIDField: 'iconID'
			,newItemTemplate: function() { return {name: "New Item", id: "new_item", category: 'other' }; }
		})
		,droplists: new DataStore({
			name: 'Droplists'
			,id: 'droplist'
			,nameField: 'id'
			,newItemTemplate: function() { return {id: "new_droplist" }; }
		})
		,dialogue: new DataStore({
			name: 'Dialogue'
			,id: 'dialogue'
			,nameField: 'id'
			,newItemTemplate: function() { return {id: "new_conversation" }; }
		})
		,monsters: new DataStore({
			name: 'Monsters'
			,id: 'monster'
			,iconIDField: 'iconID'
			,newItemTemplate: function() { return {id: "new_monster", name: "New Monster", maxAP: 10, attackCost: 5, moveCost: 5 }; }
		})
		,itemCategories: new DataStore({
			name: 'Item Categories'
			,id: 'itemcategory'
			,newItemTemplate: function() { return {id: "new_itemtype", name: 'ItemType' }; }
		})
	};
	
	var sections = [];
	var sectionIds = {};
	for (var key in model) {
		var ds = model[key];
		sections.push(ds);
		sectionIds[ds.id] = ds;
	}
	model.sections = sections;
	model.getSectionFromID = function(id) { return sectionIds[id]; };
	
	function addExampleModelItems(model) {
		var _import = function(section, data) {
			var _void = function() {};
			ATEditor.importExport.importDataObjects(section, data, _void, _void);
		};
		
		_import(model.actorConditions, [
			{id: "bless", name: "Bless", isPositive: true, iconID: "actorconditions_1:38", category: 0, hasAbilityEffect: 1, attackChance: 15, blockChance: 5}
			,{id: "poison_weak", name: "Weak Poison", iconID: "actorconditions_1:60", category: 3, hasRoundEffect: 1, round_visualEffectID: 2, round_boostHP_Min: -1, round_boostHP_Max: -1}
			]);

		_import(model.quests, [
			{id: "testQuest", name: "Test quest", stages: [ { progress: 10, logText: "Stage 10"} , { progress: 20, logText: "Stage 20", finishesQuest: 1 } ] }
			]);

		_import(model.items, [
			{id: "item0", iconID: "items_weapons:0", name: "Longsword", category: 'lsword', baseMarketCost: 51, hasEquipEffect: 1, equip_attackChance: 10, equip_attackDamage_Min: 2, equip_attackDamage_Max: 4, equip_attackCost: 4}
			,{id: "dmg_ring1", iconID: "items_jewelry:0", name: "Ring of damage +1", category: 'ring', baseMarketCost: 62, hasEquipEffect: 1, equip_attackDamage_Min: 1, equip_attackDamage_Max: 1}
			]);

		_import(model.droplists, [
			{id: "merchant1", items: [ { itemID: 'dmg_ring1', quantity_Min: 4, quantity_Max: 5, chance: 100 } , { itemID: 'item0', quantity_Min: 1, quantity_Max: 1, chance: 100 } ] }
			]);

		_import(model.dialogue, [
			{id: "mikhail_default", message: 'Anything else I can help you with?', replies: [ { text: 'Do you have any tasks for me?', nextPhraseID: 'mikhail_tasks' }, { text: 'Is there anything else you can tell me about Andor?', nextPhraseID: 'mikhail_andor1' } ]}
			,{id: 'mikhail_andor1', message: 'As I said, Andor went out yesterday and hasn\'t been back since. I\'m starting to worry about him. Please go look for your brother, he said he would only be out a short while.'}
			,{id: 'mikhail_tasks', message: 'Oh yes, there were some things I need help with, bread and rats. Which one would you like to talk about?'}
			]);

		_import(model.monsters, [
			{id: "small_ant", name: "Small ant", iconID: "monsters_insects:2", maxHP: 30 }
			,{id: "red_ant", name: "Red ant", iconID: "monsters_insects:3", maxHP: 20 }
			,{id: "wasp", name: "Wasp", iconID: "monsters_insects:1", maxHP: 10 }
			]);
	}
	addExampleModelItems(model);
	
	ATEditor.model = model;
	return ATEditor;	
})(ATEditor, ATEditor.DataStore, ATEditor.FieldList, _);
