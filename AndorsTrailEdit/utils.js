var ATEditor = (function(ATEditor, _) {
	
	function deepClone(o) {
		// https://github.com/documentcloud/underscore/issues/162
		return JSON.parse(JSON.stringify(o));
	}
		
	function removeDefaults(o, defaults) {
		for (var key in defaults) {
			if (o[key] === defaults[key]) {
				delete o[key];
			}
		}
	}
		
	function copyDefaults(o, defaults) {
		var key;
		for (key in defaults) {
			var v = defaults[key];
			if (!o[key]) {
				o[key] = v;
			} else if (_.isObject(v)) {
				copyDefaults(o[key], v);
			}
		}
	}
	
	function removeAngularFields(o) {
		var key;
		for (key in o) {
			var v = o[key];
			if (key.charAt(0) === '$') {
				delete o[key];
			} else if (_.isArray(v) || _.isObject(v)) {
				removeAngularFields(v);
			}
		}
	}
	function compact(o) {
		if (!o) { return null; }
		var key;
		for (key in o) {
			var v = o[key];
			if (_.isArray(v)) {
				v = _.map(v, compact);
			} else if (_.isObject(v)) {
				v = compact(v);
			}
			if (!hasValues(v)) { 
				delete o[key];
			} else {
				o[key] = v;
			}
		}
		if (!hasValues(o)) { return null; }
		return o;
	}
	
	function hasValues(o) {
		if (_.isArray(o)) {
			return _.some(o, hasValues);
		} else if (_.isObject(o)) {
			var key;
			for (key in o) {
				if (hasValues(o[key])) { 
					return true; 
				}
			}
			return false;
		}
		return o;
	}
	
	function convertStringsToIntegers(o) {
		var key;
		for (key in o) {
			var v = o[key];
			if (_.isString(v)) {
				v = parseInt(v);
				if (!_.isNaN(v)) {
					o[key] = v;
				}
			} else if (_.isArray(v)) {
				convertStringsToIntegers(v);
			} else if (_.isObject(v)) {
				convertStringsToIntegers(v);
			}
		}
	}
	
	function convertIntegersToStrings(o) {
		var key;
		for (key in o) {
			var v = o[key];
			if (_.isNumber(v)) {
				o[key] = String(v);
			} else if (_.isArray(v)) {
				convertIntegersToStrings(v);
			} else if (_.isObject(v)) {
				convertIntegersToStrings(v);
			}
		}
	}
	
	ATEditor.utils = {
		deepClone: deepClone
		,removeDefaults: removeDefaults
		,copyDefaults: copyDefaults
		,removeAngularFields: removeAngularFields
		,compact: compact
		,hasValues: hasValues
		,convertStringsToIntegers: convertStringsToIntegers
		,convertIntegersToStrings: convertIntegersToStrings
	};
	
	return ATEditor;
})(ATEditor || {}, _);
