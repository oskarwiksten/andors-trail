package com.gpl.rpg.AndorsTrail.controller;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.map.KeyArea;
import com.gpl.rpg.AndorsTrail.model.map.LayeredWorldMap;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.L;

public final class MovementController {
	private final ViewContext view;
    private final WorldContext world;
    private final ModelContainer model;

	public MovementController(ViewContext context) {
    	this.view = context;
    	this.world = context;
    	this.model = world.model;
    }
	
	public void placePlayerAt(String mapName, String placeName) { 
		placePlayerAt(world, mapName, placeName); 
		view.mainActivity.mainview.notifyMapChanged();
    }
	public static void placePlayerAt(final WorldContext world, String mapName, String placeName) {
    	if (mapName == null || placeName == null) return;
		LayeredWorldMap newMap = world.maps.findPredefinedMap(mapName);
		if (newMap == null) {
			L.log("Cannot find map " + mapName);
			return;
		}
		MapObject place = newMap.findEventObject(MapObject.MAPEVENT_NEWMAP, placeName);
		if (place == null) {
			L.log("Cannot find place " + placeName + " in map " + mapName);
			return;
		}
		final ModelContainer model = world.model;
		model.currentMap = newMap;
		model.player.position.set(place.position.topLeft);
		model.player.lastPosition.set(model.player.position);
	}
    
	public boolean mayMovePlayer() {
		return !model.uiSelections.isInCombat;
	}

    public void movePlayer(int dx, int dy) {
    	if (dx == 0 && dy == 0) return;
    	if (!mayMovePlayer()) return;
    	//if (isInCombat) return;

    	final Player player = model.player;
		player.nextPosition.set(
				player.position.x + dx
    			,player.position.y + dy
			);
    	final Coord newPosition = player.nextPosition;
    	if (!model.currentMap.isWalkable(newPosition)) {
    		return;
    	} 
    	
    	Monster m = model.currentMap.getMonsterAt(newPosition);
		if (m != null) {
			view.controller.steppedOnMonster(m, newPosition);
			return;
		}

		moveToNextIfPossible(true);
    }
    
    public void moveToNextIfPossible(boolean handleEvents) {
    	final Player player = model.player;
    	final LayeredWorldMap currentMap = model.currentMap;
    	final Coord newPosition = player.nextPosition;
    	
    	for (KeyArea a : currentMap.keyAreas) {
    		if (a.position.contains(newPosition)) {
    			if (!view.controller.handleKeyArea(a)) return;
    		}
    	}
		
    	player.lastPosition.set(player.position);
    	player.position.set(newPosition);
    	view.combatController.setCombatSelection(null, null);
		view.mainActivity.mainview.notifyPlayerMoved();
		view.mainActivity.redrawAll();
		
		if (handleEvents) {
	    	for (MapObject o : currentMap.eventObjects) {
	    		if (o.position.contains(newPosition)) {
	    			view.controller.handleMapEvent(o);
	    		}
	    	}
	    	
	    	Loot loot = currentMap.getBagAt(newPosition);
	    	if (loot != null) {
	    		view.itemController.handleLootBag(loot);
	    	}
		}
    }

	public static void respawnPlayer(final WorldContext world) {
		placePlayerAt(world, world.model.player.spawnMap, world.model.player.spawnPlace);
	}
}
