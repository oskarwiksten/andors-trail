package com.gpl.rpg.AndorsTrail.util;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;

import android.util.Log;

public final class L {
	private static final String TAG = "AndorsTrail";

	public static void log(String s) {
		if (AndorsTrailApplication.DEVELOPMENT_DEBUGMESSAGES) {
			Log.d(TAG, s);
		}
	}
}
