var ATEditor = (function(ATEditor, model) {

	function getExperience(obj) {
		var EXP_FACTOR_DAMAGERESISTANCE = 9;
		var EXP_FACTOR_SCALING = 0.7;
		
		var div100 = function(v) { return v / 100; }
		var v = function(i) { return i ? parseFloat(i) : 0; }
		
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
	
	function MonsterController($scope, $routeParams) {
		$scope.obj = model.monsters.findById($routeParams.id) || {};
		$scope.getExperience = function() { return getExperience($scope.obj); }
		
		$scope.addCondition = function(list) {
			list.push({magnitude:1, duration:1, chance:100});
		};
		$scope.removeCondition = function(list, cond) {
			var idx = list.indexOf(cond);
			list.splice(idx, 1);
		};
	}
	
	function MonsterTableController($scope, $routeParams) {
		$scope.monsters = model.monsters.items;
		$scope.getExperience = getExperience;
		$scope.edit = function(monster) {
			window.location = "#/" + model.monsters.id + "/edit/" + monster.id;
		};
		
		$scope.iconID = true;
		$scope.id = true;
		$scope.experience = true;
	}
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.MonsterController = MonsterController;
	ATEditor.controllers.MonsterTableController = MonsterTableController;

	return ATEditor;
})(ATEditor, ATEditor.model);
