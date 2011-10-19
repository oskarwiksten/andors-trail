package com.gpl.rpg.AndorsTrail.controller;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Handler;

import com.gpl.rpg.AndorsTrail.VisualEffectCollection;
import com.gpl.rpg.AndorsTrail.VisualEffectCollection.VisualEffect;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Size;
import com.gpl.rpg.AndorsTrail.view.MainView;

public final class VisualEffectController {
	private int effectCount = 0;

	private final VisualEffectCollection effectTypes;
	public VisualEffectController(WorldContext world) {
		this.effectTypes = world.visualEffectTypes;
	}

	public void startEffect(MainView mainview, Coord position, int effectID, int displayValue, VisualEffectCompletedCallback callback, int callbackValue) {
		++effectCount;
		(new VisualEffectAnimation(effectTypes.effects[effectID], position, mainview, displayValue, callback, callbackValue))
		.start();
	}
	
	public final class VisualEffectAnimation extends Handler implements Runnable {

		@Override
		public void run() {
			update();
			if (currentFrame == effect.lastFrame) {
				onCompleted();
			} else {
				postDelayed(this, effect.millisecondPerFrame);
			}
		}
	      
	    private void update() {
        	++currentFrame;
        	int frame = currentFrame;
        	
    		int tileID = effect.frameIconIDs[frame];
			int textYOffset = -2 * (frame);
			if (frame >= beginFadeAtFrame && displayText != null) {
				this.textPaint.setAlpha(255 * (effect.lastFrame - frame) / (effect.lastFrame - beginFadeAtFrame)); 
			}
			area.topLeft.y = position.y - 1;
			view.redrawAreaWithEffect(this, tileID, textYOffset, this.textPaint);
		}

		protected void onCompleted() {
    		--effectCount;
			view.redrawArea(area, MainView.REDRAW_AREA_EFFECT_COMPLETED);
			if (callback != null) callback.onVisualEffectCompleted(callbackValue);
		}
		
		public void start() {
			postDelayed(this, 0);
		}

		private int currentFrame = 0;
		
		private final VisualEffect effect;
		private final MainView view;
		
		public final Coord position;
		public final String displayText;
		public final CoordRect area;
		private final Paint textPaint = new Paint();
		private final int beginFadeAtFrame;
		private final VisualEffectCompletedCallback callback;
		private final int callbackValue;
		
		public VisualEffectAnimation(VisualEffect effect, Coord position, MainView view, int displayValue, VisualEffectCompletedCallback callback, int callbackValue) {
			this.position = position;
			this.callback = callback;
			this.callbackValue = callbackValue;
			this.area = new CoordRect(new Coord(position.x, position.y - 1), new Size(1, 2));
			this.effect = effect;
			this.displayText = (displayValue == 0) ? null : String.valueOf(displayValue);
			this.textPaint.setColor(effect.textColor);
			this.textPaint.setShadowLayer(2, 1, 1, Color.DKGRAY);
			this.textPaint.setTextSize(view.scaledTileSize * 0.5f); // 32dp.
			this.textPaint.setAlpha(255);
			this.textPaint.setTextAlign(Align.CENTER);
			this.view = view;
			this.beginFadeAtFrame = effect.lastFrame / 2;
		}
	}
	
	public static interface VisualEffectCompletedCallback {
		public void onVisualEffectCompleted(int callbackValue);
	}

	public boolean isRunningVisualEffect() {
		return effectCount > 0;
	}
}
