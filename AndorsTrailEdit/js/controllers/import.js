var ATEditor = (function(ATEditor, model, importExport, exampleData) {

	function ImportController($scope) {
		$scope.sections = model.sections;
		$scope.content = "";
		$scope.selectedSection = $scope.selectedSection || model.items;
		$scope.importType = $scope.importType || 'paste';
		$scope.availableFiles = exampleData.resources;
		
		var countBefore = 0;
		function success() {
			var section = $scope.selectedSection;
			var countAfter = section.items.length;
			$scope.importedMsg = "Imported " + (countAfter - countBefore) + " " + section.name;
		}
		function error(msg) {
			$scope.errorMsg = "Error importing data: " + msg;
		}
		$scope.importPastedData = function() {
			$scope.errorMsg = "";
			$scope.importedMsg = "";
			var section = $scope.selectedSection;
			countBefore = section.items.length;
			importExport.importText(section, $scope.content, success, error);
		};
		$scope.importExistingData = function() {
			$scope.errorMsg = "";
			$scope.importedMsg = "";
			var section = $scope.selectedSection;
			countBefore = section.items.length;
			exampleData.loadUrlFromGit("AndorsTrail/res/raw/" + $scope.selectedFile, function(data) {
				importExport.importDataObjects(section, data, success, error);
			});
		};
	};
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.ImportController = ImportController;

	return ATEditor;
})(ATEditor, ATEditor.model, ATEditor.importExport, ATEditor.exampleData);
