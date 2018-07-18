package com.gpl.rpg.AndorsTrail.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.gpl.rpg.AndorsTrail.R;

public final class Preferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
