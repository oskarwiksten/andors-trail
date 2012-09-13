function IncludeJavascript(jsFile) {
	document.write('<script type="text/javascript" src="' + jsFile + '"></scr' + 'ipt>'); 
}

IncludeJavascript("FieldList.js");
IncludeJavascript("DataStore.js");

var translateFiles = [ 
	'strings.xml', 
	'strings_about.xml', 
	'content_actorconditions.xml',
	'content_conversationlist.xml',
	'content_droplist.xml',
	'content_itemlist.xml',
	'content_monsterlist.xml',
	'content_questlist.xml'
];

var translations = [ 
	{ name: 'French', id: 'values-fr', files: [ 
		'content_actorconditions.xml', 
		'content_conversationlist.xml', 
		'content_itemlist.xml', 
		'content_monsterlist.xml', 
		'content_questlist.xml',
		'strings.xml'
	]},
	{ name: 'Italian', id: 'values-it', files: [ 
		'content_actorconditions.xml', 
		'content_conversationlist.xml', 
		'content_itemlist.xml', 
		'content_questlist.xml',
		'strings.xml'
	]},
	{ name: 'German', id: 'values-de', files: [ 
		'content_actorconditions.xml', 
		'content_conversationlist.xml', 
		'content_itemlist.xml', 
		'content_monsterlist.xml', 
		'content_questlist.xml', 
		'strings_about.xml',
		'strings.xml'
	]},
	{ name: 'Hebrew', id: 'values-iw', files: [ 
		'strings.xml'
	]},
	{ name: 'Russian', id: 'values-ru', files: [ 
		'content_actorconditions.xml', 
		'content_conversationlist.xml', 
		'content_itemlist.xml', 
		'content_monsterlist.xml', 
		'content_questlist.xml',
		'strings.xml'
	]},
	{ name: 'Portugese', id: 'values-pt', files: [ 
		'content_actorconditions.xml', 
		'content_monsterlist.xml', 
		'strings.xml'
	]},
	{ name: 'Japanese', id: 'values-ja', files: [ 
		'content_conversationlist.xml', 
		'strings.xml'
	]}
];

function stepRight(divToHide, divToShow, onComplete) {
	$(divToHide).hide('slow');
	$(divToShow).fadeIn('slow', onComplete);
}
function stepLeft(divToHide, divToShow) {
	$(divToHide).fadeOut('slow');
	$(divToShow).show('slow');
}

function loadResourceFile(filename, onSuccess) {
	var url = document.location.href;
	url = url.substring(0, url.lastIndexOf('/'));
	url = url.substring(0, url.lastIndexOf('/'));
	url += "/AndorsTrail/res/" + filename;
	//var url = "http://andors-trail.googlecode.com/git/AndorsTrail/res/" + filename;
	$.get(url, function(data) {
		onSuccess(data);
	}, 'text');
}

function addSelectOption(sel, val, text) {
	if (!text) { text = val; }
	sel.append($("<option/>").attr("value", val).text(text));
}

function addSelectOptions(sel, array) {
	array.forEach(function(obj) { addSelectOption( sel , obj); } );
}

function loadStep2() {
	var possibleExistingFiles = [];
	var selectedSourceFile = $("#englishFiles").val();
	translations.forEach(function(t) {
		if ($.inArray(selectedSourceFile, t.files) > -1) {
			possibleExistingFiles.push(t.id + "/" + selectedSourceFile);
		}
	});
	$("#compareToExisting").empty();
	addSelectOptions( $("#compareToExisting"), possibleExistingFiles );
	stepRight("#validate1", "#validate2"); 
}

function pushMessage(res, msg) {
	res.class2 = "yellow"; 
	res.messages.push(msg);
	return res;
}

function isTranslatableField(fieldName) {
	return fieldName == "name" || fieldName == "logText" || fieldName == "message" || fieldName == "text";
}

function compareAndorsTrailResourceHeader(result, id, header1, header2) {
	if (header1.length != header2.length) {
		pushMessage(result, "Row \"" + id + "\" was expected to contain " + f1.length + " sub-entries, but only " + f2.length + " was found.");
		return;
	}
	for (var i = 0; i < header1._fields.length; ++i) {
		var f1 = header1._fields[i];
		var fieldName1 = header1.getFieldName(i);
		var f2 = header2._fields[i];
		var fieldName2 = header2.getFieldName(i);
		if (fieldName1 != fieldName2) {
			pushMessage(result, "Row \"" + id + "\", field \"" + fieldName + "\" was expected to contain \"" + f1 + "\", but \"" + f2 + "\" was found.");
		}
		var fieldName2 = header2.getFieldName(i);
		if (f1 instanceof FieldList) {
			compareAndorsTrailResourceHeader(result, id+":"+fieldName1, f1, f2);
		}
	}
}
function extractTranslatableFields_(id, result, fieldList, prefix, obj) {
	if (!result) {
		result = {};
		result.id = id;
		result.fields = [];
	}
	if (!prefix) {
		prefix = "";
	}
	for (var i = 0; i < fieldList._fields.length; ++i) {
		var f = fieldList._fields[i];
		if (f instanceof FieldList) {
			// f is subfieldlist
			$.each(obj[f._name], function(j, elem) {
				extractTranslatableFields_(id, result, f, prefix+f._name+"["+j+"].", elem);
			});
		} else {
			// f is field name
			if (isTranslatableField(f)) {
				result.fields.push({
					"name":prefix+f,
					"value":obj[f]
				});
			}
		}
	}
	return result;
}
function extractTranslatableFields(id, fieldList, obj) {
	return extractTranslatableFields_(id, undefined, fieldList, "", obj);
}

