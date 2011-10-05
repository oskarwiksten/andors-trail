function IncludeJavascript(jsFile) {
	document.write('<script type="text/javascript" src="' + jsFile + '"></scr' + 'ipt>'); 
}

IncludeJavascript("FieldList.js");
IncludeJavascript("DataStore.js");
IncludeJavascript("ImageSelector.js");
IncludeJavascript("EditorTabs.js");
IncludeJavascript("EditorFunctions.js");
IncludeJavascript("ImportExport.js");

IncludeJavascript("Editor_ActorCondition.js");
IncludeJavascript("Editor_Quest.js");
IncludeJavascript("Editor_Item.js");
IncludeJavascript("Editor_Droplist.js");
IncludeJavascript("Editor_Conversation.js");
IncludeJavascript("Editor_Monster.js");

IncludeJavascript("inc/jquery.dynatree.min.js");


var model;
var imageSelector;
var tabs;
var questlogDialog;
var onHitConditionsDialog;
var equipConditionsDialog;
var droplistItemDialog;


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
	
	$( "#clear", div )
		.button()
		.click(function() {
			if (confirm("Are you sure?")) {
				dataStore.clear();
			}
		});
}

function addExampleModelItems(model) {
	model.actorConditions.add({id: "bless", name: "Bless", iconID: "actorconditions_1:38", category: 0, hasAbilityEffect: 1, attackChance: 15, blockChance: 5});
	model.actorConditions.add({id: "poison_weak", name: "Weak Poison", iconID: "actorconditions_1:60", category: 3, hasRoundEffect: 1, round_visualEffectID: 2, round_boostHP_Min: -1, round_boostHP_Max: -1});

	model.quests.add({id: "testQuest", name: "Test quest", stages: [ { progress: 10, logText: "Stage 10"} , { progress: 20, logText: "Stage 20", finishesQuest: 1 } ] });

	model.items.add({id: "item0", iconID: "items_weapons:0", name: "Test item", category: 0, baseMarketCost: 51, hasEquipEffect: 1, equip_attackChance: 10, equip_attackDamage_Min: 2, equip_attackDamage_Max: 4, equip_attackCost: 4});
	model.items.add({id: "dmg_ring1", iconID: "items_jewelry:0", name: "Ring of damage +1", category: 7, baseMarketCost: 62, hasEquipEffect: 1, equip_attackDamage_Min: 1, equip_attackDamage_Max: 1});

	model.droplists.add({id: "merchant1", items: [ { itemID: 'dmg_ring1', quantity_Min: 4, quantity_Max: 5, chance: 100 } , { itemID: 'item0', quantity_Min: 1, quantity_Max: 1, chance: 100 } ] } );

	model.dialogue.add({id: "mikhail_default", message: 'Anything else I can help you with?', replies: [ { text: 'Do you have any tasks for me?', nextPhraseID: 'mikhail_tasks' }, { text: 'Is there anything else you can tell me about Andor?', nextPhraseID: 'mikhail_andor1' } ]});
	model.dialogue.add({id: 'mikhail_andor1', message: 'As I said, Andor went out yesterday and hasn\'t been back since. I\'m starting to worry about him. Please go look for your brother, he said he would only be out a short while.'});
	model.dialogue.add({id: 'mikhail_tasks', message: 'Oh yes, there were some things I need help with, bread and rats. Which one would you like to talk about?'});

	model.monsters.add({id: "small_ant", name: "Small ant", iconID: "monsters_insects:2", maxHP: 30, size: '1x1'});
	model.monsters.add({id: "red_ant", name: "Red ant", iconID: "monsters_insects:3", maxHP: 20, size: '1x1'});
	model.monsters.add({id: "wasp", name: "Wasp", iconID: "monsters_insects:1", maxHP: 10, size: '1x1'});
}

