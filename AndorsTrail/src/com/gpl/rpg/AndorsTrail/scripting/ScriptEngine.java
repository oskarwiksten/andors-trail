package com.gpl.rpg.AndorsTrail.scripting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.listeners.ActorConditionListener;
import com.gpl.rpg.AndorsTrail.controller.listeners.PlayerMovementListener;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.scripting.interpreter.ScriptContext;
import com.gpl.rpg.AndorsTrail.scripting.proxyobjects.Item;
import com.gpl.rpg.AndorsTrail.util.Coord;


public class ScriptEngine implements PlayerMovementListener, ActorConditionListener {

	public static final Map<String, Script> LIBRARY = new HashMap<String, Script>();
	
	public static final ScriptEngine instance = new ScriptEngine();

	public ControllerContext controllers = null;
	public WorldContext world = null;
	
	public static void initializeEngine(ControllerContext controllers, WorldContext world) {
		ScriptEngine.instance.controllers = controllers;
		ScriptEngine.instance.world = world;
		controllers.movementController.playerMovementListeners.add(instance);
		controllers.actorStatsController.actorConditionListeners.add(instance);
	}
	
	public static Script instantiateScript(String scriptId) {
		if (!LIBRARY.containsKey(scriptId)) {
			//TODO log error
			return null;
		}
		return LIBRARY.get(scriptId).clone();
	}
	
	public void activateScript(Script s) {
		for (List<Script> listenerList : getListenersListsForScript(s)) {
			if (listenerList.contains(s)) continue;
			listenerList.add(s);
		}
	}
	
	public void deactivateScript(Script s) {
		for (List<Script> listenerList : getListenersListsForScript(s)) {
			if (!listenerList.contains(s)) continue;
			listenerList.remove(s);
		}
	}
	
	private List<List<Script>> getListenersListsForScript(Script s) {
		List<List<Script>> result = new ArrayList<List<Script>>();
		switch (s.trigger.category) {
		case map : 
			switch (s.trigger.event) {
			case onEnter :
				result.add(mapOnEnter);
				break;
			case onLeave : 
				result.add(mapOnLeave);
				break;
			}
			break;
		case player :
			switch (s.trigger.event) {
			case statsUpdated :
				result.add(playerStatsUpdated);
				break;
			}
			break;
		case item :
			switch (s.trigger.event) {
			case onUse :
				result.add(itemUsed);
				break;
			case onEquip :
				result.add(itemEquipped);
				break;
			case onUnequip :
				result.add(itemUnequipped);
				break;
			}
			break;
		}
		return result;
	}
	
	/*
	 * MAPS
	 */
	
	public void onPlayerEnteredNewMap(PredefinedMap map, com.gpl.rpg.AndorsTrail.util.Coord p, PredefinedMap oldMap) {
		if (oldMap != null) {
			mapLeft(oldMap);
		
			for (Script s : oldMap.scripts) {
				deactivateScript(s);
			}
		}
		
		for (Script s: map.scripts) {
			activateScript(s);
		}
		mapEntered(map);
	}
	public void onPlayerMoved(Coord newPosition, Coord previousPosition) {
		
	}
	
	private final List<Script> mapOnEnter = new ArrayList<Script>();
	private void mapEntered(PredefinedMap map) {
		ScriptContext context = new ScriptContext(world, controllers);
		context.map = map;
		context.player = world.model.player;
		context.actor = world.model.player;
		for (Script script : mapOnEnter) {
			context.initializeLocalVars(script.localBoolsSize, script.localNumsSize, script.localStringsSize, script.localObjectsSize);
			script.scriptASTRoot.evaluate(context);
		}
	}
	
	private final List<Script> mapOnLeave = new ArrayList<Script>();
	private void mapLeft(PredefinedMap map) {
		ScriptContext context = new ScriptContext(world, controllers);
		context.map = map;
		context.player = world.model.player;
		context.actor = world.model.player;
		for (Script script : mapOnLeave) {
			context.initializeLocalVars(script.localBoolsSize, script.localNumsSize, script.localStringsSize, script.localObjectsSize);
			script.scriptASTRoot.evaluate(context);
		}
	
	}
	
	/*
	 * PLAYER
	 */
	
	public void onPlayerStatsUpdate(Player p) {
		statsUpdated(p);
	}
	
	private final List<Script> playerStatsUpdated = new ArrayList<Script>();
	private void statsUpdated(Player p) {
		ScriptContext context = new ScriptContext(world, controllers);
		context.map = world.model.currentMap;
		context.player = p;
		context.actor = p;
		for (Script script : playerStatsUpdated) {
			context.initializeLocalVars(script.localBoolsSize, script.localNumsSize, script.localStringsSize, script.localObjectsSize);
			script.scriptASTRoot.evaluate(context);
		}
		
		for (ActorCondition ac : p.conditions) {
			if (ac.conditionType.private_scripts == null) continue;
			for (Script script : ac.private_scripts) {
				if (script.trigger.category != ScriptTrigger.Categories.player) continue;
				if (script.trigger.event != ScriptTrigger.Events.statsUpdated) continue;
				context.ac = ac;
				script.scriptASTRoot.evaluate(context);
			}
		}
		context.ac = null;
		
		for (ItemType item : p.inventory.wear) {
			if (item == null) continue;
			if (item.private_scripts == null) continue;
			for (Script script : item.private_scripts) {
				if (script == null) continue;
				if (script.trigger.category != ScriptTrigger.Categories.player) continue;
				if (script.trigger.event != ScriptTrigger.Events.statsUpdated) continue;
				context.item = new Item(item, null, item.effects_equip);
				script.scriptASTRoot.evaluate(context);
			}
		}
		context.item = null;
	
	}
	
