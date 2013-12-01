package com.gpl.rpg.AndorsTrail.scripting.interpreter;

import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.scripting.proxyobjects.Item;

public class ATSObjectFieldReference extends ATSValueReference {

	public enum ObjectFields {
		mapOutdoor{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference noUseHere) {
				throw new RuntimeException("ATScript : map.outdoor is not writable");
			}
			 @Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference noUseHere) {
				return context.map.isOutdoors;
			}
		},
		actorAc{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Actor)targetInstance.evaluate(context)).attackChance = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Actor)targetInstance.evaluate(context)).attackChance);
			}
		},
		actorBc{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Actor)targetInstance.evaluate(context)).blockChance = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Actor)targetInstance.evaluate(context)).blockChance);
			}
		},
		actorHpCur{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Actor)targetInstance.evaluate(context)).health.current = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Actor)targetInstance.evaluate(context)).health.current);
			}
		},
		actorHpMax{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Actor)targetInstance.evaluate(context)).health.max = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Actor)targetInstance.evaluate(context)).health.max);
			}
		},
		actorApCur{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Actor)targetInstance.evaluate(context)).ap.current = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Actor)targetInstance.evaluate(context)).ap.current);
			}
		},
		actorApMax{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Actor)targetInstance.evaluate(context)).ap.max = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Actor)targetInstance.evaluate(context)).ap.max);
			}
		},
		actorAdMin{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Actor)targetInstance.evaluate(context)).damagePotential.current = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Actor)targetInstance.evaluate(context)).damagePotential.current);
			}
		},
		actorAdMax{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Actor)targetInstance.evaluate(context)).damagePotential.max = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Actor)targetInstance.evaluate(context)).damagePotential.max);
			}
		},
		playerBaseAc{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Player)targetInstance.evaluate(context)).baseTraits.attackChance = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Player)targetInstance.evaluate(context)).baseTraits.attackChance);
			}
		},
		playerBaseBc{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Player)targetInstance.evaluate(context)).baseTraits.blockChance = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Player)targetInstance.evaluate(context)).baseTraits.blockChance);
			}
		},
		playerBaseMaxHP {
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Player)targetInstance.evaluate(context)).baseTraits.maxHP = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Player)targetInstance.evaluate(context)).baseTraits.maxHP);
			}
		},
		playerBaseMaxAP {
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				//TODO : check if this should be allowed.
				((Player)targetInstance.evaluate(context)).baseTraits.maxAP = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Player)targetInstance.evaluate(context)).baseTraits.maxAP);
			}
		},
		playerBaseMoveCost {
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Player)targetInstance.evaluate(context)).baseTraits.moveCost = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Player)targetInstance.evaluate(context)).baseTraits.moveCost);
			}
		},
		playerBaseEquipCost {
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				//TODO : check if this should be allowed.
				((Player)targetInstance.evaluate(context)).baseTraits.reequipCost = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Player)targetInstance.evaluate(context)).baseTraits.reequipCost);
			}
		},
		playerBaseUseCost {
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				//TODO : check if this should be allowed.
				((Player)targetInstance.evaluate(context)).baseTraits.useItemCost = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Player)targetInstance.evaluate(context)).baseTraits.useItemCost);
			}
		},
		playerBaseAdMin {
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				//TODO : check if this should be allowed.
				((Player)targetInstance.evaluate(context)).baseTraits.damagePotential.current = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Player)targetInstance.evaluate(context)).baseTraits.damagePotential.current);
			}
		},
		playerBaseAdMax {
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				//TODO : check if this should be allowed.
				((Player)targetInstance.evaluate(context)).baseTraits.damagePotential.max = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return Double.valueOf(((Player)targetInstance.evaluate(context)).baseTraits.damagePotential.max);
			}
		},
		itemCategory{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				throw new RuntimeException("ATScript : item.category is not writable");
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return ((Item)targetInstance.evaluate(context)).category;
			}
		},
		itemId{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				throw new RuntimeException("ATScript : item.id is not writable");
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return ((Item)targetInstance.evaluate(context)).id;
			}
		},
		itemRewardHpMax{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Item)targetInstance.evaluate(context)).reward.hpMax = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return  Double.valueOf(((Item)targetInstance.evaluate(context)).reward.hpMax);
			}
		},
		itemRewardHpMin{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Item)targetInstance.evaluate(context)).reward.hpMin = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return  Double.valueOf(((Item)targetInstance.evaluate(context)).reward.hpMin);
			}
		},
		itemRewardApMax{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Item)targetInstance.evaluate(context)).reward.apMax = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return  Double.valueOf(((Item)targetInstance.evaluate(context)).reward.apMax);
			}
		},
		itemRewardApMin{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference targetInstance) {
				((Item)targetInstance.evaluate(context)).reward.apMin = ((Double)value).intValue();
			}
			@Override
			public Object evaluate(ScriptContext context, ATSContextObjectReference targetInstance) {
				return  Double.valueOf(((Item)targetInstance.evaluate(context)).reward.apMin);
			}
		};
		
		
		public Object targetInstance = null;
		
		public abstract void set(ScriptContext context, Object value, ATSContextObjectReference optionalTargetInstance);
		public abstract Object evaluate(ScriptContext context, ATSContextObjectReference optionalTargetInstance);
	}
	
	public final ObjectFields targetField;
	public final ATSContextObjectReference targetInstance;
	
	public ATSObjectFieldReference(ObjectFields targetField, ATSContextObjectReference optionalTargetInstance) {
		this.targetField = targetField;
		this.targetInstance = optionalTargetInstance;
	}
	
	@Override
	public void set(ScriptContext context, Object value) {
		targetField.set(context, value, targetInstance);
	}

	@Override
	public Object evaluate(ScriptContext context) {
		return targetField.evaluate(context, targetInstance);
	}
    
}
