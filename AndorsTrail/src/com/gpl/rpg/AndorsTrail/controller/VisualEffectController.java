package com.gpl.rpg.AndorsTrail.controller;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.AsyncTask;

import com.gpl.rpg.AndorsTrail.VisualEffectCollection;
import com.gpl.rpg.AndorsTrail.VisualEffectCollection.VisualEffect;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Size;
import com.gpl.rpg.AndorsTrail.view.MainView;

public final class VisualEffectController {
	private VisualEffectAnimation currentEffect;

	private final VisualEffectCollection effectTypes;
	public VisualEffectController(WorldContext world) {
		this.effectTypes = world.visualEffectTypes;
	}

	public void startEffect(MainView mainview, Coord position, int effectID, int displayValue, VisualEffectCompletedCallback callback, int callbackValue) {
		VisualEffectAnimation e = currentEffect;
		if (e != null) {
			e.killjoin();
		}
		currentEffect = new VisualEffectAnimation(effectTypes.effects[effectID], position, mainview, displayValue, callback, callbackValue);
		currentEffect.execute();
	}
	
	public final class VisualEffectAnimation extends AsyncTask<Void, Integer, Void> {
		  
	    @Override
		protected Void doInBackground(Void... arg0) {
	    	final int sleepInterval = effect.millisecondPerFrame / 2;
	    	try {
	    		while (isAlive) {
					update();
        			Thread.sleep(sleepInterval);
	        		if (isCancelled()) return null;
				}
		    	Thread.sleep(effect.millisecondPerFrame);
	    	} catch (InterruptedException e) { }
        	
	    	return null;
        }
	    
	    @Override
	    protected void onCancelled() {
	    	isAlive = false;
	    }
	      
	    public void killjoin() { this.cancel(true); }

	    private void update() {
        	int elapsed = (int)(System.currentTimeMillis() - startTime);
        	if (elapsed > effect.duration) {
        		isAlive = false;
        		return;
        	}
        	
        	int currentFrame = (int) Math.floor(elapsed / effect.millisecondPerFrame);
        	
    		if (currentFrame > effect.lastFrame) currentFrame = effect.lastFrame;
			if (currentFrame < 0) currentFrame = 0;
			final boolean changed = currentFrame != this.lastFrame;
			if (!changed) return;
			
			this.lastFrame = currentFrame;
			this.publishProgress(currentFrame);
		}
	    
	    
		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			redrawFrame(progress[0]);
		}
		private void redrawFrame(int frame) {
        	int tileID = effect.frameIconIDs[frame];
			int textYOffset = -2 * (frame);
			if (frame >= beginFadeAtFrame && displayText != null) {
				this.textPaint.setAlpha(255 * (effect.lastFrame - frame) / (effect.lastFrame - beginFadeAtFrame)); 
			}
			view.redrawAreaWithEffect(area, this, tileID, textYOffset, this.textPaint);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			VisualEffectController.this.currentEffect = null;
			view.redrawArea(area, MainView.REDRAW_AREA_EFFECT_COMPLETED);
			if (callback != null) callback.onVisualEffectCompleted(callbackValue);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.isAlive = true;
			redrawFrame(0);
		}

		private boolean isAlive = false;
		private int lastFrame = 0;
		
		private final VisualEffect effect;
		private final long startTime;
		private final MainView view;
		
		public final Coord position;
		public final String displayText;
		private final CoordRect area;
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
			this.startTime = System.currentTimeMillis();
			this.view = view;
			this.beginFadeAtFrame = effect.lastFrame / 2;
		}
	}
	
	
	public static interface VisualEffectCompletedCallback {
		public void onVisualEffectCompleted(int callbackValue);
	}

	public void killCurrentEffect() {
		VisualEffectAnimation e = currentEffect;
		if (e != null) {
			e.killjoin();
		}
	}

	public boolean isRunningVisualEffect() {
		return currentEffect != null;
	}
}