	/*
	 * ITEMS
	 */
	
	public void onItemUse(Item item) {
		itemOnUse(item);
	}
	
	private final List<Script> itemUsed = new ArrayList<Script>();
	
	private void itemOnUse(Item item) {
		ScriptContext context = new ScriptContext(world, controllers);
		context.map = world.model.currentMap;
		context.item = item;
		context.player = world.model.player;
		context.actor = world.model.player;
		for (Script script : itemUsed) {
			context.initializeLocalVars(script.localBoolsSize, script.localNumsSize, script.localStringsSize, script.localObjectsSize);
			script.scriptASTRoot.evaluate(context);
		}
		
		if (item.privateScripts != null) {
			for (Script script : item.privateScripts) {
				if (script == null) continue;
				if (script.trigger.category != ScriptTrigger.Categories.item) continue;
				if (script.trigger.event != ScriptTrigger.Events.onUse) continue;
				context.item = item;
				context.initializeLocalVars(script.localBoolsSize, script.localNumsSize, script.localStringsSize, script.localObjectsSize);
				script.scriptASTRoot.evaluate(context);
			}
		}
	}
	
	public void onItemEquip(Item item, Inventory.WearSlot slot) {
		itemOnEquip(item, slot);
	}
	
	private final List<Script> itemEquipped = new ArrayList<Script>();
	
	private void itemOnEquip(Item item, Inventory.WearSlot slot) {
		ScriptContext context = new ScriptContext(world, controllers);
		context.map = world.model.currentMap;
		context.item = item;
		context.player = world.model.player;
		context.actor = world.model.player;
		context.slot = slot.name();
		for (Script script : itemEquipped) {
			context.initializeLocalVars(script.localBoolsSize, script.localNumsSize, script.localStringsSize, script.localObjectsSize);
			script.scriptASTRoot.evaluate(context);
		}
		
		if (item.privateScripts != null) {
			for (Script script : item.privateScripts) {
				if (script == null) continue;
				if (script.trigger.category != ScriptTrigger.Categories.item) continue;
				if (script.trigger.event != ScriptTrigger.Events.onEquip) continue;
				context.item = item;
				context.initializeLocalVars(script.localBoolsSize, script.localNumsSize, script.localStringsSize, script.localObjectsSize);
				script.scriptASTRoot.evaluate(context);
			}
		}
	}
	
	
	public void onItemUnequip(Item item, Inventory.WearSlot slot) {
		itemOnUnequip(item, slot);
	}
	
	private final List<Script> itemUnequipped = new ArrayList<Script>();
	
	private void itemOnUnequip(Item item, Inventory.WearSlot slot) {
		ScriptContext context = new ScriptContext(world, controllers);
		context.map = world.model.currentMap;
		context.item = item;
		context.player = world.model.player;
		context.actor = world.model.player;
		context.slot = slot.name();
		for (Script script : itemUnequipped) {
			context.initializeLocalVars(script.localBoolsSize, script.localNumsSize, script.localStringsSize, script.localObjectsSize);
			script.scriptASTRoot.evaluate(context);
		}
		
		if (item.privateScripts != null) {
			for (Script script : item.privateScripts) {
				if (script == null) continue;
				if (script.trigger.category != ScriptTrigger.Categories.item) continue;
				if (script.trigger.event != ScriptTrigger.Events.onUnequip) continue;
				context.item = item;
				context.initializeLocalVars(script.localBoolsSize, script.localNumsSize, script.localStringsSize, script.localObjectsSize);
				script.scriptASTRoot.evaluate(context);
			}
		}
	}
	
	/*
	 * ACTOR CONDITIONS
	 */
	@Override
	public void onActorConditionAdded(Actor actor, ActorCondition condition) {
		for(Script s : condition.scripts) {
			activateScript(s);
		}
	}
	
	@Override
	public void onActorConditionDurationChanged(Actor actor, ActorCondition condition) {
		//Do nothing		
	}
	
	@Override
	public void onActorConditionMagnitudeChanged(Actor actor, ActorCondition condition) {
		//Do nothing		
	}
	
	@Override
	public void onActorConditionRemoved(Actor actor, ActorCondition condition) {
		for(Script s : condition.scripts) {
			deactivateScript(s);
		}
	}
	
	@Override
	public void onActorConditionRoundEffectApplied(Actor actor, ActorCondition condition) {
		//Do nothing
	}
	
}
