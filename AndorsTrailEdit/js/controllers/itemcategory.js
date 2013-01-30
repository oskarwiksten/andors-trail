var ATEditor = (function(ATEditor, model) {

	function ItemCategoryController($scope, $routeParams) {
		$scope.datasource = model.itemCategories;
		$scope.obj = $scope.datasource.findById($routeParams.id);
	};
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.ItemCategoryController = ItemCategoryController;

	return ATEditor;
})(ATEditor, ATEditor.model);
