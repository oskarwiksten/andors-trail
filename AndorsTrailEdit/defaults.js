var ATEditor = (function(ATEditor, _) {

	var defaults = {
		monster: {
			size: "1x1"
			,maxHP: 1
			,maxAP: 10
			,moveCost: 10
			,unique: 0
			,monsterClass: 0
			,attackDamage: {}
			,hitEffect: { increaseCurrentHP: {}, increaseCurrentAP: {}, conditionsSource: [], conditionsTarget: [] }
		}
	};
	
	ATEditor.defaults = {
		addDefaults: function(type, o) {
			if (defaults[type]) {
				var copyOfDefaults = ATEditor.utils.deepClone(defaults[type]);
				_.defaults(o, copyOfDefaults);
			}
		},
		removeDefaults: function(type, o) {
			return ATEditor.utils.cleanCopy(o, defaults[type]);
		}
	};

	return ATEditor;
})(ATEditor || {}, _);
