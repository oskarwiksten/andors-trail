var ATEditor = (function(ATEditor, model) {

	function MonsterController($scope, $routeParams) {
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
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.MonsterController = MonsterController;

	return ATEditor;
})(ATEditor, ATEditor.model);
