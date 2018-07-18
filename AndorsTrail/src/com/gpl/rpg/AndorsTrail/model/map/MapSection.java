package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.util.ByteUtils;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class MapSection {
	public final MapLayer layerGround;
	public final MapLayer layerObjects;
	public final MapLayer layerAbove;
	public final boolean[][] isWalkable;
	private final byte[] layoutHash;

	public MapSection(
			MapLayer layerGround
			, MapLayer layerObjects
			, MapLayer layerAbove
			, boolean[][] isWalkable
			, byte[] layoutHash
	) {
		this.layerGround = layerGround;
		this.layerObjects = layerObjects;
		this.layerAbove = layerAbove;
		this.isWalkable = isWalkable;
		this.layoutHash = layoutHash;
	}

	public void replaceLayerContentsWith(final MapSection replaceLayersWith, final CoordRect replacementArea) {
		replaceTileLayerSection(layerGround, replaceLayersWith.layerGround, replacementArea);
		replaceTileLayerSection(layerObjects, replaceLayersWith.layerObjects, replacementArea);
		replaceTileLayerSection(layerAbove, replaceLayersWith.layerAbove, replacementArea);
		if (replaceLayersWith.isWalkable != null) {
			final int dy = replacementArea.topLeft.y;
			final int height = replacementArea.size.height;
			for (int sx = 0, dx = replacementArea.topLeft.x; sx < replacementArea.size.width; ++sx, ++dx) {
				System.arraycopy(replaceLayersWith.isWalkable[sx], 0, isWalkable[dx], dy, height);
			}
		}
		ByteUtils.xorArray(layoutHash, replaceLayersWith.layoutHash);
	}

	private static void replaceTileLayerSection(MapLayer dest, MapLayer src, CoordRect area) {
		if (src == null) return;
		final int dy = area.topLeft.y;
		final int height = area.size.height;
		for (int sx = 0, dx = area.topLeft.x; sx < area.size.width; ++sx, ++dx) {
			System.arraycopy(src.tiles[sx], 0, dest.tiles[dx], dy, height);
		}
	}

	public String calculateHash() {
		return ByteUtils.toHexString(layoutHash, 4);
	}
}
