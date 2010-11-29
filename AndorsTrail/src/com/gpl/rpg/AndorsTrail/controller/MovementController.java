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
		view.mainActivity.clearMessages();
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
		if (!newMap.visited) newMap.spawnAll(world);
		newMap.visited = true;
	}
    
	public boolean mayMovePlayer() {
		return !model.uiSelections.isInCombat;
	}

    public void movePlayer(int dx, int dy) {
    	if (dx == 0 && dy == 0) return;
    	if (!mayMovePlayer()) return;
    	//if (isInCombat) return;

    	if (!findWalkablePosition(dx, dy)) return;
    	
    	Monster m = model.currentMap.getMonsterAt(model.player.nextPosition);
		if (m != null) {
			view.controller.steppedOnMonster(m, model.player.nextPosition);
			return;
		}

		moveToNextIfPossible(true);
    }
    
    private boolean findWalkablePosition(int dx, int dy) {
    	if (tryWalkablePosition(sgn(dx), sgn(dy))) return true;
    	if (dx == 0 || dy == 0) return false;
    	if (abs(dx) > abs(dy)) return tryWalkablePosition(sgn(dx), 0);
    	return tryWalkablePosition(0, sgn(dy));
    }
    private boolean tryWalkablePosition(int dx, int dy) {
    	final Player player = model.player;
    	player.nextPosition.set(
				player.position.x + dx
    			,player.position.y + dy
			);
    	if (model.currentMap.isWalkable(player.nextPosition)) return true;
    	return false;
    }
	
	private static int sgn(final int v) { 
		if (v == 0) return 0;
		else if (v > 0) return 1;
		else return -1;
	}
	private static int abs(final int v) { 
		if (v == 0) return 0;
		else if (v > 0) return v;
		else return -v;
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
		
		if (handleEvents) {
			MapObject o = currentMap.getEventObjectAt(newPosition);
			if (o != null) view.controller.handleMapEvent(o);
	    	
	    	Loot loot = currentMap.getBagAt(newPosition);
	    	if (loot != null) view.itemController.handleLootBag(loot);
		}
    }

	public static void respawnPlayer(final WorldContext world) {
		placePlayerAt(world, world.model.player.spawnMap, world.model.player.spawnPlace);
	}
}
