package com.gpl.rpg.AndorsTrail;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.Controller;
import com.gpl.rpg.AndorsTrail.controller.MovementController;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.resource.ResourceLoader;
import com.gpl.rpg.AndorsTrail.util.L;

public final class WorldSetup {
	
	private final WorldContext world;
	private final WeakReference<Context> androidContext;
	private boolean isResourcesInitialized = false;
	private boolean isInitializingResources = false;
	private WeakReference<OnSceneLoadedListener> listener;

	public boolean createNewCharacter = false;
	public boolean isSceneReady = false;
	public String newHeroName;
	
	public WorldSetup(WorldContext world, Context androidContext) {
		this.world = world;
		this.androidContext = new WeakReference<Context>(androidContext);
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
					if (listener == null) return; // sceneloader will be fired by next caller.
				}
				startSceneLoader();
			}
        }).execute();
	}
	
	public void startCharacterSetup(final OnSceneLoadedListener listener) {
		synchronized (WorldSetup.this) {
			this.listener = new WeakReference<OnSceneLoadedListener>(listener);
			if (!isResourcesInitialized) return; // sceneloader will be fired by the resourceloader.
		}
		startSceneLoader();
	}
	
	private void startSceneLoader() {
		isSceneReady = false;
		(new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				if (createNewCharacter) {
					createNewWorld();
				} else {
					continueWorld();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				isSceneReady = true;
				assert(listener != null);
				OnSceneLoadedListener o = listener.get();
				listener = null;
				if (o == null) return;
				o.onSceneLoaded();
			}
        }).execute();
	}
	
	private void continueWorld() {
		loadWorld(world, androidContext.get());
	}
	
	private void createNewWorld() {
		if (world.model != null) world.reset();
		world.model = new ModelContainer();
		world.model.player.initializeNewPlayer(world.itemTypes, world.dropLists, newHeroName);
		Controller.playerRested(world, true);
		MovementController.respawnPlayer(world);
	}


    public interface OnSceneLoadedListener {
    	void onSceneLoaded();
    }
    
    private static final String FILENAME_SAVEGAME = "savegame";
    public static void saveWorld(WorldContext world, Context androidContext) {
    	try {
	    	FileOutputStream fos = androidContext.openFileOutput(FILENAME_SAVEGAME, Context.MODE_PRIVATE);
	    	DataOutputStream dest = new DataOutputStream(fos);
	    	final int flags = 0;
	    	dest.writeInt(AndorsTrailApplication.CURRENT_VERSION);
	    	world.maps.writeToParcel(dest, flags);
	    	world.model.writeToParcel(dest, flags);
	    	dest.close();
	    	fos.close();
    	} catch (IOException e) {
    		L.log("Error saving world: " + e.toString());
    	}
    }
    private static void loadWorld(WorldContext world, Context androidContext) {
    	try {
	    	FileInputStream fos = androidContext.openFileInput(FILENAME_SAVEGAME);
	    	DataInputStream src = new DataInputStream(fos);
	    	int fileversion = src.readInt();
	    	if (fileversion == 11) fileversion = 5;
	    	world.maps.readFromParcel(src, world, fileversion);
	    	world.model = new ModelContainer(src, world, fileversion);
	    	src.close();
	    	fos.close();
    	} catch (IOException e) {
    		L.log("Error loading world: " + e.toString());
    	}
    }
}
