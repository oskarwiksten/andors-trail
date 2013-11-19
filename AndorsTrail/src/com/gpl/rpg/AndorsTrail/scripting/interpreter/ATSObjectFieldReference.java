package com.gpl.rpg.AndorsTrail.scripting.interpreter;

import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.scripting.proxyobjects.Item;
import com.gpl.rpg.AndorsTrail.util.ConstRange;

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
