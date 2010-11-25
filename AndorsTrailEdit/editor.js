
var updatingOutput = false;
var parsingOutput = false;

function updateOutput() {
	if (parsingOutput) return;
	updatingOutput = true;
	var rowcontainer = $("#datarows");
	var op = "";
	op += "[";
	for (var i = 0; i < fields.length; i++) {
		if (fields[i].type == "table") continue;
		op += fields[i].name + "|";
	}
	op += "];\n";
	$('tr', rowcontainer).each(function(i){
		var row = $(this);
		if (row.find("#" + fields[0].name).size() == 0) return;
		op += "{";
		for (var i = 0; i < fields.length; i++) {
			row.find("#" + fields[i].name).each(function() { op += $(this).val() + "|"; });
		}
		op += "};\n";
	});
	$("#result").val(op);
	updatingOutput = false;
}

var parse_currentRow;
var parse_fieldDef;

function parseLine(_, line) {
	var match = line.match(/(.*?)\|/gm);
	if (match.length != parse_fieldDef.length) {
		alert("ERROR: Cannot parse row, expected " + parse_fieldDef.length + " fields, but found " + match.length + " fields.");
		return;
	}
	for (var i = 0; i < match.length; i++) {
		fields[parse_fieldDef[i]].value = match[i].replace(/\|/, '');
	}
	addRow();
}

function parseFieldDef(str) {
	var fielddef = str.match(/\[(.+?)\];/gm);
	parse_fieldDef = [];
	if (fielddef && fielddef.length >= 1) {
		fielddef = fielddef[0].match(/(.*?)\|/gm);
		for (var i = 0; i < fielddef.length; i++) {
			var fieldname = fielddef[i].replace(/[\[\]\|]/g, '');
			for (var j = 0; j < fields.length; j++) {
				if (fields[j].name == fieldname) {
					parse_fieldDef[i] = j;
					break;
				}
			}
		}
	} else {
		for (var i = 0; i < fields.length; i++) {
			parse_fieldDef[i] = i;
		}
	}
}

function parseOutput() {
	if (updatingOutput) return;
	parsingOutput = true;
	$("#datarows").html("");
	var str = $("#result").val();
	parseFieldDef(str);
	str.replace(/\{(.+?)\};/gm, parseLine);
	parsingOutput = false;
}
	
function createField(field) {
	if (field.value == undefined) field.value = "";
	return "<td>" + eval("createField_" + field.type + "(field);") + "</td>";
}

function createTextField(field, size) {
	return "<input type=\"text\" id=\"" + field.name + "\" value=\"" + field.value + "\" size=\"" + size + "\" />";
}

function createField_multiline(field) { 
	return "<textarea id=\"" + field.name + "\" cols=\"30\" rows=\"4\">" + field.value + "</textarea>";
}
function createField_longtext(field) { return createTextField(field, 20); }
function createField_text(field) { return createTextField(field, 10); }
function createField_size(field) { return createTextField(field, 5); }
function createField_range(field) { return createTextField(field, 5); }
function createField_int(field) { return createTextField(field, 3); }
function createField_table(field) { 
	var result = "<table cellspacing=\"0\"><thead><tr>";
	for (var i = 0; i < field.fields.length; i++) {
		result += "<th>" + field.fields[i].name  + "</th>";
	}
	result += "</tr></thead><tbody>";
	for (var j = 0; j < field.length; j++) {
		result += "<tr>";
		for (var i = 0; i < field.fields.length; i++) {
			var f = fields[field.startindex + (j * field.fields.length) + i];
			result += createField(f);
		}
		result += "</tr>";
	}
	result += "</tbody></table>";
	return result;
}

function createField_image(field) {
	return "<div id=\"image_" + field.name + "\" class=\"selectimage\"><input type=\"hidden\" id=\"" + field.name + "\" value=\"" + field.value + "\" /></div>";
}

function createField_select(field) {
	var result = "<select id=\"" + field.name + "\">";
	for (var i = 0; i < field.values.length; i++) {
		var value = field.values[i];
		var name = value;
		var v = value.split(':');
		if (v.length >= 2) {
			name = v[0];
			value = v[1];
		}
		result += "<option value=\"" + value + "\"";
		if (field.value == value) {
			result += " selected=\"selected\"";
		}
		result += ">" + name + "</option>";
	}
	result += "</select>";
	return result;
}

function parseimageid(str) {
	var defaultimage = {image: "defaultimage", lid: 0, path: '' };
	if (str == null || str == "") return defaultimage;
	var v = str.split(":");
	if (v.length < 1) return defaultimage;
	return {image: v[0], lid: v[1], path: imagepath };
}
function genimageid(v) {
	if (v == null) return "";
	return v.image + ":" + v.lid;
}

var currentIcon;

function selectimage(v) {
	currentIcon = $(this);
	$( "#selecticon_dialog" ).dialog("open");
}

