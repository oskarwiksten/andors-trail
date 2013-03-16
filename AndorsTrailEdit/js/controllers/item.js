var ATEditor = (function(ATEditor, model, importExport, settings, ATModelFunctions) {

	function setCategoryToObject(item, itemCategories) {
		if (_.isString(item.category)) {
			item.category = itemCategories.findById(item.category);
		}
	}
	
	function ItemController($scope, $routeParams) {
		$scope.obj = model.items.findById($routeParams.id) || {};
		$scope.itemCategories = model.itemCategories.items;
		setCategoryToObject($scope.obj, model.itemCategories);
		
		$scope.$watch('obj.category', function(val) {
			$scope.isWeapon = ATModelFunctions.itemCategoryFunctions.isWeaponCategory(val);
			$scope.isUsable = ATModelFunctions.itemCategoryFunctions.isUsableCategory(val);
			if (!$scope.isUsable) {
				$scope.obj.hasUseEffect = false;
			}
			$scope.isWearable = ATModelFunctions.itemCategoryFunctions.isWearableCategory(val);
			if (!$scope.isWearable) {
				$scope.obj.hasEquipEffect = false;
				$scope.obj.hasHitEffect = false;
				$scope.obj.hasKillEffect = false;
			}
		});
		$scope.$watch('obj.hasManualPrice', function(hasManualPrice) {
			$scope.obj.baseMarketCost = hasManualPrice ? ATModelFunctions.itemFunctions.calculateItemCost($scope.obj) : 0;
		});
		
		$scope.getItemCost = ATModelFunctions.itemFunctions.getItemCost;
		$scope.getItemSellingCost = ATModelFunctions.itemFunctions.getItemSellingCost;
		$scope.getItemBuyingCost = ATModelFunctions.itemFunctions.getItemBuyingCost;
		
		
		$scope.addCondition = function(list) {
			list.push({magnitude:1});
		};
		$scope.removeCondition = function(list, cond) {
			var idx = list.indexOf(cond);
			list.splice(idx, 1);
		};
	}
	
	function ItemTableController($scope, $routeParams) {
		var section = model.items;
		$scope.items = section.items;
		$scope.itemCategories = model.itemCategories.items;
		_.each($scope.items, function(item) {
			setCategoryToObject(item, model.itemCategories);
		});
		$scope.getItemCost = ATModelFunctions.itemFunctions.getItemCost;
		$scope.edit = function(item) {
			window.location = "#/" + section.id + "/edit/" + item.id;
		};
		$scope.addObj = function() {
			importExport.prepareObjectsForEditor(section, [ section.addNew() ]);
		};
		$scope.updateCost = function(item) {
			item.baseMarketCost = ATModelFunctions.itemFunctions.getItemCost(item);
		};
		
		if (!settings.itemTableEditorVisibleColumns) {
			settings.itemTableEditorVisibleColumns = {
				iconID: true
				,id: true
				,cost: true
			};
		}
		$scope.settings = settings.itemTableEditorVisibleColumns;
	}
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.ItemController = ItemController;
	ATEditor.controllers.ItemTableController = ItemTableController;

	return ATEditor;
})(ATEditor, ATEditor.model, ATEditor.importExport, ATEditor.settings, ATModelFunctions);
