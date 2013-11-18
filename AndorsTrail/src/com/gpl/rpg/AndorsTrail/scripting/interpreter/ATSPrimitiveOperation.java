package com.gpl.rpg.AndorsTrail.scripting.interpreter;

public class ATSPrimitiveOperation extends ATSValueReference {

	public static enum Operator {
		not {
			public Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand) {
				return ((Boolean)rightHand.evaluate(context)) ? Boolean.FALSE : Boolean.TRUE;
			};
		},
		and {
			public Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand) {
				return ((Boolean)leftHand.evaluate(context)) && ((Boolean)rightHand.evaluate(context));
			};
		},
		or {
			public Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand) {
				return ((Boolean)leftHand.evaluate(context)) || ((Boolean)rightHand.evaluate(context));
			};
		},
		gt {
			public Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand) {
				return ((Double)leftHand.evaluate(context)) > ((Double)rightHand.evaluate(context));
			};
		},
		lt {
			public Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand) {
				return ((Double)leftHand.evaluate(context)) < ((Double)rightHand.evaluate(context));
			};
		},
		goe {
			public Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand) {
				return ((Double)leftHand.evaluate(context)) >= ((Double)rightHand.evaluate(context));
			};
		},
		loe {
			public Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand) {
				return ((Double)leftHand.evaluate(context)) <= ((Double)rightHand.evaluate(context));
			};
		},
		eq {
			public Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand) {
				return leftHand.evaluate(context).equals(rightHand.evaluate(context));
			};
		},
		plus {
			public Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand) {
				return ((Double)leftHand.evaluate(context)) + ((Double)rightHand.evaluate(context));
			};
		},
		minus {
			public Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand) {
				return ((Double)leftHand.evaluate(context)) - ((Double)rightHand.evaluate(context));
			};
		},
		multiply {
			public Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand) {
				return ((Double)leftHand.evaluate(context)) * ((Double)rightHand.evaluate(context));
			};
		},
		divide {
			public Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand) {
				return ((Double)leftHand.evaluate(context)) / ((Double)rightHand.evaluate(context));
			};
		},
		concat {
			public Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand) {
				//Could be any object, including string and numbers. More flexible and efficient than a String cast.
				return leftHand.evaluate(context).toString() + rightHand.evaluate(context).toString();
			}
		};
		
		public abstract Object evaluate(ScriptContext context, ATSValueReference leftHand, ATSValueReference rightHand);
	}
	
	private final ATSValueReference leftHand, rightHand;
	private final Operator operator;
	
	public ATSPrimitiveOperation(Operator operator, ATSValueReference leftHand, ATSValueReference rightHand) {
		this.leftHand = leftHand;
		this.rightHand = rightHand;
		this.operator = operator;
	}
	
	@Override
	public void set(ScriptContext context, Object value) {
		throw new RuntimeException("ATScript : Trying to associate a value to an operation's result ? come on...");
	}

	@Override
	public Object evaluate(ScriptContext context) {
		return operator.evaluate(context, leftHand, rightHand);
	}

}
