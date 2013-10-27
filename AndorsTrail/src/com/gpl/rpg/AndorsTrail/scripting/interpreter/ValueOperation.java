package com.gpl.rpg.AndorsTrail.scripting.interpreter;

public class ValueOperation {
	
	public static enum Operators {
		not,
		gt,
		lt,
		goe,
		loe,
		eq,
		plus,
		minus,
		multiply,
		divide,
	}

	public final Object rightHandValue;
	public final Operators operator;
	
	public ValueOperation(Operators operator, Object rightHandValue) {
		if (rightHandValue instanceof Number) {
			this.rightHandValue = ((Number)rightHandValue).doubleValue();
		} else {
			this.rightHandValue = rightHandValue;
		}
		this.operator= operator;
	}

	public Object apply(Object leftHandValue) {
		if (leftHandValue instanceof Number) {
			leftHandValue = ((Number)leftHandValue).doubleValue();
		}
		switch (this.operator) {
			case not: return !((Boolean)rightHandValue).booleanValue();
			case gt: return ((Double)leftHandValue) > ((Double)rightHandValue);
			case lt: return ((Double)leftHandValue) < ((Double)rightHandValue);
			case goe: return ((Double)leftHandValue) >= ((Double)rightHandValue);
			case loe : return ((Double)leftHandValue) <= ((Double)rightHandValue);
			case eq: return leftHandValue.equals(rightHandValue);
			case plus: return ((Double)leftHandValue) + ((Double)rightHandValue);
			case minus: return ((Double)leftHandValue) - ((Double)rightHandValue);
			case multiply: return ((Double)leftHandValue) * ((Double)rightHandValue);
			case divide: return ((Double)leftHandValue) / ((Double)rightHandValue);
			
			default : return null;
		}
	}
}
