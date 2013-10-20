package com.gpl.rpg.AndorsTrail.model.map;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import android.content.res.Resources;

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
import com.gpl.rpg.AndorsTrail.model.map.TMXMapFileParser.TMXObjectMap;
import com.gpl.rpg.AndorsTrail.model.map.TMXMapFileParser.TMXProperty;
import com.gpl.rpg.AndorsTrail.model.map.TMXMapFileParser.TMXTileSet;
import com.gpl.rpg.AndorsTrail.model.script.Requirement;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCache;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Range;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class TMXMapTranslator {
	private final ArrayList<TMXObjectMap> maps = new ArrayList<TMXObjectMap>();

	public void read(Resources r, int xmlResourceId, String name) {
		maps.add(TMXMapFileParser.readObjectMap(r, xmlResourceId, name));
	}

	public static LayeredTileMap readLayeredTileMap(Resources res, TileCache tileCache, PredefinedMap map) {
		TMXLayerMap resultMap = TMXMapFileParser.readLayerMap(res, map.xmlResourceId, map.name);
		return transformMap(resultMap, tileCache);
	}

	public ArrayList<PredefinedMap> transformMaps(MonsterTypeCollection monsterTypes, DropListCollection dropLists) {
		return transformMaps(maps, monsterTypes, dropLists);
	}
	public ArrayList<PredefinedMap> transformMaps(Collection<TMXObjectMap> maps, MonsterTypeCollection monsterTypes, DropListCollection dropLists) {
		ArrayList<PredefinedMap> result = new ArrayList<PredefinedMap>();

		for (TMXObjectMap m : maps) {
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
			ArrayList<MapObjectReplace> mapObjectReplaces = new ArrayList<MapObjectReplace>();
			ArrayList<MonsterSpawnArea> spawnAreas = new ArrayList<MonsterSpawnArea>();

			ArrayList<String> objectGroupsNames = null;
			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				objectGroupsNames = new ArrayList<String>();
				for (TMXObjectGroup group : m.objectGroups) {
					objectGroupsNames.add(group.name);
				}
			}
			ArrayList<String> objectsGroupsToDisable = new ArrayList<String>();
			
			for (TMXObjectGroup group : m.objectGroups) {
				for (TMXObject object : group.objects) {
					final CoordRect position = getTMXObjectPosition(object, m);
					final Coord topLeft = position.topLeft;

					if (object.type == null) {
						if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA)
							L.log("WARNING: Map " + m.name + ", object \"" + object.name + "\"@" + topLeft.toString() + " has null type.");
					} else if (object.type.equalsIgnoreCase("sign")) {
						String phraseID = object.name;
						for (TMXProperty p : object.properties) {
							if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) L.log("OPTIMIZE: Map " + m.name + ", sign " + object.name + "@" + topLeft.toString() + " has unrecognized property \"" + p.name + "\".");
						}
						mapObjects.add(MapObject.createMapSignEvent(position, phraseID, group.name));
					} else if (object.type.equalsIgnoreCase("mapchange")) {
						String map = null;
						String place = null;
						for (TMXProperty p : object.properties) {
							if(p.name.equalsIgnoreCase("map")) map = p.value;
							else if(p.name.equalsIgnoreCase("place")) place = p.value;
							else if(AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) L.log("OPTIMIZE: Map " + m.name + ", mapchange " + object.name + "@" + topLeft.toString() + " has unrecognized property \"" + p.name + "\".");
						}
						mapObjects.add(MapObject.createMapChangeArea(position, object.name, map, place, group.name));
					} else if (object.type.equalsIgnoreCase("spawn")) {
						ArrayList<MonsterType> types = monsterTypes.getMonsterTypesFromSpawnGroup(object.name);
						int maxQuantity = 1;
						int spawnChance = 10;
						for (TMXProperty p : object.properties) {
							if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
								if (p.value.equals("")) {
									L.log("OPTIMIZE: Map " + m.name + ", spawn " + object.name + "@" + topLeft.toString() + " has property \"" + p.name + "\" without value.");
									continue;
								}
							}
							if (p.name.equalsIgnoreCase("quantity")) {
								maxQuantity = Integer.parseInt(p.value);
							} else if (p.name.equalsIgnoreCase("spawnchance")) {
								spawnChance = Integer.parseInt(p.value);
							} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
								L.log("OPTIMIZE: Map " + m.name + ", spawn " + object.name + "@" + topLeft.toString() + " has unrecognized property \"" + p.name + "\".");
							}
						}

						if (types.isEmpty()) {
							if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
								L.log("OPTIMIZE: Map " + m.name + " contains spawn \"" + object.name + "\"@" + topLeft.toString() + " that does not correspond to any monsters. The spawn will be removed.");
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
								,group.name
						);
						spawnAreas.add(area);
					} else if (object.type.equalsIgnoreCase("key")) {
						Requirement.RequirementType requireType = Requirement.RequirementType.questProgress;
						String requireId = null;
						int requireValue = 0;
						String phraseID = "";
						for (TMXProperty p : object.properties) {
							if (p.name.equalsIgnoreCase("phrase")) {
								phraseID = p.value;
							} else if (p.name.equalsIgnoreCase("requireType")) {
								requireType = Requirement.RequirementType.valueOf(p.value);
							} else if (p.name.equalsIgnoreCase("requireId")) {
								requireId = p.value;
							} else if (p.name.equalsIgnoreCase("requireValue")) {
								requireValue = Integer.parseInt(p.value);
							} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
								L.log("OPTIMIZE: Map " + m.name + ", key " + object.name + "@" + topLeft.toString() + " has unrecognized property \"" + p.name + "\".");
							}
						}
						mapObjects.add(MapObject.createKeyArea(position, phraseID, new Requirement(requireType, requireId, requireValue), group.name));
					} else if (object.type.equals("rest")) {
						mapObjects.add(MapObject.createRestArea(position, object.name, group.name));
					} else if (object.type.equals("container")) {
						DropList dropList = dropLists.getDropList(object.name);
						if (dropList == null) continue;
						mapObjects.add(MapObject.createContainerArea(position, dropList, group.name));
					} else if (object.type.equals("replace")) {
						//Externalized as used twice.
						Requirement requirement = parseRequirement(object);
						//First pass, to find out spawn strategy
						MapObjectReplace.SpawnStrategy strategy = MapObjectReplace.SpawnStrategy.doNothing;
						for (TMXProperty p : object.properties) {
							if ("spawnStrategy".equals(p.name)) {
								strategy = MapObjectReplace.SpawnStrategy.valueOf(p.value);  
							}
						}

						for (TMXProperty p : object.properties) {
							//Ignore the already parsed properties
							if (p.name.equalsIgnoreCase("spawnStrategy")) continue;
							if (isRequirementProperty(p)) continue;
								
							// Do nothing when only graphics layers are impacted. Those will be handled in the map rendering.
							if (TMXMapTranslator.isGraphicsMapLayer(p.name) ) continue;

							mapObjectReplaces.add(new MapObjectReplace(position, p.name, p.value, group.name, strategy, requirement));
							//Consider all objects/spawns that are part of a group that is a "replace" target as disabled initially.
							objectsGroupsToDisable.add(p.value);
							if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
								if (!objectGroupsNames.contains(p.name)) {
									L.log("OPTIMIZE: Map " + m.name + ", replace object " + object.name + "@" + topLeft.toString() + " tries to replace unkown Object group \"" + p.name + "\".");
								}
								if (!objectGroupsNames.contains(p.value)) {
									L.log("OPTIMIZE: Map " + m.name + ", replace object " + object.name + "@" + topLeft.toString() + " tries to replace by unkown Object group \"" + p.name + "\".");
								}
							}
						}
					} else if (object.type.equalsIgnoreCase("script")) {
						String phraseID = object.name;
						MapObject.MapObjectEvaluationType evaluateWhen = MapObject.MapObjectEvaluationType.whenEntering;
						for (TMXProperty p : object.properties) {
							if (p.name.equalsIgnoreCase("when")) {
								if (p.value.equalsIgnoreCase("enter")) {
									evaluateWhen = MapObject.MapObjectEvaluationType.whenEntering;
								} else if (p.value.equalsIgnoreCase("step")) {
									evaluateWhen = MapObject.MapObjectEvaluationType.onEveryStep;
								} else if (p.value.equalsIgnoreCase("round")) {
									evaluateWhen = MapObject.MapObjectEvaluationType.afterEveryRound;
								} else if (p.value.equalsIgnoreCase("always")) {
									evaluateWhen = MapObject.MapObjectEvaluationType.continuously;
								} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
									L.log("OPTIMIZE: Map " + m.name + ", script " + object.name + "@" + topLeft.toString() + " has unrecognized value for \"when\" property: \"" + p.value + "\".");
								}
							} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
								L.log("OPTIMIZE: Map " + m.name + ", script " + object.name + "@" + topLeft.toString() + " has unrecognized property \"" + p.name + "\".");
							}
						}
						mapObjects.add(MapObject.createScriptArea(position, phraseID, evaluateWhen, group.name));
					} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
						L.log("OPTIMIZE: Map " + m.name + ", has unrecognized object type \"" + object.type + "\" for name \"" + object.name + "\".");
					}
				}
			}
			
			//Disable MapObjects that are part of a replace target group.
			for (MapObject obj : mapObjects) {
				if (objectsGroupsToDisable.contains(obj.group)) {
					obj.isActive = false;
				}
			}
			
			//Disable MonsterSpawnAreas that are part of a replace target group.
			for (MonsterSpawnArea area : spawnAreas) {
				if (objectsGroupsToDisable.contains(area.group)) {
					area.isActive = false;
				}
			}
			
			//Heck ! Also disable MapObjectReplaces that are part of a replace target group !
			for (MapObjectReplace replace : mapObjectReplaces) {
				if (objectsGroupsToDisable.contains(replace.group)) {
					replace.isActive = false;
				}
			}
			
			MapObject[] _eventObjects = new MapObject[mapObjects.size()];
			_eventObjects = mapObjects.toArray(_eventObjects);
			MonsterSpawnArea[] _spawnAreas = new MonsterSpawnArea[spawnAreas.size()];
			_spawnAreas = spawnAreas.toArray(_spawnAreas);
			MapObjectReplace[] _objectReplaces = new MapObjectReplace[mapObjectReplaces.size()];
			_objectReplaces = mapObjectReplaces.toArray(_objectReplaces);

			result.add(new PredefinedMap(m.xmlResourceId, m.name, mapSize, _eventObjects, _objectReplaces, _spawnAreas, isOutdoors));
		}

		return result;
	}

	private static CoordRect getTMXObjectPosition(TMXObject object, TMXMap m) {
		final Coord topLeft = new Coord(
				Math.round(((float)object.x) / m.tilewidth)
				,Math.round(((float)object.y) / m.tileheight)
		);
		final int width = Math.round(((float)object.width) / m.tilewidth);
		final int height = Math.round(((float)object.height) / m.tileheight);
		return new CoordRect(topLeft, new Size(width, height));
	}

	private static final String LAYERNAME_GROUND = "ground";
	private static final String LAYERNAME_OBJECTS = "objects";
	private static final String LAYERNAME_ABOVE = "above";
	private static final String LAYERNAME_WALKABLE = "walkable";
	private static final SetOfLayerNames defaultLayerNames = new SetOfLayerNames(LAYERNAME_GROUND, LAYERNAME_OBJECTS, LAYERNAME_ABOVE, LAYERNAME_WALKABLE);
	public static boolean isGraphicsMapLayer(final String groupOrLayerName) {
		return defaultLayerNames.containsIgnoreCase(groupOrLayerName);
	}

	private static LayeredTileMap transformMap(TMXLayerMap map, TileCache tileCache) {
		final Size mapSize = new Size(map.width, map.height);
		String colorFilter = null;
		for (TMXProperty prop : map.properties) {
			if (prop.name.equalsIgnoreCase("colorfilter")) colorFilter = prop.value;
		}
		HashSet<Integer> usedTileIDs = new HashSet<Integer>();
		HashMap<String, TMXLayer> layersPerLayerName = new HashMap<String, TMXLayer>();
		for (TMXLayer layer : map.layers) {
			String layerName = layer.name;
			assert(layerName != null);
			assert(layerName.length() > 0);
			layerName = layerName.toLowerCase();
			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				if (layersPerLayerName.containsKey(layerName)) {
					L.log("WARNING: Map \"" + map.name + "\" contains multiple layers with name \"" + layerName + "\".");
				}
			}
			layersPerLayerName.put(layerName, layer);
		}

		MapSection defaultLayout = transformMapSection(map,
				tileCache,
				new CoordRect(new Coord(0,0), mapSize),
				layersPerLayerName,
				usedTileIDs,
				defaultLayerNames);
		
		ArrayList<String> objectGroupsNames = new ArrayList<String>();
		for (TMXObjectGroup group : map.objectGroups) {
			objectGroupsNames.add(group.name);
		}
		
		ArrayList<String> replacementsToDisable = new ArrayList<String>();
		
		ArrayList<ReplaceableMapSection> replaceableSections = new ArrayList<ReplaceableMapSection>();
		for (TMXObjectGroup objectGroup : map.objectGroups) {
			for(TMXObject obj : objectGroup.objects) {
				if ("replace".equals(obj.type)) {
					Requirement requirement = parseRequirement(obj);

					final CoordRect position = getTMXObjectPosition(obj, map);
					SetOfLayerNames layerNames = new SetOfLayerNames();
					for (TMXProperty prop : obj.properties) {
						if ("spawnStrategy".equals(prop.name)) continue;
						if (isRequirementProperty(prop)) continue;
						else if (prop.name.equalsIgnoreCase(LAYERNAME_GROUND)) layerNames.groundLayerName = prop.value;
						else if (prop.name.equalsIgnoreCase(LAYERNAME_OBJECTS)) layerNames.objectsLayerName = prop.value;
						else if (prop.name.equalsIgnoreCase(LAYERNAME_ABOVE)) layerNames.aboveLayersName = prop.value;
						else if (prop.name.equalsIgnoreCase(LAYERNAME_WALKABLE)) layerNames.walkableLayersName = prop.value;
						else if (objectGroupsNames.contains(prop.name)) replacementsToDisable.add(prop.value);
						else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
							L.log("OPTIMIZE: Map " + map.name + " contains replace area with unknown property \"" + prop.name + "\".");
						}
						
					}
					//Don't create the Replaceable sections if this replace has no graphics impacts.
					if (layerNames.aboveLayersName != null 
							|| layerNames.groundLayerName != null
							|| layerNames.objectsLayerName != null
							|| layerNames.walkableLayersName != null) {
						MapSection replacementSection = transformMapSection(map, tileCache, position, layersPerLayerName, usedTileIDs, layerNames);
						replaceableSections.add(new ReplaceableMapSection(position, replacementSection, requirement, objectGroup.name));
					}
				}
			}
		}
		
		//Disable map replacements that are part of another replace's target groups
		for (ReplaceableMapSection replacement : replaceableSections) {
			if (!replacementsToDisable.contains(replacement.group)) continue;
			replacement.isActive = false;
		}

		ReplaceableMapSection[] replaceableSections_ = null;
		if (!replaceableSections.isEmpty()) {
			replaceableSections_ = replaceableSections.toArray(new ReplaceableMapSection[replaceableSections.size()]);
		}
		return new LayeredTileMap(mapSize, defaultLayout, replaceableSections_, colorFilter, usedTileIDs);
	}

	private static Requirement parseRequirement(TMXObject object) {
		Requirement.RequirementType requireType = Requirement.RequirementType.questProgress;
		String requireId = null;
		int requireValue = 0;
			
		for (TMXProperty prop : object.properties) {
			if (prop.name.equalsIgnoreCase("requireType")) {
				requireType = Requirement.RequirementType.valueOf(prop.value);
			} else if (prop.name.equalsIgnoreCase("requireId")) {
				requireId = prop.value;
			} else if (prop.name.equalsIgnoreCase("requireValue")) {
				requireValue = Integer.parseInt(prop.value);
			}
		}
		
		return new Requirement(requireType, requireId, requireValue);
	}
	
	private static boolean isRequirementProperty(TMXProperty prop) {
		if (prop.name.equalsIgnoreCase("requireType")) return true;
		if (prop.name.equalsIgnoreCase("requireId")) return true;
		if (prop.name.equalsIgnoreCase("requireValue")) return true;
		return false;
	}
	
	private static MapSection transformMapSection(
			TMXLayerMap srcMap,
			TileCache tileCache,
			CoordRect area,
			HashMap<String, TMXLayer> layersPerLayerName,
			HashSet<Integer> usedTileIDs,
			SetOfLayerNames layerNames
	) {
		final MapLayer layerGround = transformMapLayer(layersPerLayerName, layerNames.groundLayerName, srcMap, tileCache, area, usedTileIDs);
		final MapLayer layerObjects = transformMapLayer(layersPerLayerName, layerNames.objectsLayerName, srcMap, tileCache, area, usedTileIDs);
		final MapLayer layerAbove = transformMapLayer(layersPerLayerName, layerNames.aboveLayersName, srcMap, tileCache, area, usedTileIDs);
		boolean[][] isWalkable = transformWalkableMapLayer(findLayer(layersPerLayerName, layerNames.walkableLayersName, srcMap.name), area);
		byte[] layoutHash = calculateLayoutHash(srcMap, layersPerLayerName, layerNames);
		return new MapSection(layerGround, layerObjects, layerAbove, isWalkable, layoutHash);
	}

	private static TMXLayer findLayer(HashMap<String, TMXLayer> layersPerLayerName, String layerName, String mapName) {
		if (layerName == null) return null;
		if (layerName.length() == 0) return null;
		TMXLayer result = layersPerLayerName.get(layerName.toLowerCase());
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (result == null) {
				L.log("WARNING: Cannot find maplayer \"" + layerName + "\" requested by map \"" + mapName + "\".");
			}
		}
		return result;
	}

	private static MapLayer transformMapLayer(
			HashMap<String, TMXLayer> layersPerLayerName,
			String layerName,
			TMXLayerMap srcMap,
			TileCache tileCache,
			CoordRect area,
			HashSet<Integer> usedTileIDs
	) {
		TMXLayer srcLayer = findLayer(layersPerLayerName, layerName, srcMap.name);
		if (srcLayer == null) return null;
		final MapLayer result = new MapLayer(area.size);
		Tile tile = new Tile();
		for (int dy = 0, sy = area.topLeft.y; dy < area.size.height; ++dy, ++sy) {
			for (int dx = 0, sx = area.topLeft.x; dx < area.size.width; ++dx, ++sx) {
				int gid = srcLayer.gids[sx][sy];
				if (gid <= 0) continue;

				if (!getTile(srcMap, gid, tile)) continue;

				int tileID = tileCache.getTileID(tile.tilesetName, tile.localId);
				result.tiles[dx][dy] = tileID;
				usedTileIDs.add(tileID);
			}
		}
		return result;
	}

	private static boolean[][] transformWalkableMapLayer(TMXLayer srcLayer, CoordRect area) {
		if (srcLayer == null) return null;
		final boolean[][] isWalkable = new boolean[area.size.width][area.size.height];
		for (int x = 0; x < area.size.width; ++x) {
			Arrays.fill(isWalkable[x], true);
		}
		for (int dy = 0, sy = area.topLeft.y; dy < area.size.height; ++dy, ++sy) {
			for (int dx = 0, sx = area.topLeft.x; dx < area.size.width; ++dx, ++sx) {
				int gid = srcLayer.gids[sx][sy];
				if (gid > 0) {
					isWalkable[dx][dy] = false;
				}
			}
		}
		return isWalkable;
	}

	private static byte[] calculateLayoutHash(TMXLayerMap map, HashMap<String, TMXLayer> layersPerLayerName, SetOfLayerNames layerNames) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digestLayer(layersPerLayerName, layerNames.groundLayerName, map, digest);
			digestLayer(layersPerLayerName, layerNames.objectsLayerName, map, digest);
			digestLayer(layersPerLayerName, layerNames.aboveLayersName, map, digest);
			return digest.digest();
		} catch (NoSuchAlgorithmException e) {
			L.log("ERROR: Failed to create layout hash for map " + map.name + " : " + e.toString());
		}
		return new byte[0];
	}

	private static void digestLayer(HashMap<String, TMXLayer> layersPerLayerName, String layerName, TMXLayerMap map, MessageDigest digest) {
		TMXLayer srcLayer = findLayer(layersPerLayerName, layerName, map.name);
		if (srcLayer == null) return;
		if (srcLayer.layoutHash == null) return;
		digest.update(srcLayer.layoutHash);
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
		L.log("WARNING: Cannot find tile for gid " + gid);
		return false;
	}

	private static final class Tile {
		public String tilesetName;
		public int localId;
	}

	private static final class SetOfLayerNames {
		public String groundLayerName;
		public String objectsLayerName;
		public String aboveLayersName;
		public String walkableLayersName;
		public SetOfLayerNames() {
			this.groundLayerName = null;
			this.objectsLayerName = null;
			this.aboveLayersName = null;
			this.walkableLayersName = null;
		}
		public SetOfLayerNames(String groundLayerName, String objectsLayerName, String aboveLayersName, String walkableLayersName) {
			this.groundLayerName = groundLayerName;
			this.objectsLayerName = objectsLayerName;
			this.aboveLayersName = aboveLayersName;
			this.walkableLayersName = walkableLayersName;
		}
		public boolean containsIgnoreCase(String s) {
			if (s == null) return false;
			return s.equalsIgnoreCase(groundLayerName) ||
					s.equalsIgnoreCase(objectsLayerName) ||
					s.equalsIgnoreCase(aboveLayersName) ||
					s.equalsIgnoreCase(walkableLayersName);
		}
	}
}
