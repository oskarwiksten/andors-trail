package com.gpl.rpg.AndorsTrail.resource.tiles;

import android.graphics.Bitmap;

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
}
