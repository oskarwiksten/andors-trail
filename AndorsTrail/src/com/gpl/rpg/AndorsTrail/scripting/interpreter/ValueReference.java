package com.gpl.rpg.AndorsTrail.scripting.interpreter;

public abstract class ValueReference {

	
	//Nothing went wrong, but nothing returned (void method call for example)
	public static final ValueReference VOID_REFERENCE = new ValueReference() {
		@Override
		public void set(Object value) {}
		@Override
		public Object get() {
			return null;
		}
	};
	
	public abstract void set(Object value);
	public abstract Object get();
	
}
