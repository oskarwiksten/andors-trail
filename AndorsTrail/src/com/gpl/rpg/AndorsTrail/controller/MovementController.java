package com.gpl.rpg.AndorsTrail.controller;

import android.content.res.Resources;

import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.model.map.TMXMapReader;
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
	
	public void placePlayerAt(int objectType, String mapName, String placeName, int offset_x, int offset_y) { 
		placePlayerAt(view.mainActivity.getResources(), world, objectType, mapName, placeName, offset_x, offset_y); 
		view.mainActivity.clearMessages();
		view.mainActivity.mainview.notifyMapChanged();
    }
	
	public static void placePlayerAt(final Resources res, final WorldContext world, int objectType, String mapName, String placeName, int offset_x, int offset_y) {
    	if (mapName == null || placeName == null) return;
		PredefinedMap newMap = world.maps.findPredefinedMap(mapName);
		if (newMap == null) {
			L.log("Cannot find map " + mapName);
			return;
		}
		MapObject place = newMap.findEventObject(objectType, placeName);
		if (place == null) {
			L.log("Cannot find place " + placeName + " of type " + objectType + " in map " + mapName);
			return;
		}
		final ModelContainer model = world.model;
		
		if (model.currentMap != null) model.currentMap.updateLastVisitTime();
		model.currentMap = newMap;
		loadCurrentTileMap(res, world);
		model.player.position.set(place.position.topLeft);
		model.player.position.x += Math.min(offset_x, place.position.size.width-1);
		model.player.position.y += Math.min(offset_y, place.position.size.height-1);
		model.player.lastPosition.set(model.player.position);
		
		if (!newMap.visited) playerVisitsMapFirstTime(world, newMap);
		else playerVisitsMap(world, newMap);
	}
    
	private static void playerVisitsMapFirstTime(final WorldContext world, PredefinedMap m) {
		m.spawnAll(world);
		m.createAllContainerLoot();
		m.visited = true;
	}
	private static void playerVisitsMap(final WorldContext world, PredefinedMap m) {
		// Respawn everything if a certain time has elapsed.
		if (!m.isRecentlyVisited()) m.spawnAll(world);
	}
	
	public boolean mayMovePlayer() {
		return !model.uiSelections.isInCombat;
	}

    public void movePlayer(int dx, int dy) {
    	if (dx == 0 && dy == 0) return;
    	if (!mayMovePlayer()) return;

    	if (!findWalkablePosition(dx, dy)) return;
    	
    	Monster m = model.currentMap.getMonsterAt(model.player.nextPosition);
		if (m != null) {
			view.controller.steppedOnMonster(m, model.player.nextPosition);
			return;
		}

		moveToNextIfPossible(true);
    }
    
    public boolean findWalkablePosition(int dx, int dy) {
    	if (view.preferences.movementMethod == AndorsTrailPreferences.MOVEMENTMETHOD_DIRECTIONAL) {
    		return findWalkablePosition_directional(dx, dy);
    	} else {
    		return findWalkablePosition_straight(dx, dy);
    	}
    }
    public boolean findWalkablePosition_straight(int dx, int dy) {
    	if (tryWalkablePosition(sgn(dx), sgn(dy))) return true;
    	if (dx == 0 || dy == 0) return false;
		if (abs(dx) == abs(dy) && tryWalkablePosition(sgn(dx), 0)) return true;
    	if (abs(dx) > abs(dy)) return tryWalkablePosition(sgn(dx), 0);
    	return tryWalkablePosition(0, sgn(dy));
    }
    public boolean findWalkablePosition_directional(int dx, int dy) {
        if (tryWalkablePosition(sgn(dx), sgn(dy))) return true; // try moving into the direction player is pointing at

        if (dx == 0) { // player wants to move north or south but there is an obstacle
            if (tryWalkablePosition( 1, sgn(dy))) return true; // try moving north-east (or south-east)
            if (tryWalkablePosition(-1, sgn(dy))) return true; // try moving north-west (or south-west)
            return false;
        }

        if (dy == 0) { // player wants to move east or west but there is an obstacle
            if (tryWalkablePosition(sgn(dx), 1)) return true; // try moving north-east (or north-west)
            if (tryWalkablePosition(sgn(dx),-1)) return true; // try moving south-east (or south-west)
            return false;
        }

        if (abs(dx) >= abs(dy)) { // player wants to move more horizontally
            if (tryWalkablePosition(sgn(dx), 0)) return true; // try moving horizontally
            if (tryWalkablePosition(0, sgn(dy))) return true; // try moving vertically
            return false;
        } else { // player wants to move more vertically
            if (tryWalkablePosition(0, sgn(dy))) return true; // try moving vertically
            if (tryWalkablePosition(sgn(dx), 0)) return true; // try moving horizontally
            return false;
        }
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
    	final PredefinedMap currentMap = model.currentMap;
    	final Coord newPosition = player.nextPosition;
    	
    	for (MapObject o : currentMap.eventObjects) {
    		if (o.type == MapObject.MAPEVENT_KEYAREA) {
	    		if (o.position.contains(newPosition)) {
	    			if (!view.controller.handleKeyArea(o)) return;
	    		}
    		}
    	}
		
    	player.lastPosition.set(player.position);
    	player.position.set(newPosition);
    	view.combatController.setCombatSelection(null, null);
		view.mainActivity.mainview.notifyPlayerMoved();
		
		if (handleEvents) {
			MapObject o = currentMap.getEventObjectAt(newPosition);
			if (o != null) view.controller.handleMapEvent(o, newPosition);
	    	
	    	Loot loot = currentMap.getBagAt(newPosition);
	    	if (loot != null) view.itemController.handleLootBag(loot);
		}
    }

	public static void respawnPlayer(final Resources res, final WorldContext world) {
		placePlayerAt(res, world, MapObject.MAPEVENT_REST, world.model.player.spawnMap, world.model.player.spawnPlace, 0, 0);
	}

	public static void moveBlockedActors(final WorldContext world) {
		final ModelContainer model = world.model;
		if (!world.model.currentMap.isWalkable(world.model.player.position)) {
			// If the player somehow spawned on an unwalkable tile, we move the player to the first mapchange area.
			// This could happen if we change some tile to non-walkable in a future version.
			MapObject dest = null;
			for (MapObject o : model.currentMap.eventObjects) {
	    		if (o.type == MapObject.MAPEVENT_NEWMAP) {
		    		dest = o;
		    		break;
	    		}
	    	}
			if (dest != null) {
				model.player.position.set(dest.position.topLeft);
			}
		}
		
		// If any monsters somehow spawned on an unwalkable tile, we move the monster to a new position on the spawnarea
		// This could happen if we change some tile to non-walkable in a future version.
		for (PredefinedMap map : world.maps.predefinedMaps) {
			for (MonsterSpawnArea a : map.spawnAreas) {
				for (Monster m : a.monsters) {
					if (!world.model.currentMap.isWalkable(m.rectPosition)) {
						Coord p = map.getRandomFreePosition(a.area, m.traits.tileSize, model.player.position);
						if (p == null) continue;
						m.position.set(p);
					}
				}
			}
		}
	}

	public static void loadCurrentTileMap(Resources res, WorldContext world) {
		world.model.currentTileMap = TMXMapReader.readLayeredTileMap(res, world.tileStore, world.model.currentMap);
	}
}
