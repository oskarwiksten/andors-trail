package com.gpl.rpg.AndorsTrail.resource.tiles;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.gpl.rpg.AndorsTrail.util.LruCache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

public final class TileCache {

	private final ReferenceQueue<Bitmap> gcQueue = new ReferenceQueue<Bitmap>();
	private ResourceFileTile[] resourceTiles = new ResourceFileTile[1];
	private final HashMap<String, SparseIntArray> tileIDsPerTilesetAndLocalID = new HashMap<String, SparseIntArray>();
	private final LruCache<Integer, Bitmap> cache = new LruCache<Integer, Bitmap>(1000);

	public int getMaxTileID() { return resourceTiles.length-1; }
	public void allocateMaxTileID(int maxTileID) {
		if (maxTileID <= 0) return;

		ResourceFileTile[] oldArray = resourceTiles;
		resourceTiles = new ResourceFileTile[maxTileID+1];
		System.arraycopy(oldArray, 0, resourceTiles, 0, oldArray.length);
	}
	public void setTile(int tileID, ResourceFileTileset tileset, int localID) {
		if (resourceTiles[tileID] == null) resourceTiles[tileID] = new ResourceFileTile(tileset, localID);
		SparseIntArray tileIDsPerLocalID = tileIDsPerTilesetAndLocalID.get(tileset.tilesetName);
		if (tileIDsPerLocalID == null) {
			tileIDsPerLocalID = new SparseIntArray();
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
		//public WeakReference<Bitmap> bitmap;
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
		int maxTileID = 0;
		HashMap<ResourceFileTileset, SparseArray<ResourceFileTile>> tilesToLoadPerSourceFile = new HashMap<ResourceFileTileset, SparseArray<ResourceFileTile>>();
		for(int tileID : iconIDs) {
			ResourceFileTile tile = resourceTiles[tileID];
			SparseArray<ResourceFileTile> tiles = tilesToLoadPerSourceFile.get(tile.tileset);
			if (tiles == null) {
				tiles = new SparseArray<TileCache.ResourceFileTile>();
				tilesToLoadPerSourceFile.put(tile.tileset, tiles);
			}
			tiles.put(tileID, tile);
			maxTileID = Math.max(maxTileID, tileID);
		}

		boolean hasLoadedTiles = false;
		if (result == null) result = new TileCollection(maxTileID);
		for(Entry<ResourceFileTileset, SparseArray<ResourceFileTile>> e : tilesToLoadPerSourceFile.entrySet()) {
			TileCutter cutter = null;

			SparseArray<ResourceFileTile> tilesToLoad = e.getValue();
			for (int i = 0; i < tilesToLoad.size(); ++i) {
				int tileID = tilesToLoad.keyAt(i);
				ResourceFileTile tile = tilesToLoad.valueAt(i);

				Bitmap bitmap = cache.get(tileID);

				if (bitmap == null) {
					if (cutter == null) {
						if (!hasLoadedTiles) cleanQueue();
						cutter = new TileCutter(e.getKey(), r);
						hasLoadedTiles = true;
					}

					bitmap = cutter.createTile(tile.localID);
					cache.put(tileID, bitmap);
					new WeakReference<Bitmap>(bitmap, gcQueue);
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
		Bitmap bitmap = cache.get(tileID);
		if (bitmap != null) return bitmap;

		TileCutter cutter = new TileCutter(tile.tileset, r);
		Bitmap result = cutter.createTile(tile.localID);
		cutter.recycle();
		cache.put(tileID, result);
		return result;
	}
}
