function IncludeJavascript(jsFile) {
	document.write('<script type="text/javascript" src="' + jsFile + '"></scr' + 'ipt>'); 
}

IncludeJavascript("FieldList.js");
IncludeJavascript("DataStore.js");

var translateFiles = [ 
	'strings.xml', 
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
	{ name: 'Brazilian Portugese', id: 'values-pt-rBR', files: [
		'content_actorconditions.xml',
		'content_conversationlist.xml',
		'content_itemlist.xml',
		'content_monsterlist.xml',
		'content_questlist.xml',
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

function compareAndorsTrailResourceRow(result, fieldList, id, obj1, obj2) {
	for (var i = 0; i < fieldList._fields.length; ++i) {
		var f = fieldList._fields[i];
		var fieldName = fieldList.getFieldName(i);
		if (f instanceof FieldList) {
			fieldName = f._name;
		}
		var isTranslatableField = (fieldName == "name" || fieldName == "logText" || fieldName == "message" || fieldName == "text");
		var f1 = obj1[fieldName];
		var f2 = obj2[fieldName];
		
		if (f instanceof FieldList) {
			if (!f2) { f2 = []; }
			if (f1.length != f2.length) {
				pushMessage(result, "Row \"" + id + "\", field \"" + fieldName + "\" was expected to contain " + f1.length + " sub-entries, but only " + f2.length + " was found.");
				continue;
			}
			$.each(f1, function(i, obj) {
				var id_ = id + ":" + obj[f._fields[0]];
				compareAndorsTrailResourceRow(result, f, id_, f1[i], f2[i]);
			});
		} else {
			if (isTranslatableField && f1.length > 1) {
				if (f1 == f2) {
					pushMessage(result, "Row \"" + id + "\", field \"" + fieldName + "\" does not seem to be translated. Both texts are \"" + f1 + "\".");
				}
			} else {
				if (f1 != f2) {
					pushMessage(result, "Row \"" + id + "\", field \"" + fieldName + "\" was expected to contain \"" + f1 + "\", but \"" + f2 + "\" was found.");
				}
			}
		}
	}
}

function compareAndorsTrailResourceFormat(text1, text2) {
	var result = { isResource: true, class1: "ok", class2: "ok", messages: [] };
	
	var header1 = findHeader(text1);
	if (!header1) { return { isResource: false }; }
	
	var header2 = findHeader(text2);
	if (!header2) { result.class2 = "red"; return result; }
	
	var ds1 = new DataStore({});
	var ds2 = new DataStore({});
	ds1.deserialize(text1);
	ds2.deserialize(text2);
	$.each(ds1.items, function(i, obj) {
		var obj1 = obj;
		var obj2 = ds2.get(i);
		var id1 = obj1[header1._fields[0]];
		if (!obj2) { return pushMessage(result, "Row " + i + ": expected to find an object with id \"" + id1 + "\", but such row was found."); }
		var id2 = obj2[header1._fields[0]];
		if (id2 != id1) { return pushMessage(result, "Row " + i + ": Expected to find id \"" + id1 + "\", but found \"" + id2 + "\" instead."); }
		
		compareAndorsTrailResourceRow(result, header1, id1, obj1, obj2);
	});
	return result;
}

function appendOutputRow(outputTable, name, data1, data2) {
	if ($("#" + name, outputTable).size() > 0) return;
	
	var text1 = data1.find("string[name=\"" + name + "\"]").text();
	var text2 = data2.find("string[name=\"" + name + "\"]").text();
	var class1 = text1 ? "ok" : "red";
	var class2 = text2 ? "ok" : "red";
	var tdTranslated = $("<td />");
	if (text1 && text2) {
		var resourceComparison = compareAndorsTrailResourceFormat(text1, text2);
		if (resourceComparison.isResource) {
			class1 = resourceComparison.class1;
			class2 = resourceComparison.class2;
			if (resourceComparison.messages.length > 0) {
				var errorList = $("<ul />").attr("id", "validationWarnings");
				$.each(resourceComparison.messages, function(i, msg) {
					errorList.append($("<li />").text(msg));
				});
				var d = $("<span />").attr("id", "showValidationWarnings").text("Expand");
				d.click(function() {
					d.hide();
					errorList.show();
				});
				tdTranslated.append(d);
				tdTranslated.append(errorList);
				errorList.hide();
			}
		}
	}
	
	outputTable.append(
		$("<tr />")
			.attr("id", name)
			.append($("<td />").text(name))
			.append($("<td />").attr("class", class1))
			.append(tdTranslated.attr("class", class2))
	);
}

function validateTranslation_(englishData) {
	$("#englishData").text(englishData);
	var compareData1 = $( englishData );
	var compareData2 = $( $("#compareToInput").val() );
	
	var resultTable = $("#validateResultContent table");
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
	var errors2 = sectionCount - $("td:nth-child(3).ok", outputTable).size();;
	$("th #count1", resultTable).text( (errors1 > 0) ? " (" + errors1 + ")" : "" );
	$("th #count2", resultTable).text( (errors2 > 0) ? " (" + errors2 + ")" : "" );
	
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
	$("#prev3").button({ icons: {primary:'ui-icon-arrowthick-1-w'} }).click(function() { stepLeft("#validateResult", "#validate2"); });
}
