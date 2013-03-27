var ATEditor = (function(ATEditor, model, importExport, settings, ATModelFunctions) {

	function setCategoryToObject(item, itemCategories) {
		if (_.isString(item.category)) {
			item.category = itemCategories.findById(item.category);
		}
	}
	
	function setItemPriceSuggestion(item) {
		if (item.hasManualPrice == 1) {
			item.baseMarketCost = ATModelFunctions.itemFunctions.calculateItemCost(item);
		} else {
			item.baseMarketCost = 0;
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
		
		$scope.updateCost = setItemPriceSuggestion;
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
		
		$scope.previous = ATEditor.navigationFunctions.editByIndexOffset(model.items, $scope.obj, -1);
		$scope.next = ATEditor.navigationFunctions.editByIndexOffset(model.items, $scope.obj, 1);
	}
	
	function ItemTableController($scope, $routeParams) {
		var section = model.items;
		$scope.items = section.items;
		$scope.itemCategories = model.itemCategories.items;
		_.each($scope.items, function(item) {
			setCategoryToObject(item, model.itemCategories);
		});
		$scope.edit = function(item) {
			ATEditor.navigationFunctions.editObj(section, item);
		};
		$scope.addObj = function() {
			importExport.prepareObjectsForEditor(section, [ section.addNew() ]);
		};
		$scope.updateCost = setItemPriceSuggestion;
		$scope.getItemCost = ATModelFunctions.itemFunctions.getItemCost;
		
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
