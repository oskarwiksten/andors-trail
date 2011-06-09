
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


function DataStore(input) {
	this.objectTypename = input.objectTypename;
	this.fieldList = input.fieldList;
	this.nameField = input.nameField ? input.nameField : 'name';
	this.idField = input.idField ? input.idField : 'id';
	this.items = [];
	
	this.add = function(obj) { 
		if (this.items.indexOf(obj) < 0) { 
			this.items.push(obj); 
			this.onAdded(obj);
		}
	}
	this.get = function(index) { return this.items[index]; }
	this.clear = function() { this.items = {}; }
	this.findById = function(id) { 
		for (var i = 0; i < this.items.length; ++i) {
			var item = this.items[i];
			if (item[this.idField] == id) return item;
		}
	}
	
	this.onAdded = function(obj) { }
	this.onNameChanged = function(obj, name) { }
	this.onDeserialized = function() { }
	
	this.deserialize = function(str) {
		var header = findHeader(str);
		if (!header) {
			alert("Could not find header row, cannot deserialize");
			return;
		}
		this.items = deserializeObjectList(header, str);
		this.onDeserialized();
	}
	this.serialize = function() {
		return serializeObjectList(this.fieldList, this.items);
	}
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
