package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.util.Size;

public final class LayeredTileMap {
	public static int LAYER_GROUND = 0;
	public static int LAYER_OBJECTS = 1;
	public static int LAYER_ABOVE = 2;
	
	public final Size size;
	public final MapLayer[] layers;
	
	public LayeredTileMap(Size size, MapLayer[] layers) {
		this.size = size;
		assert(size.width > 0);
		assert(size.height > 0);
		assert(layers.length == 3);
		this.layers = layers;
	}
}
