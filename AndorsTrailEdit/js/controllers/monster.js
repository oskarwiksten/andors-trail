var ATEditor = (function(ATEditor, model, importExport, settings, ATModelFunctions) {

	function MonsterController($scope, $routeParams) {
		$scope.obj = model.monsters.findById($routeParams.id) || {};
		$scope.getExperience = function() {
			return ATModelFunctions.monsterFunctions.getMonsterExperience($scope.obj);
		};
		
		$scope.addCondition = function(list) {
			list.push({magnitude:1, duration:1, chance:100});
		};
		$scope.removeCondition = function(list, cond) {
			var idx = list.indexOf(cond);
			list.splice(idx, 1);
		};
	}
	
	function MonsterTableController($scope, $routeParams) {
		var section = model.monsters;
		$scope.monsters = section.items;
		$scope.getExperience = ATModelFunctions.monsterFunctions.getMonsterExperience;
		$scope.edit = function(monster) {
			window.location = "#/" + section.id + "/edit/" + monster.id;
		};
		$scope.addObj = function() {
			importExport.prepareObjectsForEditor(section, [ section.addNew() ]);
		};
		
		if (!settings.monsterTableEditorVisibleColumns) {
			settings.monsterTableEditorVisibleColumns = {
				iconID: true
				,id: true
				,experience: true
			};
		}
		$scope.settings = settings.monsterTableEditorVisibleColumns;
	}
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.MonsterController = MonsterController;
	ATEditor.controllers.MonsterTableController = MonsterTableController;

	return ATEditor;
})(ATEditor, ATEditor.model, ATEditor.importExport, ATEditor.settings, ATModelFunctions);
