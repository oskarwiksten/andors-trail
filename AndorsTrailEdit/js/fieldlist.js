
var ATEditor = (function(ATEditor) {
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
	};

	function findHeader(str) {
		var match = str.match(FieldList_Header_linePattern);
		if (!match) return;
		return new FieldList(match[0]);
	}



	var DataStore_Data_fieldValue = '[^\\{\\}\\|]*';
	var DataStore_Data_arrayObject = '\\{(' + DataStore_Data_fieldValue + '\\|)*\\}\\s*';
	var DataStore_Data_arrayObjectPattern = new RegExp(DataStore_Data_arrayObject, 'g');
	var DataStore_Data_arrayField = '\\{\\s*(' + DataStore_Data_arrayObject + ')*\\s*\\}';
	var DataStore_Data_field = '(' + DataStore_Data_fieldValue + '|' + DataStore_Data_arrayField + ')\\|';
	var DataStore_Data_pattern = new RegExp(DataStore_Data_field, 'gm');
	var DataStore_Data_line = "^(\\{(" + DataStore_Data_field + ")*\\};)$";
	var DataStore_Data_linePattern = new RegExp(DataStore_Data_line, 'gm');

	var showErrorMessages = true;

	var specialEncodings = [
		{
			decoded: "'", decoded_Regex: /'/gm, 
			encoded: "\\'", encoded_Regex: /\\'/gm 
		},
		{
			decoded: "\n", decoded_Regex: /\n/gm, 
			encoded: "\\n", encoded_Regex: /\\n/gm
		}
	];

	var deserialize = function(str) {
		var header = findHeader(str);
		if (!header) {
			return;
		}
		return {
			header: header
			,items: deserializeObjectList(header, str)
		};
	}
	var serialize = function(dataStore) {
		return serializeObjectList(dataStore.fieldList, dataStore.items);
	}

	
	function deserializeObject(fieldList, data) {
		var match = data.match(DataStore_Data_pattern);
		if (!match) return;
		
		if (match.length != fieldList._fields.length) {
			if (showErrorMessages) {
				alert("Error parsing data object. Expected " + fieldList._fields.length + " fields, but found " + match.length + " fields.\ndata = \"" + data + "\"");
				showErrorMessages = false;
			}
			return;
		}
		
		var obj = {};
		for (var i = 0; i < fieldList._fields.length; ++i) {
			var s = match[i].match(DataStore_Data_field)[1]; // Strip trailing pipe
			
			var f = fieldList._fields[i];
			var v = s;
			var fieldName = fieldList.getFieldName(i);
			if (f instanceof FieldList) {
				fieldName = f._name;
				v = [];
				var objects = s.match(DataStore_Data_arrayObjectPattern);
				if (objects) {
					for (var j = 0; j < objects.length; ++j) {
						v[j] = deserializeObject(f, objects[j]);
					}
				}
			} else {
				for(var j = 0; j < specialEncodings.length; ++j) {
					var e = specialEncodings[j];
					v = v.replace(e.encoded_Regex, e.decoded);
				}
			}
			obj[fieldName] = v;
		}
		return obj;
	}

	function deserializeObjectList(fieldList, data) {
		var result = [];
		if(!data) return result;
		var match = data.match(DataStore_Data_linePattern);
		if(!match) return result;
		for(var i = 0; i < match.length; ++i) {
			result[i] = deserializeObject(fieldList, match[i]);
		}
		return result;
	}
		
	function serializeObject(fieldList, obj) {
		if (!obj) return "";
		var result = "{";
		
		for(var i = 0; i < fieldList._fields.length; ++i) {
			var fieldName = fieldList.getFieldName(i);
			var f = fieldList._fields[i];
			var v = obj[fieldName];
			if (f instanceof FieldList) {
				if (v && v.length > 0) {
					result += "{";
					if (v.length > 1) { result += "\n"; }
					for(var j = 0; j < v.length; ++j) {
						if (v.length > 1) { result += "\t"; }
						result += serializeObject(f, v[j]);
						if (v.length > 1) { result += "\n"; }
					}
					if (v.length > 1) { result += "\t"; }
					result += "}";
				}
			} else if (v != undefined) {
				v = "" + v;
				for(var j = 0; j < specialEncodings.length; ++j) {
					var e = specialEncodings[j];
					v = v.replace(e.decoded_Regex, e.encoded);
				}
				result += v;
			}
			result += "|";
		}
		result += "}";
		return result;
	}

	function serializeObjectList(fieldList, obj) {
		var result = fieldList.getHeader() + ";\n";
		if(!obj) return result;
		
		for(var i = 0; i < obj.length; ++i) {
			result += serializeObject(fieldList, obj[i]) + ";\n";
		}
		return result;
	}

	ATEditor.FieldList = FieldList;
	
	ATEditor.legacy = ATEditor.legacy || {};
	ATEditor.legacy.deserialize = deserialize;
	ATEditor.legacy.serialize = serialize;
	ATEditor.legacy.findHeader = findHeader;
	
	return ATEditor;
})(ATEditor || {});
