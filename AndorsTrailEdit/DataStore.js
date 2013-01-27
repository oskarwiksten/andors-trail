
var DataStore = (function(_) {
	function DataStore(options) {
		var defaultOptions = {
			nameField: 'name'
			,idField: 'id'
			,iconIDField: 'iconID'
			,newItemTemplate: function() { return {}; }
		};
		options = _.extend(defaultOptions, options);

		this.items = [];

		this.name = options.name;
		this.objectTypename = options.objectTypename;
		this.legacyFieldList = options.legacyFieldList;
		
		this.findById = function(id) { 
			return _.find(this.items, function(obj) { return obj[options.idField] === id; });
		};
		this.hasObjectWithId = function(id) { 
			return _.some(this.items, function(obj) { return obj[options.idField] === id; });
		};
		this.hasIcon = function() { return iconIDField; };
		this.getIcon = function(obj) { return obj[options.iconIDField]; };
		this.getId = function(obj) { return obj[options.idField]; };
		this.getName = function(obj) { 
			return obj[options.nameField]; 
		};
		this.addNew = function() {
			var obj = options.newItemTemplate();
			this.ensureUniqueId(obj);
			this.items.push(obj);
			return obj;
		};
		this.add = function(o) { 
			this.items.push(o);
		};
		this.clone = function(o) { 
			var obj = _.extend({}, o);
			this.ensureUniqueId(obj);
			this.items.push(obj);
			return obj;
		};
		this.remove = function(o) { 
			this.items = _.without(this.items, o);
		};
		

		this.findFirstFreeId = function(id) {
			var i = 0;
			var result = id;
			while(this.hasObjectWithId(result)) {
				i = i + 1;
				result = id + i;
			}
			return result;
		};
		this.ensureUniqueId = function(obj) {
			obj[options.idField] = this.findFirstFreeId(obj[options.idField]);
		};
	}


	return DataStore;
})(_);
