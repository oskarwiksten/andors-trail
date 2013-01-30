var ATEditor = (function(ATEditor, model) {

	function ActorConditionController($scope, $routeParams) {
		$scope.datasource = model.actorConditions;
		$scope.obj = $scope.datasource.findById($routeParams.id);
	};
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.ActorConditionController = ActorConditionController;

	return ATEditor;
})(ATEditor, ATEditor.model);
