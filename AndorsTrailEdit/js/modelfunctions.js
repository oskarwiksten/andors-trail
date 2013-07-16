var ATModelFunctions = (function(ATModelFunctions) {

	ATModelFunctions.itemCategoryFunctions = (function() {
		function isWearableCategory(itemCategory) {
			if (!itemCategory) { return false; }
			return itemCategory.actionType == 'equip';
		}
		
		function isUsableCategory(itemCategory) {
			if (!itemCategory) { return false; }
			return itemCategory.actionType == 'use';
		}
		
		function isWeaponCategory(itemCategory) {
			if (!isWearableCategory(itemCategory)) { return false; }
			if (itemCategory.inventorySlot != 'weapon') { return false; }
			return true;
		}
		
		function isShieldCategory(itemCategory) {
			if (!isWearableCategory(itemCategory)) { return false; }
			if (itemCategory.inventorySlot != 'shield') { return false; }
			return true;
		}
		
		function isArmorCategory(itemCategory) {
			if (!isWearableCategory(itemCategory)) { return false; }
			if (itemCategory.inventorySlot == 'head') { return true; }
			if (itemCategory.inventorySlot == 'body') { return true; }
			if (itemCategory.inventorySlot == 'hand') { return true; }
			if (itemCategory.inventorySlot == 'feet') { return true; }
			return false;
		}
		
		return {
			isWearableCategory: isWearableCategory
			,isUsableCategory: isUsableCategory
			,isWeaponCategory: isWeaponCategory
			,isShieldCategory: isShieldCategory
			,isArmorCategory: isArmorCategory
		};
	})();
	
	
	ATModelFunctions.itemFunctions = (function(itemCategoryFunctions) {
		function getItemCost(obj) {
			if (obj.hasManualPrice == 1) {
				return obj.baseMarketCost;
			}
			return calculateItemCost(obj);
		}
		
		function calculateItemCost(obj) {
			var v = function(i) { return i ? parseFloat(i) : 0; };
			var sgn = function(v) {
				if (v < 0) return -1;
				else if (v > 0) return 1;
				else return 0;
			};
			var avg = function(o) {
				if (!o) return 0;
				return (v(o.min) + v(o.max)) / 2;
			};
			
			var itemUsageCost = 0;
			if (obj.useEffect && obj.hasUseEffect) {
				var averageHPBoost = avg(obj.useEffect.increaseCurrentHP);
				var costBoostHP = Math.round(0.1*sgn(averageHPBoost)*Math.pow(Math.abs(averageHPBoost), 2) + 3*averageHPBoost);
				itemUsageCost = costBoostHP;
			}
			
			var itemEquipCost = 0;
			if (obj.equipEffect && obj.hasEquipEffect) {
				var isWeapon = itemCategoryFunctions.isWeaponCategory(obj.category);
				
				var equip_blockChance = v(obj.equipEffect.increaseBlockChance);
				var equip_attackChance = v(obj.equipEffect.increaseAttackChance);
				var equip_attackCost = v(obj.equipEffect.increaseAttackCost);
				var equip_damageResistance = v(obj.equipEffect.increaseDamageResistance);
				var equip_attackDamage_Min = 0;
				var equip_attackDamage_Max = 0;
				if (obj.equipEffect.increaseAttackDamage) { 
					equip_attackDamage_Min = v(obj.equipEffect.increaseAttackDamage.min);
					equip_attackDamage_Max = v(obj.equipEffect.increaseAttackDamage.max);
				}
				var equip_criticalChance = v(obj.equipEffect.increaseCriticalSkill);
				var equip_criticalMultiplier = v(obj.equipEffect.setCriticalMultiplier);
				var costBC = Math.round(3*Math.pow(Math.max(0,equip_blockChance), 2.5) + 28*equip_blockChance);
				var costAC = Math.round(0.4*Math.pow(Math.max(0,equip_attackChance), 2.5) - 6*Math.pow(Math.abs(Math.min(0,equip_attackChance)),2.7));
				var costAP = isWeapon ?
						Math.round(0.2*Math.pow(10/equip_attackCost, 8) - 25*equip_attackCost)
						: -3125 * equip_attackCost;
				var costDR = 1325*equip_damageResistance;
				var costDMG_Min = isWeapon ?
						Math.round(10*Math.pow(equip_attackDamage_Min, 2.5))
						:Math.round(10*Math.pow(equip_attackDamage_Min, 3) + equip_attackDamage_Min*80);
				var costDMG_Max = isWeapon ?
						Math.round(2*Math.pow(equip_attackDamage_Max, 2.1))
						:Math.round(2*Math.pow(equip_attackDamage_Max, 3) + equip_attackDamage_Max*20);
				var costCC = Math.round(2.2*Math.pow(equip_criticalChance, 3));
				var costCM = Math.round(50*Math.pow(Math.max(0, equip_criticalMultiplier), 2));
				var costCombat = costBC + costAC + costAP + costDR + costDMG_Min + costDMG_Max + costCC + costCM;
				
				var equip_boostMaxHP = v(obj.equipEffect.increaseMaxHP);
				var equip_boostMaxAP = v(obj.equipEffect.increaseMaxAP);
				var costMaxHP = Math.round(30*Math.pow(Math.max(0,equip_boostMaxHP), 1.2) + 70*equip_boostMaxHP);
				var costMaxAP = Math.round(50*Math.pow(Math.max(0,equip_boostMaxAP), 3) + 750*equip_boostMaxAP);
				
				var equip_moveCostPenalty = v(obj.equipEffect.increaseMoveCost);
				var equip_useItemCostPenalty = v(obj.equipEffect.increaseUseItemCost);
				var equip_reequipCostPenalty = v(obj.equipEffect.increaseReequipCost);
				var costMovement = Math.round(510*Math.pow(Math.max(0,-equip_moveCostPenalty), 2.5) - 350*equip_moveCostPenalty);
				var costUseItem = Math.round(915*Math.pow(Math.max(0,-equip_useItemCostPenalty), 3) - 430*equip_useItemCostPenalty);
				var costReequip = Math.round(450*Math.pow(Math.max(0,-equip_reequipCostPenalty), 2) - 250*equip_reequipCostPenalty);
				var costAPModifiers = costMovement + costUseItem + costReequip;
				
				itemEquipCost = costCombat + costMaxHP + costMaxAP + costAPModifiers;
			}
			
			var hitEffectsCost = 0;
			if (obj.hitEffect && obj.hasHitEffect) {
				var averageHPBoost = avg(obj.hitEffect.increaseCurrentHP);
				var averageAPBoost = avg(obj.hitEffect.increaseCurrentAP);
				var costBoostHP = Math.round(2770*Math.pow(Math.max(0,averageHPBoost), 2.5) + 450*averageHPBoost);
				var costBoostAP = Math.round(3100*Math.pow(Math.max(0,averageAPBoost), 2.5) + 300*averageAPBoost);
				hitEffectsCost = costBoostHP + costBoostAP;
			}
			
			var killEffectsCost = 0;
			if (obj.killEffect && obj.hasKillEffect) {
				var averageHPBoost = avg(obj.killEffect.increaseCurrentHP);
				var averageAPBoost = avg(obj.killEffect.increaseCurrentAP);
				var costBoostHP = Math.round(923*Math.pow(Math.max(0,averageHPBoost), 2.5) + 450*averageHPBoost);
				var costBoostAP = Math.round(1033*Math.pow(Math.max(0,averageAPBoost), 2.5) + 300*averageAPBoost);
				killEffectsCost = costBoostHP + costBoostAP;
			}
			
			var result = itemEquipCost + itemUsageCost + hitEffectsCost + killEffectsCost;
			if (result <= 0) { result = 1; }
			
			return result;
		}
		
		function getItemSellingCost(item) {
			var cost = getItemCost(item);
			return Math.round(cost * (100 + 15) / 100);
		}
		
		function getItemBuyingCost(item) {
			var cost = getItemCost(item);
			return Math.round(cost * (100 - 15) / 100);
		}
		
		return {
			getItemCost: getItemCost
			,calculateItemCost: calculateItemCost
			,getItemSellingCost: getItemSellingCost
			,getItemBuyingCost: getItemBuyingCost
		};
	})(ATModelFunctions.itemCategoryFunctions);
	
	
	ATModelFunctions.monsterFunctions = (function() {
		function getMonsterExperience(obj) {
			var EXP_FACTOR_DAMAGERESISTANCE = 9;
			var EXP_FACTOR_SCALING = 0.7;
			
			var div100 = function(v) { return v / 100; }
			var v = function(i) { return i ? parseFloat(i) : 0; }
			
			var attacksPerTurn = Math.floor(v(obj.maxAP) / v(obj.attackCost));
			var avgDamagePotential = 0;
			if (obj.attackDamage) { 
				avgDamagePotential = (v(obj.attackDamage.min) + v(obj.attackDamage.max)) / 2; 
			}
			var avgCrit = 0;
			if (obj.hasCritical) {
				avgCrit = div100(v(obj.criticalSkill)) * v(obj.criticalMultiplier);
			}
			var avgAttackHP  = attacksPerTurn * div100(v(obj.attackChance)) * avgDamagePotential * (1 + avgCrit);
			var avgDefenseHP = v(obj.maxHP) * (1 + div100(v(obj.blockChance))) + EXP_FACTOR_DAMAGERESISTANCE * v(obj.damageResistance);
			var attackConditionBonus = 0;
			if (obj.hitEffect && obj.hitEffect.conditionsTarget && v(obj.hitEffect.conditionsTarget.length) > 0) {
				attackConditionBonus = 50;
			}
			var experience = (avgAttackHP * 3 + avgDefenseHP) * EXP_FACTOR_SCALING + attackConditionBonus;
			
			return Math.ceil(experience);
		};
		
		return {
			getMonsterExperience: getMonsterExperience
		};
	})();
	
	return ATModelFunctions;
})(ATModelFunctions || {});

if (typeof module != 'undefined') { module.exports = ATModelFunctions; }