function findTileImage(name) {
	for (var i = 0; i < tileimages.length; i++) {
		if (tileimages[i].name == name) {
			return tileimages[i];
		}
	}
	return 0;
}

function updateImageFromFormField(control) {
	var imagestruct = parseimageid($(control).find("input").val());
	var sourceimage = findTileImage(imagestruct.image);
	var x = -(imagestruct.lid % sourceimage.numtilesx) * sourceimage.tilesizex;
	var y = -Math.floor(imagestruct.lid / sourceimage.numtilesx) * sourceimage.tilesizey;
	control.css({"background-image": "url(" +imagestruct.path + imagestruct.image + ".png)", "background-position": x+"px " + y+"px"});
}

function setSelectedImage(control, imagestruct) {
	$(control).find("input").val(genimageid(imagestruct));
	updateImageFromFormField(control);
	$(control).change();
}

function addDefaultRow() { 
	for (var i = 0; i < fields.length; i++) {
		fields[i].value = fields[i].default;
	}
	addRow();
}
function addNameRow(tagname) {
	var result = "";
	for (var i = 0; i < fields.length; i++) {
		if (!fields[i].hide) {
			result += "<" + tagname + ">" + fields[i].name + "</" + tagname + ">";
		}
	}
	return result;
}

function addRow() {
	var str = "";
	for (var i = 0; i < fields.length; i++) {
		if (!fields[i].hide) {
			str += createField(fields[i]);
		}
	}
	$("#datarows").append("<tr class='rowdiv'>" + str + "</tr>");
	$("#datarows").find(".selectimage").click(selectimage);
	$("#datarows").find(".selectimage").each(function() { updateImageFromFormField($(this)); });
	
	if ($("#datarows > tr").size() % 10 == 0) {
		$("#datarows").append("<tr>" + addNameRow("td") + "</tr>");
	}
	$("#datarows").change();
	
}

function explodeTableFields() {
	var moreFields = [];
	for (var n = 0; n < fields.length; n++) {
		var field = fields[n];
		if (field.type == "table") {
			field.startindex = fields.length;
			for (var j = 0; j < field.length; j++) {
				for (var i = 0; i < field.fields.length; i++) {
					var f = jQuery.extend(true, {}, field.fields[i]); // Deep copy
					f.hide = 1;
					f.name += j;
					fields.push(f);
				}
			}
		}
	}
}

$(document).ready(function() {
	explodeTableFields();
	
	$("#result").resizable();
	$("#inputarea").resizable();
	
	var headerrow = $("#headerrow");
	headerrow.append(addNameRow("th"));

	var hasWarning = false;
	for (var i = 0; i < tileimages.length; i++) {
		tileimages[i].url = imagepath + tileimages[i].name + ".png";
		var img = new Image();
		img.src = tileimages[i].url;
		tileimages[i].imgwidth = img.width;
		tileimages[i].imgheight = img.height;
		if (!hasWarning && (tileimages[i].imgwidth <= 0 || tileimages[i].imgheight <= 0)) {
			alert("WARNING: could not load tile image " + tileimages[i].url + " . You may need to reload the page once.");
			hasWarning = true;
		}
		tileimages[i].tilesizex = Math.floor(tileimages[i].imgwidth / tileimages[i].numtilesx);
		tileimages[i].tilesizey = Math.floor(tileimages[i].imgheight / tileimages[i].numtilesy);
		
		$("#selecticon_dialog_tileset").append("<img src=\"" + tileimages[i].url + "\" id=\"" + tileimages[i].name + "\" /><br />");		
	}
	
	$("#selecticon_dialog_tileset img").each(function() {
		var img = findTileImage($(this).attr("id"));
		$(this).click(function(e) {
			var x = e.pageX - $(this).offset().left;
			var y = e.pageY - $(this).offset().top;
			setSelectedImage(currentIcon, {
				image: img.name, 
				lid: Math.floor(x / img.tilesizex) + img.numtilesx * Math.floor(y / img.tilesizey)
			});
			$( "#selecticon_dialog" ).dialog("close");
		});
	});
	
	$( "#selecticon_dialog" ).dialog({
		autoOpen: false,
		modal: true,
		width: 600,
		height: 800,
		position: [50,50],
		buttons: {
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		}
	});
	
	$("#buttons").append("<input type=\"button\" value=\"New\" id=\"newrow\" class=\"editorbutton\" />");
	$("#newrow").click(addDefaultRow).click();
	
	$("#buttons").append("<input type=\"checkbox\" id=\"autooutput\" checked=\"checked\" /><span class=\"editorbutton\" >Auto update output</span>");
	$("#autooutput").change(function() {
		if ($(this).attr('checked')) {
			$("#datarows").change(updateOutput);
			$("#result").change(parseOutput);
		} else {
			$("#datarows").unbind("change");
			$("#result").unbind("change");
		}
	}).change();
	
	$("#datarows").sortable();

	//$("#updateoutput").click(updateOutput);
	//$("#parseoutput").click(parseOutput);
});
