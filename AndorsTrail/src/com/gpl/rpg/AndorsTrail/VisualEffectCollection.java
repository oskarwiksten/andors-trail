package com.gpl.rpg.AndorsTrail;

import android.graphics.Color;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.util.ConstRange;

public final class VisualEffectCollection {
	public static final int EFFECT_BLOOD = 0;
	public static final int EFFECT_RESTORE_HP = 1;
	public static final int EFFECT_RESTORE_AP = EFFECT_RESTORE_HP;
	public static final int EFFECT_POISON = 2;
	
	public static final int NUM_EFFECTS = EFFECT_POISON + 1;
	
	public final VisualEffect[] effects = new VisualEffect[NUM_EFFECTS];
	
	public void initialize(DynamicTileLoader loader) {
		effects[EFFECT_BLOOD] = createEffect(loader, R.drawable.effect_blood4, new ConstRange(14, 0), 400, Color.RED);
		effects[EFFECT_RESTORE_HP] = createEffect(loader, R.drawable.effect_heal2, new ConstRange(16, 0), 400, Color.rgb(150, 150, 255));
		effects[EFFECT_POISON] = createEffect(loader, R.drawable.effect_poison1, new ConstRange(16, 0), 400, Color.GREEN);
	}

	private static VisualEffect createEffect(DynamicTileLoader loader, int drawableID, ConstRange frameRange, int duration, int textColor) {
		int[] frameIconIDs = new int[frameRange.max - frameRange.current];
		for(int i = 0; i < frameIconIDs.length; ++i) {
			frameIconIDs[i] = loader.prepareTileID(drawableID, frameRange.current + i);
		}
		return new VisualEffect(frameIconIDs, duration, textColor);
	}

	public static final class VisualEffect {
		public final int[] frameIconIDs;
		public final int duration; // milliseconds
		public final int textColor;
		//public final int fps = ModelContainer.attackAnimationFPS;
		//public final int millisecondPerFrame = 1000 / fps;
		//public final int totalFrames = duration / millisecondPerFrame;
		public final int fps;
		public final int millisecondPerFrame;
		public final int totalFrames;
		public final int lastFrame;
		
		public VisualEffect(int[] frameIconIDs, int duration, int textColor) {
			this.frameIconIDs = frameIconIDs;
			this.duration = duration;
			this.textColor = textColor;
			totalFrames = frameIconIDs.length;
			lastFrame = totalFrames - 1;
			millisecondPerFrame = duration / totalFrames;
			fps = 1000 / millisecondPerFrame;
		}
	}
}
