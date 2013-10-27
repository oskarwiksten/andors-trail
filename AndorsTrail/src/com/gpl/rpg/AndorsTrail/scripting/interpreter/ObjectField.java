package com.gpl.rpg.AndorsTrail.scripting.interpreter;

import java.lang.reflect.Field;

public class ObjectField extends ValueReference {

	public final Field f;
	public final Object obj;
	
	public ObjectField(Object obj, Field f) {
		this.f = f;
		this.obj = obj;
	}
	
	public void set(Object value) {
		try {
			if (f.getType().isAssignableFrom(int.class)) {
				value = ((Number)value).intValue();
			} else if (f.getType().isAssignableFrom(float.class)) {
				value = ((Number)value).floatValue();
			}
			f.set(obj, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public Object get() {
		try {
			return f.get(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
