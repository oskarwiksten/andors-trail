package com.gpl.rpg.AndorsTrail.scripting.interpreter;

public class ATSLocalVarReference extends ATSValueReference {

	public enum VarType {
		num {
			@Override
			public void set(ScriptContext context, Object value, int index) {
				context.localNums[index] = (Double)value;
			}
			@Override
			public Object evaluate(ScriptContext context, int index) {
				return context.localNums[index];
			}
		},
		bool{
			@Override
			public void set(ScriptContext context, Object value, int index) {
				context.localBools[index] = (Boolean)value;
			}
			@Override
			public Object evaluate(ScriptContext context, int index) {
				return context.localBools[index];
			}
		},
		string{
			@Override
			public void set(ScriptContext context, Object value, int index) {
				context.localStrings[index] = (String)value;
			}
			@Override
			public Object evaluate(ScriptContext context, int index) {
				return context.localStrings[index];
			}
		};
		
		public abstract void set(ScriptContext context, Object value, int index);
		public abstract Object evaluate(ScriptContext context, int index);
	}
	
	public final int index;
	public final VarType type;
	
	public ATSLocalVarReference(VarType type, int index) {
		this.type = type;
		this.index = index;
	}
	
	@Override
	public void set(ScriptContext context, Object value) {
		type.set(context, value, index);
	}

	@Override
	public Object evaluate(ScriptContext context) {
		return type.evaluate(context, index);
	}

}
