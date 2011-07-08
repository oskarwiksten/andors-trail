package com.gpl.rpg.AndorsTrail;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AndorsTrailPreferences {
	public static final int DISPLAYLOOT_DIALOG = 0;
	public static final int DISPLAYLOOT_TOAST = 1;
	public static final int DISPLAYLOOT_NONE = 2;
	public static final int MOVEMENTMETHOD_STRAIGHT = 0;
	public static final int MOVEMENTMETHOD_DIRECTIONAL = 1;
	public static final int MOVEMENTAGGRESSIVENESS_NORMAL = 0;
	public static final int MOVEMENTAGGRESSIVENESS_AGGRESSIVE = 1;
	public static final int MOVEMENTAGGRESSIVENESS_DEFENSIVE = 2;
	public static final int DPAD_POSITION_DISABLED = 0;
	public static final int DPAD_POSITION_LOWER_RIGHT = 1;
	public static final int DPAD_POSITION_LOWER_LEFT = 2;
	public static final int DPAD_POSITION_LOWER_CENTER = 3;
	public static final int DPAD_POSITION_CENTER_LEFT = 4;
	public static final int DPAD_POSITION_CENTER_RIGHT = 5;
	public static final int DPAD_POSITION_UPPER_LEFT = 6;
	public static final int DPAD_POSITION_UPPER_RIGHT = 7;
	public static final int DPAD_POSITION_UPPER_CENTER = 8;
	
	public boolean confirmRest = true;
	public boolean confirmAttack = true;
	public int displayLoot = DISPLAYLOOT_DIALOG;
	public boolean fullscreen = true;
	public int attackspeed_milliseconds = 1000;
	public int movementMethod = MOVEMENTMETHOD_STRAIGHT;
	public int movementAggressiveness = MOVEMENTAGGRESSIVENESS_NORMAL;
	public float scalingFactor = 1.0f;
	public int dpadPosition;
	
	public static void read(final Context androidContext, AndorsTrailPreferences dest) {
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(androidContext);
			dest.confirmRest = prefs.getBoolean("confirm_rest", true);
			dest.confirmAttack = prefs.getBoolean("confirm_attack", true);
			dest.displayLoot = Integer.parseInt(prefs.getString("display_lootdialog", Integer.toString(DISPLAYLOOT_DIALOG)));
			dest.fullscreen = prefs.getBoolean("fullscreen", true);
			dest.attackspeed_milliseconds = Integer.parseInt(prefs.getString("attackspeed", "1000"));
			dest.movementMethod = Integer.parseInt(prefs.getString("movementmethod", Integer.toString(MOVEMENTMETHOD_STRAIGHT)));
			dest.scalingFactor = Float.parseFloat(prefs.getString("scaling_factor", "1.0f"));
			dest.dpadPosition = Integer.parseInt(prefs.getString("dpadposition", Integer.toString(DPAD_POSITION_DISABLED)));
			
			// This might be implemented as a skill in the future.
			//dest.movementAggressiveness = Integer.parseInt(prefs.getString("movementaggressiveness", Integer.toString(MOVEMENTAGGRESSIVENESS_NORMAL)));
		} catch (Exception e) {
			dest.confirmRest = true;
			dest.confirmAttack = true;
			dest.displayLoot = DISPLAYLOOT_DIALOG;
			dest.fullscreen = true;
			dest.attackspeed_milliseconds = 1000;
			dest.movementMethod = MOVEMENTMETHOD_STRAIGHT;
			dest.movementAggressiveness = MOVEMENTAGGRESSIVENESS_NORMAL;
			dest.scalingFactor = 1.0f;
			dest.dpadPosition = DPAD_POSITION_DISABLED;
		}
	}
	
	public static boolean shouldUseFullscreen(final Context androidContext) {
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(androidContext);
			return prefs.getBoolean("fullscreen", true);
		} catch (Exception e) {
		}
		return true;
	}
}
