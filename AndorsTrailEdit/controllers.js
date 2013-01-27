var controllers = (function(model) {

	function NavigationController($scope, $routeParams) {
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
			window.location = "#/" + section.objectTypename + "/edit/" + obj.id;
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
		
	}

	function ActorConditionController($scope, $routeParams) {
		$scope.datasource = model.actorConditions;
		$scope.obj = model.actorConditions.findById($routeParams.id);
	}
	function QuestController($scope, $routeParams) {
		$scope.datasource = model.quests;
		$scope.obj = model.quests.findById($routeParams.id);
	}
	function ItemController($scope, $routeParams) {
		$scope.datasource = model.items;
		$scope.obj = model.items.findById($routeParams.id);
	}
	function DropListController($scope, $routeParams) {
		$scope.datasource = model.droplists;
		$scope.obj = model.droplists.findById($routeParams.id);
	}
	function DialogueController($scope, $routeParams) {
		$scope.datasource = model.dialogue;
		$scope.obj = model.dialogue.findById($routeParams.id);
	}
	function MonsterController($scope, $routeParams) {
		$scope.datasource = model.monsters;
		var m = model.monsters.findById($routeParams.id) || {};
		m.attackDamage = m.attackDamage || {};
		m.hasConversation = m.phraseID;
		m.hasCombatTraits = m.attackChance || m.attackDamage.min || m.criticalSkill || m.criticalMultiplier || m.blockChance || m.damageResistance || m.hitEffect;
		m.hasHitEffect = m.hitEffect;
		m.hitEffect = m.hitEffect || { conditionsSource: [], conditionsTarget: [] };
		$scope.obj = m;
		$scope.getExperience = function(obj) {
			/*
			final float avgAttackHP  = t.getAttacksPerTurn(maxAP) * div100(t.attackChance) * t.damagePotential.averagef() * (1 + div100(t.criticalChance) * t.criticalMultiplier);
			final float avgDefenseHP = maxHP * (1 + div100(t.blockChance)) + Constants.EXP_FACTOR_DAMAGERESISTANCE * t.damageResistance;
			return (int) Math.ceil((avgAttackHP * 3 + avgDefenseHP) * Constants.EXP_FACTOR_SCALING);
			*/
			
			var EXP_FACTOR_DAMAGERESISTANCE = 9;
			var EXP_FACTOR_SCALING = 0.7;
			
			var div100 = function(v) { return v / 100; }
			var v = function(i) { return i ? i : 0; }
			
			var attacksPerTurn = Math.floor(v(obj.maxAP) / v(obj.attackCost));
			var avgDamagePotential = (v(obj.attackDamage.min) + v(obj.attackDamage.max)) / 2;
			var avgAttackHP  = attacksPerTurn * div100(v(obj.attackChance)) * avgDamagePotential * (1 + div100(v(obj.criticalSkill)) * v(obj.criticalMultiplier));
			var avgDefenseHP = v(obj.maxHP) * (1 + div100(v(obj.blockChance))) + EXP_FACTOR_DAMAGERESISTANCE * v(obj.damageResistance);
			var experience = (avgAttackHP * 3 + avgDefenseHP) * EXP_FACTOR_SCALING;
			
			return Math.ceil(experience);
		};
		$scope.recalculateExperience = function() {
			$scope.experience = $scope.getExperience($scope.obj);
		};
		$scope.recalculateExperience();
		$scope.addCondition = function(list) {
			list.push({magnitude:1, duration:1, chance:100});
		};
		$scope.removeCondition = function(list, cond) {
			var idx = list.indexOf(cond);
			list.splice(idx, 1);
		};
	}
	function ItemCategoryController($scope, $routeParams) {
		$scope.datasource = model.itemCategories;
		$scope.obj = model.itemCategories.findById($routeParams.id);
	}
	
	return {
		NavigationController: NavigationController
		,ActorConditionController: ActorConditionController
		,QuestController: QuestController
		,ItemController: ItemController
		,DropListController: DropListController
		,DialogueController: DialogueController
		,MonsterController: MonsterController
		,ItemCategoryController: ItemCategoryController
	};
})(model);
