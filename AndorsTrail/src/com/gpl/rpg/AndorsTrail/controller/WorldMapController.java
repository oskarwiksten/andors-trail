package com.gpl.rpg.AndorsTrail.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail.model.map.MapLayer;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCollection;
import com.gpl.rpg.AndorsTrail.util.L;

public final class WorldMapController {
	public static void updateWorldMap(final WorldContext world, final PredefinedMap map, final LayeredTileMap mapTiles, final TileCollection cachedTiles) {
		(new AsyncTask<Void, Void, Void>()  {
			@Override
			protected Void doInBackground(Void... arg0) {
				
				File file = getFileForMap(map);
		    	if (file.exists()) return null;
				
		    	try {
		    		Bitmap image = new MapRenderer(world, map, mapTiles, cachedTiles).drawMap();
		    		ensureWorldmapDirectoryExists();
		        	FileOutputStream fos = new FileOutputStream(file);
		        	image.compress(Bitmap.CompressFormat.PNG, 70, fos);
                    fos.flush();
                    fos.close();
                    image.recycle();
		    	} catch (IOException e) {
		    		L.log("Error creating worldmap file for map " + map.name + " : " + e.toString());
		    	}
		    	return null;
			}
		}).execute();
	}

	private static final class MapRenderer {
		private final PredefinedMap map;
		private final LayeredTileMap mapTiles;
		private final TileCollection cachedTiles;
		private final int tileSize;
		private static final int outputTileSize = 16;
		private final float scale;
		private final Paint mPaint = new Paint();
		
		public MapRenderer(final WorldContext world, final PredefinedMap map, final LayeredTileMap mapTiles, final TileCollection cachedTiles) {
			this.map = map;
			this.mapTiles = mapTiles;
			this.cachedTiles = cachedTiles;
			this.tileSize = world.tileManager.tileSize;
			this.scale = (float) outputTileSize / world.tileManager.tileSize;
			L.log("outputTileSize=" + outputTileSize + ", tileSize=" + tileSize + ", scale=" + scale);
		}
		
		public Bitmap drawMap() {
			Bitmap image = Bitmap.createBitmap(map.size.width * outputTileSize, map.size.height * outputTileSize, Config.RGB_565);
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
    private static File getFileForMap(PredefinedMap map) {
    	File root = getWorldmapDirectory();
		return new File(root, map.name + ".png");
    }
    private static File getWorldmapDirectory() {
    	File dir = Environment.getExternalStorageDirectory();
    	dir = new File(dir, Constants.FILENAME_SAVEGAME_DIRECTORY);
    	return new File(dir, Constants.FILENAME_WORLDMAP_DIRECTORY);
    }
}
