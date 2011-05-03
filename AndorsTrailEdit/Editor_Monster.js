
function createMonsterEditor(obj) {
	var div = $( "#templates #editMonster" ).clone();
	applyCommonEditorBindings(div, obj, model.monsters);
	checkboxHidesElement(div.find('#hasConversation'), div.find('#hasConversationDisplay'), obj.phraseID);
	checkboxHidesElement(div.find('#hasCombat'), div.find('#hasCombatDisplay'), obj.attackChance);
	checkboxHidesElement(div.find('#hasCritical'), div.find('#hasCriticalDisplay'), obj.criticalChance || obj.criticalMultiplier);
	checkboxHidesElement(div.find('#hasHitEffect'), div.find('#hasHitEffectDisplay'), obj.hasHitEffect);
	imageSelector.imageify(div.find('#monsterimage'), div.find('#iconID'), 'monsters');
	bindFieldToDataStore( $( "#droplistID", div ), model.droplists , function(obj) { return obj.id; } );

	var createNewCondition = function() { return { chance: 100, magnitude: 1 }; }
	if (!obj.onHit_conditionsSource) obj.onHit_conditionsSource = [];
	if (!obj.onHit_conditionsTarget) obj.onHit_conditionsTarget = [];
	var setupEditor = function(div) {
		bindFieldToDataStore( $( "#condition", div ), model.actorConditions , function(obj) { return obj.id; } );
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
	return div;
}

