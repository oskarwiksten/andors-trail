package com.gpl.rpg.AndorsTrail.scripting.interpreter;

public class ATSReturnStatement extends ATSExpression {

	public final ATSValueReference value;
	
	public ATSReturnStatement(ATSValueReference value) {
		this.value = value;
	}
	
	@Override
	public Object evaluate(ScriptContext context) {
		Object returned = value.evaluate(context);
		context.returnReached = true;
		return returned;
	}

}
