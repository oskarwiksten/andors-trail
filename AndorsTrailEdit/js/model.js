var ATEditor = (function(ATEditor, DataStore, FieldList, _) {
	
	var model = {
		actorConditions: new DataStore({
			name: 'Actor Conditions'
			,id: 'actorcondition'
			,iconIDField: 'iconID'
		})
		,quests: new DataStore({
			name: 'Quests'
			,id: 'quest'
		})
		,items: new DataStore({
			name: 'Items'
			,id: 'item'
			,iconIDField: 'iconID'
		})
		,droplists: new DataStore({
			name: 'Droplists'
			,id: 'droplist'
			,nameField: 'id'
		})
		,dialogue: new DataStore({
			name: 'Dialogue'
			,id: 'dialogue'
			,nameField: 'id'
		})
		,monsters: new DataStore({
			name: 'Monsters'
			,id: 'monster'
			,iconIDField: 'iconID'
		})
		,itemCategories: new DataStore({
			name: 'Item Categories'
			,id: 'itemcategory'
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
