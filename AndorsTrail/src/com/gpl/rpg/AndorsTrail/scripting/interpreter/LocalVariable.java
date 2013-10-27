package com.gpl.rpg.AndorsTrail.scripting.interpreter;

import java.util.Map;

public class LocalVariable extends ValueReference {

	public final String varName;
	public final Map<String, Object> context;
	
	public LocalVariable(Map<String, Object>context, String varName) {
		this.varName =varName;
		this.context = context;
	}
	
	@Override
	public void set(Object value) {
		context.put(varName, value);
	}
	
	public Object get() {
		return context.get(varName);
	}

}
