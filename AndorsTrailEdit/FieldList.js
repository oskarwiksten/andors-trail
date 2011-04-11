
var FieldList_Header_fieldName = '[^\\[\\]\\|]*';
var FieldList_Header_arrayField = FieldList_Header_fieldName + '\\[(' + FieldList_Header_fieldName + '\\|)*\\]';
var FieldList_Header_arrayFieldName = new RegExp(FieldList_Header_fieldName);
var FieldList_Header_field = '(' + FieldList_Header_fieldName + '|' + FieldList_Header_arrayField + ')\\|';
var FieldList_Header_pattern = new RegExp(FieldList_Header_field, 'g');
var FieldList_Header_line = "^(\\[(" + FieldList_Header_field + ")*\\];)$";
var FieldList_Header_linePattern = new RegExp(FieldList_Header_line, 'm');

function FieldList(header, name) {
	this._name = name ? name : "";
	this._fields = [];
	
	var match = header.match(FieldList_Header_pattern);
	if (!match) return;
	
	for (var i = 0; i < match.length; ++i) {
		var s = match[i].match(FieldList_Header_field)[1]; // Strip trailing pipe
		
		var f = s;
		if (s.match(FieldList_Header_arrayField)) {
			var name = s.match(FieldList_Header_arrayFieldName)[0];
			f = new FieldList(s, name);
		}
		this._fields[i] = f;
	}
	
	this.getFieldName = function(i) {
		var f = this._fields[i];
		if (f instanceof FieldList) {
			return f._name;
		} else {
			return f;
		}
	}
		
	this.getHeader = function() {
		var result = this._name + "[";
		for(var i = 0; i < this._fields.length; ++i) {
			var f = this._fields[i];
			if (f instanceof FieldList) {
				result += f.getHeader();
			} else {
				result += f;
			}
			result += "|";
		}
		result += "]";
		return result;
	}
	
	this.getHeaderLine = function() {
		return this.getHeader() + ";";
	}
}

function findHeader(str, name) {
	var match = str.match(FieldList_Header_linePattern);
	if (!match) return;
	return new FieldList(match[0]);
}

