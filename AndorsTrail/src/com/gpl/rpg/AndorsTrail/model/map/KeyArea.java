package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class KeyArea {
	public final CoordRect position;
	public final String requiredKey;
	public final String message;
	
	public KeyArea(final CoordRect position, final String requiredKey, final String message) {
		this.position = new CoordRect(position);
		this.requiredKey = requiredKey;
		this.message = message;
	}
}
