package com.gpl.rpg.AndorsTrail.scripting.interpreter;

public class ATSContextObjectReference extends ATSValueReference {

	public enum ContextObject {
		world {
			@Override
			public Object evaluate(ScriptContext context) {
				return context.world;
			}
		},
		map{
			@Override
			public Object evaluate(ScriptContext context) {
				return context.map;
			}
		},
		player{
			@Override
			public Object evaluate(ScriptContext context) {
				return context.player;
			}
		},
		actor{
			@Override
			public Object evaluate(ScriptContext context) {
				return context.actor;
			}
		},
		item{
			@Override
			public Object evaluate(ScriptContext context) {
				return context.item;
			}
		};
		
		public abstract Object evaluate(ScriptContext context);
	}
	
	public final ContextObject contextObject;
	
	public ATSContextObjectReference(ContextObject contextObject) {
		this.contextObject = contextObject;
	}
	
	@Override
	public void set(ScriptContext context, Object value) {
		throw new RuntimeException("ATScript : No associations to non-primitive objects please...");
	}

	@Override
	public Object evaluate(ScriptContext context) {
		return contextObject.evaluate(context);
	}

}
