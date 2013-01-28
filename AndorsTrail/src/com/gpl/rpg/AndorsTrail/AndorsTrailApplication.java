package com.gpl.rpg.AndorsTrail;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

public final class AndorsTrailApplication extends Application {

	public static final boolean DEVELOPMENT_DEBUGRESOURCES = false;
	public static final boolean DEVELOPMENT_FORCE_STARTNEWGAME = false;
	public static final boolean DEVELOPMENT_FORCE_CONTINUEGAME = false;
	public static final boolean DEVELOPMENT_DEBUGBUTTONS = false;
	public static final boolean DEVELOPMENT_VALIDATEDATA = true;
	public static final boolean DEVELOPMENT_DEBUGMESSAGES = true;
	public static final boolean DEVELOPMENT_INCOMPATIBLE_SAVEGAMES = DEVELOPMENT_DEBUGRESOURCES;
	public static final int CURRENT_VERSION = DEVELOPMENT_INCOMPATIBLE_SAVEGAMES ? 999 : 34;
	public static final String CURRENT_VERSION_DISPLAY = "0.7.0dev";
	
	private final AndorsTrailPreferences preferences = new AndorsTrailPreferences();
	private final WorldContext world = new WorldContext();
	private final ViewContext view = new ViewContext(this, world);
	private final WorldSetup setup = new WorldSetup(world, view, this);
	public WorldContext getWorld() { return world; }
	public WorldSetup getWorldSetup() { return setup; }
	public AndorsTrailPreferences getPreferences() { return preferences; }
	public ViewContext getViewContext() { return view; }
	
	public static AndorsTrailApplication getApplicationFromActivity(Activity activity) {
		return ((AndorsTrailApplication) activity.getApplication());
	}
	public static AndorsTrailApplication getApplicationFromActivityContext(Context context) {
		return getApplicationFromActivity(getActivityFromActivityContext(context)); 
	}
	public static Activity getActivityFromActivityContext(Context context) {
		return (Activity) context;
	}
	
	public boolean isInitialized() { return world.model != null; }
	
	public void setWindowParameters(Activity activity) { 
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (preferences.fullscreen) {
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			activity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
}