function startEditor() {
	
	model = {
		actorConditions: new DataStore({
			objectTypename: 'actorcondition'
			,fieldList: new FieldList("[id|name|iconID|category|isStacking|"
				+ "hasRoundEffect|round_visualEffectID|round_boostHP_Min|round_boostHP_Max|round_boostAP_Min|round_boostAP_Max|"
				+ "hasFullRoundEffect|fullround_visualEffectID|fullround_boostHP_Min|fullround_boostHP_Max|fullround_boostAP_Min|fullround_boostAP_Max|"
				+ "hasAbilityEffect|boostMaxHP|boostMaxAP|moveCostPenalty|attackCost|attackChance|criticalChance|criticalMultiplier|attackDamage_Min|attackDamage_Max|blockChance|damageResistance|"
				+ "];"
			)
			,idField: 'id'
			,nameField: 'name'
		})
		,quests: new DataStore({
			objectTypename: 'quest'
			,fieldList: new FieldList("[id|name|showInLog|stages[progress|logText|rewardExperience|finishesQuest|]|];")
			,idField: 'id'
			,nameField: 'name'
		})
		,items: new DataStore({
			objectTypename: 'item'
			,fieldList: new FieldList("[id|iconID|name|category|displaytype|hasManualPrice|baseMarketCost|"
				+ "hasEquipEffect|equip_boostMaxHP|equip_boostMaxAP|equip_moveCostPenalty|equip_attackCost|equip_attackChance|equip_criticalChance|equip_criticalMultiplier|equip_attackDamage_Min|equip_attackDamage_Max|equip_blockChance|equip_damageResistance|equip_conditions[condition|magnitude|]|"
				+ "hasUseEffect|use_boostHP_Min|use_boostHP_Max|use_boostAP_Min|use_boostAP_Max|use_conditionsSource[condition|magnitude|duration|chance|]|"
				+ "hasHitEffect|hit_boostHP_Min|hit_boostHP_Max|hit_boostAP_Min|hit_boostAP_Max|hit_conditionsSource[condition|magnitude|duration|chance|]|hit_conditionsTarget[condition|magnitude|duration|chance|]|"
				+ "hasKillEffect|kill_boostHP_Min|kill_boostHP_Max|kill_boostAP_Min|kill_boostAP_Max|kill_conditionsSource[condition|magnitude|duration|chance|]|"
				+ "];"
			)
			,idField: 'id'
			,nameField: 'name'
		})
		,droplists: new DataStore({
			objectTypename: 'droplist'
			,fieldList: new FieldList("[id|items[itemID|quantity_Min|quantity_Max|chance|]|];")
			,idField: 'id'
			,nameField: 'id'
		})
		,dialogue: new DataStore({
			objectTypename: 'dialogue'
			,fieldList: new FieldList("[id|message|progressQuest|rewardDropListID|replies[text|nextPhraseID|requires_Progress|requires_itemID|requires_Quantity|]|];")
			,idField: 'id'
			,nameField: 'id'
		})
		,monsters: new DataStore({
			objectTypename: 'monster'
			,fieldList: new FieldList("[id|iconID|name|tags|size|maxHP|maxAP|moveCost|attackCost|attackChance|criticalChance|criticalMultiplier|attackDamage_Min|attackDamage_Max|blockChance|damageResistance|droplistID|phraseID|"
				+ "hasHitEffect|onHit_boostHP_Min|onHit_boostHP_Max|onHit_boostAP_Min|onHit_boostAP_Max|onHit_conditionsSource[condition|magnitude|duration|chance|]|onHit_conditionsTarget[condition|magnitude|duration|chance|]|"
				+ "];"
			)
			,idField: 'id'
			,nameField: 'name'
		})
	};
	
	addExampleModelItems(model);

	
	$( "#left #tools" ).accordion({ fillSpace: true });
	
	tabs = new EditorTabs( $( "#center #tabs" ) );
	
	bindEditorType(model.actorConditions, $( "#tools #actorconditionlist" ), createActorConditionEditor, function() {
		return {name: "New Condition", id: 'new_condition' };
	});
	bindEditorType(model.quests, $( "#tools #questlist" ), createQuestEditor, function() {
		return {name: "New Quest", id: 'new_quest' };
	});
	bindEditorType(model.items, $( "#tools #itemlist" ), createItemEditor, function() {
		return {name: "New Item", id: "new_item", category: 31 };
	});
	bindEditorType(model.droplists, $( "#tools #droplist" ), createDroplistEditor, function() {
		return {id: "new_droplist" };
	});
	bindEditorType(model.dialogue, $( "#tools #conversationlist" ), createConversationEditor, function() {
		return {id: "new_conversation" };
	});
	bindEditorType(model.monsters, $( "#tools #monsterlist" ), createMonsterEditor, function() {
		return {id: "new_monster", name: "New Monster", maxAP: 10, attackCost: 5, moveCost: 5 };
	});
	
	
	
	$( "#buttons #import" ).button().click( showImportDialog );
	$( "#buttons #export" ).button().click( showExportDialog );
	
	
	var defaultButtons = { 
		Close: function() { $( this ).dialog( "close" ); }
	};
	
	prepareImportExportDialogs(defaultButtons);
	
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
	
	imageSelector = new ImageSelector("../AndorsTrail/res/drawable/", $( "#dialog-images" ) );
	imageSelector.add(new TilesetImage("actorconditions_1", {x:14, y:8}, undefined, [ 'conditions' ] ));
	imageSelector.add(new TilesetImage("actorconditions_2", {x:3, y:1}, undefined, [ 'conditions' ] ));
	imageSelector.add(new TilesetImage("items_armours", {x:14, y:3}, undefined, [ 'items' ] ));
	imageSelector.add(new TilesetImage("items_armours_3", {x:10, y:4}, undefined, [ 'items' ] ));
	imageSelector.add(new TilesetImage("items_armours_2", {x:7, y:1}, undefined, [ 'items' ] ));
	imageSelector.add(new TilesetImage("items_weapons", {x:14, y:6}, undefined, [ 'items' ] ));
	imageSelector.add(new TilesetImage("items_weapons_3", {x:13, y:5}, undefined, [ 'items' ] ));
	imageSelector.add(new TilesetImage("items_weapons_2", {x:7, y:1}, undefined, [ 'items' ] ));
	imageSelector.add(new TilesetImage("items_jewelry", {x:14, y:1}, undefined, [ 'items' ] ));
	imageSelector.add(new TilesetImage("items_rings_1", {x:10, y:3}, undefined, [ 'items' ] ));
	imageSelector.add(new TilesetImage("items_necklaces_1", {x:10, y:3}, undefined, [ 'items' ] ));
	imageSelector.add(new TilesetImage("items_consumables", {x:14, y:5}, undefined, [ 'items' ] ));
	imageSelector.add(new TilesetImage("items_books", {x:11, y:1}, undefined, [ 'items' ] ));
	imageSelector.add(new TilesetImage("items_misc", {x:14, y:4}, undefined, [ 'items' ] ));
	imageSelector.add(new TilesetImage("monsters_armor1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_demon1", {x: 1, y:1}, {x:64, y:64}, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_dogs", {x: 7, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_eye1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_eye2", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_eye3", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_eye4", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_ghost1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_hydra1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_insects", {x: 6, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_liches", {x: 4, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_mage", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_mage2", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_man1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_men", {x: 9, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_men2", {x: 10, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_misc", {x: 12, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_rats", {x: 5, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_rogue1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_skeleton1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_skeleton2", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_snakes", {x: 6, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_cyclops", {x: 1, y:1}, {x:64, y:96}, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_warrior1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_wraiths", {x: 3, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_zombie1", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_zombie2", {x: 1, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_karvis1", {x: 2, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_karvis2", {x: 9, y:1}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_rltiles1", {x:20, y:8}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_rltiles2", {x:20, y:9}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_rltiles3", {x:10, y:3}, undefined, [ 'monsters' ] ));
	imageSelector.add(new TilesetImage("monsters_redshrike1", {x:6, y:1}, undefined, [ 'monsters' ] ));
	
}

