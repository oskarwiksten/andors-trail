package com.gpl.rpg.AndorsTrail.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail.model.map.MapLayer;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCollection;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.L;

public final class WorldMapController {

	public static int WORLDMAP_SCREENSHOT_TILESIZE = 12;
    public static int WORLDMAP_DISPLAY_TILESIZE = WORLDMAP_SCREENSHOT_TILESIZE;
    
	public static void updateWorldMap(final WorldContext world, final PredefinedMap map, final LayeredTileMap mapTiles, final TileCollection cachedTiles, final Resources res) {
		
		if (!shouldUpdateWorldMap(map)) return;
		
		(new AsyncTask<Void, Void, Void>()  {
			@Override
			protected Void doInBackground(Void... arg0) {
				final MapRenderer renderer = new MapRenderer(world, map, mapTiles, cachedTiles);
				updateCachedBitmap(map, renderer);
				try {
					updateCombinedWorldMap(res, world);
				} catch (IOException e) {}
		    	return null;
			}
		}).execute();
	}
	
	private static boolean isVisibleOnWorldMap(PredefinedMap map) {
		return map.isOutdoors;
	}

	private static boolean shouldUpdateWorldMap(PredefinedMap map) {
		if (!isVisibleOnWorldMap(map)) return false;
    	if (!map.visited) return true;
		File file = getFileForMap(map);
		if (!file.exists()) return true;
		
		if (map.lastVisitVersion < AndorsTrailApplication.CURRENT_VERSION) return true;
		
		file = getCombinedWorldMapFile();
		if (!file.exists()) return true;
		
		return false;
	}

	private static void updateCachedBitmap(PredefinedMap map, MapRenderer renderer) {

		File file = getFileForMap(map);
    	if (file.exists()) return;
		
    	try {
    		Bitmap image = renderer.drawMap();
    		ensureWorldmapDirectoryExists();
        	FileOutputStream fos = new FileOutputStream(file);
        	image.compress(Bitmap.CompressFormat.PNG, 70, fos);
            fos.flush();
            fos.close();
            image.recycle();
    	} catch (IOException e) {
    		L.log("Error creating worldmap file for map " + map.name + " : " + e.toString());
    	}
	}

	private static final class MapRenderer {
		private final PredefinedMap map;
		private final LayeredTileMap mapTiles;
		private final TileCollection cachedTiles;
		private final int tileSize;
		private final float scale;
		private final Paint mPaint = new Paint();
		
		public MapRenderer(final WorldContext world, final PredefinedMap map, final LayeredTileMap mapTiles, final TileCollection cachedTiles) {
			this.map = map;
			this.mapTiles = mapTiles;
			this.cachedTiles = cachedTiles;
			this.tileSize = world.tileManager.tileSize;
			this.scale = (float) WORLDMAP_SCREENSHOT_TILESIZE / world.tileManager.tileSize;
		}
		
		public Bitmap drawMap() {
			Bitmap image = Bitmap.createBitmap(map.size.width * WORLDMAP_SCREENSHOT_TILESIZE, map.size.height * WORLDMAP_SCREENSHOT_TILESIZE, Config.RGB_565);
			image.setDensity(Bitmap.DENSITY_NONE);
			Canvas canvas = new Canvas(image);
			canvas.scale(scale, scale);

			drawMapLayer(canvas, mapTiles.layers[LayeredTileMap.LAYER_GROUND]);
	        tryDrawMapLayer(canvas, LayeredTileMap.LAYER_OBJECTS);
	        tryDrawMapLayer(canvas, LayeredTileMap.LAYER_ABOVE);
	        
	        return image;
		}
		
		private void tryDrawMapLayer(Canvas canvas, final int layerIndex) {
	    	if (mapTiles.layers.length > layerIndex) drawMapLayer(canvas, mapTiles.layers[layerIndex]);        
	    }
	    
