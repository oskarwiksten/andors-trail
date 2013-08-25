var ATEditor = (function(ATEditor, model, importExport, exampleData) {

	function editObjId(section, objId) {
		window.location = "#/" + section.id + "/edit/" + objId;
	}
	function editObj(section, obj) {
		editObjId(section, obj.id);
	}
	
	function editByIndexOffset(section, obj, offset) {
		return function() {
			var nextObj = section.findByIndexOffset(obj, offset);
			if (!nextObj) { return; }
			editObj(section, nextObj);
		};
	}
	
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
			editObj(section, obj);
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
		$scope.supportsTableEdit = function(section) {
			if (section.id == ATEditor.model.monsters.id) { return true; }
			if (section.id == ATEditor.model.items.id) { return true; }
			return false;
		};
		$scope.editAsTable = function(section) {
			window.location = "#/" + section.id + "/table";
		};
		
		
		exampleData.init($http);
	};
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.NavigationController = NavigationController;
	ATEditor.navigationFunctions = {
		editObj: editObj
		,editObjId: editObjId
		,editByIndexOffset: editByIndexOffset
	};
		
	return ATEditor;
})(ATEditor, ATEditor.model, ATEditor.importExport, ATEditor.exampleData);