function compareAndorsTrailResourceRow(result, fieldList, id, obj1, obj2) {
	// Assume the headers of both objects are correctly matched
	trans1 = extractTranslatableFields(id, fieldList, obj1);
	trans2 = extractTranslatableFields(id, fieldList, obj2);
	$.each(trans1.fields, function(i, f1) {
		f2 = trans2.fields[i];
		if (f1.name != f2.name) {
			pushMessage(result, "Row \"" + id + "\", field \"" + f1.name + "\" does not match in translated data field\"" + f2.name + "\".");
		}
		if (f1.value.length > 1) {
			if (f1.value == f2.value) {
				pushMessage(result, "Row \"" + id + "\", field \"" + f1.name + "\" does not seem to be translated. Both texts are \"" + f1.value + "\".");
			}
		}
	});
}

function compareAndorsTrailResourceFormat(text1, text2) {
	var result = {
		isResource: true,
		class1: "ok",
		class2: "ok",
		messages: [],
		header: undefined,
		ds_english: undefined,
		ds_translated: undefined
	};
	
	var header1 = findHeader(text1);
	if (!header1) { return { isResource: false }; }
	result.header = header1;
	
	var header2 = findHeader(text2);
	if (!header2) { result.class2 = "red"; return result; }
	
	compareAndorsTrailResourceHeader(result, "", header1, header2);
	if (result.class2 != "ok") { return result; }
	
	var ds1 = new DataStore({});
	var ds2 = new DataStore({});
	ds1.deserialize(text1);
	ds2.deserialize(text2);
	$.each(ds1.items, function(i, obj) {
		var obj1 = obj;
		var obj2 = ds2.get(i);
		var id1 = obj1[header1._fields[0]];
		if (!obj2) { return pushMessage(result, "Row " + i + ": expected to find an object with id \"" + id1 + "\", but such row was not found."); }
		var id2 = obj2[header1._fields[0]];
		if (id2 != id1) { return pushMessage(result, "Row " + i + ": Expected to find id \"" + id1 + "\", but found \"" + id2 + "\" instead."); }
		
		compareAndorsTrailResourceRow(result, header1, id1, obj1, obj2);
	});
	
	result.ds_english = ds1;
	result.ds_translated = ds2;
	return result;
}

function applyChangeToObject(trans_obj, fieldName, newValue) {
	(new Function("x", "v", "x."+fieldName+" = v;"))(trans_obj, newValue);
}

function appendEditRow(editTable, updateHook, trans_obj, id, fieldName, english_text, translated_text) {
	if (english_text.length <= 1) {
		// there's no meaningful text
		return;
	}
	var row_id = name+"__row";
	var cell_id = name+"__cell";
	var edit_id = name+"__edit";
	var cell = $("<span />").text(translated_text)
					.attr("id", cell_id)
					.attr("class", "clickToEdit");
	var editor = $("<textarea />").val(translated_text)
					.attr("id", cell_id)
					.hide();
	cell.click(function() {
		editor.val(cell.text());
		editor.show().focus();
		cell.hide(); 
	});
	editor.blur(function() {
		var new_text = editor.val();
		cell.text(new_text);
		editor.hide(); cell.show();
		applyChangeToObject(trans_obj, fieldName, new_text);
		updateHook();
	});
	editTable.append(
		$("<tr />")
			.attr("id", name+"__row")
			.append($("<td />").text(id))
			.append($("<td />").text(fieldName))
			.append($("<td />").text(english_text))
			.append($("<td />").append(cell).append(editor))
	);
}

function clearEditTable() {
	var editTable = $("#editTranslation tbody");
	editTable.children().remove();
	$("#export_text").val("");
}

