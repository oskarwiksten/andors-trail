package com.gpl.rpg.AndorsTrail;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AndorsTrailPreferences {
	public boolean confirmRest = true;
	public boolean confirmAttack = true;
	public int displayLoot = DISPLAYLOOT_DIALOG;
	public static final int DISPLAYLOOT_DIALOG = 0;
	public static final int DISPLAYLOOT_TOAST = 1;
	public static final int DISPLAYLOOT_NONE = 2;
	public boolean fullscreen = true;
	public int attackspeed_milliseconds = 1000;
	public int movementMethod = MOVEMENTMETHOD_STRAIGHT;
	public static final int MOVEMENTMETHOD_STRAIGHT = 0;
	public static final int MOVEMENTMETHOD_DIRECTIONAL = 1;
	
	public static void read(final Context androidContext, AndorsTrailPreferences dest) {
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(androidContext);
			dest.confirmRest = prefs.getBoolean("confirm_rest", true);
			dest.confirmAttack = prefs.getBoolean("confirm_attack", true);
			dest.displayLoot = Integer.parseInt(prefs.getString("display_lootdialog", Integer.toString(DISPLAYLOOT_DIALOG)));
			dest.fullscreen = prefs.getBoolean("fullscreen", true);
			dest.attackspeed_milliseconds = Integer.parseInt(prefs.getString("attackspeed", "1000"));
			dest.movementMethod = Integer.parseInt(prefs.getString("movementmethod", Integer.toString(MOVEMENTMETHOD_STRAIGHT)));
		} catch (Exception e) {
			dest.confirmRest = true;
			dest.confirmAttack = true;
			dest.displayLoot = DISPLAYLOOT_DIALOG;
			dest.fullscreen = true;
			dest.attackspeed_milliseconds = 1000;
			dest.movementMethod = MOVEMENTMETHOD_STRAIGHT;
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
