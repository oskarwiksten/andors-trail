package com.gpl.rpg.AndorsTrail.model.map;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.xmlpull.v1.XmlPullParserException;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterTypeCollection;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.util.Base64;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Range;
import com.gpl.rpg.AndorsTrail.util.Size;

import android.content.res.XmlResourceParser;

public final class TMXMapReader {
	private ArrayList<TMXMap> maps = new ArrayList<TMXMap>();
	
	public TMXMap read(XmlResourceParser xrp, String name) {
		final TMXMap currentMap = new TMXMap();
		try {
			// Map format: http://sourceforge.net/apps/mediawiki/tiled/index.php?title=Examining_the_map_format
			int eventType;
			while ((eventType = xrp.next()) != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					String s = xrp.getName();
					if (s.equals("map")) {
						currentMap.name = name;
						currentMap.orientation = xrp.getAttributeValue(null, "orientation");
						currentMap.width = xrp.getAttributeIntValue(null, "width", -1);
						currentMap.height = xrp.getAttributeIntValue(null, "height", -1);
						currentMap.tilewidth = xrp.getAttributeIntValue(null, "tilewidth", -1);
						currentMap.tileheight = xrp.getAttributeIntValue(null, "tileheight", -1);
						readCurrentTagUntilEnd(xrp, new TagHandler() {
							public void handleTag(XmlResourceParser xrp, String tagName) throws XmlPullParserException, IOException {
								if (tagName.equals("tileset")) {
									final TMXTileSet ts = new TMXTileSet();
									currentMap.tileSets.add(ts); 
									ts.firstgid = xrp.getAttributeIntValue(null, "firstgid", 1);
									ts.name = xrp.getAttributeValue(null, "name");
									ts.tilewidth = xrp.getAttributeIntValue(null, "tilewidth", -1);
									ts.tileheight = xrp.getAttributeIntValue(null, "tileheight", -1);
									readCurrentTagUntilEnd(xrp, new TagHandler() {
										public void handleTag(XmlResourceParser xrp, String tagName) {
											if (tagName.equals("image")) {
												ts.imageSource = xrp.getAttributeValue(null, "source");
												ts.imageName = ts.imageSource;
												
												int v = ts.imageName.lastIndexOf('/');
												if (v >= 0) ts.imageName = ts.imageName.substring(v+1);
											}
										}
									});
								} else if (tagName.equals("layer")) {
									final TMXLayer layer = new TMXLayer();
									currentMap.layers.add(layer);
									layer.name = xrp.getAttributeValue(null, "name");
									layer.width = xrp.getAttributeIntValue(null, "width", 1);
									layer.height = xrp.getAttributeIntValue(null, "height", 1);
									layer.gids = new int[layer.width][layer.height];
									readCurrentTagUntilEnd(xrp, new TagHandler() {
										public void handleTag(XmlResourceParser xrp, String tagName) throws XmlPullParserException, IOException {
											if (tagName.equals("data")) {
												xrp.next();
												String data = xrp.getText().trim();
												final int len = layer.width * layer.height * 4;
												
												//L.log("Layer " + layer.name + " with data " + data);
												ByteArrayInputStream bi = new ByteArrayInputStream(Base64.decode(data)); 
												GZIPInputStream zi = new GZIPInputStream(bi, len);
												byte[] buffer = new byte[len];
												zi.read(buffer, 0, len);
												zi.close();
												bi.close();
												int i = 0;
												for(int y = 0; y < layer.height; ++y) {
													for(int x = 0; x < layer.width; ++x, i += 4) {
														int gid = readIntLittleEndian(buffer, i);
														//if (gid != 0) L.log(getHexString(buffer, i) + " -> " + gid);
														layer.gids[x][y] = gid;
														//L.log("(" + x + "," + y + ") : " + layer.gids[x][y]);
													}
												}
											}
										}
									});
								} else if (tagName.equals("objectgroup")) {
									final TMXObjectGroup group = new TMXObjectGroup();
									currentMap.objectGroups.add(group);
									group.name = xrp.getAttributeValue(null, "name");
									group.width = xrp.getAttributeIntValue(null, "width", 1);
									group.height = xrp.getAttributeIntValue(null, "height", 1);
									readCurrentTagUntilEnd(xrp, new TagHandler() {
										public void handleTag(XmlResourceParser xrp, String tagName) throws XmlPullParserException, IOException {
											if (tagName.equals("object")) {
												final TMXObject object = new TMXObject();
												group.objects.add(object);
												object.name = xrp.getAttributeValue(null, "name");
												object.type = xrp.getAttributeValue(null, "type");
												object.x = xrp.getAttributeIntValue(null, "x", -1);
												object.y = xrp.getAttributeIntValue(null, "y", -1);
												object.width = xrp.getAttributeIntValue(null, "width", -1);
												object.height = xrp.getAttributeIntValue(null, "height", -1);
												readCurrentTagUntilEnd(xrp, new TagHandler() {
													public void handleTag(XmlResourceParser xrp, String tagName) {
														if (tagName.equals("property")) {
															final TMXProperty property = new TMXProperty();
															object.properties.add(property);
															property.name = xrp.getAttributeValue(null, "name");
															property.value = xrp.getAttributeValue(null, "value");
														}
													}
												});
											}
										}
									});
								}
							}
						});
					} 
				}
            }
            xrp.close();
		} catch (XmlPullParserException e) {
			L.log(e.toString());
		} catch (IOException e) {
			L.log(e.toString());
		}
		maps.add(currentMap);
		return currentMap;
	}
	
	private interface TagHandler {
		void handleTag(XmlResourceParser xrp, String tagName) throws XmlPullParserException, IOException;
	}
	private static void readCurrentTagUntilEnd(XmlResourceParser xrp, TagHandler handler) throws XmlPullParserException, IOException {
		String outerTagName = xrp.getName();
		String tagName;
		int eventType;
		while ((eventType = xrp.next()) != XmlResourceParser.END_DOCUMENT) {
			if (eventType == XmlResourceParser.START_TAG) {
				tagName = xrp.getName();
				handler.handleTag(xrp, tagName);
			} else if (eventType == XmlResourceParser.END_TAG) {
				tagName = xrp.getName();
				if (tagName.equals(outerTagName)) return;
			}
		}
	}
	/*
	private static String getHexString(byte v) {
		String result = Integer.toHexString(v & 0xff);
		if (result.length() < 2) result = '0' + result;
		return result;
	}
	private static String getHexString(byte[] buffer, int offset) {
		return getHexString(buffer[offset]) 
			+ getHexString(buffer[offset+1]) 
			+ getHexString(buffer[offset+2]) 
			+ getHexString(buffer[offset+3]);
	}
	*/
	private static int readIntLittleEndian(byte[] buffer, int offset) {
		return  (buffer[offset + 0] << 0 ) & 0x000000ff |
				(buffer[offset + 1] << 8 ) & 0x0000ff00 |
				(buffer[offset + 2] << 16) & 0x00ff0000 |
				(buffer[offset + 3] << 24) & 0xff000000;
	}
	
	public ArrayList<LayeredWorldMap> transformMaps(DynamicTileLoader tileLoader, MonsterTypeCollection monsterTypes) {
		return transformMaps(maps, tileLoader, monsterTypes);
	}
	public ArrayList<LayeredWorldMap> transformMaps(Collection<TMXMap> maps, DynamicTileLoader tileLoader, MonsterTypeCollection monsterTypes) {
		ArrayList<LayeredWorldMap> result = new ArrayList<LayeredWorldMap>();
		
		for (TMXMap m : maps) {
			assert(m.name != null);
			assert(m.name.length() > 0);
			assert(m.width > 0);
			assert(m.height > 0);
			
			boolean[][] isWalkable = new boolean[m.width][m.height];
			for (int y = 0; y < m.height; ++y) {
				for (int x = 0; x < m.width; ++x) {
					isWalkable[x][y] = true;
				}
			}
			final Size mapSize = new Size(m.width, m.height);
			MapLayer[] layers = new MapLayer[] {
				new MapLayer(mapSize)
				,new MapLayer(mapSize)
				,new MapLayer(mapSize)
			};
			for (TMXLayer layer : m.layers) {
				int ixMapLayer = -2;
				String layerName = layer.name;
				assert(layerName != null);
				assert(layerName.length() > 0);
				layerName = layerName.toLowerCase();
				if (layerName.startsWith("object")) {
					ixMapLayer = LayeredWorldMap.LAYER_OBJECTS;
				} else if (layerName.startsWith("ground")) {
					ixMapLayer = LayeredWorldMap.LAYER_GROUND;
				} else if (layerName.startsWith("above")) {
					ixMapLayer = LayeredWorldMap.LAYER_ABOVE;
				} else if (layerName.startsWith("walk")) {
					ixMapLayer = -1;
				} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
					L.log("OPTIMIZE: cannot handle layer " + layerName + " from map " + m.name);
					continue;
				}
				
				for (int y = 0; y < layer.height; ++y) {
					for (int x = 0; x < layer.width; ++x) {
						int gid = layer.gids[x][y];
						if (gid <= 0) continue;
						
						if (ixMapLayer == -1) {
							isWalkable[x][y] = false;
						} else {
							Pair<String, Integer> p = getTile(m, gid);
							if (p == null) continue;
							
							String tilesetName = (String) p.first;
							int localId = (Integer) p.second;
							layers[ixMapLayer].tiles[x][y] = tileLoader.getTileID(tilesetName, localId);
						}
					}
				}
			}
			
			ArrayList<MapObject> mapObjects = new ArrayList<MapObject>();
			ArrayList<MonsterSpawnArea> spawnAreas = new ArrayList<MonsterSpawnArea>();
			ArrayList<KeyArea> keyAreas = new ArrayList<KeyArea>();
			
			for (TMXObjectGroup group : m.objectGroups) {
				for (TMXObject object : group.objects) {
					final Coord topLeft = new Coord(
						Math.round(((float)object.x) / m.tilewidth)
						,Math.round(((float)object.y) / m.tileheight)
					);
					final int width = Math.round(((float)object.width) / m.tilewidth);
					final int height = Math.round(((float)object.height) / m.tileheight);
					final CoordRect position = new CoordRect(topLeft, new Size(width, height));
						
					if (object.type.equalsIgnoreCase("sign")) {
						String title = object.name; 
						String text = null;
						String enableKey = null;
						for (TMXProperty p : object.properties) {
							if(p.name.equalsIgnoreCase("text")) text = p.value;
							else if(p.name.equalsIgnoreCase("title")) title = p.value;
							else if(p.name.equalsIgnoreCase("enablekey")) enableKey = p.value;
							else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) L.log("OPTIMIZE: Map " + m.name + ", sign " + object.name + "  has unrecognized property \"" + p.name + "\".");
						}
						mapObjects.add(MapObject.createMapSignEvent(position, title, text, enableKey));
					} else if (object.type.equalsIgnoreCase("mapchange")) {
						String map = null;
						String place = null;
						for (TMXProperty p : object.properties) {
							if(p.name.equalsIgnoreCase("map")) map = p.value;
							else if(p.name.equalsIgnoreCase("place")) place = p.value;
							else if(AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) L.log("OPTIMIZE: Map " + m.name + ", mapchange " + object.name + "  has unrecognized property \"" + p.name + "\".");
						}
						mapObjects.add(MapObject.createNewMapEvent(position, object.name, map, place));
					} else if (object.type.equalsIgnoreCase("spawn")) {
						ArrayList<MonsterType> types = new ArrayList<MonsterType>();
						types.addAll(monsterTypes.getMonsterTypesFromTags(object.name));
						int maxQuantity = 1;
						int spawnChance = 10;
						boolean isUnique = false;
						for (TMXProperty p : object.properties) {
							if (p.name.equalsIgnoreCase("type")) {
								types.addAll(monsterTypes.getMonsterTypesFromTags(p.value));
							} else if (p.name.equalsIgnoreCase("quantity")) {
								maxQuantity = Integer.parseInt(p.value);
							} else if (p.name.equalsIgnoreCase("spawnchance")) {
								spawnChance = Integer.parseInt(p.value);
							} else if (p.name.equalsIgnoreCase("respawn")) {
								isUnique = !Boolean.parseBoolean(p.value);
							} else if (p.name.equalsIgnoreCase("unique")) {
								isUnique = Boolean.parseBoolean(p.value);
							} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
								L.log("OPTIMIZE: Map " + m.name + ", spawn " + object.name + "  has unrecognized property \"" + p.name + "\".");
							}
						}
						
						if (types.isEmpty()) {
							if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
								L.log("OPTIMIZE: Map " + m.name + " contains spawn \"" + object.name + "\" that does not correspond to any monsters. The spawn will be removed.");
							}
							continue;
						}
						
						int[] monsterTypeIDs = new int[types.size()];
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
						String requiredKey = object.name;
						String message = "";
						for (TMXProperty p : object.properties) {
							if (p.name.equalsIgnoreCase("message")) {
								message = p.value;
							} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
								L.log("OPTIMIZE: Map " + m.name + ", key " + object.name + "  has unrecognized property \"" + p.name + "\".");
							}
						}
						
						keyAreas.add(new KeyArea(position, requiredKey, message));
					} else if (object.type.equals("rest")) {
						mapObjects.add(MapObject.createNewRest(position));
					} else if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
						L.log("OPTIMIZE: Map " + m.name + ", has unrecognized object type \"" + object.type + "\" for name \"" + object.name + "\".");
					}
				}
			}
			MapObject[] _eventObjects = new MapObject[mapObjects.size()];
			_eventObjects = mapObjects.toArray(_eventObjects);
			MonsterSpawnArea[] _spawnAreas = new MonsterSpawnArea[spawnAreas.size()];
			_spawnAreas = spawnAreas.toArray(_spawnAreas);
			KeyArea[] _keyAreas = new KeyArea[keyAreas.size()];
			_keyAreas = keyAreas.toArray(_keyAreas);

			result.add(new LayeredWorldMap(m.name, mapSize, layers, isWalkable, _eventObjects, _keyAreas, _spawnAreas, false));
		}
		
		return result;
	}
	
	private static Pair<String, Integer> getTile(final TMXMap map, final int gid) {
		for(int i = map.tileSets.size() - 1; i >= 0; --i) {
			TMXTileSet ts = map.tileSets.get(i);
			if (ts.firstgid <= gid) {
				return new Pair<String, Integer>(ts.imageName, (gid - ts.firstgid));
			}
		}
		L.log("WARNING: Cannot find tile for gid " + gid); //(" + x + ", " + y + "), ")
		return null;
	}
	
	private static class Pair<T1, T2> {
		public final T1 first;
		public final T2 second;
		public Pair(T1 first, T2 second) {
			this.first = first;
			this.second = second;
		}
	}
	
	public class TMXMap {
		public String name;
		public String orientation;
		public int width;
		public int height;
		public int tilewidth;
		public int tileheight;
		public List<TMXTileSet> tileSets = new ArrayList<TMXTileSet>();
		public List<TMXLayer> layers = new ArrayList<TMXLayer>();
		public List<TMXObjectGroup> objectGroups = new ArrayList<TMXObjectGroup>();
	}
	public class TMXTileSet {
		public int firstgid;
		public String name;
		public int tilewidth;
		public int tileheight;
		public String imageSource;
		public String imageName;
	}
	public class TMXLayer {
		public String name;
		public int width;
		public int height;
		public int[][] gids;
	}
	public class TMXObjectGroup {
		public String name;
		public int width;
		public int height;
		public List<TMXObject> objects = new ArrayList<TMXObject>();
	}
	public class TMXObject {
		public String name;
		public String type;
		public int x;
		public int y;
		public int width;
		public int height;
		public List<TMXProperty> properties = new ArrayList<TMXProperty>();
	}
	public class TMXProperty {
		public String name;
		public String value;
	}
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
