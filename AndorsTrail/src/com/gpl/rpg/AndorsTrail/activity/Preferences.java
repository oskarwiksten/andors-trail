package com.gpl.rpg.AndorsTrail.activity;

import com.gpl.rpg.AndorsTrail.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public final class Preferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
