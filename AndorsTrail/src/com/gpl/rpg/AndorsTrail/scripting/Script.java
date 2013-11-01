package com.gpl.rpg.AndorsTrail.scripting;

import com.gpl.rpg.AndorsTrail.scripting.interpreter.ATSNode;

public class Script {

	public final String id;
	public final ScriptTrigger trigger;
	public final ATSNode scriptASTRoot;
	
	public final int localNumsSize;
	public final int localBoolsSize;
	public final int localStringsSize;
	
	public Script(String id, ScriptTrigger trigger, ATSNode scriptASTRoot, int localNumsSize, int localBoolsSize, int localStringsSize) {
		this.id = id;
		this.trigger = trigger;
		this.scriptASTRoot = scriptASTRoot;
		this.localNumsSize = localNumsSize;
		this.localBoolsSize = localBoolsSize;
		this.localStringsSize = localStringsSize;
	}
	
	public Script clone() {
		return new Script(id, trigger, scriptASTRoot, localNumsSize, localBoolsSize, localStringsSize);
	}
}
