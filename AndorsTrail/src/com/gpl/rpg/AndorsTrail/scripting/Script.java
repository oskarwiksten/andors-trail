package com.gpl.rpg.AndorsTrail.scripting;

public class Script {

	public final String id;
	public final ScriptTrigger trigger;
	public final String scriptCode;
	
	public Script(String id, ScriptTrigger trigger, String scriptCode) {
		this.id = id;
		this.trigger = trigger;
		this.scriptCode = scriptCode;
	}
	
	public Script clone() {
		return new Script(id, trigger, scriptCode);
	}
}
