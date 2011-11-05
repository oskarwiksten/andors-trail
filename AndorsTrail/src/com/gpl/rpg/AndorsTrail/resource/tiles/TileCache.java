package com.gpl.rpg.AndorsTrail.resource.tiles;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.util.L;

import android.content.res.Resources;
import android.graphics.Bitmap;

public final class TileCache {

	private final ReferenceQueue<Bitmap> gcQueue = new ReferenceQueue<Bitmap>(); 
	private ResourceFileTile[] resourceTiles = new ResourceFileTile[1];
	private HashMap<String, HashMap<Integer, Integer>> tileIDsPerTilesetAndLocalID = new HashMap<String, HashMap<Integer,Integer>>();
	
	public int getMaxTileID() { return resourceTiles.length-1; }
	public void allocateMaxTileID(int maxTileID) {
        if (maxTileID <= 0) return;
        
        ResourceFileTile[] oldArray = resourceTiles;
        resourceTiles = new ResourceFileTile[maxTileID+1];
        System.arraycopy(oldArray, 0, resourceTiles, 0, oldArray.length);
	}
	public void setTile(int tileID, ResourceFileTileset tileset, int localID) {
		if (resourceTiles[tileID] == null) resourceTiles[tileID] = new ResourceFileTile(tileset, localID);
		HashMap<Integer, Integer> tileIDsPerLocalID = tileIDsPerTilesetAndLocalID.get(tileset.tilesetName);
		if (tileIDsPerLocalID == null) {
			tileIDsPerLocalID = new HashMap<Integer, Integer>();
			tileIDsPerTilesetAndLocalID.put(tileset.tilesetName, tileIDsPerLocalID);
		}
		tileIDsPerLocalID.put(localID, tileID);
	}
	public int getTileID(String tileSetName, int localID) {
		return tileIDsPerTilesetAndLocalID.get(tileSetName).get(localID);
	}
	
	private static final class ResourceFileTile {
		public final ResourceFileTileset tileset;
		public final int localID;
		public SoftReference<Bitmap> bitmap;
		public ResourceFileTile(ResourceFileTileset tileset, int localID) {
			this.tileset = tileset;
			this.localID = localID;
		}
	}
	
	private void cleanQueue() {
		System.gc();
		Reference<? extends Bitmap> ref;
		while ((ref = gcQueue.poll()) != null) {
			Bitmap b = ref.get();
			if (b != null) b.recycle();
		}
	}
	
	public TileCollection loadTilesFor(Collection<Integer> iconIDs, Resources r) { return loadTilesFor(iconIDs, r, null); }
	public TileCollection loadTilesFor(Collection<Integer> iconIDs, Resources r, TileCollection result) {
		if (AndorsTrailApplication.DEVELOPMENT_DEBUGMESSAGES) L.log("TileCache::loadTilesFor({" + iconIDs.size() + " items})");
		int maxTileID = 0;
		HashMap<ResourceFileTileset, HashMap<Integer, ResourceFileTile>> tilesToLoadPerSourceFile = new HashMap<ResourceFileTileset, HashMap<Integer, ResourceFileTile>>();
		for(int tileID : iconIDs) {
			ResourceFileTile tile = resourceTiles[tileID];
			HashMap<Integer, ResourceFileTile> tiles = tilesToLoadPerSourceFile.get(tile.tileset);
			if (tiles == null) {
				tiles = new HashMap<Integer, TileCache.ResourceFileTile>();
				tilesToLoadPerSourceFile.put(tile.tileset, tiles);
			}
			tiles.put(tileID, tile);
			maxTileID = Math.max(maxTileID, tileID);
		}
		
		boolean hasLoadedTiles = false;
		if (result == null) result = new TileCollection(maxTileID);
		for(Entry<ResourceFileTileset, HashMap<Integer, ResourceFileTile>> e : tilesToLoadPerSourceFile.entrySet()) {
			TileCutter cutter = null;
			
			for(Entry<Integer, ResourceFileTile> j : e.getValue().entrySet()) {
				int tileID = j.getKey();
				ResourceFileTile tile = j.getValue();
				
				Bitmap bitmap = null;
				if (tile.bitmap != null) bitmap = tile.bitmap.get();
				
				if (bitmap == null) {
					if (cutter == null) {
						if (AndorsTrailApplication.DEVELOPMENT_DEBUGMESSAGES) {
							L.log("Loading tiles from tileset " + e.getKey().tilesetName);
						}
						if (!hasLoadedTiles) cleanQueue();
						cutter = new TileCutter(e.getKey(), r);
						hasLoadedTiles = true;
					}
					
					bitmap = cutter.createTile(tile.localID);
					tile.bitmap = new SoftReference<Bitmap>(bitmap, gcQueue);
				}
				result.setBitmap(tileID, bitmap);
			}
			
			if (cutter != null) cutter.recycle();
		}
		if (hasLoadedTiles) cleanQueue();
		return result;
	}
	
	public Bitmap loadSingleTile(int tileID, Resources r) {
		cleanQueue();
		ResourceFileTile tile = resourceTiles[tileID];
		if (tile.bitmap != null) {
			Bitmap bitmap = tile.bitmap.get();
			if (bitmap != null) return bitmap;
		}
		
		if (AndorsTrailApplication.DEVELOPMENT_DEBUGMESSAGES) {
			L.log("Loading single tile from tileset " + tile.tileset.tilesetName);
		}
		TileCutter cutter = new TileCutter(tile.tileset, r);
		Bitmap result = cutter.createTile(tile.localID);
		cutter.recycle();
		tile.bitmap = new SoftReference<Bitmap>(result, gcQueue);
		return result;
	}
}
