
function createActorConditionEditor(obj) {
	var div = $( "#templates #editActorCondition" ).clone();
	applyCommonEditorBindings(div, obj, model.actorConditions);
	checkboxHidesElement(div.find('#hasRoundEffect'), div.find('#hasRoundEffectDisplay'), obj.hasRoundEffect);
	checkboxHidesElement(div.find('#hasFullRoundEffect'), div.find('#hasFullRoundEffectDisplay'), obj.hasFullRoundEffect);
	checkboxHidesElement(div.find('#hasAbilityEffect'), div.find('#hasAbilityEffectDisplay'), obj.hasAbilityEffect);
	checkboxHidesElement(div.find('#hasCritical'), div.find('#hasCriticalDisplay'), obj.criticalChance || obj.criticalMultiplier);
	imageSelector.imageify(div.find('#actorconditionimage'), div.find('#iconID'), 'conditions');
	return div;
}

