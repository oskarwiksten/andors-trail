package com.gpl.rpg.AndorsTrail.view;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.InputController;
import com.gpl.rpg.AndorsTrail.controller.VisualEffectController.BloodSplatter;
import com.gpl.rpg.AndorsTrail.controller.VisualEffectController.VisualEffectAnimation;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.model.map.MapLayer;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCollection;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
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

public final class MainView extends SurfaceView implements SurfaceHolder.Callback {

	private final int tileSize;
    private float scale;
    public int scaledTileSize;

    private Size screenSizeTileCount = null;
    private final Coord screenOffset = new Coord(); // pixel offset where the image begins
    private final Coord mapTopLeft = new Coord(); // Map coords of visible map
    private CoordRect mapViewArea; // Area in mapcoordinates containing the visible map. topleft == this.topleft
    
    private final ModelContainer model;
    private final WorldContext world;
	private final ViewContext view;
	private final InputController inputController;
	private final AndorsTrailPreferences preferences;
	
    private final SurfaceHolder holder;
    private final Paint mPaint = new Paint();
	private final CoordRect p1x1 = new CoordRect(new Coord(), new Size(1,1));
	private boolean hasSurface = false;
	
	private PredefinedMap currentMap;
	private LayeredTileMap currentTileMap;
	private TileCollection tiles;
	private final Coord playerPosition = new Coord();
	private Size surfaceSize;

	public MainView(Context context, AttributeSet attr) {
		super(context, attr);
		this.holder = getHolder();
		
		AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
        this.view = app.currentView.get();
        this.model = app.world.model;
    	this.world = app.world;
    	this.tileSize = world.tileManager.tileSize;
    	this.inputController = view.inputController;
    	this.preferences = app.preferences;

    	holder.addCallback(this);
    	
        setFocusable(true);
        requestFocus();
        setOnClickListener(this.inputController);
        setOnLongClickListener(this.inputController);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
    	if (!canAcceptInput()) return true;

		if (inputController.onKeyboardAction(keyCode)) return true;
		else return super.onKeyDown(keyCode, msg);
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
		if (!canAcceptInput()) return true;
		
		inputController.onKeyboardCancel();
		
    	return super.onKeyUp(keyCode, msg);
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (w <= 0 || h <= 0) return;

		this.scale = world.tileManager.scale;
		this.mPaint.setFilterBitmap(scale != 1);
		this.scaledTileSize = world.tileManager.viewTileSize;
		this.surfaceSize = new Size(w, h);
		this.screenSizeTileCount = new Size(
				(int) Math.floor(w / scaledTileSize)
				,(int) Math.floor(h / scaledTileSize)
			);
		
    	if (model.currentMap != null) {
    		notifyMapChanged(model);
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
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
        if (!canAcceptInput()) return true;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			final int tile_x = (int) Math.floor(((int)event.getX() - screenOffset.x) / scaledTileSize) + mapTopLeft.x;
			final int tile_y = (int) Math.floor(((int)event.getY() - screenOffset.y) / scaledTileSize) + mapTopLeft.y;
			if (inputController.onTouchedTile(tile_x, tile_y)) return true;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			inputController.onTouchCancell();
			break;
		}
		return super.onTouchEvent(event);
	}
    
	private boolean canAcceptInput() {
		if (!model.uiSelections.isMainActivityVisible) return false;
		if (!hasSurface) return false;
		return true;
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
	}
	public void redrawArea(final CoordRect area, int why) {
		redrawArea_(area);
	}
	private void redrawArea_(CoordRect area) {
		if (!hasSurface) return;
		//if (!preferences.optimizedDrawing) area = mapViewArea;
        
		boolean b = currentMap.isOutside(area);
        if (b) return;
				
		calculateRedrawRect(area);
		Canvas c = null;
		try {
	        c = holder.lockCanvas(redrawRect);
	        synchronized (holder) { synchronized (tiles) {
	        	c.translate(screenOffset.x, screenOffset.y);
	        	c.scale(scale, scale);
	        	doDrawRect(c, area);
	        } }
	    } finally {
	        // do this in a finally so that if an exception is thrown
	        // during the above, we don't leave the Surface in an
	        // inconsistent state
	        if (c != null) {
	        	holder.unlockCanvasAndPost(c);
	        }
	    }
	}
	
