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
	
	ATEditor.model = ATEditor.model || model;
	return ATEditor;	
})(ATEditor, ATEditor.DataStore, ATEditor.FieldList, _);
