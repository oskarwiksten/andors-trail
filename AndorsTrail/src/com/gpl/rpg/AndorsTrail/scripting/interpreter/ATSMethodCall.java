package com.gpl.rpg.AndorsTrail.scripting.interpreter;

import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.util.ConstRange;

public class ATSMethodCall extends ATSValueReference {

	public enum ObjectMethod {
		actorAddCondition {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				context.controllers.actorStatsController.rollForConditionEffect(((Actor)targetInstance), new ActorConditionEffect(context.world.actorConditionsTypes.getActorConditionType((String)parameters[0].evaluate(context)), ((Double)parameters[1].evaluate(context)).intValue(), ((Double)parameters[2].evaluate(context)).intValue(), new ConstRange(100, ((Double)parameters[3].evaluate(context)).intValue()) ));
				return null;
			}
		},
		actorClearCondition {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				context.controllers.actorStatsController.rollForConditionEffect(((Actor)targetInstance), new ActorConditionEffect(context.world.actorConditionsTypes.getActorConditionType((String)parameters[0].evaluate(context)), ActorCondition.MAGNITUDE_REMOVE_ALL, 1, new ConstRange(100, ((Double)parameters[1].evaluate(context)).intValue()) ));
				return null;
			}
		};
		
		public abstract Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance);
	}
	
	public final ObjectMethod method;
	public final ATSValueReference[] parameters;
	public final ATSValueReference targetInstance;
	
	public ATSMethodCall(ObjectMethod method, ATSValueReference[] parameters, ATSValueReference optionalTargetInstance) {
		this.method = method;
		this.parameters = parameters;
		this.targetInstance = optionalTargetInstance;
	}
	
	@Override
	public void set(ScriptContext context, Object value) {
		throw new RuntimeException("ATScript : Trying to associate a value to a method result ? come on...");
	}

	@Override
	public Object evaluate(ScriptContext context) {
		Object returned = method.evaluate(context, parameters, targetInstance.evaluate(context));
		return next!=null ? next.evaluate(context) : returned;
	}
	

}