	    private void drawMapLayer(Canvas canvas, final MapLayer layer) {
	    	int py = 0;
			for (int y = 0; y < map.size.height; ++y, py += tileSize) {
	        	int px = 0;
	        	for (int x = 0; x < map.size.width; ++x, px += tileSize) {
	        		final int tile = layer.tiles[x][y];
	        		if (tile != 0) {
	        			canvas.drawBitmap(cachedTiles.bitmaps[tile], px, py, mPaint);
	        		}
	            }
	        }
	    }
	}
	
    private static void ensureWorldmapDirectoryExists() {
    	File root = Environment.getExternalStorageDirectory();
		File dir = new File(root, Constants.FILENAME_SAVEGAME_DIRECTORY);
		if (!dir.exists()) dir.mkdir();
		dir = new File(dir, Constants.FILENAME_WORLDMAP_DIRECTORY);
		if (!dir.exists()) dir.mkdir();
    }
    public static File getFileForMap(PredefinedMap map) {
    	File root = getWorldmapDirectory();
		return new File(root, map.name + ".png");
    }
    public static File getWorldmapDirectory() {
    	File dir = Environment.getExternalStorageDirectory();
    	dir = new File(dir, Constants.FILENAME_SAVEGAME_DIRECTORY);
    	return new File(dir, Constants.FILENAME_WORLDMAP_DIRECTORY);
    }
    public static File getCombinedWorldMapFile() {
    	return new File(getWorldmapDirectory(), Constants.FILENAME_WORLDMAP_HTMLFILE);
    }
	
	private static String getCombinedWorldMapAsHtml(Resources res, WorldContext world) throws IOException {
		StringBuffer sb = new StringBuffer();
		
		Coord offsetWorldmapTo = new Coord(999999, 999999);
		for (PredefinedMap map : world.maps.predefinedMaps) {
			File f = WorldMapController.getFileForMap(map);
			if (!f.exists()) continue;
			
			offsetWorldmapTo.x = Math.min(offsetWorldmapTo.x, map.worldMapPosition.x);
			offsetWorldmapTo.y = Math.min(offsetWorldmapTo.y, map.worldMapPosition.y);
		}
		
		for (PredefinedMap map : world.maps.predefinedMaps) {
			File f = WorldMapController.getFileForMap(map);
			if (!f.exists()) continue;
			
			sb
				.append("<img src=\"")
				.append(f.getName())
				.append("\" id=\"")
				.append(map.name)
				.append("\" style=\"width: ")
				.append(map.size.width * WorldMapController.WORLDMAP_DISPLAY_TILESIZE)
				.append("px; height: ")
				.append(map.size.height * WorldMapController.WORLDMAP_DISPLAY_TILESIZE)
				.append("px; position: absolute; left: ")
				.append((map.worldMapPosition.x - offsetWorldmapTo.x) * WorldMapController.WORLDMAP_DISPLAY_TILESIZE)
				.append("px; top: ")
				.append((map.worldMapPosition.y - offsetWorldmapTo.y) * WorldMapController.WORLDMAP_DISPLAY_TILESIZE)
				.append("px;\" />");
		}
		
		return res.getString(R.string.worldmap_template)
				.replace("{{maps}}", sb.toString())
				.replace("{{offsetx}}", Integer.toString(offsetWorldmapTo.x * WorldMapController.WORLDMAP_DISPLAY_TILESIZE))
				.replace("{{offsety}}", Integer.toString(offsetWorldmapTo.y * WorldMapController.WORLDMAP_DISPLAY_TILESIZE));
	}
	
	private static void updateCombinedWorldMap(Resources res, WorldContext world) throws IOException {
		String mapAsHtml = getCombinedWorldMapAsHtml(res, world);
		File outputFile = new File(WorldMapController.getWorldmapDirectory(), Constants.FILENAME_WORLDMAP_HTMLFILE);
		PrintWriter pw = new PrintWriter(outputFile);
		pw.write(mapAsHtml);
		pw.close();
	}

	public static Coord getPlayerWorldPosition(WorldContext world) {
		PredefinedMap map = world.model.currentMap;
		if (!isVisibleOnWorldMap(map)) return null;
		
		return new Coord(
				world.model.player.position.x + map.worldMapPosition.x,
				world.model.player.position.y + map.worldMapPosition.y
			);
	}
}
