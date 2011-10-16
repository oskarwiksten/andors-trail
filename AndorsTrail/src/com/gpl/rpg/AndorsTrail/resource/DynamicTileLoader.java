package com.gpl.rpg.AndorsTrail.resource;

import java.util.HashMap;
import java.util.Map.Entry;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.resource.tiles.ResourceFileTileset;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCache;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class DynamicTileLoader {
	private final TileCache tileCache;
	
	private final HashMap<Integer, ResourceFileTilesetLoadList> preparedTilesetsByResourceId = new HashMap<Integer, ResourceFileTilesetLoadList>();
	private final HashMap<String, ResourceFileTilesetLoadList> preparedTilesetsByResourceName = new HashMap<String, ResourceFileTilesetLoadList>();
	private int currentTileStoreIndex;
	
	private static final class ResourceFileTilesetLoadList {
		public final ResourceFileTileset tileset;
		public final HashMap<Integer, Integer> tileIDsToLoadPerLocalID = new HashMap<Integer, Integer>();
		public ResourceFileTilesetLoadList(ResourceFileTileset tileset) {
			this.tileset = tileset;
		}
	}
	
	public DynamicTileLoader(TileCache tileCache) {
		this.tileCache = tileCache;
		initialize();
	}
	
	private void initialize() {
		preparedTilesetsByResourceId.clear();
		preparedTilesetsByResourceName.clear();
		currentTileStoreIndex = tileCache.getMaxTileID();
	}
	
	public void prepareTileset(int resourceId, String tilesetName, Size numTiles, Size destinationTileSize) {
		ResourceFileTileset b = new ResourceFileTileset(resourceId, tilesetName, numTiles, destinationTileSize);
		ResourceFileTilesetLoadList loadList = new ResourceFileTilesetLoadList(b);
		preparedTilesetsByResourceId.put(resourceId, loadList);
		preparedTilesetsByResourceName.put(tilesetName, loadList);
	}
	private ResourceFileTilesetLoadList getTilesetBitmap(int tilesetImageResourceID) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!preparedTilesetsByResourceId.containsKey(tilesetImageResourceID)) {
				L.log("WARNING: Cannot load tileset " + tilesetImageResourceID);
				return null;
			}
		}
		return preparedTilesetsByResourceId.get(tilesetImageResourceID);
	}
	private ResourceFileTilesetLoadList getTilesetBitmap(String tilesetName) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!preparedTilesetsByResourceName.containsKey(tilesetName)) {
				L.log("WARNING: Cannot load tileset " + tilesetName);
				return null;
			}
		}
		return preparedTilesetsByResourceName.get(tilesetName);
	}
	
	public int prepareTileID(int tilesetImageResourceID, int localID) {
		ResourceFileTilesetLoadList b = getTilesetBitmap(tilesetImageResourceID);
		return prepareTileID(b, localID);
	}

	public int prepareTileID(String tilesetName, int localID) {
		ResourceFileTilesetLoadList b = getTilesetBitmap(tilesetName);
		return prepareTileID(b, localID);
	}
	public Size getTilesetSize(String tilesetName) {
		ResourceFileTilesetLoadList b = getTilesetBitmap(tilesetName);
		return b.tileset.destinationTileSize;
	}
	
	private int prepareTileID(ResourceFileTilesetLoadList tileset, int localID) {
		int tileID = 0;
		if (tileset.tileIDsToLoadPerLocalID.containsKey(localID)) {
			tileID = tileset.tileIDsToLoadPerLocalID.get(localID);
		} else {
			++currentTileStoreIndex;
			tileID = currentTileStoreIndex;
			tileset.tileIDsToLoadPerLocalID.put(localID, tileID);
		}
		return tileID;
	}
	
	
	public void flush() {
		tileCache.allocateMaxTileID(currentTileStoreIndex);	
		for(Entry<Integer, ResourceFileTilesetLoadList> e : preparedTilesetsByResourceId.entrySet()) {
			ResourceFileTileset tileset = e.getValue().tileset;
			for(Entry<Integer, Integer> tile : e.getValue().tileIDsToLoadPerLocalID.entrySet()) {
				tileCache.setTile(tile.getValue(), tileset, tile.getKey());
			}
		}
	}
}
