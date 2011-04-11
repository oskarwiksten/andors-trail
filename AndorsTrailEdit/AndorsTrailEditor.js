function IncludeJavascript(jsFile) {
	document.write('<script type="text/javascript" src="' + jsFile + '"></scr' + 'ipt>'); 
}

IncludeJavascript("FieldList.js");
IncludeJavascript("DataStore.js");
IncludeJavascript("ImageSelector.js");
IncludeJavascript("EditorTabs.js");
IncludeJavascript("inc/jquery.shorten.min.js");


var model;
var imageSelector;
var tabs;
var questlogDialog;
var onHitConditionsDialog;
var equipConditionsDialog;
var droplistItemDialog;


function checkboxHidesElement(checkbox, element, visibleCondition) {
	checkbox.change(function () {
		if (checkbox.attr("checked")) { 
			element.fadeIn("slow");
		} else {
			element.fadeOut("slow");
		}
	});
	var visible = bool(visibleCondition);
	checkbox.attr("checked", visible);
	element.toggle(visible);
}

function bool(v) { 
	return v ? true : false; 
}

function setInputFieldsToObjectValues(div, obj) {
	div.find("input,select,textarea").each(function() {
		$(this).val(obj[$(this).attr("id")]);
	});
	div.find("input:checkbox").each(function() {
		//$(this).unbind();
		$(this).attr("checked", bool(obj[$(this).attr("id")]));
	});
}

function bindInputFieldChangesToObject(div, obj) {
	div.find("input,select,textarea").unbind().change(function() {
		obj[$(this).attr("id")] = $(this).val();
	});
	div.find("input:checkbox").unbind("change").change(function() {
		obj[$(this).attr("id")] = $(this).attr("checked") ? 1 : 0;
	});
}

function applyEditorBindingsForObject(div, obj) {
	div.find("input").addClass("ui-widget-content ui-corner-all");
	setInputFieldsToObjectValues(div, obj);
	bindInputFieldChangesToObject(div, obj);
}

function applyCommonEditorBindings(div, obj, dataStore) {
	applyEditorBindingsForObject(div, obj);
	div.find("#" + dataStore.nameField).change(function() { dataStore.onNameChanged(obj, $(this).val()); });
}

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
		bindFieldToDataStore( $( "#condition", div ), model.actorEffects , function(obj) { return obj.id; } );
	}
	applyTableEditor( $( "#onHit_conditionsSource", div ) , onHitConditionsDialog, obj.onHit_conditionsSource, createNewCondition, setupEditor);
	applyTableEditor( $( "#onHit_conditionsTarget", div ) , onHitConditionsDialog, obj.onHit_conditionsTarget, createNewCondition, setupEditor);
	
	return div;
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
		bindFieldToDataStore( $( "#condition", div ), model.actorEffects , function(obj) { return obj.id; } );
	}
	applyTableEditor( $( "#equip_conditions", div ) , equipConditionsDialog, obj.equip_conditions, createNewCondition, setupEditor);
	applyTableEditor( $( "#use_conditionsSource", div ) , onHitConditionsDialog, obj.use_conditionsSource, createNewCondition, setupEditor);
	applyTableEditor( $( "#hit_conditionsSource", div ) , onHitConditionsDialog, obj.hit_conditionsSource, createNewCondition, setupEditor);
	applyTableEditor( $( "#hit_conditionsTarget", div ) , onHitConditionsDialog, obj.hit_conditionsTarget, createNewCondition, setupEditor);
	applyTableEditor( $( "#kill_conditionsSource", div ) , onHitConditionsDialog, obj.kill_conditionsSource, createNewCondition, setupEditor);
	
	return div;
}

function bindFieldToDataStore(field, dataStore, converter) {
	var dataCallback = function(request, response) {
		var result = [];
		var pattern = new RegExp(request.term, "i");
		dataStore.items.forEach(function(obj) {
			var name = converter(obj);
			if (name.match(pattern)) {
				result.push(name);
			}
		});
		response(result);
	};
	field.autocomplete( "destroy" ).autocomplete({ source: dataCallback, minLength: 0 });
}

function createStatusEffectEditor(obj) {
	var div = $( "#templates #editActorEffect" ).clone();
	applyCommonEditorBindings(div, obj, model.actorEffects);
	checkboxHidesElement(div.find('#hasRoundEffect'), div.find('#hasRoundEffectDisplay'), obj.hasRoundEffect);
	checkboxHidesElement(div.find('#hasFullRoundEffect'), div.find('#hasFullRoundEffectDisplay'), obj.hasFullRoundEffect);
	checkboxHidesElement(div.find('#hasAbilityEffect'), div.find('#hasAbilityEffectDisplay'), obj.hasAbilityEffect);
	checkboxHidesElement(div.find('#hasCritical'), div.find('#hasCriticalDisplay'), obj.criticalChance || obj.criticalMultiplier);
	imageSelector.imageify(div.find('#statuseffectimage'), div.find('#iconID'), 'effects');
	return div;
}

