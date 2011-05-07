
function createMonsterEditor(obj) {
	if (!obj.maxAP) { obj.maxAP = 10; }
	if (!obj.size) { obj.size = "1x1"; }
	
	var div = $( "#templates #editMonster" ).clone();
	applyCommonEditorBindings(div, obj, model.monsters);
	checkboxHidesElement(div.find('#hasConversation'), div.find('#hasConversationDisplay'), obj.phraseID);
	checkboxHidesElement(div.find('#hasCombat'), div.find('#hasCombatDisplay'), obj.attackChance);
	checkboxHidesElement(div.find('#hasCritical'), div.find('#hasCriticalDisplay'), obj.criticalChance || obj.criticalMultiplier);
	checkboxHidesElement(div.find('#hasHitEffect'), div.find('#hasHitEffectDisplay'), obj.hasHitEffect);
	imageSelector.imageify(div.find('#monsterimage'), div.find('#iconID'), 'monsters');
	bindFieldToDataStore( $( "#droplistID", div ), model.droplists );

	var createNewCondition = function() { return { chance: 100, magnitude: 1 }; }
	if (!obj.onHit_conditionsSource) obj.onHit_conditionsSource = [];
	if (!obj.onHit_conditionsTarget) obj.onHit_conditionsTarget = [];
	var setupEditor = function(div) {
		bindFieldToDataStore( $( "#condition", div ), model.actorConditions );
	}
	applyTableEditor({
		table: $( "#onHit_conditionsSource", div ),
		dialog: onHitConditionsDialog, 
		array: obj.onHit_conditionsSource, 
		templateFunction: createNewCondition, 
		editorSetup: setupEditor
	});
	applyTableEditor({
		table: $( "#onHit_conditionsTarget", div ),
		dialog: onHitConditionsDialog, 
		array: obj.onHit_conditionsTarget, 
		templateFunction: createNewCondition, 
		editorSetup: setupEditor
	});
	
	var expDependsOn = [];
	var div100 = function(v) { return v / 100; }
	var v = function(s) { 
		var field = $( s, div );
		expDependsOn.push(field);
		var val = field.val();
		if (!val) return 0;
		return parseInt(val);
	}
	var updateExperience = function() {
		/*
		final float avgAttackHP  = t.getAttacksPerTurn(maxAP) * div100(t.attackChance) * t.damagePotential.averagef() * (1 + div100(t.criticalChance) * t.criticalMultiplier);
		final float avgDefenseHP = maxHP * (1 + div100(t.blockChance)) + Constants.EXP_FACTOR_DAMAGERESISTANCE * t.damageResistance;
		return (int) Math.ceil((avgAttackHP * 3 + avgDefenseHP) * Constants.EXP_FACTOR_SCALING);
		*/
		
		expDependsOn = [];
		
		var EXP_FACTOR_DAMAGERESISTANCE = 9;
		var EXP_FACTOR_SCALING = 0.7;
		
		var attacksPerTurn = Math.floor(v("#maxAP") / v("#attackCost"));
		var avgDamagePotential = (v("#attackDamage_Min") + v("#attackDamage_Max")) / 2;
		var avgAttackHP  = attacksPerTurn * div100(v("#attackChance")) * avgDamagePotential * (1 + div100(v("#criticalChance")) * v("#criticalMultiplier"));
		var avgDefenseHP = v("#maxHP") * (1 + div100(v("#blockChance"))) + EXP_FACTOR_DAMAGERESISTANCE * v("#damageResistance");
		var experience = (avgAttackHP * 3 + avgDefenseHP) * EXP_FACTOR_SCALING;
		
		$( "#experience", div ).val(Math.ceil(experience));
	};
	
	updateExperience();
	jQuery.each(expDependsOn, function(idx, o) {
		o.change(updateExperience);
	});
	
	return div;
}

