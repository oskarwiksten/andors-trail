package com.gpl.rpg.AndorsTrail.scripting.interpreter;

import com.gpl.rpg.AndorsTrail.model.actor.Actor;

public class ATSObjectFieldReference extends ATSValueReference {

	public enum ObjectFields {
		mapOutdoor{
			@Override
			public void set(ScriptContext context, Object value, ATSContextObjectReference noUseHere) {
				throw new RuntimeException("ATScript : map.outside is not writable");
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
