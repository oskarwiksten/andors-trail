var ATEditor = (function(ATEditor, tilesets) {

	function SelectIconController($scope, $routeParams) {
		var _callback = function() {};
		$scope.selectedSection = '';
		$scope.sections = [];
		_.each([ 'monster', 'item', 'actorcondition' ], function(id) {
			$scope.sections.push({id: id, imageIDs: tilesets.getIconIDsForSection(id)});
		});
		
		this.startSelecting = function(sectionId, callback) {
			_callback = callback;
			$scope.selectedSection = sectionId;
			$scope.$apply();
		};
		
		$scope.imageSelected = function(iconID) {
			if (_callback) { _callback(iconID); }
		};
	};
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.SelectIconController = SelectIconController;

	return ATEditor;
})(ATEditor, ATEditor.tilesets);
