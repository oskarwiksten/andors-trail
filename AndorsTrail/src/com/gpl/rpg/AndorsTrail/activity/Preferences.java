package com.gpl.rpg.AndorsTrail.activity;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.InterfaceData;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Preferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
	
	public static void read(final Context androidContext, InterfaceData dest) {
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(androidContext);
			dest.confirmRest = prefs.getBoolean("confirm_rest", true);
			dest.confirmAttack = prefs.getBoolean("confirm_attack", true);
			dest.displayLoot = Integer.parseInt(prefs.getString("display_lootdialog", Integer.toString(InterfaceData.DISPLAYLOOT_DIALOG)));
			dest.fullscreen = prefs.getBoolean("fullscreen", true);
		} catch (Exception e) {
			dest.confirmRest = true;
			dest.confirmAttack = true;
			dest.displayLoot = InterfaceData.DISPLAYLOOT_DIALOG;
			dest.fullscreen = true;
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
