var ATEditor = (function(ATEditor, _) {

	var defaults = {
		actorcondition: {
			isStacking: 0
			,isPositive: 0
			,roundEffect: { visualEffectID: -1, increaseCurrentHP: {}, increaseCurrentAP: {} }
			,fullRoundEffect: { visualEffectID: -1, increaseCurrentHP: {}, increaseCurrentAP: {} }
			,abilityEffect: { increaseAttackDamage: {} }
		}
		,quest: {
			showInLog: 0
			,stages: []
		}
		,item: {
			displaytype: 0
			,hasManualPrice: 0
			,category: 'other'
			,equipEffect: { increaseAttackDamage: {}, addedConditions: [] }
			,useEffect: { increaseCurrentHP: {}, increaseCurrentAP: {}, conditionsSource: [], conditionsTarget: [] }
			,hitEffect: { increaseCurrentHP: {}, increaseCurrentAP: {}, conditionsSource: [], conditionsTarget: [] }
			,killEffect: { increaseCurrentHP: {}, increaseCurrentAP: {}, conditionsSource: [], conditionsTarget: [] }
		}
		,droplist: {
			items: []
		}
		,dialogue: {
			rewards: []
			,replies: []
		}
		,monster: {
			size: "1x1"
			,maxHP: 1
			,maxAP: 10
			,moveCost: 10
			,unique: 0
			,monsterClass: 0
			,attackDamage: {}
			,hitEffect: { increaseCurrentHP: {}, increaseCurrentAP: {}, conditionsSource: [], conditionsTarget: [] }
		}
		,itemcategory: {
			actionType: 0
			,inventorySlot: -1
			,size: 0
		}
		,reply: {
			requires: { item: { requireType: 0 } }
		}
	};
	
	ATEditor.defaults = {
		addDefaults: function(type, o) {
			var def = defaults[type];
			if (def) {
				var copyOfDefaults = ATEditor.utils.deepClone(def);
				ATEditor.utils.copyDefaults(o, copyOfDefaults);
			}
		},
		removeDefaults: function(type, o) {
			return ATEditor.utils.removeDefaults(o, defaults[type]);
		}
	};

	return ATEditor;
})(ATEditor || {}, _);
