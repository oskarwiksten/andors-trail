package com.gpl.rpg.AndorsTrail.model.map;

import java.util.Collection;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.gpl.rpg.AndorsTrail.util.Size;

public final class LayeredTileMap {
	public static final int LAYER_GROUND = 0;
	public static final int LAYER_OBJECTS = 1;
	public static final int LAYER_ABOVE = 2;

	private static final ColorFilter colorFilterBlack20 = createGrayScaleColorFilter(0.2f);
	private static final ColorFilter colorFilterBlack40 = createGrayScaleColorFilter(0.4f);
	private static final ColorFilter colorFilterBlack60 = createGrayScaleColorFilter(0.6f);
	private static final ColorFilter colorFilterBlack80 = createGrayScaleColorFilter(0.8f);

	public final MapLayer[] layers;
	public final Collection<Integer> usedTileIDs;
	public final String colorFilter;
	
	public LayeredTileMap(Size size, MapLayer[] layers, Collection<Integer> usedTileIDs, String colorFilter) {
		assert(size.width > 0);
		assert(size.height > 0);
		assert(layers.length == 3);
		this.layers = layers;
		this.usedTileIDs = usedTileIDs;
		this.colorFilter = colorFilter;
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
