
function createDroplistEditor(obj) {
	var div = $( "#templates #editDroplist" ).clone(true);
	applyCommonEditorBindings(div, obj, model.droplists);
	if (!obj.items) obj.items = [];
	
	applyTableEditor({
		table: $( "#items", div ),
		dialog: droplistItemDialog, 
		array: obj.items, 
		templateFunction: function() { return { quantity: 1, chance: 100 } }, 
		editorSetup: function(div) {
			bindFieldToDataStore( $( "#itemID", div ), model.items , function(obj) { return obj.searchTag; } );
		}
	});
	
	return div;
}
