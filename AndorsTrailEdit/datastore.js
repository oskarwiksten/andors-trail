var ATEditor = (function(ATEditor, _) {
	
	ATEditor.DataStore = function(options) {
		var defaultOptions = {
			nameField: 'name'
			,idField: 'id'
		};
		_.defaults(options, defaultOptions);

		var items = [];
		this.items = items;

		this.name = options.name;
		this.id = options.id;
		
		this.findById = function(id) { 
			return _.find(this.items, function(obj) { return obj[options.idField] === id; });
		};
		this.hasObjectWithId = function(id) { 
			return _.some(this.items, function(obj) { return obj[options.idField] === id; });
		};
		this.hasIcon = function() { return _.toBool(options.iconIDField); };
		this.getIcon = function(obj) { return obj[options.iconIDField]; };
		this.getId = function(obj) { return obj[options.idField]; };
		this.getName = function(obj) { 
			return obj[options.nameField]; 
		};
		this.addNew = function() {
			var obj = { };
			obj[options.idField] = 'new_' + options.id;
			if (options.idField != options.nameField) {
				obj[options.nameField] = 'New ' + options.id;
			}
			this.ensureUniqueId(obj);
			items.push(obj);
			return obj;
		};
		this.add = function(o) { 
			items.push(o);
		};
		this.clone = function(o) { 
			var obj = ATEditor.utils.deepClone(o);
			this.ensureUniqueId(obj);
			items.push(obj);
			return obj;
		};
		this.remove = function(o) {
			var idx = items.indexOf(o);
			if (idx >= 0) {
				items.splice(idx, 1);
			}
		};
		this.clear = function() {
			items = [];
			this.items = items;
		};
		
		this.findFirstFreeId = function(id) {
			if(!this.hasObjectWithId(id)) {
				return id;
			}
			
			var prefix;
			var n = 1;
			
			var match = (/^(.*\D)(\d+)$/g).exec(id);
			if (match) {
				prefix = match[1];
				n = parseInt(match[2]) + 1;
			} else {
				prefix = id + "_";
			}
			
			var result = prefix + n;
			while(this.hasObjectWithId(result)) {
				n = n + 1;
				result = prefix + n;
			}
			return result;
		};
		this.ensureUniqueId = function(obj) {
			obj[options.idField] = this.findFirstFreeId(obj[options.idField]);
		};
	};

	return ATEditor;
})(ATEditor || {}, _);
