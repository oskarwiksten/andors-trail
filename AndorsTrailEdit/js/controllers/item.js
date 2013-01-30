var ATEditor = (function(ATEditor, model) {

	function ItemController($scope, $routeParams) {
		$scope.datasource = model.items;
		var obj = $scope.datasource.findById($routeParams.id);
		$scope.obj = obj;
		if (_.isString($scope.obj.category)) {
			$scope.obj.category = model.itemCategories.findById($scope.obj.category);
		}
		$scope.itemCategories = model.itemCategories.items;
		
		$scope.$watch('obj.category', function(val) {
			$scope.isWeapon = _.toBool(val && val.actionType == 2 && val.inventorySlot == 0);
		});
		
		function calculateItemCost(o) {
			var v = function(i) { return i ? i : 0; }
			var sgn = function(v) {
				if (v < 0) return -1;
				else if (v > 0) return 1;
				else return 0;
			}
			
			var averageHPBoost = (v(o.useEffect.increaseCurrentHP.min) + v(o.useEffect.increaseCurrentHP.max)) / 2;
			var costBoostHP = Math.round(0.1*sgn(averageHPBoost)*Math.pow(Math.abs(averageHPBoost), 2) + 3*averageHPBoost);
			var itemUsageCost = costBoostHP;
			
			
			var isWeapon = $scope.isWeapon;
			
			var equip_blockChance = v(obj.equipEffect.increaseBlockChance);
			var equip_attackChance = v(obj.equipEffect.increaseAttackChance);
			var equip_attackCost = v(obj.equipEffect.increaseAttackCost);
			var equip_damageResistance = v(obj.equipEffect.increaseDamageResistance);
			var equip_attackDamage_Min = v(obj.equipEffect.increaseAttackDamage.min);
			var equip_attackDamage_Max = v(obj.equipEffect.increaseAttackDamage.max);
			var equip_criticalChance = v(obj.equipEffect.increaseCriticalSkill);
			var equip_criticalMultiplier = v(obj.equipEffect.setCriticalMultiplier);
			var costBC = Math.round(3*Math.pow(Math.max(0,equip_blockChance), 2.5) + 28*equip_blockChance);
			var costAC = Math.round(0.4*Math.pow(Math.max(0,equip_attackChance), 2.5) - 6*Math.pow(Math.abs(Math.min(0,equip_attackChance)),2.7));
			var costAP = isWeapon ?
					Math.round(0.2*Math.pow(10/equip_attackCost, 8) - 25*equip_attackCost)
					: -3125 * equip_attackCost;
			var costDR = 1325*equip_damageResistance;
			var costDMG_Min = isWeapon ?
					Math.round(10*Math.pow(equip_attackDamage_Min, 2.5))
					:Math.round(10*Math.pow(equip_attackDamage_Min, 3) + equip_attackDamage_Min*80);
			var costDMG_Max = isWeapon ?
					Math.round(2*Math.pow(equip_attackDamage_Max, 2.1))
					:Math.round(2*Math.pow(equip_attackDamage_Max, 3) + equip_attackDamage_Max*20);
			var costCC = Math.round(2.2*Math.pow(equip_criticalChance, 3));
			var costCM = Math.round(50*Math.pow(Math.max(0, equip_criticalMultiplier), 2));
			var costCombat = costBC + costAC + costAP + costDR + costDMG_Min + costDMG_Max + costCC + costCM;
			
			var equip_boostMaxHP = v(obj.equipEffect.increaseMaxHP);
			var equip_boostMaxAP = v(obj.equipEffect.increaseMaxAP);
			var equip_moveCostPenalty = v(obj.equipEffect.increaseMoveCost);
			var costMaxHP = Math.round(30*Math.pow(Math.max(0,equip_boostMaxHP), 1.2) + 70*equip_boostMaxHP);
			var costMaxAP = Math.round(50*Math.pow(Math.max(0,equip_boostMaxAP), 3) + 750*equip_boostMaxAP);
			var costMovement = Math.round(510*Math.pow(Math.max(0,-equip_moveCostPenalty), 2.5) - 350*equip_moveCostPenalty);
			var itemEquipCost = costCombat + costMaxHP + costMaxAP + costMovement;
			
			if (!obj.hasEquipEffect) { itemEquipCost = 0; }
			if (!obj.hasUseEffect) { itemUsageCost = 0; }
			
			return itemEquipCost + itemUsageCost;
		}
		
		$scope.recalculateStorePrice = function() {
			var val = parseInt(obj.baseMarketCost);
			if (obj.hasManualPrice === "0") {
				val = calculateItemCost(obj);
				if (val <= 0) { val = 1; }
				obj.baseMarketCost = val;
			}
			$scope.marketCost_Sell = Math.round(val * (100 + 15) / 100);
			$scope.marketCost_Buy = Math.round(val * (100 - 15) / 100);
		};
		$scope.recalculateStorePrice();
		$scope.$watch('obj.useEffect.increaseCurrentHP.min', $scope.recalculateStorePrice);
		$scope.$watch('obj.useEffect.increaseCurrentHP.max', $scope.recalculateStorePrice);
		$scope.$watch('obj.category', $scope.recalculateStorePrice);
		$scope.$watch('obj.equipEffect.increaseBlockChance', $scope.recalculateStorePrice);
		$scope.$watch('obj.equipEffect.increaseAttackChance', $scope.recalculateStorePrice);
		$scope.$watch('obj.equipEffect.increaseAttackCost', $scope.recalculateStorePrice);
		$scope.$watch('obj.equipEffect.increaseDamageResistance', $scope.recalculateStorePrice);
		$scope.$watch('obj.equipEffect.increaseAttackDamage.min', $scope.recalculateStorePrice);
		$scope.$watch('obj.equipEffect.increaseCriticalSkill', $scope.recalculateStorePrice);
		$scope.$watch('obj.equipEffect.setCriticalMultiplier', $scope.recalculateStorePrice);
		$scope.$watch('obj.equipEffect.increaseMaxHP', $scope.recalculateStorePrice);
		$scope.$watch('obj.equipEffect.increaseMaxAP', $scope.recalculateStorePrice);
		$scope.$watch('obj.equipEffect.increaseMoveCost', $scope.recalculateStorePrice);
		$scope.$watch('obj.hasEquipEffect', $scope.recalculateStorePrice);
		$scope.$watch('obj.hasUseEffect', $scope.recalculateStorePrice);
		$scope.$watch('obj.baseMarketCost', $scope.recalculateStorePrice);
		$scope.$watch('obj.hasManualPrice', $scope.recalculateStorePrice);
		
		$scope.addCondition = function(list) {
			list.push({magnitude:1});
		};
		$scope.removeCondition = function(list, cond) {
			var idx = list.indexOf(cond);
			list.splice(idx, 1);
		};
	};
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.ItemController = ItemController;

	return ATEditor;
})(ATEditor, ATEditor.model);
