
function changeHidesElement(changedElement, elementToHide, visibilityEvaluator) {
	changedElement.change(function () {
		if (visibilityEvaluator()) { 
			elementToHide.fadeIn("slow");
		} else {
			elementToHide.fadeOut("slow");
		}
	});
	elementToHide.toggle(visibilityEvaluator());
}

function checkboxHidesElement(checkbox, element, visibleCondition) {
	var visible = bool(visibleCondition);
	checkbox.attr("checked", visible);
	var evaluator = function() { return checkbox.attr("checked"); };
	changeHidesElement(checkbox, element, evaluator);
}

function checkboxShowsElement(checkbox, element, visibleCondition) {
	var visible = !bool(visibleCondition);
	checkbox.attr("checked", visible);
	var evaluator = function() { return !checkbox.attr("checked"); };
	changeHidesElement(checkbox, element, evaluator);
}

function bool(v) {
	if (typeof(v) == 'undefined') return false;
	if (v == '') return false;
	if (v == '0') return false;
	if (v == 'false') return false;
	return true;
}

function setInputFieldsToObjectValues(div, obj) {
	div.find("input,select,textarea").each(function() {
		$(this).val(obj[$(this).attr("id")]);
	});
	div.find("input:checkbox").each(function() {
		$(this).attr("checked", bool(obj[$(this).attr("id")]));
	});
}

function bindInputFieldChangesToObject(div, obj) {
	div.find("input,select,textarea").unbind("change").change(function() {
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
	if (dataStore) {
		div.find("#" + dataStore.nameField).change(function() { dataStore.onNameChanged(obj, $(this).val()); });
	}
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
	field.autocomplete( "destroy" ).autocomplete({ 
		source: dataCallback, 
		minLength: 0,
		select: function(event, ui) { 
			field.val(ui.item.value);
			field.change();
		}
	});
}

function applyTableEditor(input) {
	
	var updateRowText = function(row, obj) {
		$( "td", row ).each(function() {
			var id = $( this ).attr("id");
			var val = obj[id];
			val = val ? val : "";
			$( this ).text(val).shorten({
				 width: '200'
			}).css('display','');
		});
	};
	
	var onItemSelected = input.onItemSelected ? input.onItemSelected : function(obj, row) { 
		var dialog = input.dialog;
		applyEditorBindingsForObject( dialog, obj );
		if (input.editorSetup) { input.editorSetup(dialog); }
		dialog.unbind( "dialogclose" ).bind( "dialogclose", function() { 
			updateRowText(row, obj); 
		});
		dialog.dialog( "open" );
	};
	
	var table = input.table;
	var addToList = function(obj) {
		var row = $( "<tr>" );
		table.find("th").each(function() {
			var id = $( this ).attr("id");
			row.append( $( "<td>" ).attr("id", id) );
		});
		updateRowText(row, obj);
		table.append(row);
		row.click(function() { onItemSelected(obj, row); });
		return row;
	};
	table.parent().find("#add").button().unbind("click").click(function() {
		var obj = input.templateFunction();
		input.array.push( obj );
		if (input.onItemAdded) { input.onItemAdded(obj); }
		addToList( obj ).click();
	});
	table.addClass("ui-corner-all");
	$( "thead", table ).addClass("ui-widget-header");
	$( "tbody", table ).empty();
	input.array.forEach(addToList);
}

