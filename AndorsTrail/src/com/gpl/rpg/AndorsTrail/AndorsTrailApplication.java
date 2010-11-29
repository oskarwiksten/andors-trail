package com.gpl.rpg.AndorsTrail;

import java.lang.ref.WeakReference;

import com.gpl.rpg.AndorsTrail.activity.Preferences;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

public final class AndorsTrailApplication extends Application {

	public static final boolean DEVELOPMENT_DEBUGRESOURCES = false;
	public static final boolean DEVELOPMENT_QUICKSTART = false;
	public static final boolean DEVELOPMENT_DEBUGBUTTONS = false;
	public static final boolean DEVELOPMENT_VALIDATEDATA = false;
	public static final boolean DEVELOPMENT_DEBUGMESSAGES = false;
	public static final int CURRENT_VERSION = 13;
	public static final String CURRENT_VERSION_DISPLAY = "0.6.6";
	
	public final WorldContext world = new WorldContext();
	public WorldSetup setup = new WorldSetup(world, this);
	public WeakReference<ViewContext> currentView;
	
	public static AndorsTrailApplication getApplicationFromActivity(Activity activity) {
		return ((AndorsTrailApplication) activity.getApplication());
	}
	public static AndorsTrailApplication getApplicationFromActivityContext(Context context) {
		return getApplicationFromActivity(getActivityFromActivityContext(context)); 
	}
	public static Activity getActivityFromActivityContext(Context context) {
		return (Activity) context;
	}
	
	public static void setWindowParameters(Activity activity, boolean fullscreen) {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (fullscreen) {
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			activity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
	public static void setWindowParameters(Activity activity) {
		setWindowParameters(activity, Preferences.shouldUseFullscreen(activity));
	}
}
