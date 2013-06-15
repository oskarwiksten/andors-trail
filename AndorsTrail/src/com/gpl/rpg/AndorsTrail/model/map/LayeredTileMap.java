package com.gpl.rpg.AndorsTrail.model.map;

import java.util.Collection;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class LayeredTileMap {
	public static final int LAYER_GROUND = 0;
	public static final int LAYER_OBJECTS = 1;
	public static final int LAYER_ABOVE = 2;

	private static final ColorFilter colorFilterBlack20 = createGrayScaleColorFilter(0.2f);
	private static final ColorFilter colorFilterBlack40 = createGrayScaleColorFilter(0.4f);
	private static final ColorFilter colorFilterBlack60 = createGrayScaleColorFilter(0.6f);
	private static final ColorFilter colorFilterBlack80 = createGrayScaleColorFilter(0.8f);

	private final Size size;
	public final MapLayer[] layers;
	public final Collection<Integer> usedTileIDs;
	private final boolean[][] isWalkable;
	public final String colorFilter;
	
	public LayeredTileMap(
			Size size,
			MapLayer[] layers,
			boolean[][] isWalkable,
			Collection<Integer> usedTileIDs,
			String colorFilter) {
		assert(size.width > 0);
		assert(size.height > 0);
		assert(layers.length == 3);
		assert(isWalkable.length == size.width);
		assert(isWalkable[0].length == size.height);
		this.size = size;
		this.layers = layers;
		this.usedTileIDs = usedTileIDs;
		this.isWalkable = isWalkable;
		this.colorFilter = colorFilter;
	}

	public final boolean isWalkable(final Coord p) {
		if (isOutside(p.x, p.y)) return false;
		return isWalkable[p.x][p.y];
	}
	public final boolean isWalkable(final int x, final int y) {
		if (isOutside(x, y)) return false;
		return isWalkable[x][y];
	}
	public final boolean isWalkable(final CoordRect p) {
		for (int y = 0; y < p.size.height; ++y) {
			for (int x = 0; x < p.size.width; ++x) {
				if (!isWalkable(p.topLeft.x + x, p.topLeft.y + y)) return false;
			}
		}
		return true;
	}
	public final boolean isOutside(final Coord p) { return isOutside(p.x, p.y); }
	public final boolean isOutside(final int x, final int y) {
		if (x < 0) return true;
		if (y < 0) return true;
		if (x >= size.width) return true;
		if (y >= size.height) return true;
		return false;
	}
	public final boolean isOutside(final CoordRect area) {
		if (isOutside(area.topLeft)) return true;
		if (area.topLeft.x + area.size.width > size.width) return true;
		if (area.topLeft.y + area.size.height > size.height) return true;
		return false;
	}

	public void setColorFilter(Paint mPaint) {
		mPaint.setColorFilter(getColorFilter());
	}

	public ColorFilter getColorFilter() {
		if (colorFilter == null) return null;
		else if (colorFilter.length() <= 0) return null;
		else if (colorFilter.equals("black20")) return colorFilterBlack20;
		else if (colorFilter.equals("black40")) return colorFilterBlack40;
		else if (colorFilter.equals("black60")) return colorFilterBlack60;
		else if (colorFilter.equals("black80")) return colorFilterBlack80;
		return null;
	}

	private static ColorMatrixColorFilter createGrayScaleColorFilter(float blackOpacity) {
		final float f = blackOpacity;
		return new ColorMatrixColorFilter(new float[] {
			f,     0.00f, 0.00f, 0.0f, 0.0f,
			0.00f, f,     0.00f, 0.0f, 0.0f,
			0.00f, 0.00f, f,     0.0f, 0.0f,
			0.00f, 0.00f, 0.00f, 1.0f, 0.0f
		});
	}
}
