
function createItemEditor(obj) {
	var div = $( "#templates #editItem" ).clone();
	applyCommonEditorBindings(div, obj, model.items);
	checkboxHidesElement(div.find('#hasEquipEffect'), div.find('#hasEquipEffectDisplay'), obj.hasEquipEffect);
	checkboxHidesElement(div.find('#hasUseEffect'), div.find('#hasUseEffectDisplay'), obj.hasUseEffect);
	checkboxHidesElement(div.find('#equip_hasCritical'), div.find('#equip_hasCriticalDisplay'), obj.equip_criticalChance || obj.equip_criticalMultiplier);
	checkboxHidesElement(div.find('#hasHitEffect'), div.find('#hasHitEffectDisplay'), obj.hasHitEffect);
	checkboxHidesElement(div.find('#hasKillEffect'), div.find('#hasKillEffectDisplay'), obj.hasKillEffect);
	imageSelector.imageify(div.find('#itemimage'), div.find('#iconID'), 'items');
	
	var createNewCondition = function() { return { chance: 100, magnitude: 1 }; }
	if (!obj.equip_conditions) obj.equip_conditions = [];
	if (!obj.use_conditionsSource) obj.use_conditionsSource = [];
	if (!obj.hit_conditionsSource) obj.hit_conditionsSource = [];
	if (!obj.hit_conditionsTarget) obj.hit_conditionsTarget = [];
	if (!obj.kill_conditionsSource) obj.kill_conditionsSource = [];
	var setupEditor = function(div) {
		bindFieldToDataStore( $( "#condition", div ), model.actorConditions);
	}

	applyTableEditor({
		table: $( "#equip_conditions", div ),
		dialog: equipConditionsDialog, 
		array: obj.equip_conditions, 
		templateFunction: createNewCondition, 
		editorSetup: setupEditor
	});
	applyTableEditor({
		table: $( "#use_conditionsSource", div ),
		dialog: onHitConditionsDialog, 
		array: obj.use_conditionsSource, 
		templateFunction: createNewCondition, 
		editorSetup: setupEditor
	});
	applyTableEditor({
		table: $( "#hit_conditionsSource", div ),
		dialog: onHitConditionsDialog, 
		array: obj.hit_conditionsSource, 
		templateFunction: createNewCondition, 
		editorSetup: setupEditor
	});
	applyTableEditor({
		table: $( "#hit_conditionsTarget", div ),
		dialog: onHitConditionsDialog, 
		array: obj.hit_conditionsTarget, 
		templateFunction: createNewCondition, 
		editorSetup: setupEditor
	});
	applyTableEditor({
		table: $( "#kill_conditionsSource", div ),
		dialog: onHitConditionsDialog, 
		array: obj.kill_conditionsSource, 
		templateFunction: createNewCondition, 
		editorSetup: setupEditor
	});
	
	$( "#baseMarketCost", div ).change(function() {
		var val = parseInt( $( this ).val() );
		$( "#marketCost_Sell", div ).val(Math.round(val * (100 + 15) / 100));
		$( "#marketCost_Buy", div ).val(Math.round(val * (100 - 15) / 100));
	}).change();
	
	return div;
}

