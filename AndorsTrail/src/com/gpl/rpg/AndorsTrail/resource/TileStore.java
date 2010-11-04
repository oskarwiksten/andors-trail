package com.gpl.rpg.AndorsTrail.resource;

import android.graphics.Bitmap;

public final class TileStore {
	public static final int CHAR_HERO = 1;
	public static final int iconID_attackselect = 2;
	public static final int iconID_moveselect = 3;
	public static final int iconID_groundbag = 4;
	public static final int iconID_shop = iconID_groundbag;
	public static final int iconID_mapsign = 5;
	
	public int displayTileSize = 32;
    
	//TODO: should be final.
	public Bitmap[] bitmaps = new Bitmap[1];

	public void allocateTiles(int tilecount) {
		if (tilecount <= 0) return;
		
		Bitmap[] oldArray = bitmaps;
		bitmaps = new Bitmap[bitmaps.length + tilecount];
		System.arraycopy(oldArray, 0, bitmaps, 0, oldArray.length);
    }
}