function applyTableEditor(table, dialog, array, templateFunction, editorSetup) {
	var updateRowText = function(row, obj) {
		$( "td", row ).each(function() {
			var id = $( this ).attr("id");
			var val = obj[id];
			val = val ? val : "";
			$( "td#" + id, row ).text(val).shorten({
				 width: '200'
			}).css('display','');
		});
	};
		
	var addToList = function(obj) {
		var row = $( "<tr>" );
		table.find("th").each(function() {
			var id = $( this ).attr("id");
			row.append( $( "<td>" ).attr("id", id) );
		});
		updateRowText(row, obj);
		table.append(row);
		row.click(function() { 
			applyEditorBindingsForObject( dialog, obj );
			if (editorSetup) { editorSetup(dialog); }
			dialog.unbind( "dialogclose" ).bind( "dialogclose", function() { 
				updateRowText(row, obj); 
			});
			dialog.dialog( "open" );
		});
		return row;
	};
	table.parent().find("#add").button().click(function() {
		var obj = templateFunction();
		array.push( obj );
		addToList( obj ).click();
	});
	table.addClass("ui-corner-all");
	$( "thead", table ).addClass("ui-widget-header");
	array.forEach(addToList);
}

function createQuestEditor(obj) {
	var div = $( "#templates #editQuest" ).clone(true);
	applyCommonEditorBindings(div, obj, model.quests);
	if (!obj.stages) obj.stages = [];
	var array = obj.stages;
	var createNewStage = function() {
		var nextProgress;
		if (array.length > 0) { nextProgress = parseInt(array[array.length - 1].progress) + 10; }
		if (!nextProgress) { nextProgress = 10; }
		return { progress: nextProgress };
	};
	applyTableEditor( $( "#stages", div ) , questlogDialog, array, createNewStage, function() {});
	return div;
}

function createDroplistEditor(obj) {
	var div = $( "#templates #editDroplist" ).clone(true);
	applyCommonEditorBindings(div, obj, model.droplists);
	if (!obj.items) obj.items = [];
	var createNewDroplistItem = function() { return { quantity: 1, chance: 100 } };
	var setupEditor = function(div) {
		bindFieldToDataStore( $( "#itemID", div ), model.items , function(obj) { return obj.searchTag; } );
	}
	applyTableEditor( $( "#items", div ) , droplistItemDialog, obj.items, createNewDroplistItem, setupEditor);
	return div;
}


function openTabForObject(obj, dataStore) {
	tabs.openTabForObject(obj, dataStore.objectTypename, obj[dataStore.nameField]);
}

function bindObjectsToItemList(itemListDiv, dataStore) {
	itemListDiv.children().remove();
	var addToList = function(obj) { 
		var item = $("<li>" + obj[dataStore.nameField] + "</li>");
		item.click(function() { openTabForObject(obj, dataStore); });
		itemListDiv.append(item);
		item.hide().fadeIn('slow');
	};
	dataStore.items.forEach(addToList);
	dataStore.onAdded = addToList;
	dataStore.onDeserialized = function() { 
		bindObjectsToItemList(itemListDiv, dataStore);
		// TODO: Should also close all tabs.
	};
	dataStore.onNameChanged = function(obj, name) {
		$("li:eq(" + dataStore.items.indexOf(obj) + ")", itemListDiv).html(name);
		//TODO: Should this really be in the same function? 
		// (splitting the left part from the tab controls would reduce coupling, which would be a good thing.)
		tabs.renameTabForObject(obj, name);
	};
}

		
function exportIfExists(dataStore, div) {
	var exists = false;
	if (dataStore && dataStore.items.length > 0) exists = true;
	div.toggle(exists);
	if (!exists) { return; }
	var exportData = dataStore.serialize();
	$( "#value" , div ).val(exportData);
}

function prepareImport(dataStore, div) {
	var importButton = $( "#import", div );
	var textarea = $( "#value", div );
	importButton.button({ disabled: true }).click(function() {
		if (!textarea.val()) return;
		dataStore.deserialize(textarea.val());
		div.hide('slow');
	});
	textarea.val("").change(function() {
		var disabled = $(this).val() ? false : true;
		importButton.button( "option", "disabled", disabled );
	});
}

function bindEditorType(dataStore, div, createObjectEditor, newObjectCreator) {
	tabs.registerEditorType(dataStore.objectTypename, createObjectEditor);
	
	bindObjectsToItemList( $( "ul", div ), dataStore );
	
	$( "#add", div )
		.button()
		.click(function() {
			var obj = newObjectCreator();
			dataStore.add(obj);
			openTabForObject( obj, dataStore );
		});
}

