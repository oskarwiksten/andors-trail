package com.gpl.rpg.AndorsTrail.scripting.interpreter;

public class ATSAssociation extends ATSExpression {

	public final ATSValueReference register;
	public final ATSValueReference value;
	
	public ATSAssociation(ATSValueReference register, ATSValueReference value) {
		this.register = register;
		this.value = value;
	}
	
	@Override
	public Object evaluate(ScriptContext context) {
		register.set(context, value.evaluate(context));
		return next!=null ? next.evaluate(context) : null;
	}

}
