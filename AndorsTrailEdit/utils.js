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
	function cleanCopy(o, defaults) {
		if (!o) { return null; }
		o = _.clone(o);
		if (defaults) {
			removeDefaults(o, defaults);
		}
		for (var key in o) {
			if (key.charAt(0) === '$') {
				delete o[key];
			}
		}
		for (var key in o) {
			var v = o[key];
			if (!v) {
				delete o[key];
			} else if (_.isArray(v)) {
				if (!_.some(v)) {
					delete o[key];
				} else {
					o[key] = _.map(v, function(o) { cleanCopy(o); });
				}
			} else if (_.isObject(v)) {
				v = cleanCopy(v);
				if (v) {
					o[key] = v;
				} else {
					delete o[key];
				}
			}
		}
		if (!_.some(_.keys(o))) { return null; }
		return o;
	}
	
	ATEditor.utils = {
		deepClone: deepClone
		,cleanCopy: cleanCopy
	};
	
	return ATEditor;
})(ATEditor || {}, _);