function editTranslationHandler(data2, name, resourceComparison) {
	var updateHook = function () {
		var header = resourceComparison.header;
		var ds2 = resourceComparison.ds_translated;
		var text2 = ds2.serialize();
		data2.find("string[name=\"" + name + "\"]").text(text2);
	};
	return function () {
		clearEditTable();
		var editTable = $("#editTranslation tbody");
		var header = resourceComparison.header;
		var ds1 = resourceComparison.ds_english;
		var ds2 = resourceComparison.ds_translated;
		$.each(ds1.items, function(i, obj1) {
			var obj2 = ds2.get(i);
			var id = obj1["id"];
			var english_list = extractTranslatableFields(id, header, obj1);
			var translated_list = extractTranslatableFields(id, header, obj2);
			
			$.each(english_list.fields, function(j, f1) {
				var f2 = translated_list.fields[j];
				appendEditRow(
					editTable, updateHook, obj2, id,
					f1.name, f1.value, f2.value
				);
			});
		});
	};
}
function initTranslationHandler(data1, data2, name) {
	var f = function() {
		if (!confirm("Create new translation unit in this resouce?")) {
			// if canceled
			return;
		}
		clearEditTable();
		// create new node in resource xml by copying english ver.
		var node1 = data1.find("string[name=\"" + name + "\"]");
		var text1 = node1.text();
		data2.find("resources").append($("<string name=\""+name+"\" />").text(text1));
		var resourceComparison = compareAndorsTrailResourceFormat(text1, text1);
		// repalce this handler with "edit" event handler　(and etc.)
		var editHandler = editTranslationHandler(data2, name, resourceComparison);
		$(this).parent().attr("class","yellow");
		$(this).text("Edit").unbind('click', f).click(editHandler);
		$(this).click();
	}
    return f;
}
function exportHandler(data2) {
	var xmlString = function (data) {
		var xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\n";
		xml += "<resources>\n";
		$.each(data.find("string"), function() {
			xml += "\t<string name=\""+$(this).attr("name")+"\">\n";
			xml += $(this).text();
			xml += "\n\t</string>\n\n";
		});
		xml += "</resources>\n";
		return xml;
	}
	return function () {
		$("#export_text").val(xmlString(data2));
	};
}

function appendOutputRow(outputTable, name, data1, data2) {
	if ($("#" + name, outputTable).size() > 0) return;
	
	var text1 = data1.find("string[name=\"" + name + "\"]").text();
	var text2 = data2.find("string[name=\"" + name + "\"]").text();
	var class2 = text2 ? "ok" : "red";
	var tdTranslated = $("<td />");
	if (text1) {
		if (text2) {
			var resourceComparison = compareAndorsTrailResourceFormat(text1, text2);
			if (resourceComparison.isResource) {
				class2 = resourceComparison.class2;
				if (resourceComparison.messages.length > 0 || class2 == "ok") {
					// yellow || ok
					var d = $("<span />").text("Edit");
					d.click(editTranslationHandler(data2, name, resourceComparison));
					tdTranslated.append(d);
				}
			}
		} else /* if (class2 == "red") */ { 
			var d = $("<span />").text("Init");
			d.click(initTranslationHandler(data1, data2, name));
			tdTranslated.append(d);
		}
	}
	
	outputTable.append(
		$("<tr />")
			.attr("id", name)
			.append($("<td />").text(name))
			.append(tdTranslated.attr("class", class2))
	);
}

function validateTranslation_(englishData) {
	$("#englishData").text(englishData);
	var compareData1 = $( $.parseXML(englishData) );
	var compareData2 = $( $.parseXML($("#compareToInput").val()) );
	
	var resultTable = $("#validateResultContent #result");
	var outputTable = $("tbody", resultTable );
	outputTable.empty();
	
	compareData1.find("string").each(function() {
		appendOutputRow(outputTable, $(this).attr("name"), compareData1, compareData2);
	});
	compareData2.find("string").each(function() {
		appendOutputRow(outputTable, $(this).attr("name"), compareData1, compareData2);
	});
	
	var sectionCount = $("tr", outputTable).size();
	var errors1 = sectionCount - $("td:nth-child(2).ok", outputTable).size();
	$("th #count2", resultTable).text( (errors1 > 0) ? " (" + errors1 + ")" : "" );

	$("#export").click(exportHandler(compareData2));
	
	$("#validateResultContent #loading").hide();
	$("#validateResultContent #result").show();
}
function validateTranslation(englishData) {
	try {
		validateTranslation_(englishData);
	} catch (err) {
		alert(err);
	}
}

function loadStep3() {
	var compareToContent = $("#compareToInput").val();
	if (compareToContent.length <= 0) {
		alert("You must enter (or import) some translated content to compare.");
		return;
	}
	
	$("#validateResultContent #loading").show();
	$("#validateResultContent #result").hide();
	stepRight("#validate2", "#validateResult", function() { 
		loadResourceFile( "values/" + $("#englishFiles").val() , validateTranslation )
	});
}

function startTranslationValidator() {
	addSelectOptions( $("#englishFiles"), translateFiles );
	
	$("#next1").button({ icons: {primary:'ui-icon-arrowthick-1-e'} }).click(loadStep2);
	$("#prev2").button({ icons: {primary:'ui-icon-arrowthick-1-w'} }).click(function() { stepLeft("#validate2", "#validate1"); });
	$("#btnImportFromSVN").button().click(function() {
		$("#compareToInput").val("Loading...");
		loadResourceFile( $("#compareToExisting").val(), function(data) { $("#compareToInput").val(data); } );
	});
	$("#next2").button({ icons: {primary:'ui-icon-arrowthick-1-e'} }).click(loadStep3);
	$("#prev3").button({ icons: {primary:'ui-icon-arrowthick-1-w'} }).click(function() {
		if (confirm("Leaving the result page will discard any modifications on current resource. Leave anyway?")) {
			stepLeft("#validateResult", "#validate2");
			clearEditTable();
		}
	});
	$("#export").button({ icons: {primary:'ui-icon-arrowthick-1-e'} })
}

