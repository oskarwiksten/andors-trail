var ATEditor = (function(ATEditor, _) {

	var prep = {};
	prep.item = function(o) {
	};
	prep.monster = function(o) {
		ATEditor.defaults.addDefaults('monster', o);
		o.hasConversation = o.phraseID;
		o.hasHitEffect = o.hitEffect.increaseCurrentHP.min || o.hitEffect.increaseCurrentAP.min || _.some(o.hitEffect.conditionsSource) || _.some(o.hitEffect.conditionsTarget);
		o.hasCombatTraits = o.attackChance || o.attackDamage.min || o.criticalSkill || o.criticalMultiplier || o.blockChance || o.damageResistance || o.hasHitEffect;
	};
	var unprep = {};
	unprep.monster = function(o) {
		o = ATEditor.defaults.removeDefaults('monster', o);
		delete o.hasConversation;
		delete o.hasCombatTraits;
		delete o.hasHitEffect;
		return o;
	};
	
	function prepareObjectsForEditor(section, objs) {
		var p = prep[section.id];
		if (p) {
			_.each(objs, p);
		}
	}
	
	function importDataObjects(section, data, success, error) {
		if (!data || _.isEmpty(data)) {
			error("No data?");
			return;
		}
		
		var first = data;
		if (_.isArray(data)) {
			first = _.first(data);
		} else if (_.isObject(data)) {
			data = [ data ];
		} else {
			error("Malformed data? Expected array or object.");
			return;
		}
		
		if (!section.getId(first)) {
			error("Malformed data? Expected to find at least an id field, but no such field was found.");
			return;
		}
		
		prepareObjectsForEditor(section, data);
		
		_.each(data, section.add);
		success();
	};
	
	function importData(section, content, success, error) {
		if (ATEditor.legacy.findHeader(content)) {
			var data = ATEditor.legacy.deserialize(content, section);
			var convert = ATEditor.legacy.convertFromLegacyFormat[section.id];
			if (convert) {
				_.each(data, convert);
			}
			
			prepareObjectsForEditor(section, data);
			success();
			return;
		}
		
		var data;
		try {
			data = JSON.parse(content);
		} catch(e) {
			error("Unable to parse data as JSON.");
			return;
		}
		
		importDataObjects(section, data, success, error);
	};
	function exportData(section) {
		var resultObjs = [];
		var objs = section.items;
		var p = unprep[section.id];
		if (p) {
			resultObjs = _.map(objs, p);
		} else {
			resultObjs = objs;
		}
		
		return JSON.stringify(resultObjs, undefined, 2);
	};
	
	ATEditor.importExport = {
		importData: importData
		,importDataObjects: importDataObjects
		,exportData: exportData
	};
	return ATEditor;
})(ATEditor || {}, _);
