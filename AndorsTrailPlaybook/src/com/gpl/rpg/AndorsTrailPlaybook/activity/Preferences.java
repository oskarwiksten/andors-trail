package com.gpl.rpg.AndorsTrailPlaybook.activity;

import com.gpl.rpg.AndorsTrailPlaybook.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public final class Preferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
