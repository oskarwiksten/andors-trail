package com.gpl.rpg.AndorsTrail;

import java.lang.ref.WeakReference;

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
	public static final boolean DEVELOPMENT_VALIDATEDATA = false;
	public static final boolean DEVELOPMENT_DEBUGMESSAGES = false;
	public static final boolean DEVELOPMENT_INCOMPATIBLE_SAVEGAMES = DEVELOPMENT_DEBUGRESOURCES || false;
	public static final int CURRENT_VERSION = DEVELOPMENT_INCOMPATIBLE_SAVEGAMES ? 999 : 27;
	public static final String CURRENT_VERSION_DISPLAY = "0.6.11a1";
	
	public final WorldContext world = new WorldContext();
	public final WorldSetup setup = new WorldSetup(world, this);
	public WeakReference<ViewContext> currentView;
	public final AndorsTrailPreferences preferences = new AndorsTrailPreferences();
	
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
	
	public static void setWindowParameters(Activity activity, final AndorsTrailPreferences preferences) {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (preferences.fullscreen) {
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			activity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
}
