var ATEditor = (function(ATEditor, model) {

	function DropListController($scope, $routeParams) {
		$scope.datasource = model.droplists;
		$scope.obj = $scope.datasource.findById($routeParams.id);
		$scope.addDropItem = function() {
			$scope.obj.items.push({quantity: {}});
		};
		$scope.removeDropItem = function(dropItem) {
			var idx = $scope.obj.items.indexOf(dropItem);
			$scope.obj.items.splice(idx, 1);
		};
	};
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.DropListController = DropListController;

	return ATEditor;
})(ATEditor, ATEditor.model);
