package com.gpl.rpg.AndorsTrail.controller;

import android.content.res.Resources;
import android.os.AsyncTask;

import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.model.map.TMXMapTranslator;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCollection;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.TimedMessageTask;

public final class MovementController implements TimedMessageTask.Callback {
	private final ViewContext view;
    private final WorldContext world;
    private final ModelContainer model;
    private final TimedMessageTask movementHandler;

	public MovementController(ViewContext context) {
    	this.view = context;
    	this.world = context;
    	this.model = world.model;
    	this.movementHandler = new TimedMessageTask(this, Constants.MINIMUM_INPUT_INTERVAL, false);
    }
	
	public void placePlayerAt(final int objectType, final String mapName, final String placeName, final int offset_x, final int offset_y) {
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>()  {
			@Override
			protected Void doInBackground(Void... arg0) {
				stopMovement();
				
				placePlayerAt(view.mainActivity.getResources(), world, objectType, mapName, placeName, offset_x, offset_y); 
				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				view.mainActivity.clearMessages();
				view.mainActivity.mainview.notifyMapChanged(model);
				stopMovement();
				view.gameRoundController.resume();
			}
			
		};
		view.gameRoundController.pause();
		task.execute();
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
		cacheCurrentMapData(res, world, newMap);
		model.currentMap = newMap;
		model.player.position.set(place.position.topLeft);
		model.player.position.x += Math.min(offset_x, place.position.size.width-1);
		model.player.position.y += Math.min(offset_y, place.position.size.height-1);
		model.player.lastPosition.set(model.player.position);
		
		if (!newMap.visited) playerVisitsMapFirstTime(world, newMap);
		else playerVisitsMap(world, newMap);
		
		refreshMonsterAggressiveness(newMap, model.player);
		VisualEffectController.updateSplatters(newMap);
	}
    
