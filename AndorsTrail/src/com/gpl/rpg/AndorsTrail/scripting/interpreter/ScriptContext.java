package com.gpl.rpg.AndorsTrail.scripting.interpreter;

import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.scripting.proxyobjects.Item;

public class ScriptContext {
	
	public Double[] localNums;
	public Boolean[] localBools;
	public String[] localStrings;
	
	public PredefinedMap map;
	public Player player;
	public Actor actor;
	public ActorCondition ac;
	public Item item;
	
	public final WorldContext world;
	public final ControllerContext controllers;

	public boolean returnReached = false;
	
	public ScriptContext(WorldContext world, ControllerContext controllers) {
		this.world = world;
		this.controllers = controllers;
	}
	
	public void initializeLocalVars(int localBoolsSize, int localNumsSize, int localStringsSize) {
		this.localNums = localNumsSize > 0 ? new Double[localNumsSize] : null;
		this.localBools = localBoolsSize > 0 ? new Boolean[localBoolsSize] : null;
		this.localStrings = localStringsSize > 0 ? new String[localStringsSize] : null;
	}

}
