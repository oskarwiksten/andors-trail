package com.gpl.rpg.AndorsTrail.scripting.interpreter;

public class ATSWhileLoop extends ATSExpression {

	public final ATSValueReference condition;
	public final ATSExpression whileBlock;
	
	public ATSWhileLoop(ATSValueReference condition, ATSExpression whileBlock) {
		this.condition = condition;
		this.whileBlock = whileBlock;
	}
	
	@Override
	public Object evaluate(ScriptContext context) {
		Object returned = null;
		while (!(Boolean)condition.evaluate(context)) {
			returned = whileBlock.evaluate(context);
		}
		
		if (context.returnReached) return returned;
		
		return next!=null ? next.evaluate(context) : returned;
	}

}