function startEditor() {
	
	model = {
		actorEffects: new DataStore('effect', new FieldList("[id|name|iconID|isStacking|"
				+ "hasRoundEffect|round_visualEffectID|round_boostHP_Min|round_boostHP_Max|round_boostAP_Min|round_boostAP_Max|"
				+ "hasFullRoundEffect|fullround_visualEffectID|fullround_boostHP_Min|fullround_boostHP_Max|fullround_boostAP_Min|fullround_boostAP_Max|"
				+ "hasAbilityEffect|boostMaxHP|boostMaxAP|moveCostPenalty|attackCost|attackChance|criticalChance|criticalMultiplier|attackDamage_Min|attackDamage_Max|blockChance|damageResistance|"
				+ "];"))
		,quests: new DataStore('quest', new FieldList("[id|name|showInLog|stages[progress|logText|rewardExperience|finishesQuest|]|];"))
		,items: new DataStore('item', new FieldList("[iconID|name|searchTag|category|baseMarketCost|"
				+ "hasEquipEffect|equip_boostMaxHP|equip_boostMaxAP|equip_moveCostPenalty|equip_attackCost|equip_attackChance|equip_criticalChance|equip_criticalMultiplier|equip_attackDamage_Min|equip_attackDamage_Max|equip_blockChance|equip_damageResistance|equip_conditions[condition|magnitude|]|"
				+ "hasUseEffect|use_boostHP_Min|use_boostHP_Max|use_boostAP_Min|use_boostAP_Max|use_conditionsSource[condition|magnitude|duration|chance|]|"
				+ "hasHitEffect|hit_boostHP_Min|use_boostHP_Max|hit_boostAP_Min|hit_boostAP_Max|hit_conditionsSource[condition|magnitude|duration|chance|]|hit_conditionsTarget[condition|magnitude|duration|chance|]|"
				+ "hasKillEffect|kill_boostHP_Min|kill_boostHP_Max|kill_boostAP_Min|kill_boostAP_Max|kill_conditionsSource[condition|magnitude|duration|chance|]|"
				+ "];"))
		,droplists: new DataStore('droplist', new FieldList("[id|items[itemID|quantity|chance|]|];"), 'id')
		,dialogue: new DataStore('dialogue', new FieldList("[id|name|];"))
		,monsters: new DataStore('monster', new FieldList("[iconID|name|tags|size|exp|maxHP|maxAP|moveCost|attackCost|attackChance|criticalChance|criticalMultiplier|attackDamage_Min|attackDamage_Max|blockChance|damageResistance|droplistID|phraseID|"
				+ "hasHitEffect|onHit_boostHP_Min|onHit_boostHP_Max|onHit_boostAP_Min|onHit_boostAP_Max|onHit_conditionsSource[condition|magnitude|duration|chance|]|onHit_conditionsTarget[condition|magnitude|duration|chance|]|"
				+ "];"))
	};

	model.actorEffects.add({id: "bless", name: "Bless", iconID: "items_tiles:318", hasAbilityEffect: true, attackChance: 15, blockChance: 5});
	model.actorEffects.add({id: "poison_weak", name: "Weak Poison", iconID: "items_tiles:340", hasRoundEffect: true, round_visualEffectID: 2, round_boostHP_Min: -1, round_boostHP_Max: -1});

	model.quests.add({id: "testQuest", name: "Test quest", stages: [ { progress: 10, logText: "Stage 10"} , { progress: 20, logText: "Stage 20", finishesQuest: 1 } ] });

	model.items.add({iconID: "items_tiles:70", name: "Test item", searchTag: "item0", category: 0, baseMarketCost: 51, hasEquipEffect: 1, equip_attackChance: 10, equip_attackDamage_Min: 2, equip_attackDamage_Max: 4});
	model.items.add({iconID: "items_tiles:266", name: "Ring of damage +1", searchTag: "dmg_ring1", category: 7, baseMarketCost: 62, hasEquipEffect: 1, equip_attackDamage_Min: 1, equip_attackDamage_Max: 1});

	model.droplists.add({id: "merchant1", items: [ { itemID: 'dmg_ring1', quantity: 5, chance: 100 } , { itemID: 'item0', quantity: 1, chance: 100 } ] } );

	model.monsters.add({name: "Small ant", iconID: "monsters_insects:2", maxHP: 30, size: '1x1'});
	model.monsters.add({name: "Red ant", iconID: "monsters_insects:3", maxHP: 20, size: '1x1'});
	model.monsters.add({name: "Wasp", iconID: "monsters_insects:1", maxHP: 10, size: '1x1'});
	
	

	
	$( "#left #tools" ).accordion({ fillSpace: true });
	
	tabs = new EditorTabs( $( "#center #tabs" ) );
	
	bindEditorType(model.actorEffects, $( "#tools #effectlist" ), createStatusEffectEditor, function() {
		return {name: "New Effect", id: 'new_effect' };
	});
	bindEditorType(model.quests, $( "#tools #questlist" ), createQuestEditor, function() {
		return {name: "New Quest", id: 'new_quest' };
	});
	bindEditorType(model.items, $( "#tools #itemlist" ), createItemEditor, function() {
		return {name: "New Item", searchTag: "new_item", category: 31 };
	});
	bindEditorType(model.droplists, $( "#tools #droplist" ), createDroplistEditor, function() {
		return {id: "new_droplist" };
	});
	bindEditorType(model.monsters, $( "#tools #monsterlist" ), createMonsterEditor, function() {
		return {name: "New Monster", maxAP: 10, attackCost: 5, moveCost: 5, size: '1x1'};
	});
	
	
	var importExportDialog;
	
	$( "#buttons #import" )
		.button()
		.click(function() {
			importExportDialog.dialog({ title: "Import data" });
			$( "div", importExportDialog ).show();
			prepareImport(model.actorEffects, $( "#statuseffects", importExportDialog ));
			prepareImport(model.quests, $( "#quests", importExportDialog ));
			prepareImport(model.items, $( "#items", importExportDialog ));
			prepareImport(model.droplists, $( "#droplists", importExportDialog ));
			prepareImport(model.dialogue, $( "#dialogue", importExportDialog ));
			prepareImport(model.monsters, $( "#monsters", importExportDialog ));
			importExportDialog.dialog( "open" );
		});
	$( "#buttons #export" )
		.button()
		.click(function() {
			importExportDialog.dialog({ title: "Export data" });
			exportIfExists(model.actorEffects, $( "#statuseffects", importExportDialog ));
			exportIfExists(model.quests, $( "#quests", importExportDialog ));
			exportIfExists(model.items, $( "#items", importExportDialog ));
			exportIfExists(model.droplists, $( "#droplists", importExportDialog ));
			exportIfExists(model.dialogue, $( "#dialogue", importExportDialog ));
			exportIfExists(model.monsters, $( "#monsters", importExportDialog ));
			$( "#import", importExportDialog ).hide();
			importExportDialog.dialog( "open" );
		});
	
	var defaultButtons = { 
		Close: function() { $( this ).dialog( "close" ); }
	};
	importExportDialog = $( "#templates #dialog-importexport" )
		.dialog({
			modal: true,
			autoOpen: false,
			width: 800,
			height: 500,
			buttons: defaultButtons
		});
	
	questlogDialog = $( "#templates #dialog-questlog" )
		.dialog({
			title: "Quest log item",
			modal: true,
			autoOpen: false,
			width: 450,
			buttons: defaultButtons
		});
	
	onHitConditionsDialog = $( "#templates #dialog-onHitConditions" )
		.dialog({
			title: "Actor status conditon",
			modal: true,
			autoOpen: false,
			width: 350,
			buttons: defaultButtons
		});
	
	equipConditionsDialog = $( "#templates #dialog-equipConditions" )
		.dialog({
			title: "Actor status conditon",
			modal: true,
			autoOpen: false,
			width: 350,
			buttons: defaultButtons
		});
	
	droplistItemDialog = $( "#templates #dialog-droplistItem" )
		.dialog({
			title: "Droplist item",
			modal: true,
			autoOpen: false,
			width: 350,
			buttons: defaultButtons
		});
		
		
	
	imageSelector = new ImageSelector("http://andors-trail.googlecode.com/svn/trunk/AndorsTrail/res/drawable/", $( "#dialog-images" ) );
	imageSelector.add(new TilesetImage("items_tiles", {x: 14, y:30}, {x: 34, y:34}, [ 'items', 'effects' ] ));
	imageSelector.add(new TilesetImage("monsters_armor1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_demon1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_demon2", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_dogs", {x: 7, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_dragons", {x: 7, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_eye1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_eye2", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_eye3", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_eye4", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_ghost1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_ghost2", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_hydra1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_insects", {x: 6, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_liches", {x: 4, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_mage", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_mage2", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_mage3", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_mage4", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_man1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_men", {x: 9, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_men2", {x: 10, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_misc", {x: 12, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_rats", {x: 5, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_rogue1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_skeleton1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_skeleton2", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_snakes", {x: 6, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_cyclops", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_warrior1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_wraiths", {x: 3, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_zombie1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_zombie2", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_dragon1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	
}

