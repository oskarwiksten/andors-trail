package com.gpl.rpg.AndorsTrail.scripting.interpreter;

public class ATSFlowControl extends ATSExpression {

	public final ATSValueReference condition;
	public final ATSExpression ifBlock, elseBlock;
	
	public ATSFlowControl(ATSValueReference condition, ATSExpression ifBlock, ATSExpression optionalElseBlock) {
		this.condition = condition;
		this.ifBlock = ifBlock;
		this.elseBlock = optionalElseBlock;
	}
	
	@Override
	public Object evaluate(ScriptContext context) {
		Object returned = null;
		if ((Boolean)condition.evaluate(context)) {
			returned = ifBlock.evaluate(context);
		} else if (elseBlock != null) {
			returned = elseBlock.evaluate(context);
		}
		
		if (context.returnReached) return returned;
		
		return next!=null ? next.evaluate(context) : returned;
	}

}
