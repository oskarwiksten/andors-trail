package com.gpl.rpg.AndorsTrail.view;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.controller.VisualEffectController.VisualEffectAnimation;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.map.LayeredWorldMap;
import com.gpl.rpg.AndorsTrail.model.map.MapLayer;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.resource.TileStore;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Size;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public final class MainView extends SurfaceView implements SurfaceHolder.Callback {

    private int displayTileSize = 32;

    private Size screenSizeTileCount = null;
    private final Coord screenOffset = new Coord(); // pixel offset where the image begins
    private final Coord mapTopLeft = new Coord(); // Map coords of visible map
    private CoordRect mapViewArea; // Area in mapcoordinates containing the visible map. topleft == this.topleft
    
    private final ModelContainer model;
    private final TileStore tiles;
	private final ViewContext view;
	
    private final SurfaceHolder holder;
    private final Paint mPaint = new Paint();
	private final CoordRect p1x1 = new CoordRect(new Coord(), new Size(1,1));
	private boolean hasSurface = false;

	private final Coord lastTouchPosition_tileCoords = new Coord();
    private int lastTouchPosition_dx = 0;
    private int lastTouchPosition_dy = 0;
    private long lastTouchEventTime = 0;

	public MainView(Context context, AttributeSet attr) {
		super(context, attr);
		this.holder = getHolder();
		
    	AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
        this.view = app.currentView.get();
        this.model = app.world.model;
    	this.tiles = app.world.tileStore;
    	
    	holder.addCallback(this);
    	
        setFocusable(true);
        requestFocus();
        setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainView.this.onClick();
			}
		});
        setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return MainView.this.onLongClick();
			}
		});
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
		if (!model.uiSelections.isMainActivityVisible) return true;

    	if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
    		keyboardAction(0, -1);
    	} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
    		keyboardAction(0, 1);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
        	keyboardAction(-1, 0);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
        	keyboardAction(1, 0);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
        	keyboardAction(0, 0);
        } else {
        	return super.onKeyDown(keyCode, msg);
        }
    	//TODO: add more keys
    	return true;
    }

	private void keyboardAction(int dx, int dy) {
		if (!allowInputInterval()) return;
		
		lastTouchPosition_dx = dx;
		lastTouchPosition_dy = dy;
		onClick();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (w <= 0 || h <= 0) return;

		L.log("surfaceChanged " + w + ", " + h);

		displayTileSize = tiles.displayTileSize;
		
		screenSizeTileCount = new Size(
				(int) Math.floor(w / displayTileSize)
				,(int) Math.floor(h / displayTileSize)
			);

    	screenOffset.set(
				(w - (displayTileSize * screenSizeTileCount.width)) / 2
				,(h - (displayTileSize * screenSizeTileCount.height)) / 2
			);
	    	
    	if (model.currentMap != null) {
    		notifyMapChanged();
    	}
    	
    	redrawAll(REDRAW_ALL_SURFACE_CHANGED);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		hasSurface = true;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
		L.log("surfaceDestroyed");
	}

    private void onClick() {
    	if (model.uiSelections.isInCombat) {
			view.combatController.executeMoveAttack(lastTouchPosition_dx, lastTouchPosition_dy);
		} else {
			view.movementController.movePlayer(lastTouchPosition_dx, lastTouchPosition_dy);
		}
    }
    
    private boolean onLongClick() {
		if (model.uiSelections.isInCombat) {
			//TODO: Should be able to mark positions far away (mapwalk / ranged combat)
			if (lastTouchPosition_dx == 0 && lastTouchPosition_dy == 0) return false;
			if (Math.abs(lastTouchPosition_dx) > 1) return false;
			if (Math.abs(lastTouchPosition_dy) > 1) return false;
				
			view.combatController.setCombatSelection(lastTouchPosition_tileCoords);
			return true;
		}
		return false;
    }
    
    private boolean allowInputInterval() {
		final long now = System.currentTimeMillis();
		if ((now - lastTouchEventTime) < Constants.MINIMUM_INPUT_INTERVAL) return false;
		lastTouchEventTime = now;
		return true;
    }
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!model.uiSelections.isMainActivityVisible) return true;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			if (!allowInputInterval()) return true;
			
			lastTouchPosition_tileCoords.set(
					(int) Math.floor(((int)event.getX() - screenOffset.x) / displayTileSize) + mapTopLeft.x
					,(int) Math.floor(((int)event.getY() - screenOffset.y) / displayTileSize) + mapTopLeft.y);
			lastTouchPosition_dx = lastTouchPosition_tileCoords.x - model.player.position.x;
			lastTouchPosition_dy = lastTouchPosition_tileCoords.y - model.player.position.y;
			
			if (!model.uiSelections.isInCombat) {
				view.movementController.movePlayer(lastTouchPosition_dx, lastTouchPosition_dy);
				return true;
			}
		}
		return super.onTouchEvent(event);
	}
    
    public static final int REDRAW_ALL_SURFACE_CHANGED = 1;
    public static final int REDRAW_ALL_MAP_CHANGED = 2;
    public static final int REDRAW_ALL_PLAYER_MOVED = 3;
    public static final int REDRAW_ALL_MONSTER_MOVED = 4;
    public static final int REDRAW_ALL_MONSTER_KILLED = 10;
    public static final int REDRAW_AREA_EFFECT_STARTING = 5;
    public static final int REDRAW_AREA_EFFECT_COMPLETED = 6;
    public static final int REDRAW_TILE_SELECTION_REMOVED = 7;
    public static final int REDRAW_TILE_SELECTION_ADDED = 8;
    public static final int REDRAW_TILE_BAG = 9;
    
	public void redrawAll(int why) {
		redrawArea_(mapViewArea);
	}	
	public void redrawTile(final Coord p, int why) {
		p1x1.topLeft.set(p);
		redrawArea_(p1x1);
		//redrawAll(why);
	}
	public void redrawArea(final CoordRect area, int why) {
		redrawArea_(area);
		//redrawAll(why);
	}
	private void redrawArea_(final CoordRect area) {
		if (!hasSurface) return;
		final LayeredWorldMap currentMap = model.currentMap;
        boolean b = currentMap.isOutside(area);
        if (b) return;
		
		calculateRedrawRect(area);
		Canvas c = null;
		try {
	        c = holder.lockCanvas(redrawRect);
	        synchronized (holder) {
	        	c.translate(screenOffset.x, screenOffset.y);
	        	doDrawRect(c, area);
	        }
	    } finally {
	        // do this in a finally so that if an exception is thrown
	        // during the above, we don't leave the Surface in an
	        // inconsistent state
	        if (c != null) {
	        	holder.unlockCanvasAndPost(c);
	        }
	    }
	}
	
	private final Rect redrawRect = new Rect();
	public void redrawAreaWithEffect(final CoordRect area, final VisualEffectAnimation effect) {
		if (!hasSurface) return;
		final LayeredWorldMap currentMap = model.currentMap;
        if (currentMap.isOutside(area)) return;
		
		calculateRedrawRect(area);
		Canvas c = null;
		try {
	        c = holder.lockCanvas(redrawRect);
	        synchronized (holder) {
	        	c.translate(screenOffset.x, screenOffset.y);
	        	doDrawRect(c, area);
	        	drawFromMapPosition(c, area, effect.position, effect.currentTileID);
    			if (effect.displayText != null) {
    				drawEffectText(c, area, effect);
    			}
	        }
	    } finally {
	        // do this in a finally so that if an exception is thrown
	        // during the above, we don't leave the Surface in an
	        // inconsistent state
	        if (c != null) {
	        	holder.unlockCanvasAndPost(c);
	        }
	    }
	}
	private void clearCanvas() {
		if (!hasSurface) return;
		Canvas c = null;
		try {
			c = holder.lockCanvas(null);
	        synchronized (holder) {
	        	c.drawColor(Color.BLACK);
	        }
	    } finally {
	        // do this in a finally so that if an exception is thrown
	        // during the above, we don't leave the Surface in an
	        // inconsistent state
	        if (c != null) {
	        	holder.unlockCanvasAndPost(c);
	        }
	    }
	}
	
	private void calculateRedrawRect(final CoordRect area) {
		worldCoordsToScreenCords(area, redrawRect);
	}
	
	private void worldCoordsToScreenCords(final CoordRect worldArea, Rect destScreenRect) {
		destScreenRect.left = screenOffset.x + (worldArea.topLeft.x - mapViewArea.topLeft.x) * displayTileSize;
		destScreenRect.top = screenOffset.y + (worldArea.topLeft.y - mapViewArea.topLeft.y) * displayTileSize;
		destScreenRect.right = destScreenRect.left + worldArea.size.width * displayTileSize;
		destScreenRect.bottom = destScreenRect.top + worldArea.size.height * displayTileSize;
	}
	
	private void doDrawRect(Canvas canvas, CoordRect area) {
    	final LayeredWorldMap currentMap = model.currentMap;
        
        drawMapLayer(canvas, area, currentMap.layers[LayeredWorldMap.LAYER_GROUND]);
        tryDrawMapLayer(canvas, area, currentMap, LayeredWorldMap.LAYER_OBJECTS);
        
        for (Loot l : currentMap.groundBags) {
        	if (l.isVisible) {
        		drawFromMapPosition(canvas, area, l.position, TileStore.iconID_groundbag);
        	}
		}
        
		drawFromMapPosition(canvas, area, model.player.position, model.player.traits.iconID);
		for (MonsterSpawnArea a : currentMap.spawnAreas) {
			for (Monster m : a.monsters) {
				drawFromMapPosition(canvas, area, m.rectPosition, m.traits.iconID);
			}
		}
		
		tryDrawMapLayer(canvas, area, currentMap, LayeredWorldMap.LAYER_ABOVE);
        
		if (model.uiSelections.selectedPosition != null) {
			if (model.uiSelections.selectedMonster != null) {
				drawFromMapPosition(canvas, area, model.uiSelections.selectedPosition, TileStore.iconID_attackselect);
			} else {
				drawFromMapPosition(canvas, area, model.uiSelections.selectedPosition, TileStore.iconID_moveselect);
			}
		}
    }
    
	private void tryDrawMapLayer(Canvas canvas, final CoordRect area, final LayeredWorldMap currentMap, final int layerIndex) {
    	if (currentMap.layers.length > layerIndex) drawMapLayer(canvas, area, currentMap.layers[layerIndex]);        
    }
    
    private void drawMapLayer(Canvas canvas, final CoordRect area, final MapLayer layer) {
    	int my = area.topLeft.y;
    	int py = (area.topLeft.y - mapViewArea.topLeft.y) * displayTileSize;
    	int px0 = (area.topLeft.x - mapViewArea.topLeft.x) * displayTileSize;
		for (int y = 0; y < area.size.height; ++y, ++my, py += displayTileSize) {
        	int mx = area.topLeft.x;
        	int px = px0;
        	for (int x = 0; x < area.size.width; ++x, ++mx, px += displayTileSize) {
        		final int tile = layer.tiles[mx][my];
        		if (tile != 0) {
        			canvas.drawBitmap(tiles.bitmaps[tile], px, py, mPaint);
        		}
            }
        }
    }

	private void drawFromMapPosition(Canvas canvas, final CoordRect area, final Coord p, final int tile) {
		if (!area.contains(p)) return;
		_drawFromMapPosition(canvas, area, p.x, p.y, tile);
    }
	private void drawFromMapPosition(Canvas canvas, final CoordRect area, final CoordRect p, final int tile) {
		if (!area.intersects(p)) return;
		_drawFromMapPosition(canvas, area, p.topLeft.x, p.topLeft.y, tile);
    }
	private void _drawFromMapPosition(Canvas canvas, final CoordRect area, int x, int y, final int tile) {
    	x -= mapViewArea.topLeft.x;
    	y -= mapViewArea.topLeft.y;
		if (	   (x >= 0 && x < mapViewArea.size.width)
				&& (y >= 0 && y < mapViewArea.size.height)) {
			canvas.drawBitmap(tiles.bitmaps[tile], 
	        		x * displayTileSize,
	        		y * displayTileSize,
	        		mPaint);
		}
    }
	
	private void drawEffectText(Canvas canvas, final CoordRect area, final VisualEffectAnimation e) {
    	int x = (e.position.x - mapViewArea.topLeft.x) * displayTileSize + displayTileSize/2;
    	int y = (e.position.y - mapViewArea.topLeft.y) * displayTileSize + displayTileSize/2 + e.textYOffset;
		canvas.drawText(e.displayText, x, y, e.textPaint);
    }
    
	public void notifyMapChanged() {
		Size mapViewSize = new Size(
    			Math.min(screenSizeTileCount.width, model.currentMap.size.width)
    			,Math.min(screenSizeTileCount.height, model.currentMap.size.height)
			);
		mapViewArea = new CoordRect(mapTopLeft, mapViewSize);
		
		clearCanvas();
	    
		recalculateMapTopLeft();
		redrawAll(REDRAW_ALL_MAP_CHANGED);
	}
	
	private void recalculateMapTopLeft() {
		mapTopLeft.set(0, 0);
		
		final LayeredWorldMap currentMap = model.currentMap;
		final Coord playerpos = model.player.position;
		
    	if (currentMap.size.width > screenSizeTileCount.width) {
    		mapTopLeft.x = Math.max(0, playerpos.x - mapViewArea.size.width/2);
    		mapTopLeft.x = Math.min(mapTopLeft.x, currentMap.size.width - mapViewArea.size.width);
    	}
    	if (currentMap.size.height > screenSizeTileCount.height) {
    		mapTopLeft.y = Math.max(0, playerpos.y - mapViewArea.size.height/2);
    		mapTopLeft.y = Math.min(mapTopLeft.y, currentMap.size.height - mapViewArea.size.height);
    	}
	}
	
	public void notifyPlayerMoved() {
		recalculateMapTopLeft();
		redrawAll(REDRAW_ALL_PLAYER_MOVED);
	}
}
