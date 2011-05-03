
function exportIfExists(dataStore, div) {
	var exists = false;
	if (dataStore && dataStore.items.length > 0) exists = true;
	div.toggle(exists);
	if (!exists) { return; }
	var exportData = dataStore.serialize();
	$( "#value" , div ).val(exportData);
}

function importDatastore(dataStore, content) {
	dataStore.deserialize(content);
}

function prepareImport(dataStore, div) {
	var importButton = $( "#import", div );
	var textarea = $( "#value", div );
	importButton.button({ disabled: true }).click(function() {
		if (!textarea.val()) return;
		importDatastore(dataStore, textarea.val());
		div.hide('slow');
	});
	textarea.val("").change(function() {
		var disabled = $(this).val() ? false : true;
		importButton.button( "option", "disabled", disabled );
	});
}


var importExportDialog;

function showImportDialog() {
	importExportDialog.dialog({ title: "Import data" });
	$( "div", importExportDialog ).show();
	prepareImport(model.actorConditions, $( "#actorconditions", importExportDialog ));
	prepareImport(model.quests, $( "#quests", importExportDialog ));
	prepareImport(model.items, $( "#items", importExportDialog ));
	prepareImport(model.droplists, $( "#droplists", importExportDialog ));
	prepareImport(model.dialogue, $( "#dialogue", importExportDialog ));
	prepareImport(model.monsters, $( "#monsters", importExportDialog ));
	importExportDialog.dialog( "open" );
}

function showExportDialog() {
	importExportDialog.dialog({ title: "Export data" });
	exportIfExists(model.actorConditions, $( "#actorconditions", importExportDialog ));
	exportIfExists(model.quests, $( "#quests", importExportDialog ));
	exportIfExists(model.items, $( "#items", importExportDialog ));
	exportIfExists(model.droplists, $( "#droplists", importExportDialog ));
	exportIfExists(model.dialogue, $( "#dialogue", importExportDialog ));
	exportIfExists(model.monsters, $( "#monsters", importExportDialog ));
	$( "#import", importExportDialog ).hide();
	importExportDialog.dialog( "open" );
}

