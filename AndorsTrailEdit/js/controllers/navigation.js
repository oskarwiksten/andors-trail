var ATEditor = (function(ATEditor, model, importExport, exampleData) {

	function NavigationController($scope, $routeParams, $http) {
		$scope.sections = model.sections;
		$scope.previousItems = [];
		
		$scope.editObj = function(section, obj) {
			$scope.previousItems = _.reject($scope.previousItems, function(i) {
				return (i.section === section) && (i.obj === obj);
			});
			$scope.previousItems.unshift({section: section, obj: obj});
			if ($scope.previousItems.length > 5) {
				$scope.previousItems.pop();
			}
			window.location = "#/" + section.id + "/edit/" + obj.id;
		};
		$scope.addObj = function(section) {
			var item = section.addNew();
			importExport.prepareObjectsForEditor(section, [ item ]);
			$scope.editObj(section, item);
		};
		$scope.clear = function(section) {
			if(!confirm("Are you sure you want to clear all " + section.name + " ?")) return;
			section.clear();
		};
		$scope.getName = function(section, obj) {
			return section.getName(obj);
		}
		$scope.delObj = function(section, obj) {
			if(!confirm("Are you sure you want to remove " + section.getName(obj) + " ?")) return;
			this.destroy(function() {
				section.remove(obj);
			});
		};
		$scope.dupObj = function(section, obj) {
			var item = section.clone(obj);
			$scope.editObj(section, item);
		};
		
		exampleData.init($http);
	};
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.NavigationController = NavigationController;
		
	return ATEditor;
})(ATEditor, ATEditor.model, ATEditor.importExport, ATEditor.exampleData);
