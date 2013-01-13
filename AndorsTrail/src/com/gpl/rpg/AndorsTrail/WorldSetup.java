package com.gpl.rpg.AndorsTrail;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.resource.ResourceLoader;

public final class WorldSetup {
	
	private final WorldContext world;
	private final ViewContext view;
	private final WeakReference<Context> androidContext;
	private boolean isResourcesInitialized = false;
	private boolean isInitializingResources = false;
	private WeakReference<OnResourcesLoadedListener> onResourcesLoadedListener;
	private WeakReference<OnSceneLoadedListener> onSceneLoadedListener;
	private Object sceneLoaderId;

	public boolean createNewCharacter = false;
	public int loadFromSlot = Savegames.SLOT_QUICKSAVE;
	public boolean isSceneReady = false;
	public String newHeroName;
	private int loadResult;
	
	public WorldSetup(WorldContext world, ViewContext view, Context androidContext) {
		this.world = world;
		this.view = view;
		this.androidContext = new WeakReference<Context>(androidContext);
	}

	public void setOnResourcesLoadedListener(OnResourcesLoadedListener listener) {
		synchronized (this) {
			onResourcesLoadedListener = null;
			if (isResourcesInitialized) {
				if (listener != null) listener.onResourcesLoaded();
				return;
			}
			onResourcesLoadedListener = new WeakReference<WorldSetup.OnResourcesLoadedListener>(listener);
		}
	}
	
	public void startResourceLoader(final Resources r) {
		if (isResourcesInitialized) return;
		
		synchronized (this) {
			if (isInitializingResources) return;
			isInitializingResources = true;
		}
		
		(new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
		        ResourceLoader.loadResources(world, r);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				synchronized (WorldSetup.this) {
					isResourcesInitialized = true;
					isInitializingResources = false;
					
					if (onResourcesLoadedListener == null) return;
					WorldSetup.OnResourcesLoadedListener listener = onResourcesLoadedListener.get();
					onResourcesLoadedListener = null;
					if (listener == null) return;
					listener.onResourcesLoaded();
				}
			}
        }).execute();
	}
	
	public void startCharacterSetup(final OnSceneLoadedListener listener) {
		synchronized (WorldSetup.this) {
			this.onSceneLoadedListener = new WeakReference<OnSceneLoadedListener>(listener);
		}
		startSceneLoader();
	}
	public void removeOnSceneLoadedListener(final OnSceneLoadedListener listener) {
		synchronized (WorldSetup.this) {
			if (this.onSceneLoadedListener == null) return;
			if (this.onSceneLoadedListener.get() == listener) this.onSceneLoadedListener = null;
		}
	}
	
	private final Object onlyOneThreadAtATimeMayLoadSavegames = new Object();
	private void startSceneLoader() {
		isSceneReady = false;
		final Object thisLoaderId = new Object();
		synchronized (WorldSetup.this) {
			sceneLoaderId = thisLoaderId;
		}
		
		(new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				synchronized (onlyOneThreadAtATimeMayLoadSavegames) {
					if (world.model != null) world.reset();
					if (createNewCharacter) {
						createNewWorld();
						loadResult = Savegames.LOAD_RESULT_SUCCESS;
					} else {
						loadResult = continueWorld();
					}
					createNewCharacter = false;
				}
		    	return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				synchronized (WorldSetup.this) {
					if (sceneLoaderId != thisLoaderId) return; // Some other thread has started after we started.
					isSceneReady = true;
					
					if (onSceneLoadedListener == null) return;
					OnSceneLoadedListener o = onSceneLoadedListener.get();
					onSceneLoadedListener = null;
					if (o == null) return;
					
					if (loadResult == Savegames.LOAD_RESULT_SUCCESS) {
						o.onSceneLoaded();
					} else {
						o.onSceneLoadFailed(loadResult);
					}
				}
			}
        }).execute();
	}
	
	private int continueWorld() {
		Context ctx = androidContext.get();
		int result = Savegames.loadWorld(world, view, ctx, loadFromSlot);
    	if (result == Savegames.LOAD_RESULT_SUCCESS) {
			view.movementController.cacheCurrentMapData(ctx.getResources(), world.model.currentMap);
		}
		return result;
	}
	
	private void createNewWorld() {
		Context ctx = androidContext.get();
		world.model = new ModelContainer();
		world.model.player.initializeNewPlayer(world.itemTypes, world.dropLists, newHeroName);
		
		view.actorStatsController.recalculatePlayerStats(world.model.player);
		view.movementController.respawnPlayer(ctx.getResources());
		view.controller.lotsOfTimePassed();
	}


    public interface OnSceneLoadedListener {
    	void onSceneLoaded();
    	void onSceneLoadFailed(int loadResult);
    }
    public interface OnResourcesLoadedListener {
    	void onResourcesLoaded();
    }
}
