package com.gpl.rpg.AndorsTrail.scripting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.listeners.PlayerMovementListener;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.scripting.interpreter.ATScriptInterpreter;
import com.gpl.rpg.AndorsTrail.scripting.interpreter.ParseException;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.L;


public class ScriptEngine implements PlayerMovementListener {

	public static final Map<String, Script> LIBRARY = new HashMap<String, Script>();
	
	public static final ScriptEngine instance = new ScriptEngine();

	public ControllerContext controllers = null;
	public WorldContext world = null;
	
	public static void initializeEngine(ControllerContext controllers, WorldContext world) {
		ScriptEngine.instance.controllers = controllers;
		ScriptEngine.instance.world = world;
		controllers.movementController.playerMovementListeners.add(instance);
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
		}
		return result;
	}
	
	/*
	 * MAPS
	 */
	
	public void onPlayerEnteredNewMap(PredefinedMap map, com.gpl.rpg.AndorsTrail.util.Coord p, PredefinedMap oldMap) {
		if (oldMap != null) {
			mapLeft(oldMap, world);
		
			for (Script s : oldMap.scripts) {
				deactivateScript(s);
			}
		}
		
		for (Script s: map.scripts) {
			activateScript(s);
		}
		mapEntered(map, world);
	}
	public void onPlayerMoved(Coord newPosition, Coord previousPosition) {
		
	}
	
	private final List<Script> mapOnEnter = new ArrayList<Script>();
	private void mapEntered(PredefinedMap map, WorldContext world) {
		Map<String, Object> context = prepareContext();
		context.put("map", map);
		context.put("world", world);
		for (Script script : mapOnEnter) {
			try {
				ATScriptInterpreter.runScript(script.scriptCode, context);
			} catch (ParseException e) {
				L.log("Error running script "+script.id+" on map "+map.name);
				e.printStackTrace();
			}
		}
	
	}
	
	private final List<Script> mapOnLeave = new ArrayList<Script>();
	public void mapLeft(PredefinedMap map, WorldContext world) {
		Map<String, Object> context = prepareContext();
		context.put("map", map);
		context.put("world", world);
		for (Script script : mapOnLeave) {
			try {
				ATScriptInterpreter.runScript(script.scriptCode, context);
			} catch (ParseException e) {
				L.log("Error running script "+script.id+" on map "+map.name);
				e.printStackTrace();
			}
		}
	
	}
	
	private Map<String, Object> prepareContext() {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("controllers", controllers);
		
		return context;
	}
	
	
	
}
