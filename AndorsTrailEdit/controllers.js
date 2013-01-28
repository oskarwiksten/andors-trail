var ATEditor = (function(ATEditor, model, importExport) {

	var controllers = {};
	
	controllers.NavigationController = function($scope, $routeParams) {
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
			$scope.editObj(section, item);
		};
		$scope.clear = function(section) {
			if(!confirm("Are you sure you want to clear all " + section.name + " ?")) return;
			section.items = [];
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
		
	};

	controllers.ActorConditionController = function($scope, $routeParams) {
		$scope.datasource = model.actorConditions;
		$scope.obj = $scope.datasource.findById($routeParams.id);
	};
	controllers.QuestController = function($scope, $routeParams) {
		$scope.datasource = model.quests;
		$scope.obj = $scope.datasource.findById($routeParams.id);
		$scope.addQuestStage = function() {
			$scope.obj.stages.push({});
		};
		$scope.removeQuestStage = function(stage) {
			var idx = $scope.obj.stages.indexOf(stage);
			$scope.obj.stages.splice(idx, 1);
		};
	};
	controllers.ItemController = function($scope, $routeParams) {
		$scope.datasource = model.items;
		var obj = $scope.datasource.findById($routeParams.id);
		$scope.obj = obj;
		if (_.isString($scope.obj.category)) {
			$scope.obj.category = model.itemCategories.findById($scope.obj.category);
		}
		$scope.itemCategories = model.itemCategories.items;
		
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
			
			
			var isWeapon = false;
			if ($scope.obj.category) {
				isWeapon = $scope.obj.category.inventorySlot == 0;
			}
			
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
			if (!obj.equipEffect.hasCritical) {
				costCC = 0;
				costCM = 0;
			}
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
		$scope.$watch('obj.equipEffect.hasCritical', $scope.recalculateStorePrice);
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
	controllers.DropListController = function($scope, $routeParams) {
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
	controllers.DialogueController = function($scope, $routeParams) {
		$scope.datasource = model.dialogue;
		$scope.obj = $scope.datasource.findById($routeParams.id);
	};
	controllers.MonsterController = function($scope, $routeParams) {
		$scope.datasource = model.monsters;
		var m = $scope.datasource.findById($routeParams.id) || {};
		$scope.obj = m;
		$scope.getExperience = function(obj) {
			var EXP_FACTOR_DAMAGERESISTANCE = 9;
			var EXP_FACTOR_SCALING = 0.7;
			
			var div100 = function(v) { return v / 100; }
			var v = function(i) { return i ? i : 0; }
			
			var attacksPerTurn = Math.floor(v(obj.maxAP) / v(obj.attackCost));
			var avgDamagePotential = 0;
			if (obj.attackDamage) { avgDamagePotential = (v(obj.attackDamage.min) + v(obj.attackDamage.max)) / 2; }
			var avgCrit = 0;
			if (obj.hasCritical) {
				avgCrit = div100(v(obj.criticalSkill)) * v(obj.criticalMultiplier);
			}
			var avgAttackHP  = attacksPerTurn * div100(v(obj.attackChance)) * avgDamagePotential * (1 + avgCrit);
			var avgDefenseHP = v(obj.maxHP) * (1 + div100(v(obj.blockChance))) + EXP_FACTOR_DAMAGERESISTANCE * v(obj.damageResistance);
			var attackConditionBonus = 0;
			if (obj.hitEffect && obj.hitEffect.conditionsTarget && v(obj.hitEffect.conditionsTarget.length) > 0) {
				attackConditionBonus = 50;
			}
			var experience = (avgAttackHP * 3 + avgDefenseHP) * EXP_FACTOR_SCALING + attackConditionBonus;
			
			return Math.ceil(experience);
		};
		$scope.recalculateExperience = function() {
			$scope.experience = $scope.getExperience($scope.obj);
		};
		$scope.recalculateExperience();
		$scope.$watch('obj.maxAP', $scope.recalculateExperience);
		$scope.$watch('obj.attackCost', $scope.recalculateExperience);
		$scope.$watch('obj.attackDamage.min', $scope.recalculateExperience);
		$scope.$watch('obj.attackDamage.max', $scope.recalculateExperience);
		$scope.$watch('obj.attackChance', $scope.recalculateExperience);
		$scope.$watch('obj.hasCritical', $scope.recalculateExperience);
		$scope.$watch('obj.criticalSkill', $scope.recalculateExperience);
		$scope.$watch('obj.criticalMultiplier', $scope.recalculateExperience);
		$scope.$watch('obj.maxHP', $scope.recalculateExperience);
		$scope.$watch('obj.blockChance', $scope.recalculateExperience);
		$scope.$watch('obj.damageResistance', $scope.recalculateExperience);
		$scope.$watch('obj.hitEffect.conditionsTarget.length', $scope.recalculateExperience);
		$scope.addCondition = function(list) {
			list.push({magnitude:1, duration:1, chance:100});
		};
		$scope.removeCondition = function(list, cond) {
			var idx = list.indexOf(cond);
			list.splice(idx, 1);
		};
	};
	controllers.ItemCategoryController = function($scope, $routeParams) {
		$scope.datasource = model.itemCategories;
		$scope.obj = $scope.datasource.findById($routeParams.id);
	};
	
	String.prototype.trim = String.prototype.trim || (function(){return this.replace(/^\s+|\s+$/g, '');});
	
	controllers.ImportController = function($scope) {
		$scope.sections = model.sections;
		$scope.content = "";
		$scope.selectedSection = $scope.selectedSection || model.items;
		
		$scope.importData = function() {
			$scope.errorMsg = "";
			$scope.importedMsg = "";
			
			var section = $scope.selectedSection;
			var countBefore = section.items.length;
			function success() {
				var countAfter = section.items.length;
				$scope.importedMsg = "Imported " + (countAfter - countBefore) + " " + section.name;
			}
			function error(msg) {
				$scope.errorMsg = "Error importing data: " + msg;
			}
			importExport.importData(section, $scope.content, success, error);
		};
	};
	controllers.ExportController = function($scope) {
		$scope.sections = model.sections;
		$scope.content = "";
		$scope.selectedSection = $scope.selectedSection || model.items;
		$scope.exportData = function() {
			$scope.content = importExport.exportData($scope.selectedSection);
		};
	};
	
	ATEditor.controllers = controllers;
	return ATEditor;
})(ATEditor, ATEditor.model, ATEditor.importExport);
