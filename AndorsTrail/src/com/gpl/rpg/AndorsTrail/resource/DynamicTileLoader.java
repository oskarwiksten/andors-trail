package com.gpl.rpg.AndorsTrail.resource;

import java.util.ArrayList;
import java.util.HashMap;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Size;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.BitmapFactory.Options;

public final class DynamicTileLoader {
	private final TileStore store;
	private final Resources r;
	
	private final ArrayList<TilesetBitmap> preparedTilesets = new ArrayList<TilesetBitmap>();
	//private final HashMap<String, Integer> DEBUG_tilefrequency = new HashMap<String, Integer>();
	private int allocatedTiles = 0;
	private int currentTileStoreIndex;
	
	public DynamicTileLoader(TileStore store, Resources r) {
		this.store = store;
		this.r = r;
		initialize();
	}
	
	private void initialize() {
		allocatedTiles = 0;
		preparedTilesets.clear();
		currentTileStoreIndex = store.bitmaps.length;
		/*if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			DEBUG_tilefrequency.clear();
		}*/
	}
	
	public void prepareTileset(int resourceId, String tilesetName, Size numTiles, Size destinationTileSize) {
		preparedTilesets.add(new TilesetBitmap(resourceId, tilesetName, numTiles, destinationTileSize));
	}
	private TilesetBitmap getTilesetBitmap(int tilesetImageResourceID) {
		for (TilesetBitmap b : preparedTilesets) {
			if (b.resourceId == tilesetImageResourceID) {
				return b;
			}
		}
		return null;
	}
	
	public int getTileID(int tilesetImageResourceID, int localId) {
		TilesetBitmap b = getTilesetBitmap(tilesetImageResourceID);
		if (b == null) {
			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				L.log("WARNING: Cannot load tileset " + tilesetImageResourceID);
			}
			return currentTileStoreIndex-1;
		}
		return getTileID(b, localId);
	}

	public int getTileID(String tilesetName, int localId) {
		for (TilesetBitmap b : preparedTilesets) {
			if (b.tilesetName.equals(tilesetName)) {
				return getTileID(b, localId);
			}
		}
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Cannot load tileset " + tilesetName);
		}
		return currentTileStoreIndex-1;
	}
	public Size getTilesetSize(String tilesetName) {
		for (TilesetBitmap b : preparedTilesets) {
			if (b.tilesetName.equals(tilesetName)) {
				return b.destinationTileSize;
			}
		}
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Cannot load tileset " + tilesetName);
		}
		return new Size(1, 1);
	}
	
	private int getTileID(TilesetBitmap tileset, int localId) {
		int tileStoreIndex = 0;
		if (tileset.tilesToLoad.containsKey(localId)) {
			tileStoreIndex = tileset.tilesToLoad.get(localId);
		} else {
			tileStoreIndex = currentTileStoreIndex;
			++currentTileStoreIndex;
			++allocatedTiles;
			tileset.tilesToLoad.put(localId, tileStoreIndex);
		}
		/*if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			final int x = localId % tileset.numTiles.width;
			final int y = (localId - x) / tileset.numTiles.width;
			final String s = tileset.tilesetName + "(" + x + "," + y + ")";
			int n = 0;
			if (DEBUG_tilefrequency.containsKey(s)) n = DEBUG_tilefrequency.get(s);
			++n;
			DEBUG_tilefrequency.put(s, n);
		}*/
		return tileStoreIndex;
	}
	
	public void flush() {
		store.allocateTiles(allocatedTiles);
		
		for (TilesetBitmap b : preparedTilesets) {
			if (b.tilesToLoad.isEmpty()) {
				if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
					L.log("OPTIMIZE: Tileset " + b.tilesetName + " does not contain any loaded tiles. The file could be removed from the project.");
				}
				continue;
			}
			
			boolean recycle = true;
			Bitmap tilesetImage = createTilesetImage(b);
			for (int localId : b.tilesToLoad.keySet()) {
				int tileStoreIndex = b.tilesToLoad.get(localId);
				store.bitmaps[tileStoreIndex] = createTileFromTileset(tilesetImage, b, localId);
				if (store.bitmaps[tileStoreIndex] == tilesetImage) recycle = false;
			}
			if (recycle) tilesetImage.recycle();
		}
		
		/*if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			ArrayList<Entry<String, Integer>> l = new ArrayList<Entry<String,Integer>>(DEBUG_tilefrequency.entrySet());
			Collections.sort(l, new Comparator<Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> a, Entry<String, Integer> b) {
					return b.getValue() - a.getValue();
				}
			});
			for (Entry<String, Integer> e : l) {
				L.log("INFO: " + e.getValue() + " times requested " + e.getKey());
			}
		}*/
		
		initialize();
	}
	
	private Bitmap createTilesetImage(TilesetBitmap b) {
		Options o = new Options();
		o.inScaled = false;
		Bitmap sourceImage = BitmapFactory.decodeResource(r, b.resourceId, o);
		sourceImage.setDensity(Bitmap.DENSITY_NONE);
		b.calculateFromSourceImageSize(sourceImage.getWidth(), sourceImage.getHeight());
		return sourceImage;
	}

	private Bitmap createTileFromTileset(Bitmap tilesetImage, TilesetBitmap tileset, int localId) {
		final int x = localId % tileset.numTiles.width;
		final int y = (localId - x) / tileset.numTiles.width;
		final int left = x * tileset.sourceTileSize.width;
		final int top = y * tileset.sourceTileSize.height;
		if (tileset.scale != null) {
			return Bitmap.createBitmap(tilesetImage, left, top, tileset.sourceTileSize.width, tileset.sourceTileSize.height, tileset.scale, true);
		} else {
			return Bitmap.createBitmap(tilesetImage, left, top, tileset.sourceTileSize.width, tileset.sourceTileSize.height);
		}
	}
	
	public static int measureBitmapWidth(Resources r, int resourceId) {
		Bitmap b = BitmapFactory.decodeResource(r, resourceId);
		int width = b.getWidth();
		b.recycle();
		return width;
	}

	private static class TilesetBitmap {
		public final int resourceId;
		public final String tilesetName;
		public final Size destinationTileSize;
		public final Size numTiles;
		public Size sourceTileSize;
		public Matrix scale;
		
		public HashMap<Integer, Integer> tilesToLoad = new HashMap<Integer, Integer>();
		
		public TilesetBitmap(int resourceId, String tilesetName, Size numTiles, Size destinationTileSize) {
			this.resourceId = resourceId;
			this.tilesetName = tilesetName;
			this.destinationTileSize = destinationTileSize;
			this.numTiles = numTiles;
		}
		
		public void calculateFromSourceImageSize(final int sourceWidth, final int sourceHeight) {
			sourceTileSize = new Size(
	        		sourceWidth / numTiles.width
	        		,sourceHeight / numTiles.height
	    		);
			
			if (destinationTileSize.width == sourceTileSize.width && destinationTileSize.height == sourceTileSize.height) {
				scale = null;
			} else {
		        scale = new Matrix();
		        scale.postScale(
		        		((float) destinationTileSize.width) / sourceTileSize.width
		    			,((float) destinationTileSize.height) / sourceTileSize.height
					);
	        
		        if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			        L.log("OPTIMIZE: Tileset " + tilesetName + " will be resized from " + sourceTileSize.toString() + " to " + destinationTileSize.toString());
		        }
			}
		}
	}
}
