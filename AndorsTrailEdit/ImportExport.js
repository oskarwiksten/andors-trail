
var importDialog;
var exportDialog;

function exportIfExists(dataStore, div) {
	var exportData = dataStore.serialize();
	$( "#value" , div ).val(exportData);
}

function importDatastore(dataStore, content) {
	dataStore.deserialize(content);
}

function prepareImport(dataStore, div) {
	var importButton = $( "#import", div );
	var textarea = $( "#value", div );
	importButton.button().click(function() {
		alert("Running import for " + dataStore.objectTypename);
		if (!textarea.val()) return;
		importDatastore(dataStore, textarea.val());
	});
	textarea.keyup(function() {
		var disabled = $(this).val() ? false : true;
		importButton.button( "option", "disabled", disabled );
	});
}

function showImportDialog() {
	$( "#import", importDialog ).button( "option", "disabled", true );
	$( "#value", importDialog ).val("");
	importDialog.dialog( "open" );
}

function showExportDialog() {
	exportIfExists(model.actorConditions, $( ".export-actorconditions", exportDialog ));
	exportIfExists(model.quests, $( ".export-quests", exportDialog ));
	exportIfExists(model.items, $( ".export-items", exportDialog ));
	exportIfExists(model.droplists, $( ".export-droplists", exportDialog ));
	exportIfExists(model.dialogue, $( ".export-dialogue", exportDialog ));
	exportIfExists(model.monsters, $( ".export-monsters", exportDialog ));
	exportDialog.dialog( "open" );
}

function prepareImportExportDialogs(buttons) {
	importDialog = $( "#templates #dialog-import" )
		.dialog({
			title: "Import data",
			modal: true,
			autoOpen: false,
			width: 800,
			buttons: buttons
		});
	prepareImport(model.actorConditions, $( ".import-actorconditions", importDialog ));
	prepareImport(model.quests, $( ".import-quests", importDialog ));
	prepareImport(model.items, $( ".import-items", importDialog ));
	prepareImport(model.droplists, $( ".import-droplists", importDialog ));
	prepareImport(model.dialogue, $( ".import-dialogue", importDialog ));
	prepareImport(model.monsters, $( ".import-monsters", importDialog ));
	$( "#importsections", importDialog ).accordion({ autoHeight: false });
	
	exportDialog = $( "#templates #dialog-export" )
		.dialog({
			title: "Export data",
			modal: true,
			autoOpen: false,
			width: 800,
			buttons: buttons
		});
		
	$( "#exportsections", exportDialog ).accordion({ autoHeight: false });
}
