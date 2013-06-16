package com.gpl.rpg.AndorsTrail.model.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.model.map.TMXMapFileParser.TMXLayer;
import com.gpl.rpg.AndorsTrail.model.map.TMXMapFileParser.TMXLayerMap;
import com.gpl.rpg.AndorsTrail.model.map.TMXMapFileParser.TMXMap;
import com.gpl.rpg.AndorsTrail.model.map.TMXMapFileParser.TMXObject;
import com.gpl.rpg.AndorsTrail.model.map.TMXMapFileParser.TMXObjectGroup;
import com.gpl.rpg.AndorsTrail.model.map.TMXMapFileParser.TMXProperty;
import com.gpl.rpg.AndorsTrail.model.map.TMXMapFileParser.TMXTileSet;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCache;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Range;
import com.gpl.rpg.AndorsTrail.util.Size;

import android.content.res.Resources;

public final class TMXMapTranslator {
	private final ArrayList<TMXMap> maps = new ArrayList<TMXMap>();
	
	public void read(Resources r, int xmlResourceId, String name) {
		maps.add(TMXMapFileParser.read(r, xmlResourceId, name));
	}
	
	public static LayeredTileMap readLayeredTileMap(Resources res, TileCache tileCache, PredefinedMap map) {
		TMXLayerMap resultMap = TMXMapFileParser.readLayeredTileMap(res, map.xmlResourceId, map.name);
		return transformMap(resultMap, tileCache, map.name);
	}

