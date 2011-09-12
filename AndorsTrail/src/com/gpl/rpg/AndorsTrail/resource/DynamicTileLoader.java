package com.gpl.rpg.AndorsTrail.resource;

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
	
	private final HashMap<Integer, TilesetBitmap> preparedTilesetsByResourceId = new HashMap<Integer, TilesetBitmap>();
	private final HashMap<String, TilesetBitmap> preparedTilesetsByResourceName = new HashMap<String, TilesetBitmap>();
	private int allocatedTiles = 0;
	private int currentTileStoreIndex;
	
	public DynamicTileLoader(TileStore store, Resources r) {
		this.store = store;
		this.r = r;
		initialize();
	}
	
	private void initialize() {
		allocatedTiles = 0;
		preparedTilesetsByResourceId.clear();
		preparedTilesetsByResourceName.clear();
		currentTileStoreIndex = store.bitmaps.length;
	}
	
	public void prepareTileset(int resourceId, String tilesetName, Size numTiles, Size destinationTileSize) {
		TilesetBitmap b = new TilesetBitmap(resourceId, tilesetName, numTiles, destinationTileSize);
		preparedTilesetsByResourceId.put(resourceId, b);
		preparedTilesetsByResourceName.put(tilesetName, b);
	}
	private TilesetBitmap getTilesetBitmap(int tilesetImageResourceID) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!preparedTilesetsByResourceId.containsKey(tilesetImageResourceID)) {
				L.log("WARNING: Cannot load tileset " + tilesetImageResourceID);
				return null;
			}
		}
		return preparedTilesetsByResourceId.get(tilesetImageResourceID);
	}
	private TilesetBitmap getTilesetBitmap(String tilesetName) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!preparedTilesetsByResourceName.containsKey(tilesetName)) {
				L.log("WARNING: Cannot load tileset " + tilesetName);
				return null;
			}
		}
		return preparedTilesetsByResourceName.get(tilesetName);
	}
	
	public int prepareTileID(int tilesetImageResourceID, int localId) {
		TilesetBitmap b = getTilesetBitmap(tilesetImageResourceID);
		return prepareTileID(b, localId);
	}

	public int prepareTileID(String tilesetName, int localId) {
		TilesetBitmap b = getTilesetBitmap(tilesetName);
		return prepareTileID(b, localId);
	}
	public Size getTilesetSize(String tilesetName) {
		TilesetBitmap b = getTilesetBitmap(tilesetName);
		return b.destinationTileSize;
	}
	
	private int prepareTileID(TilesetBitmap tileset, int localId) {
		int tileStoreIndex = 0;
		if (tileset.tilesToLoad.containsKey(localId)) {
			tileStoreIndex = tileset.tilesToLoad.get(localId);
		} else {
			tileStoreIndex = currentTileStoreIndex;
			++currentTileStoreIndex;
			++allocatedTiles;
			tileset.tilesToLoad.put(localId, tileStoreIndex);
		}
		return tileStoreIndex;
	}
	
	public void flush() {
		store.allocateTiles(allocatedTiles);
		
		for (TilesetBitmap b : preparedTilesetsByResourceId.values()) {
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
				Bitmap tile = createTileFromTileset(tilesetImage, b, localId);
				if (tile == tilesetImage) recycle = false;
				
				store.setBitmap(tileStoreIndex, tile, b.tilesetName, localId);
			}
			if (recycle) tilesetImage.recycle();
		}
		
		initialize();
	}
	
	private Bitmap createTilesetImage(TilesetBitmap b) {
		//return BitmapFactory.decodeResource(r, b.resourceId);
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
