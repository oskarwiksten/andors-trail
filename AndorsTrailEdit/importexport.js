var ATEditor = (function(ATEditor, _) {

	var prep = {};
	prep.actorcondition = function(o) {
		o.hasRoundEffect = ATEditor.utils.hasValues(_.omit(o.roundEffect, 'visualEffectID'));
		o.hasFullRoundEffect = ATEditor.utils.hasValues(_.omit(o.fullRoundEffect, 'visualEffectID'));
		o.abilityEffect.hasCritical = o.abilityEffect.increaseCriticalSkill || o.abilityEffect.setCriticalMultiplier;
		o.hasAbilityEffect = ATEditor.utils.hasValues(o.abilityEffect);
	};
	prep.quest = function(o) {
	};
	prep.item = function(o) {
		o.hasEquipEffect = ATEditor.utils.hasValues(o.equipEffect);
		o.equipEffect.hasCritical = o.equipEffect.increaseCriticalSkill || o.equipEffect.setCriticalMultiplier;
		o.hasUseEffect = ATEditor.utils.hasValues(o.useEffect);
		o.hasHitEffect = ATEditor.utils.hasValues(o.hitEffect);
		o.hasKillEffect = ATEditor.utils.hasValues(o.killEffect);
	};
	prep.droplist = function(o) {
	};
	prep.dialogue = function(o) {
	};
	prep.monster = function(o) {
		o.hasConversation = o.phraseID;
		o.hasHitEffect = o.hitEffect.increaseCurrentHP.min || o.hitEffect.increaseCurrentAP.min || _.some(o.hitEffect.conditionsSource) || _.some(o.hitEffect.conditionsTarget);
		o.hasCombatTraits = o.attackChance || o.attackDamage.min || o.criticalSkill || o.criticalMultiplier || o.blockChance || o.damageResistance || o.hasHitEffect;
	};
	prep.itemcategory = function(o) {
	};
	
	function prepareObjectsForEditor(section, objs) {
		var p = prep[section.id];
		_.each(objs, function(o) {
			ATEditor.defaults.addDefaults(section.id, o);
			if (p) { p(o); }
			ATEditor.utils.convertIntegersToStrings(o);
		});
	}
	
	var unprep = {};
	unprep.actorcondition = function(o) {
		if (!o.hasRoundEffect) { delete o.roundEffect; }
		if (!o.hasFullRoundEffect) { delete o.fullRoundEffect; }
		if (!o.hasAbilityEffect) { delete o.abilityEffect; }
		delete o.hasRoundEffect;
		delete o.hasFullRoundEffect;
		if (o.abilityEffect) { 
			if (!o.abilityEffect.hasCritical) { 
				delete o.abilityEffect.increaseCriticalSkill; 
				delete o.abilityEffect.setCriticalMultiplier; 
			}
			delete o.abilityEffect.hasCritical; 
		}
		delete o.hasAbilityEffect;
	};
	unprep.quest = function(o) {
	};
	unprep.item = function(o) {
		if (!o.hasEquipEffect) { delete o.equipEffect; }
		if (o.equipEffect) { 
			if (!o.equipEffect.hasCritical) { 
				delete o.equipEffect.increaseCriticalSkill; 
				delete o.equipEffect.setCriticalMultiplier; 
			}
			delete o.equipEffect.hasCritical; 
		}
		if (_.isObject(o.category)) { o.category = o.category.id; }
		if (!o.hasUseEffect) { delete o.useEffect; }
		if (!o.hasHitEffect) { delete o.hitEffect; }
		if (!o.hasKillEffect) { delete o.killEffect; }
		delete o.hasEquipEffect;
		delete o.hasUseEffect;
		delete o.hasHitEffect;
		delete o.hasKillEffect;
	};
	unprep.droplist = function(o) {
	};
	unprep.dialogue = function(o) {
	};
	unprep.monster = function(o) {
		if (!o.hasCritical) { 
			delete o.criticalSkill; 
			delete o.criticalMultiplier; 
		}
		delete o.hasCritical; 
		delete o.hasConversation;
		delete o.hasCombatTraits;
		delete o.hasHitEffect;
	};
	unprep.itemcategory = function(o) {
	};
	
	function prepareObjectsForExport(section, objs) {
		var p = unprep[section.id];
		return _.map(objs, function(o) {
			o = ATEditor.utils.deepClone(o);
			ATEditor.utils.removeAngularFields(o);
			if (p) { p(o); }
			ATEditor.utils.convertStringsToIntegers(o);
			ATEditor.defaults.removeDefaults(section.id, o);
			ATEditor.utils.compact(o);
			return o;
		});
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
		var data = ATEditor.legacy.deserialize(content);
		if (data) {
			data = data.items;
			var convert = ATEditor.legacy.convertFromLegacyFormat[section.id];
			if (convert) {
				data = _.map(data, convert);
			}
		} else {
			try {
				data = JSON.parse(content);
			} catch(e) {
				error("Unable to parse data as JSON.");
				return;
			}
		}
		
		importDataObjects(section, data, success, error);
	};
	function exportData(section) {
		var objs = section.items;
		var resultObjs = prepareObjectsForExport(section, objs);
		return JSON.stringify(resultObjs, undefined, 2);
	};
	
	ATEditor.importExport = {
		importData: importData
		,importDataObjects: importDataObjects
		,exportData: exportData
	};
	return ATEditor;
})(ATEditor || {}, _);
