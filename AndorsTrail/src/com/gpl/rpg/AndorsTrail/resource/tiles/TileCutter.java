package com.gpl.rpg.AndorsTrail.resource.tiles;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public final class TileCutter {
	private final ResourceFileTileset sourceFile;
	private final Bitmap tilesetImage;
	private boolean recycle = true;

	public TileCutter(ResourceFileTileset sourceFile, Resources r) {
		this.sourceFile = sourceFile;
		this.tilesetImage = createTilesetImage(r);
	}

	private Bitmap createTilesetImage(Resources r) {
		//return BitmapFactory.decodeResource(r, b.resourceId);
		Options o = new Options();
		o.inScaled = false;
		Bitmap sourceImage = BitmapFactory.decodeResource(r, sourceFile.resourceID, o);
		sourceImage.setDensity(Bitmap.DENSITY_NONE);
		sourceFile.calculateFromSourceImageSize(sourceImage.getWidth(), sourceImage.getHeight());
		return sourceImage;
	}

	public Bitmap createTile(int localID) {
		final int x = localID % sourceFile.numTiles.width;
		final int y = (localID - x) / sourceFile.numTiles.width;
		final int left = x * sourceFile.sourceTileSize.width;
		final int top = y * sourceFile.sourceTileSize.height;
		Bitmap result;
		if (sourceFile.scale != null) {
			result = Bitmap.createBitmap(tilesetImage, left, top, sourceFile.sourceTileSize.width, sourceFile.sourceTileSize.height, sourceFile.scale, true);
		} else {
			result = Bitmap.createBitmap(tilesetImage, left, top, sourceFile.sourceTileSize.width, sourceFile.sourceTileSize.height);
		}
		if (result == tilesetImage) recycle = false;
		return result;
	}

	public void recycle() {
		if (recycle) tilesetImage.recycle();
	}
}
