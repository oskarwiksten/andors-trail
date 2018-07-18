var ATEditor = (function(ATEditor, model) {

	function QuestController($scope, $routeParams) {
		$scope.datasource = model.quests;
		$scope.obj = $scope.datasource.findById($routeParams.id);
		$scope.addQuestStage = function() {
			$scope.obj.stages.push({});
		};
		$scope.removeQuestStage = function(stage) {
			var idx = $scope.obj.stages.indexOf(stage);
			$scope.obj.stages.splice(idx, 1);
		};
	};
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.QuestController = QuestController;

	return ATEditor;
})(ATEditor, ATEditor.model);
