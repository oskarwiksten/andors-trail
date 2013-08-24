package com.gpl.rpg.AndorsTrail.resource.tiles;

import android.graphics.Matrix;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class ResourceFileTileset {
	public final int resourceID;
	public final String tilesetName;
	public final Size destinationTileSize;
	public final Size numTiles;
	public Size sourceTileSize;
	public Matrix scale;

	public ResourceFileTileset(int resourceID, String tilesetName, Size numTiles, Size destinationTileSize) {
		this.resourceID = resourceID;
		this.tilesetName = tilesetName;
		this.destinationTileSize = destinationTileSize;
		this.numTiles = numTiles;
	}

	@Override public int hashCode() { return resourceID; }

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
				//L.log("OPTIMIZE: Tileset " + tilesetName + " will be resized from " + sourceTileSize.toString() + " to " + destinationTileSize.toString());
			}
		}
	}

}