	public ArrayList<PredefinedMap> transformMaps(MonsterTypeCollection monsterTypes, DropListCollection dropLists) {
		return transformMaps(maps, monsterTypes, dropLists);
	}
	public ArrayList<PredefinedMap> transformMaps(Collection<TMXMap> maps, MonsterTypeCollection monsterTypes, DropListCollection dropLists) {
		ArrayList<PredefinedMap> result = new ArrayList<PredefinedMap>();
		
		Tile tile = new Tile();
		for (TMXMap m : maps) {
			assert(m.name != null);
			assert(m.name.length() > 0);
			assert(m.width > 0);
			assert(m.height > 0);
			
			boolean isOutdoors = false;
			for (TMXProperty p : m.properties) {
				if(p.name.equalsIgnoreCase("outside")) isOutdoors = (Integer.parseInt(p.value) != 0);
				else if(AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) L.log("OPTIMIZE: Map " + m.name + " has unrecognized property \"" + p.name + "\".");
			}
			
			final Size mapSize = new Size(m.width, m.height);
			ArrayList<MapObject> mapObjects = new ArrayList<MapObject>();
			ArrayList<MonsterSpawnArea> spawnAreas = new ArrayList<MonsterSpawnArea>();
			
			for (TMXObjectGroup group : m.objectGroups) {
				for (TMXObject object : group.objects) {
					final Coord topLeft = new Coord(
						Math.round(((float)object.x) / m.tilewidth)
						,Math.round(((float)object.y) / m.tileheight)
					);
					final int width = Math.round(((float)object.width) / m.tilewidth);
					final int height = Math.round(((float)object.height) / m.tileheight);
					final CoordRect position = new CoordRect(topLeft, new Size(width, height));
					
					if (object.type == null) {
						if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) 
							L.log("WARNING: Map " + m.name + ", object \"" + object.name + "\" has null type.");
					} else if (object.type.equalsIgnoreCase("sign")) {
						String phraseID = object.name;
						for (TMXProperty p : object.properties) {
							if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) L.log("OPTIMIZE: Map " + m.name + ", sign " + object.name + " has unrecognized property \"" + p.name + "\".");
						}
						mapObjects.add(MapObject.createMapSignEvent(position, phraseID));
					} else if (object.type.equalsIgnoreCase("mapchange")) {
						String map = null;
						String place = null;
						for (TMXProperty p : object.properties) {
							if(p.name.equalsIgnoreCase("map")) map = p.value;
							else if(p.name.equalsIgnoreCase("place")) place = p.value;
							else if(AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) L.log("OPTIMIZE: Map " + m.name + ", mapchange " + object.name + " has unrecognized property \"" + p.name + "\".");
						}
						mapObjects.add(MapObject.createNewMapEvent(position, object.name, map, place));
					} else if (object.type.equalsIgnoreCase("spawn")) {
						ArrayList<MonsterType> types = monsterTypes.getMonsterTypesFromSpawnGroup(object.name);
						int maxQuantity = 1;
						int spawnChance = 10;
						for (TMXProperty p : object.properties) {
							if (p.name.equalsIgnoreCase("quantity")) {
								maxQuantity = Integer.parseInt(p.value);
							} else if (p.name.equalsIgnoreCase("spawnchance")) {
								spawnChance = Integer.parseInt(p.value);
							} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
								L.log("OPTIMIZE: Map " + m.name + ", spawn " + object.name + " has unrecognized property \"" + p.name + "\".");
							}
						}
						
						if (types.isEmpty()) {
							if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
								L.log("OPTIMIZE: Map " + m.name + " contains spawn \"" + object.name + "\" that does not correspond to any monsters. The spawn will be removed.");
							}
							continue;
						}
						
						String[] monsterTypeIDs = new String[types.size()];
						boolean isUnique = types.get(0).isUnique;
						for (int i = 0; i < monsterTypeIDs.length; ++i) {
							monsterTypeIDs[i] = types.get(i).id;
						}
						MonsterSpawnArea area = new MonsterSpawnArea(
								position
								,new Range(maxQuantity, 0)
								,new Range(1000, spawnChance)
								,monsterTypeIDs
								,isUnique
							);
						spawnAreas.add(area);
					} else if (object.type.equalsIgnoreCase("key")) {
						QuestProgress requireQuestStage = QuestProgress.parseQuestProgress(object.name);
						if (requireQuestStage == null) {
							if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
								L.log("OPTIMIZE: Map " + m.name + " contains key area that cannot be parsed as a quest stage.");
							}
							continue;
						}
						String phraseID = "";
						for (TMXProperty p : object.properties) {
							if (p.name.equalsIgnoreCase("phrase")) {
								phraseID = p.value;
							} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
								L.log("OPTIMIZE: Map " + m.name + ", key " + object.name + " has unrecognized property \"" + p.name + "\".");
							}
						}
						
						mapObjects.add(MapObject.createNewKeyArea(position, phraseID, requireQuestStage));
					} else if (object.type.equals("rest")) {
						mapObjects.add(MapObject.createNewRest(position, object.name));
					} else if (object.type.equals("container")) {
						DropList dropList = dropLists.getDropList(object.name);
						if (dropList == null) continue;
						mapObjects.add(MapObject.createNewContainerArea(position, dropList));
					} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
						L.log("OPTIMIZE: Map " + m.name + ", has unrecognized object type \"" + object.type + "\" for name \"" + object.name + "\".");
					}
				}
			}
			MapObject[] _eventObjects = new MapObject[mapObjects.size()];
			_eventObjects = mapObjects.toArray(_eventObjects);
			MonsterSpawnArea[] _spawnAreas = new MonsterSpawnArea[spawnAreas.size()];
			_spawnAreas = spawnAreas.toArray(_spawnAreas);

			result.add(new PredefinedMap(m.xmlResourceId, m.name, mapSize, _eventObjects, _spawnAreas, isOutdoors));
		}
		
		return result;
	}
	

	private static LayeredTileMap transformMap(TMXLayerMap map, TileCache tileCache, String mapName) {
		final Size mapSize = new Size(map.width, map.height);
		final MapLayer[] layers = new MapLayer[] {
			new MapLayer(mapSize)
			,new MapLayer(mapSize)
			,new MapLayer(mapSize)
		};
		boolean[][] isWalkable = new boolean[map.width][map.height];
		for (int y = 0; y < map.height; ++y) {
			for (int x = 0; x < map.width; ++x) {
				isWalkable[x][y] = true;
			}
		}
		Tile tile = new Tile();
		String colorFilter = null;
		for (TMXProperty prop : map.properties) {
			if (prop.name.equalsIgnoreCase("colorfilter")) colorFilter = prop.value;
		}
		HashSet<Integer> usedTileIDs = new HashSet<Integer>();
		for (TMXLayer layer : map.layers) {
			int ixMapLayer = 0;
			String layerName = layer.name;
			assert(layerName != null);
			assert(layerName.length() > 0);
			layerName = layerName.toLowerCase();
			boolean isWalkableLayer = false;
			if (layerName.startsWith("object")) {
				ixMapLayer = LayeredTileMap.LAYER_OBJECTS;
			} else if (layerName.startsWith("ground")) {
				ixMapLayer = LayeredTileMap.LAYER_GROUND;
			} else if (layerName.startsWith("above")) {
				ixMapLayer = LayeredTileMap.LAYER_ABOVE;
			} else if (layerName.startsWith("walk")) {
				isWalkableLayer = true;
			} else {
				continue;
			}
			
			for (int y = 0; y < layer.height; ++y) {
				for (int x = 0; x < layer.width; ++x) {
					int gid = layer.gids[x][y];
					if (gid <= 0) continue;
					
					if (!getTile(map, gid, tile)) continue;

					if (isWalkableLayer) {
						isWalkable[x][y] = false;
					} else {
						int tileID = tileCache.getTileID(tile.tilesetName, tile.localId);
						layers[ixMapLayer].tiles[x][y] = tileID;
						usedTileIDs.add(tileID);
					}
				}
			}
		}
		return new LayeredTileMap(mapSize, layers, isWalkable, usedTileIDs, colorFilter);
	}
	
	private static boolean getTile(final TMXLayerMap map, final int gid, final Tile dest) {
		for(int i = map.tileSets.length - 1; i >= 0; --i) {
			TMXTileSet ts = map.tileSets[i];
			if (ts.firstgid <= gid) {
				dest.tilesetName = ts.imageName;
				dest.localId = (gid - ts.firstgid);
				return true;
			}
		}
		L.log("WARNING: Cannot find tile for gid " + gid); //(" + x + ", " + y + "), ")
		return false;
	}
	
	private static final class Tile {
		public String tilesetName;
		public int localId;
	}
	
	/*
	public static final class TMXMap extends TMXLayerMap {
		public int xmlResourceId;
		public String name;
		public String orientation;
		public int tilewidth;
		public int tileheight;
		public ArrayList<TMXObjectGroup> objectGroups = new ArrayList<TMXObjectGroup>();
	}
	public static class TMXLayerMap {
		public int width;
		public int height;
		public TMXTileSet[] tileSets;
		public TMXLayer[] layers;
	}
	public static final class TMXTileSet {
		public int firstgid;
		public String name;
		public int tilewidth;
		public int tileheight;
		public String imageSource;
		public String imageName;
	}
	public static final class TMXLayer {
		public String name;
		public int width;
		public int height;
		public int[][] gids;
	}
	public static final class TMXObjectGroup {
		public String name;
		public int width;
		public int height;
		public ArrayList<TMXObject> objects = new ArrayList<TMXObject>();
	}
	public static final class TMXObject {
		public String name;
		public String type;
		public int x;
		public int y;
		public int width;
		public int height;
		public ArrayList<TMXProperty> properties = new ArrayList<TMXProperty>();
	}
	public static final class TMXProperty {
		public String name;
		public String value;
	}
	*/
	/*
	 
	 <map version="1.0" orientation="orthogonal" width="10" height="10" tilewidth="32" tileheight="32">
		 <tileset firstgid="1" name="tiles" tilewidth="32" tileheight="32">
	  		<image source="tilesets/tiles.png"/>
		 </tileset>
		 <layer name="Tile Layer 1" width="10" height="10">
			<data encoding="base64" compression="gzip">
			   H4sIAAAAAAAAA/NgYGDwIBK7AbEnHkyOOmwYXR02MwZSHQyTah4xGADnAt2SkAEAAA==
			</data>
		 </layer>
		 <layer name="Tile Layer 2" width="10" height="10">
			<data encoding="base64" compression="gzip">
			   H4sIAAAAAAAAA2NgoA1gYUHlP2HGro6NBbt4MysqXw2oLhEqlgSlU4H0YjR12EAbUE0KFnXPgG5iRLJ/GQ6zHuNwOy7gxE6aemQAAJRT7VKQAQAA
			</data>
		 </layer>
	 </map>

	 */
}
