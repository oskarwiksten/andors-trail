package com.gpl.rpg.AndorsTrail.scripting.interpreter;

public class ATSConstantReference extends ATSValueReference {

	public final Object value;
	
	public ATSConstantReference(Object value) {
		this.value = value;
	}
	
	@Override
	public void set(ScriptContext context, Object value) {
		throw new RuntimeException("ATScript : trying to associate a value to a constant ? come on...");
	}

	@Override
	public Object evaluate(ScriptContext context) {
		return value;
	}

}
