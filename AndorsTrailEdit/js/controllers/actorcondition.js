var ATEditor = (function(ATEditor, model) {

	function ActorConditionController($scope, $routeParams) {
		$scope.datasource = model.actorConditions;
		$scope.obj = $scope.datasource.findById($routeParams.id);
		$scope.previous = ATEditor.navigationFunctions.editByIndexOffset($scope.datasource, $scope.obj, -1);
		$scope.next = ATEditor.navigationFunctions.editByIndexOffset($scope.datasource, $scope.obj, 1);
	};
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.ActorConditionController = ActorConditionController;

	return ATEditor;
})(ATEditor, ATEditor.model);
