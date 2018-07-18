var ATEditor = (function(ATEditor, model, importExport) {

	function ExportController($scope) {
		$scope.sections = model.sections;
		$scope.content = "";
		$scope.selectedSection = $scope.selectedSection || model.items;
		$scope.exportData = function() {
			$scope.content = importExport.exportData($scope.selectedSection);
		};
	};
	
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.ExportController = ExportController;

	return ATEditor;
})(ATEditor, ATEditor.model, ATEditor.importExport);
