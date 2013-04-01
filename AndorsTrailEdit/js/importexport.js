var ATEditor = (function(ATEditor, _) {

	var prep = {};
	prep.actorcondition = function(o) {
		o.hasRoundEffect = ATEditor.utils.hasValues(_.omit(o.roundEffect, 'visualEffectID'));
		o.hasFullRoundEffect = ATEditor.utils.hasValues(_.omit(o.fullRoundEffect, 'visualEffectID'));
		o.hasAbilityEffect = ATEditor.utils.hasValues(o.abilityEffect);
	};
	prep.quest = function(o) {
	};
	prep.item = function(o) {
		o.hasEquipEffect = ATEditor.utils.hasValues(o.equipEffect);
		o.hasUseEffect = ATEditor.utils.hasValues(o.useEffect);
		o.hasHitEffect = ATEditor.utils.hasValues(o.hitEffect);
		o.hasKillEffect = ATEditor.utils.hasValues(o.killEffect);
	};
	prep.droplist = function(o) {
	};
	prep.dialogue = function(o) {
		o.hasRewards = ATEditor.utils.hasValues(o.rewards);
		if (o.replies.length === 1 && o.replies[0].text === "N") {
			o.nextPhraseID = o.replies[0].nextPhraseID;
			o.hasOnlyNextReply = true;
		} else {
			o.nextPhraseID = "";
			o.hasOnlyNextReply = false;
		}
		_.each(o.replies, function(reply) {
			ATEditor.defaults.addDefaults('reply', reply);
			if (reply.nextPhraseID && reply.nextPhraseID.length === 1) { reply.replyLeadsTo = reply.nextPhraseID; }
			reply.requiresItems = ATEditor.utils.hasValues(reply.requires.item);
			reply.requiresQuest = _.toBool(reply.requires.progress);
		});
	};
	prep.monster = function(o) {
		o.hasConversation = _.toBool(o.phraseID);
		o.hasHitEffect = _.toBool(o.hitEffect.increaseCurrentHP.min || o.hitEffect.increaseCurrentAP.min || _.some(o.hitEffect.conditionsSource) || _.some(o.hitEffect.conditionsTarget));
		o.hasCritical = _.toBool(o.criticalSkill || o.criticalMultiplier);
		o.hasCombatTraits = _.toBool(o.attackChance || o.attackDamage.min || o.hasCritical || o.blockChance || o.damageResistance || o.hasHitEffect);
		o.showAdvanced = _.toBool(o.faction || (o.size != '1x1'));
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
		delete o.hasAbilityEffect;
	};
	unprep.quest = function(o) {
	};
	unprep.item = function(o) {
		if (!o.hasEquipAPEffect) { 
			var e = o.equipEffect;
			delete e.increaseMaxAP;
			delete e.increaseMoveCost;
			delete e.increaseUseItemCost;
			delete e.increaseReequipCost; 
		}
		delete o.hasEquipAPEffect;
		if (!o.hasEquipEffect) { delete o.equipEffect; }
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
		if (!o.hasRewards) { delete o.rewards; }
		delete o.hasRewards;
		_.each(o.replies, function(reply) {
			if (reply.replyLeadsTo) { reply.nextPhraseID = reply.replyLeadsTo; }
			delete reply.replyLeadsTo;
			var requires = reply.requires;
			if (!reply.requiresItems) { delete requires.item; }
			delete reply.requiresItems;
			if (!reply.requiresQuest) { delete requires.progress; }
			delete reply.requiresQuest;
		});
		if (o.hasOnlyNextReply) {
			o.replies = [ { text: "N", nextPhraseID: o.nextPhraseID } ];
		}
		delete o.nextPhraseID;
		delete o.hasOnlyNextReply;
		delete o.tree;
	};
	unprep.monster = function(o) {
		if (!o.hasCritical) { 
			delete o.criticalSkill; 
			delete o.criticalMultiplier; 
		}
		if (!o.showAdvanced) { 
			delete o.faction; 
			delete o.size; 
		}
		delete o.showAdvanced;
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
			if (error) { error("No data?"); }
			return;
		}
		
		var first = data;
		if (_.isArray(data)) {
			first = _.first(data);
		} else if (_.isObject(data)) {
			data = [ data ];
		} else {
			if (error) { error("Malformed data? Expected array or object."); }
			return;
		}
		
		if (!section.getId(first)) {
			if (error) { error("Malformed data? Expected to find at least an id field, but no such field was found."); }
			return;
		}
		
		prepareObjectsForEditor(section, data);
		
		_.each(data, section.add);
		if (success) { success(); }
	};
	
	function importText(section, content, success, error) {
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
				if (error) { error("Unable to parse data as JSON."); }
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
		importText: importText
		,importDataObjects: importDataObjects
		,exportData: exportData
		,prepareObjectsForEditor: prepareObjectsForEditor
	};
	return ATEditor;
})(ATEditor || {}, _);
