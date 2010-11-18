package com.gpl.rpg.AndorsTrail;

import java.lang.ref.WeakReference;

import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public final class AndorsTrailApplication extends Application {

	public static final boolean DEVELOPMENT_DEBUGRESOURCES = false;
	public static final boolean DEVELOPMENT_QUICKSTART = false;
	public static final boolean DEVELOPMENT_DEBUGBUTTONS = false;
	public static final boolean DEVELOPMENT_VALIDATEDATA = false;
	public static final boolean DEVELOPMENT_DEBUGMESSAGES = false;
	public static final int CURRENT_VERSION = 9;
	
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
}
