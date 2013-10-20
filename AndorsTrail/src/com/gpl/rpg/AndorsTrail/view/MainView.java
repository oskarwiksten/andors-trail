package com.gpl.rpg.AndorsTrail.view;

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
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.InputController;
import com.gpl.rpg.AndorsTrail.controller.VisualEffectController.BloodSplatter;
import com.gpl.rpg.AndorsTrail.controller.VisualEffectController.VisualEffectAnimation;
import com.gpl.rpg.AndorsTrail.controller.listeners.*;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail.model.map.MapLayer;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCollection;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class MainView extends SurfaceView
	implements SurfaceHolder.Callback,
		PlayerMovementListener,
		CombatSelectionListener,
		MonsterSpawnListener,
		MonsterMovementListener,
		MapLayoutListener,
		VisualEffectFrameListener,
		GameRoundListener {

	private final int tileSize;
	private float scale;
	private int scaledTileSize;

	private Size screenSizeTileCount = null;
	private final Coord screenOffset = new Coord(); // pixel offset where the image begins
	private final Coord mapTopLeft = new Coord(); // Map coords of visible map
	private CoordRect mapViewArea; // Area in mapcoordinates containing the visible map. topleft == this.topleft

	private final ModelContainer model;
	private final WorldContext world;
	private final ControllerContext controllers;
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
	private boolean redrawNextTick = false;

	public MainView(Context context, AttributeSet attr) {
		super(context, attr);
		this.holder = getHolder();

		AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
		this.controllers = app.getControllerContext();
		this.world = app.getWorld();
		this.model = world.model;
		this.tileSize = world.tileManager.tileSize;
		this.inputController = controllers.inputController;
		this.preferences = app.getPreferences();

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
	public void surfaceChanged(SurfaceHolder sh, int format, int w, int h) {
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
			onPlayerEnteredNewMap(model.currentMap, model.player.position);
		} else {
			redrawAll(RedrawAllDebugReason.SurfaceChanged);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder sh) {
		hasSurface = true;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder sh) {
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
			inputController.onTouchCancel();
			break;
		}
		return super.onTouchEvent(event);
	}

	private boolean canAcceptInput() {
		if (!model.uiSelections.isMainActivityVisible) return false;
		if (!hasSurface) return false;
		return true;
	}

	private static enum RedrawAllDebugReason {
		SurfaceChanged, MapChanged, PlayerMoved
	}
	private static enum RedrawAreaDebugReason {
		MonsterMoved, MonsterKilled, EffectCompleted
	}
	private static enum RedrawTileDebugReason {
		SelectionRemoved, SelectionAdded, Bag
	}

	private void redrawAll(RedrawAllDebugReason why) {
		redrawArea_(mapViewArea, null, 0, 0);
	}
	private void redrawTile(final Coord p, RedrawTileDebugReason why) {
		p1x1.topLeft.set(p);
		redrawArea_(p1x1, null, 0, 0);
	}
	private void redrawArea(final CoordRect area, RedrawAreaDebugReason why) {
		redrawArea_(area, null, 0, 0);
	}
	private void redrawArea_(CoordRect area, final VisualEffectAnimation effect, int tileID, int textYOffset) {
		if (!hasSurface) return;

		if (currentMap.isOutside(area)) return;
		if (!mapViewArea.intersects(area)) return;

		calculateRedrawRect(area);
		Canvas c = null;
		try {
			c = holder.lockCanvas(redrawRect);
			// lockCanvas sometimes changes redrawRect, when the double-buffer has not been
			// sufficiently filled beforehand. In those cases, we need to redraw the whole scene.
			if (area != mapViewArea) {
				if (isRedrawRectWholeScreen(redrawRect)) {
					area = mapViewArea;
				}
			}
			synchronized (holder) { synchronized (tiles) {
				c.translate(screenOffset.x, screenOffset.y);
				c.scale(scale, scale);
				doDrawRect(c, area);
				if (effect != null) {
					drawFromMapPosition(c, area, effect.position, tileID);
					if (effect.displayText != null) {
						drawEffectText(c, area, effect, textYOffset, effect.textPaint);
					}
				}
			} }
		} finally {
			if (c != null) holder.unlockCanvasAndPost(c);
		}
	}

	private boolean isRedrawRectWholeScreen(Rect redrawRect) {
		if (redrawRect.width() < mapViewArea.size.width * scaledTileSize) return false;
		if (redrawRect.height() < mapViewArea.size.height * scaledTileSize) return false;
		return true;
	}

	private boolean shouldRedrawEverythingForVisualEffect() {
		if (preferences.optimizedDrawing) return false;
		if (model.uiSelections.isInCombat) return false; // Discard the "optimized drawing" setting while in combat.
		return true;
	}
	private final Rect redrawRect = new Rect();
	private void redrawAreaWithEffect(final VisualEffectAnimation effect, int tileID, int textYOffset) {
		CoordRect area = effect.area;
		if (shouldRedrawEverythingForVisualEffect()) area = mapViewArea;
		redrawArea_(area, effect, tileID, textYOffset);
	}
	private void clearCanvas() {
		if (!hasSurface) return;
		Canvas c = null;
		try {
			c = holder.lockCanvas();
			synchronized (holder) {
				c.drawColor(Color.BLACK);
			}
		} finally {
			if (c != null) holder.unlockCanvasAndPost(c);
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

		drawMapLayer(canvas, area, currentTileMap.currentLayout.layerGround);
		tryDrawMapLayer(canvas, area, currentTileMap.currentLayout.layerObjects);

		for (BloodSplatter splatter : currentMap.splatters) {
			drawFromMapPosition(canvas, area, splatter.position, splatter.iconID);
		}

		for (Loot l : currentMap.groundBags) {
			if (l.isVisible) {
				drawFromMapPosition(canvas, area, l.position, TileManager.iconID_groundbag);
			}
		}

		drawFromMapPosition(canvas, area, playerPosition, model.player.iconID);
		for (MonsterSpawnArea a : currentMap.spawnAreas) {
			for (Monster m : a.monsters) {
				drawFromMapPosition(canvas, area, m.rectPosition, m.iconID);
			}
		}

		tryDrawMapLayer(canvas, area, currentTileMap.currentLayout.layerAbove);

		if (model.uiSelections.selectedPosition != null) {
			if (model.uiSelections.selectedMonster != null) {
				drawFromMapPosition(canvas, area, model.uiSelections.selectedPosition, TileManager.iconID_attackselect);
			} else {
				drawFromMapPosition(canvas, area, model.uiSelections.selectedPosition, TileManager.iconID_moveselect);
			}
		}
	}

	private void tryDrawMapLayer(Canvas canvas, final CoordRect area, final MapLayer layer) {
		if (layer != null) drawMapLayer(canvas, area, layer);
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

	@Override
	public void onPlayerEnteredNewMap(PredefinedMap map, Coord p) {
		synchronized (holder) {
			currentMap = map;
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
		redrawAll(RedrawAllDebugReason.MapChanged);
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

	@Override
	public void onPlayerMoved(Coord newPosition, Coord previousPosition) {
		recalculateMapTopLeft(newPosition);
		redrawAll(RedrawAllDebugReason.PlayerMoved);
	}

	public void subscribe() {
		controllers.gameRoundController.gameRoundListeners.add(this);
		controllers.effectController.visualEffectFrameListeners.add(this);
		controllers.mapController.mapLayoutListeners.add(this);
		controllers.movementController.playerMovementListeners.add(this);
		controllers.combatController.combatSelectionListeners.add(this);
		controllers.monsterSpawnController.monsterSpawnListeners.add(this);
		controllers.monsterMovementController.monsterMovementListeners.add(this);
	}
	public void unsubscribe() {
		controllers.monsterMovementController.monsterMovementListeners.remove(this);
		controllers.monsterSpawnController.monsterSpawnListeners.remove(this);
		controllers.combatController.combatSelectionListeners.remove(this);
		controllers.movementController.playerMovementListeners.remove(this);
		controllers.mapController.mapLayoutListeners.remove(this);
		controllers.effectController.visualEffectFrameListeners.remove(this);
		controllers.gameRoundController.gameRoundListeners.remove(this);
	}

	@Override
	public void onMonsterSelected(Monster m, Coord selectedPosition, Coord previousSelection) {
		if (previousSelection != null) redrawTile(previousSelection, RedrawTileDebugReason.SelectionRemoved);
		redrawTile(selectedPosition, RedrawTileDebugReason.SelectionAdded);
	}

	@Override
	public void onMovementDestinationSelected(Coord selectedPosition, Coord previousSelection) {
		if (previousSelection != null) redrawTile(previousSelection, RedrawTileDebugReason.SelectionRemoved);
		redrawTile(selectedPosition, RedrawTileDebugReason.SelectionAdded);
	}

	@Override
	public void onCombatSelectionCleared(Coord previousSelection) {
		redrawTile(previousSelection, RedrawTileDebugReason.SelectionRemoved);
	}

	@Override
	public void onMonsterSpawned(PredefinedMap map, Monster m) {
		if (map != currentMap) return;
		if (!mapViewArea.intersects(m.rectPosition)) return;
		redrawNextTick = true;
	}

	@Override
	public void onMonsterRemoved(PredefinedMap map, Monster m, CoordRect previousPosition) {
		if (map != currentMap) return;
		redrawArea(previousPosition, RedrawAreaDebugReason.MonsterKilled);
	}

	@Override
	public void onMonsterSteppedOnPlayer(Monster m) {
	}

	@Override
	public void onMonsterMoved(PredefinedMap map, Monster m, CoordRect previousPosition) {
		if (map != currentMap) return;
		if (!mapViewArea.intersects(m.rectPosition) && !mapViewArea.intersects(previousPosition)) return;
		if (model.uiSelections.isInCombat) {
			redrawArea(previousPosition, RedrawAreaDebugReason.MonsterMoved);
			redrawArea(m.rectPosition, RedrawAreaDebugReason.MonsterMoved);
		} else {
			redrawNextTick = true;
		}
	}

	@Override
	public void onSplatterAdded(PredefinedMap map, Coord p) {
		if (map != currentMap) return;
		if (!mapViewArea.contains(p)) return;
		redrawNextTick = true;
	}

	@Override
	public void onSplatterChanged(PredefinedMap map, Coord p) {
		if (map != currentMap) return;
		if (!mapViewArea.contains(p)) return;
		redrawNextTick = true;
	}

	@Override
	public void onSplatterRemoved(PredefinedMap map, Coord p) {
		if (map != currentMap) return;
		if (!mapViewArea.contains(p)) return;
		redrawNextTick = true;
	}

	@Override
	public void onLootBagCreated(PredefinedMap map, Coord p) {
		if (map != currentMap) return;
		redrawTile(p, RedrawTileDebugReason.Bag);
	}

	@Override
	public void onLootBagRemoved(PredefinedMap map, Coord p) {
		if (map != currentMap) return;
		redrawTile(p, RedrawTileDebugReason.Bag);
	}

	@Override
	public void onMapTilesChanged(PredefinedMap map, LayeredTileMap tileMap) {
		if (map != currentMap) return;
		redrawAll(RedrawAllDebugReason.MapChanged);
	}

	@Override
	public void onNewAnimationFrame(VisualEffectAnimation animation, int tileID, int textYOffset) {
		redrawAreaWithEffect(animation, tileID, textYOffset);
	}

	@Override
	public void onAnimationCompleted(VisualEffectAnimation animation) {
		redrawArea(animation.area, RedrawAreaDebugReason.EffectCompleted);
	}

	@Override
	public void onNewTick() {
		if (!redrawNextTick) return;

		redrawAll(RedrawAllDebugReason.PlayerMoved);

		redrawNextTick = false;
	}

	@Override
	public void onNewRound() { }

	@Override
	public void onNewFullRound() { }
}
