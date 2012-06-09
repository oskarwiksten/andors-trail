package com.gpl.rpg.AndorsTrail.resource.tiles;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class TileCollection {
	public final Bitmap[] bitmaps;
	
	public TileCollection(int maxTileID) {
		bitmaps = new Bitmap[maxTileID+1];
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
