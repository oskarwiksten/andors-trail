
function sgn(v) {
	if (v < 0) return -1;
	else if (v > 0) return 1;
	else return 0;
}

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
	
	
	var itemCostDependsOn = [];
	var v = function(s) { 
		var field = $( s, div );
		itemCostDependsOn.push(field);
		var val = field.val();
		if (!val) return 0;
		return parseInt(val);
	}
	var cb = function(s) { 
		var field = $( s, div );
		itemCostDependsOn.push(field);
		return field.attr("checked");
	}
	var calculateItemCost = function() {
		itemCostDependsOn = [];
		var averageHPBoost = (v("#use_boostHP_Min") + v("#use_boostHP_Max")) / 2;
		var costBoostHP = Math.round(0.1*sgn(averageHPBoost)*Math.pow(Math.abs(averageHPBoost), 2) + 3*averageHPBoost);
		var itemUsageCost = costBoostHP;
		
		var isWeapon = v("#category") == 0;
		
		var equip_blockChance = v("#equip_blockChance");
		var equip_attackChance = v("#equip_attackChance");
		var equip_attackCost = v("#equip_attackCost");
		var equip_damageResistance = v("#equip_damageResistance");
		var equip_attackDamage_Min = v("#equip_attackDamage_Min");
		var equip_attackDamage_Max = v("#equip_attackDamage_Max");
		var equip_criticalChance = v("#equip_criticalChance");
		var equip_criticalMultiplier = v("#equip_criticalMultiplier");
		var costBC = Math.round(3*Math.pow(Math.max(0,equip_blockChance), 2.5) + 28*equip_blockChance);
		var costAC = Math.round(0.4*Math.pow(Math.max(0,equip_attackChance), 2.5) - 7*Math.pow(Math.abs(Math.min(0,equip_attackChance)),2.7));
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
		if (!cb("#equip_hasCritical")) {
			costCC = 0;
			costCM = 0;
		}
		var costCombat = costBC + costAC + costAP + costDR + costDMG_Min + costDMG_Max + costCC + costCM;
		
		var equip_boostMaxHP = v("#equip_boostMaxHP");
		var equip_boostMaxAP = v("#equip_boostMaxAP");
		var equip_moveCostPenalty = v("#equip_moveCostPenalty");
		var costMaxHP = Math.round(30*Math.pow(Math.max(0,equip_boostMaxHP), 1.2) + 70*equip_boostMaxHP);
		var costMaxAP = Math.round(50*Math.pow(Math.max(0,equip_boostMaxAP), 3) + 750*equip_boostMaxAP);
		var costMovement = Math.round(10*Math.pow(Math.max(0,equip_moveCostPenalty), 2) + 350*equip_moveCostPenalty);
		var itemEquipCost = costCombat + costMaxHP + costMaxAP + costMovement;
		
		if (!cb("#hasEquipEffect")) { itemEquipCost = 0; }
		if (!cb("#hasUseEffect")) { itemUsageCost = 0; }
		
		return itemEquipCost + itemUsageCost;
	}
	
	var divBaseMarketCost = $( "#baseMarketCost", div );
	var recalculateStorePrice = function() {
		var val = parseInt(obj.baseMarketCost);
		if (!obj.hasManualPrice) {
			val = calculateItemCost(obj);
			obj.baseMarketCost = val;
			divBaseMarketCost.val(val);
		}
		$( "#marketCost_Sell", div ).val(Math.round(val * (100 + 15) / 100));
		$( "#marketCost_Buy", div ).val(Math.round(val * (100 - 15) / 100));
	};
	
	divBaseMarketCost.change(recalculateStorePrice);
	$( "#hasManualPrice", div ).change(function() {
		if (obj.hasManualPrice) {
			divBaseMarketCost.removeAttr("readonly");
		} else {
			divBaseMarketCost.attr("readonly", "readonly");
		}
		recalculateStorePrice();
	}).change();
	
	calculateItemCost();
	jQuery.each(itemCostDependsOn, function(idx, o) {
		o.change(recalculateStorePrice);
	});
	
	return div;
}