	private static void playerVisitsMapFirstTime(final WorldContext world, PredefinedMap m) {
		m.reset();
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

    private void movePlayer(int dx, int dy) {
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
    	// try to move with movementAggresiveness, if that fails fall back to MOVEMENTAGGRESSIVENESS_NORMAL
    	if (findWalkablePosition(dx, dy, view.preferences.movementAggressiveness)) return true;
    	
    	if (view.preferences.movementAggressiveness == AndorsTrailPreferences.MOVEMENTAGGRESSIVENESS_NORMAL) return false;
    	
    	return findWalkablePosition(dx, dy, AndorsTrailPreferences.MOVEMENTAGGRESSIVENESS_NORMAL);
    }

    public boolean findWalkablePosition(int dx, int dy, int aggressiveness) {
    	if (view.preferences.movementMethod == AndorsTrailPreferences.MOVEMENTMETHOD_STRAIGHT) {
    		return findWalkablePosition_straight(dx, dy, aggressiveness);
    	} else  {
    		return findWalkablePosition_directional(dx, dy, aggressiveness);
    	}
    }
    public boolean findWalkablePosition_straight(int dx, int dy, int aggressiveness) {
    	if (tryWalkablePosition(sgn(dx), sgn(dy), aggressiveness)) return true;                 // try moving into the direction player is pointing at
    	if (dx == 0 || dy == 0) return false;                                                   // if moving purely east, west, north or south failed - do nothing  
		if (abs(dx) == abs(dy) && tryWalkablePosition(sgn(dx), 0, aggressiveness)) return true; // try moving horizontally or vertically otherwise (prefer the direction where he is pointing more) 
    	if (abs(dx) > abs(dy)) return tryWalkablePosition(sgn(dx), 0, aggressiveness);
    	return tryWalkablePosition(0, sgn(dy), aggressiveness);
    }
    public boolean findWalkablePosition_directional(int dx, int dy, int aggressiveness) {
        if (tryWalkablePosition(sgn(dx), sgn(dy), aggressiveness)) return true; // try moving into the direction player is pointing at

        if (dx == 0) { // player wants to move north or south but there is an obstacle
            if (tryWalkablePosition( 1, sgn(dy), aggressiveness)) return true; // try moving north-east (or south-east)
            if (tryWalkablePosition(-1, sgn(dy), aggressiveness)) return true; // try moving north-west (or south-west)
            return false;
        }

        if (dy == 0) { // player wants to move east or west but there is an obstacle
            if (tryWalkablePosition(sgn(dx), 1, aggressiveness)) return true; // try moving north-east (or north-west)
            if (tryWalkablePosition(sgn(dx),-1, aggressiveness)) return true; // try moving south-east (or south-west)
            return false;
        }

        if (abs(dx) >= abs(dy)) { // player wants to move more horizontally
            if (tryWalkablePosition(sgn(dx), 0, aggressiveness)) return true; // try moving horizontally
            if (tryWalkablePosition(0, sgn(dy), aggressiveness)) return true; // try moving vertically
            return false;
        } else { // player wants to move more vertically
            if (tryWalkablePosition(0, sgn(dy), aggressiveness)) return true; // try moving vertically
            if (tryWalkablePosition(sgn(dx), 0, aggressiveness)) return true; // try moving horizontally
            return false;
        }
    }
    
    private boolean tryWalkablePosition(int dx, int dy, int aggressiveness) {
    	final Player player = model.player;
    	player.nextPosition.set(
				player.position.x + dx
    			,player.position.y + dy
			);

    	if (!model.currentMap.isWalkable(player.nextPosition)) return false;
    	
		// allow player to enter every field when he is NORMAL
		// prevent player from entering "non-monster-fields" when he is AGGRESSIVE
		// prevent player from entering "monster-fields" when he is DEFENSIVE
		if (aggressiveness == AndorsTrailPreferences.MOVEMENTAGGRESSIVENESS_NORMAL) return true;
		
		Monster m = model.currentMap.getMonsterAt(player.nextPosition);
		if (m != null && !m.isAgressive()) return true; // avoid MOVEMENTAGGRESSIVENESS settings for NPCs
		
		if (aggressiveness == AndorsTrailPreferences.MOVEMENTAGGRESSIVENESS_AGGRESSIVE && m == null) return false;
		else if (aggressiveness == AndorsTrailPreferences.MOVEMENTAGGRESSIVENESS_DEFENSIVE && m != null) return false;
		
		return true;
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
		view.mainActivity.mainview.notifyPlayerMoved(newPosition);
		
		if (handleEvents) {
			MapObject o = currentMap.getEventObjectAt(newPosition);
			if (o != null) {
				if (!o.position.contains(player.lastPosition)) { // Do not trigger event if the player already was on the same MapObject before.
					view.controller.handleMapEvent(o, newPosition);
				}
			}
	    	
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
			Coord playerPosition = null;
			if (map == model.currentMap) playerPosition = model.player.position;
			for (MonsterSpawnArea a : map.spawnAreas) {
				for (Monster m : a.monsters) {
					if (!map.isWalkable(m.rectPosition)) {
						Coord p = map.getRandomFreePosition(a.area, m.actorTraits.tileSize, playerPosition);
						if (p == null) continue;
						m.position.set(p);
					}
				}
			}
		}
	}

	public static void cacheCurrentMapData(final Resources res, final WorldContext world, final PredefinedMap nextMap) {
		LayeredTileMap mapTiles = TMXMapTranslator.readLayeredTileMap(res, world.tileManager.tileCache, nextMap);
		TileCollection cachedTiles = world.tileManager.loadTilesFor(nextMap, mapTiles, world, res);
		world.model.currentTileMap = mapTiles;
		world.tileManager.currentMapTiles = cachedTiles;
		world.tileManager.cacheAdjacentMaps(res, world, nextMap);
	}
	
	
	private int movementDx;
	private int movementDy;
	public void startMovement(int dx, int dy, Coord destination) {
		if (model.uiSelections.isInCombat) return;
		if (dx == 0 && dy == 0) return;
		
		movementDx = dx;
		movementDy = dy;
		movementHandler.start();
	}
	
	public void stopMovement() {
		movementHandler.stop();
	}
	
	public boolean onTick(TimedMessageTask task) {
		if (!model.uiSelections.isMainActivityVisible) return false;
    	if (model.uiSelections.isInCombat) return false;
    	
    	movePlayer(movementDx, movementDy);
		
    	return true;
	}

	public static void refreshMonsterAggressiveness(final PredefinedMap map, final Player player) {
		for(MonsterSpawnArea a : map.spawnAreas) {
			for (Monster m : a.monsters) {
				if (m.faction == null) continue;
				if (player.getAlignment(m.faction) < 0) m.forceAggressive = true;
			}
		}
	}
	
	public static boolean hasAdjacentAggressiveMonster(PredefinedMap map, Coord position) {
		return getAdjacentAggressiveMonster(map, position) != null;
	}
	public static Monster getAdjacentAggressiveMonster(PredefinedMap map, Coord position) {
		for (MonsterSpawnArea a : map.spawnAreas) {
			for (Monster m : a.monsters) {
				if (!m.isAgressive()) continue;
				if (m.rectPosition.isAdjacentTo(position)) return m;
			}
		}
		return null;
	}
}
