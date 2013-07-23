package com.gpl.rpg.AndorsTrail.resource.tiles;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public final class TileCollection {
	private final Bitmap[] bitmaps;
	public final int maxTileID;

	public TileCollection(int maxTileID) {
		this.bitmaps = new Bitmap[maxTileID+1];
		this.maxTileID = maxTileID;
	}

	public Bitmap getBitmap(int tileID) {
		return bitmaps[tileID];
	}

	public void setBitmap(int tileID, Bitmap bitmap) {
		bitmaps[tileID] = bitmap;
	}

	public void drawTile(Canvas canvas, int tile, int px, int py, Paint mPaint) {
		canvas.drawBitmap(bitmaps[tile], px, py, mPaint);
	}
}
