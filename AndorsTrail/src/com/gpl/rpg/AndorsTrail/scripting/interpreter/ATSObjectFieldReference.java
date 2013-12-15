package com.gpl.rpg.AndorsTrail.scripting.interpreter;

import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.scripting.proxyobjects.Item;

public class ATSObjectFieldReference extends ATSValueReference {

	public enum ObjectFields {
		mapOutdoor{
			@Override
			public void set(ScriptContext context, Object value, Object noUseHere) {
				throw new RuntimeException("ATScript : map.outdoor is not writable");
			}
			 @Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return ((PredefinedMap)targetInstance).isOutdoors;
			}
		},
		actorAc{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Actor)targetInstance).attackChance = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Actor)targetInstance).attackChance);
			}
		},
		actorBc{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Actor)targetInstance).blockChance = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Actor)targetInstance).blockChance);
			}
		},
		actorHpCur{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Actor)targetInstance).health.current = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Actor)targetInstance).health.current);
			}
		},
		actorHpMax{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Actor)targetInstance).health.max = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Actor)targetInstance).health.max);
			}
		},
		actorApCur{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Actor)targetInstance).ap.current = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Actor)targetInstance).ap.current);
			}
		},
		actorApMax{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Actor)targetInstance).ap.max = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Actor)targetInstance).ap.max);
			}
		},
		actorAdMin{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Actor)targetInstance).damagePotential.current = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Actor)targetInstance).damagePotential.current);
			}
		},
		actorAdMax{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Actor)targetInstance).damagePotential.max = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Actor)targetInstance).damagePotential.max);
			}
		},
		playerBaseAc{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Player)targetInstance).baseTraits.attackChance = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Player)targetInstance).baseTraits.attackChance);
			}
		},
		playerBaseBc{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Player)targetInstance).baseTraits.blockChance = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Player)targetInstance).baseTraits.blockChance);
			}
		},
		playerBaseMaxHP {
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Player)targetInstance).baseTraits.maxHP = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Player)targetInstance).baseTraits.maxHP);
			}
		},
		playerBaseMaxAP {
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				//TODO : check if this should be allowed.
				((Player)targetInstance).baseTraits.maxAP = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Player)targetInstance).baseTraits.maxAP);
			}
		},
		playerBaseMoveCost {
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Player)targetInstance).baseTraits.moveCost = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Player)targetInstance).baseTraits.moveCost);
			}
		},
		playerBaseEquipCost {
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				//TODO : check if this should be allowed.
				((Player)targetInstance).baseTraits.reequipCost = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Player)targetInstance).baseTraits.reequipCost);
			}
		},
		playerBaseUseCost {
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				//TODO : check if this should be allowed.
				((Player)targetInstance).baseTraits.useItemCost = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Player)targetInstance).baseTraits.useItemCost);
			}
		},
		playerBaseAdMin {
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				//TODO : check if this should be allowed.
				((Player)targetInstance).baseTraits.damagePotential.current = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Player)targetInstance).baseTraits.damagePotential.current);
			}
		},
		playerBaseAdMax {
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				//TODO : check if this should be allowed.
				((Player)targetInstance).baseTraits.damagePotential.max = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Player)targetInstance).baseTraits.damagePotential.max);
			}
		},
		playerLevel {
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				throw new RuntimeException("ATScript : player.level is not writable");
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Player)targetInstance).level);
			}
		},
		playerTotalExp {
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				//TODO : check if this should be allowed.
				((Player)targetInstance).totalExperience = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Player)targetInstance).totalExperience);
			}
		},
		playerCurrentExp {
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				//TODO : check if this should be allowed.
				((Player)targetInstance).levelExperience.current = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return Double.valueOf(((Player)targetInstance).levelExperience.current);
			}
		},
		itemCategory{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				throw new RuntimeException("ATScript : item.category is not writable");
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return ((Item)targetInstance).category;
			}
		},
		itemId{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				throw new RuntimeException("ATScript : item.id is not writable");
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return ((Item)targetInstance).id;
			}
		},
		itemRewardHpMax{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Item)targetInstance).reward.hpMax = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return  Double.valueOf(((Item)targetInstance).reward.hpMax);
			}
		},
		itemRewardHpMin{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Item)targetInstance).reward.hpMin = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return  Double.valueOf(((Item)targetInstance).reward.hpMin);
			}
		},
		itemRewardApMax{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Item)targetInstance).reward.apMax = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return  Double.valueOf(((Item)targetInstance).reward.apMax);
			}
		},
		itemRewardApMin{
			@Override
			public void set(ScriptContext context, Object value, Object targetInstance) {
				((Item)targetInstance).reward.apMin = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, Object targetInstance) {
				return  Double.valueOf(((Item)targetInstance).reward.apMin);
			}
		};
		
		
		public Object targetInstance = null;
		
		public abstract void set(ScriptContext context, Object value, Object optionalTargetInstance);
		public abstract Object evaluate(ScriptContext context, Object optionalTargetInstance);
	}
	
	public final ObjectFields targetField;
	public final ATSValueReference targetInstance;
	
	public ATSObjectFieldReference(ObjectFields targetField, ATSValueReference optionalTargetInstance) {
		this.targetField = targetField;
		this.targetInstance = optionalTargetInstance;
	}
	
	@Override
	public void set(ScriptContext context, Object value) {
		targetField.set(context, value, targetInstance == null ? null : targetInstance.evaluate(context));
	}

	@Override
	public Object evaluate(ScriptContext context) {
		return targetField.evaluate(context, targetInstance == null ? null : targetInstance.evaluate(context));
	}
    
}
