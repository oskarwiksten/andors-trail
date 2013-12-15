package com.gpl.rpg.AndorsTrail.scripting;

import com.gpl.rpg.AndorsTrail.scripting.interpreter.ATSNode;

public class Script {

	public final String id;
	public final String description;
	public final ScriptTrigger trigger;
	public final ATSNode scriptASTRoot;
	
	public final int localNumsSize;
	public final int localBoolsSize;
	public final int localStringsSize;
	public final int localObjectsSize;
	
	public Script(String id, String description, ScriptTrigger trigger, ATSNode scriptASTRoot, int localNumsSize, int localBoolsSize, int localStringsSize, int localObjectsSize) {
		this.id = id;
		this.description = description;
		this.trigger = trigger;
		this.scriptASTRoot = scriptASTRoot;
		this.localNumsSize = localNumsSize;
		this.localBoolsSize = localBoolsSize;
		this.localStringsSize = localStringsSize;
		this.localObjectsSize = localObjectsSize;
	}
	
	public Script clone() {
		return new Script(id, description, trigger, scriptASTRoot, localNumsSize, localBoolsSize, localStringsSize, localObjectsSize);
	}
}
