package com.gpl.rpg.AndorsTrail.controller;

import android.graphics.Color;
import android.graphics.Paint;

import com.gpl.rpg.AndorsTrail.EffectCollection;
import com.gpl.rpg.AndorsTrail.EffectCollection.Effect;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Size;
import com.gpl.rpg.AndorsTrail.view.MainView;

public final class EffectController {
	private EffectAnimation currentEffect;

	private final EffectCollection effectTypes;
	public EffectController(WorldContext world) {
		this.effectTypes = world.effectTypes;
	}

	public void startEffect(MainView mainview, Coord position, int effectID, int displayValue) {
		EffectAnimation e = currentEffect;
		if (e != null) {
			e.killjoin();
		}
		currentEffect = new EffectAnimation(effectTypes.effects[effectID], position, mainview, displayValue);
		currentEffect.start();
	}

	public final class EffectAnimation extends Thread {
		  
	    @Override
        public void run() {
	    	while (isAlive) {
	            update();
	        	try {
	        		sleep(8);
	        	} catch (InterruptedException e) {
	        		isAlive = false;
	        	}
	    	}
	    	view.redrawArea(area, MainView.REDRAW_AREA_EFFECT_COMPLETED);
	    	EffectController.this.currentEffect = null;
        }
	    
	    public void killjoin() {
	    	isAlive = false;
	    	safejoin();
	    }
	    public void safejoin() {
	    	try {
	    		join();
	    	} catch (InterruptedException e) {}
	    }
	    private void update() {
        	int elapsed = (int)(System.currentTimeMillis() - startTime);
        	if (elapsed > effect.duration) {
        		isAlive = false;
        		return;
        	}
        	
        	int currentFrame = (int) Math.floor((float)elapsed / effect.millisecondPerFrame);
        	setCurrentTile(currentFrame);
        }
	    
		private final Effect effect;
		private final long startTime;
		private final MainView view;
		
		public final Coord position;
		private final CoordRect area;
		public final String displayText;
		public final Paint textPaint = new Paint();
		public int currentTileID = 0;
		public int textYOffset = 0;
		private boolean isAlive = false;
		
		public EffectAnimation(Effect effect, Coord position, MainView view, int displayValue) {
			this.position = position;
			this.area = new CoordRect(new Coord(position.x, position.y - 1), new Size(1, 2));
			this.effect = effect;
			this.displayText = (displayValue == 0) ? null : String.valueOf(displayValue);
			this.textPaint.setColor(effect.textColor);
			this.textPaint.setShadowLayer(1, 1, 1, Color.DKGRAY);
			this.isAlive = true;
			this.startTime = System.currentTimeMillis();
			this.view = view;
			setCurrentTile(0);
		}
		
		private void setCurrentTile(int currentFrame) {
			if (currentFrame > effect.lastFrame) currentFrame = effect.lastFrame;
			if (currentFrame < 0) currentFrame = 0;
			int newTileID = effect.frameIconIDs[currentFrame];
			final boolean changed = newTileID != this.currentTileID;
			this.currentTileID = newTileID;
			this.textYOffset = -2 * (currentFrame);
			if (changed) {
				view.redrawAreaWithEffect(area, this);
			}
		}
	}

	public void waitForCurrentEffect() {
		EffectAnimation e = currentEffect;
		if (e != null) {
			e.safejoin();
		}
	}
	public void killCurrentEffect() {
		EffectAnimation e = currentEffect;
		if (e != null) {
			e.killjoin();
		}
	}
}
