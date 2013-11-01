package com.gpl.rpg.AndorsTrail.scripting.interpreter;

import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;

public class ScriptContext {
	
	public final Double[] localNums;
	public final Boolean[] localBools;
	public final String[] localStrings;
	
	public final WorldContext world;
	public final PredefinedMap map;
	public final Player player;
	public final Actor actor;
	
	public final ControllerContext controllers;

	public boolean returnReached = false;
	
	public ScriptContext(
			int localNumsSize,
			int localBoolsSize,
			int localStringsSize,
			WorldContext world,
			PredefinedMap map,
			Player player,
			Actor actor,
			ControllerContext controllers
			) {
		
		this.localNums = localNumsSize > 0 ? new Double[localNumsSize] : null;
		this.localBools = localBoolsSize > 0 ? new Boolean[localBoolsSize] : null;
		this.localStrings = localStringsSize > 0 ? new String[localStringsSize] : null;
		
		this.world = world;
		this.map = map;
		this.player = player;
		this.actor = actor;
		
		this.controllers = controllers;
	}
	
}
