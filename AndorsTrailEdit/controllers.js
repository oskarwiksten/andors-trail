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
	};
	controllers.ItemController = function($scope, $routeParams) {
		$scope.datasource = model.items;
		$scope.obj = $scope.datasource.findById($routeParams.id);
	};
	controllers.DropListController = function($scope, $routeParams) {
		$scope.datasource = model.droplists;
		$scope.obj = $scope.datasource.findById($routeParams.id);
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
			var avgAttackHP  = attacksPerTurn * div100(v(obj.attackChance)) * avgDamagePotential * (1 + div100(v(obj.criticalSkill)) * v(obj.criticalMultiplier));
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
