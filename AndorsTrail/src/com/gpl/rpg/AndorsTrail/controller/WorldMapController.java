package com.gpl.rpg.AndorsTrail.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.activity.DisplayWorldMapActivity;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail.model.map.MapLayer;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.model.map.WorldMapSegment;
import com.gpl.rpg.AndorsTrail.model.map.WorldMapSegment.WorldMapSegmentMap;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCollection;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.L;

public final class WorldMapController {

	public static int WORLDMAP_SCREENSHOT_TILESIZE = 8;
    public static int WORLDMAP_DISPLAY_TILESIZE = WORLDMAP_SCREENSHOT_TILESIZE;
    
	public static void updateWorldMap(final WorldContext world, final PredefinedMap map, final LayeredTileMap mapTiles, final TileCollection cachedTiles, final Resources res) {
		
		final String worldMapSegmentName = world.maps.getWorldMapSegmentNameForMap(map.name);
		if (worldMapSegmentName == null) return;
		
		if (!shouldUpdateWorldMap(map, worldMapSegmentName)) return;
		
		(new AsyncTask<Void, Void, Void>()  {
			@Override
			protected Void doInBackground(Void... arg0) {
				final MapRenderer renderer = new MapRenderer(world, map, mapTiles, cachedTiles);
				try {
					updateCachedBitmap(map, renderer);
					updateWorldMapSegment(res, world, worldMapSegmentName);
				} catch (IOException e) {
					L.log("Error creating worldmap file for map " + map.name + " : " + e.toString());
				}
		    	return null;
			}
		}).execute();
	}
	
	private static boolean shouldUpdateWorldMap(PredefinedMap map, String worldMapSegmentName) {
		if (!map.visited) return true;
		File file = getFileForMap(map);
		if (!file.exists()) return true;
		
		if (map.lastVisitVersion < AndorsTrailApplication.CURRENT_VERSION) return true;
		
		file = getCombinedWorldMapFile(worldMapSegmentName);
		if (!file.exists()) return true;
		
		return false;
	}

	private static void updateCachedBitmap(PredefinedMap map, MapRenderer renderer) throws IOException {

		File file = getFileForMap(map);
    	if (file.exists()) {
    		if (map.lastVisitVersion == AndorsTrailApplication.CURRENT_VERSION) return;
    	}
		
		Bitmap image = renderer.drawMap();
		ensureWorldmapDirectoryExists();
    	FileOutputStream fos = new FileOutputStream(file);
    	image.compress(Bitmap.CompressFormat.PNG, 70, fos);
        fos.flush();
        fos.close();
        image.recycle();
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
    public static File getFileForMap(PredefinedMap map) { return getFileForMap(map.name); }
    public static File getFileForMap(String mapName) {
    	return new File(getWorldmapDirectory(), mapName + ".png");
    }
    public static File getWorldmapDirectory() {
    	File dir = Environment.getExternalStorageDirectory();
    	dir = new File(dir, Constants.FILENAME_SAVEGAME_DIRECTORY);
    	return new File(dir, Constants.FILENAME_WORLDMAP_DIRECTORY);
    }
    public static File getCombinedWorldMapFile(String segmentName) {
    	return new File(getWorldmapDirectory(), Constants.FILENAME_WORLDMAP_HTMLFILE_PREFIX + segmentName + Constants.FILENAME_WORLDMAP_HTMLFILE_SUFFIX);
    }
	
	private static String getWorldMapSegmentAsHtml(Resources res, WorldContext world, String segmentName) throws IOException {
		WorldMapSegment segment = world.maps.worldMapSegments.get(segmentName);
		
		StringBuffer sb = new StringBuffer();
		
		Coord offsetWorldmapTo = new Coord(999999, 999999);
		for (WorldMapSegmentMap map : segment.maps.values()) {
			File f = WorldMapController.getFileForMap(map.mapName);
			if (!f.exists()) continue;
			
			offsetWorldmapTo.x = Math.min(offsetWorldmapTo.x, map.worldPosition.x);
			offsetWorldmapTo.y = Math.min(offsetWorldmapTo.y, map.worldPosition.y);
		}
		
		for (WorldMapSegmentMap segmentMap : segment.maps.values()) {
			File f = WorldMapController.getFileForMap(segmentMap.mapName);
			if (!f.exists()) continue;
			
			PredefinedMap map = world.maps.findPredefinedMap(segmentMap.mapName);
			
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
				.append((segmentMap.worldPosition.x - offsetWorldmapTo.x) * WorldMapController.WORLDMAP_DISPLAY_TILESIZE)
				.append("px; top: ")
				.append((segmentMap.worldPosition.y - offsetWorldmapTo.y) * WorldMapController.WORLDMAP_DISPLAY_TILESIZE)
				.append("px;\" />");
		}
		
		return res.getString(R.string.worldmap_template)
				.replace("{{maps}}", sb.toString())
				.replace("{{offsetx}}", Integer.toString(offsetWorldmapTo.x * WorldMapController.WORLDMAP_DISPLAY_TILESIZE))
				.replace("{{offsety}}", Integer.toString(offsetWorldmapTo.y * WorldMapController.WORLDMAP_DISPLAY_TILESIZE));
	}
	
	private static void updateWorldMapSegment(Resources res, WorldContext world, String segmentName) throws IOException {
		String mapAsHtml = getWorldMapSegmentAsHtml(res, world, segmentName);
		File outputFile = getCombinedWorldMapFile(segmentName);
		PrintWriter pw = new PrintWriter(outputFile);
		pw.write(mapAsHtml);
		pw.close();
	}

	public static boolean displayWorldMap(Context context, WorldContext world) {
		String worldMapSegmentName = world.maps.getWorldMapSegmentNameForMap(world.model.currentMap.name);
		if (worldMapSegmentName == null) {
			Toast.makeText(context, context.getResources().getString(R.string.display_worldmap_not_available), Toast.LENGTH_LONG).show();
			return false;
		}
		
		Intent intent = new Intent(context, DisplayWorldMapActivity.class);
		intent.putExtra("worldMapSegmentName", worldMapSegmentName);
		context.startActivity(intent);

		return true;
	}
}
