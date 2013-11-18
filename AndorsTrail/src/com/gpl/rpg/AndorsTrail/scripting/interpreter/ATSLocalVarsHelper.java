package com.gpl.rpg.AndorsTrail.scripting.interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ATSLocalVarsHelper {

	public int maxNumNeeded = 0;
	public int maxBoolNeeded = 0;
	public int maxStringNeeded = 0;
	
	public Stack<Integer> numIndexInScope = new Stack<Integer>();
	public Stack<Integer> boolIndexInScope = new Stack<Integer>();
	public Stack<Integer> stringIndexInScope = new Stack<Integer>();
	
	public Stack<Map<String, ATSLocalVarReference>> variablesInScope = new Stack<Map<String,ATSLocalVarReference>>();

	public ATSLocalVarsHelper() {
		numIndexInScope.push(maxNumNeeded);
		boolIndexInScope.push(maxBoolNeeded);
		stringIndexInScope.push(maxStringNeeded);
		
		variablesInScope.push(new HashMap<String, ATSLocalVarReference>());
	}
	
	public void pushScope() {
		numIndexInScope.push(numIndexInScope.peek());
		boolIndexInScope.push(boolIndexInScope.peek());
		stringIndexInScope.push(stringIndexInScope.peek());
		
		variablesInScope.push(new HashMap<String, ATSLocalVarReference>(variablesInScope.peek()));
	}
	
	public void popScope() {
		maxNumNeeded = Math.max(maxNumNeeded, numIndexInScope.pop());
		maxBoolNeeded = Math.max(maxBoolNeeded, boolIndexInScope.pop());
		maxStringNeeded = Math.max(maxStringNeeded, stringIndexInScope.pop());
		
		variablesInScope.pop();
	}

	public ATSLocalVarReference newNumVariable(String name) {
		int index = numIndexInScope.pop();
		ATSLocalVarReference varRef = new ATSLocalVarReference(ATSLocalVarReference.VarType.num, index);
		numIndexInScope.push(++index);
		variablesInScope.peek().put(name, varRef);
		return varRef;
	}
	
	public ATSLocalVarReference newBoolVariable(String name) {
		int index = boolIndexInScope.pop();
		ATSLocalVarReference varRef = new ATSLocalVarReference(ATSLocalVarReference.VarType.bool, index);
		boolIndexInScope.push(++index);
		variablesInScope.peek().put(name, varRef);
		return varRef;
	}
	
	public ATSLocalVarReference newStringVariable(String name) {
		int index = stringIndexInScope.pop();
		ATSLocalVarReference varRef = new ATSLocalVarReference(ATSLocalVarReference.VarType.string, index);
		stringIndexInScope.push(++index);
		variablesInScope.peek().put(name, varRef);
		return varRef;
	}
	
	public ATSLocalVarReference getLocalVar(String name) {
		return variablesInScope.peek().get(name);
	}
}
