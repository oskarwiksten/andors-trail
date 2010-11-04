package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class MapLayer {
	public final Size size;
	public final int[][] tiles;
	
	public MapLayer(Size size) {
		this.size = size;
		tiles = new int[size.width][size.height];
	}
	public void setTile(int type, int x, int y) {
		tiles[x][y] = type;
    }
    public void setTile(int type, Coord p) {
    	setTile(type, p.x, p.y);
    }
    public final boolean isOutside(int x, int y) {
    	if (x < 0) return true;
    	if (y < 0) return true;
    	if (x >= size.width) return true;
    	if (y >= size.height) return true;
    	return false;
    }
    public final boolean isOutside(Coord p) {
    	return isOutside(p.x, p.y);
    }
}