	private boolean shouldRedrawEverythingForVisualEffect() {
		if (preferences.optimizedDrawing) return false;
		if (model.uiSelections.isInCombat) return false; // Discard the "optimized drawing" setting while in combat.
		return true;
	}
	private final Rect redrawRect = new Rect();
	public void redrawAreaWithEffect(final VisualEffectAnimation effect, int tileID, int textYOffset, Paint textPaint) {
		CoordRect area = effect.area;
		if (!hasSurface) return;
		if (shouldRedrawEverythingForVisualEffect()) area = mapViewArea;
		
		if (currentMap.isOutside(area)) return;
		
		calculateRedrawRect(area);
		Canvas c = null;
		try {
	        c = holder.lockCanvas(redrawRect);
	        synchronized (holder) { synchronized (tiles) {
	        	c.translate(screenOffset.x, screenOffset.y);
	        	c.scale(scale, scale);
	        	doDrawRect(c, area);
	        	drawFromMapPosition(c, area, effect.position, tileID);
    			if (effect.displayText != null) {
    				drawEffectText(c, area, effect, textYOffset, textPaint);
    			}
	        } }
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
		destScreenRect.left = screenOffset.x + (worldArea.topLeft.x - mapViewArea.topLeft.x) * scaledTileSize;
		destScreenRect.top = screenOffset.y + (worldArea.topLeft.y - mapViewArea.topLeft.y) * scaledTileSize;
		destScreenRect.right = destScreenRect.left + worldArea.size.width * scaledTileSize;
		destScreenRect.bottom = destScreenRect.top + worldArea.size.height * scaledTileSize;
	}
	
	private void doDrawRect(Canvas canvas, CoordRect area) {
		
    	drawMapLayer(canvas, area, currentTileMap.layers[LayeredTileMap.LAYER_GROUND]);
        tryDrawMapLayer(canvas, area, currentTileMap, LayeredTileMap.LAYER_OBJECTS);
        
        for (BloodSplatter splatter : currentMap.splatters) {
    		drawFromMapPosition(canvas, area, splatter.position, splatter.iconID);
        }
        
        for (Loot l : currentMap.groundBags) {
        	if (l.isVisible) {
        		drawFromMapPosition(canvas, area, l.position, TileManager.iconID_groundbag);
        	}
		}
        
		drawFromMapPosition(canvas, area, playerPosition, model.player.actorTraits.iconID);
		for (MonsterSpawnArea a : currentMap.spawnAreas) {
			for (Monster m : a.monsters) {
				drawFromMapPosition(canvas, area, m.rectPosition, m.actorTraits.iconID);
			}
		}
		
		tryDrawMapLayer(canvas, area, currentTileMap, LayeredTileMap.LAYER_ABOVE);
        
		if (model.uiSelections.selectedPosition != null) {
			if (model.uiSelections.selectedMonster != null) {
				drawFromMapPosition(canvas, area, model.uiSelections.selectedPosition, TileManager.iconID_attackselect);
			} else {
				drawFromMapPosition(canvas, area, model.uiSelections.selectedPosition, TileManager.iconID_moveselect);
			}
		}
    }
    
	private void tryDrawMapLayer(Canvas canvas, final CoordRect area, final LayeredTileMap currentTileMap, final int layerIndex) {
    	if (currentTileMap.layers.length > layerIndex) drawMapLayer(canvas, area, currentTileMap.layers[layerIndex]);        
    }
    
    private void drawMapLayer(Canvas canvas, final CoordRect area, final MapLayer layer) {
    	int my = area.topLeft.y;
    	int py = (area.topLeft.y - mapViewArea.topLeft.y) * tileSize;
    	int px0 = (area.topLeft.x - mapViewArea.topLeft.x) * tileSize;
		for (int y = 0; y < area.size.height; ++y, ++my, py += tileSize) {
        	int mx = area.topLeft.x;
        	int px = px0;
        	for (int x = 0; x < area.size.width; ++x, ++mx, px += tileSize) {
        		final int tile = layer.tiles[mx][my];
        		if (tile == 0) continue;
    			tiles.drawTile(canvas, tile, px, py, mPaint);
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
			tiles.drawTile(canvas, tile, x * tileSize, y * tileSize, mPaint);
		}
    }
	
	private void drawEffectText(Canvas canvas, final CoordRect area, final VisualEffectAnimation e, int textYOffset, Paint textPaint) {
    	int x = (e.position.x - mapViewArea.topLeft.x) * tileSize + tileSize/2;
    	int y = (e.position.y - mapViewArea.topLeft.y) * tileSize + tileSize/2 + textYOffset;
		canvas.drawText(e.displayText, x, y, textPaint);
    }
    
	public void notifyMapChanged(ModelContainer model) {
		synchronized (holder) {
			currentMap = model.currentMap;
			currentTileMap = model.currentTileMap;
			tiles = world.tileManager.currentMapTiles;
			
			Size visibleNumberOfTiles = new Size(
					Math.min(screenSizeTileCount.width, currentMap.size.width)
					,Math.min(screenSizeTileCount.height, currentMap.size.height)
				);
			mapViewArea = new CoordRect(mapTopLeft, visibleNumberOfTiles);

			screenOffset.set(
					(surfaceSize.width - scaledTileSize * visibleNumberOfTiles.width) / 2
					,(surfaceSize.height - scaledTileSize * visibleNumberOfTiles.height) / 2
				);

			currentTileMap.setColorFilter(this.mPaint);
		}
		
		clearCanvas();
	    
		recalculateMapTopLeft(model.player.position);
		redrawAll(REDRAW_ALL_MAP_CHANGED);
	}

	private void recalculateMapTopLeft(Coord playerPosition) {
		synchronized (holder) {	
			this.playerPosition.set(playerPosition);
			mapTopLeft.set(0, 0);
			
	    	if (currentMap.size.width > screenSizeTileCount.width) {
	    		mapTopLeft.x = Math.max(0, playerPosition.x - mapViewArea.size.width/2);
	    		mapTopLeft.x = Math.min(mapTopLeft.x, currentMap.size.width - mapViewArea.size.width);
	    	}
	    	if (currentMap.size.height > screenSizeTileCount.height) {
	    		mapTopLeft.y = Math.max(0, playerPosition.y - mapViewArea.size.height/2);
	    		mapTopLeft.y = Math.min(mapTopLeft.y, currentMap.size.height - mapViewArea.size.height);
	    	}
		}
	}
	
	public void notifyPlayerMoved(Coord newPosition) {
		recalculateMapTopLeft(newPosition);
		redrawAll(REDRAW_ALL_PLAYER_MOVED);
	}
}
