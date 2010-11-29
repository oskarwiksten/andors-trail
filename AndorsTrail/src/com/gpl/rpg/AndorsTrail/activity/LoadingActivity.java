package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.WorldSetup.OnSceneLoadedListener;
import com.gpl.rpg.AndorsTrail.util.L;

public class LoadingActivity extends Activity implements OnSceneLoadedListener {

    private static final int DIALOG_LOADING = 1;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication.setWindowParameters(this, Preferences.shouldUseFullscreen(this));
        
        L.log("LoadingActivity::onCreate");
        
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        
        showDialog(DIALOG_LOADING);
        app.setup.startCharacterSetup(this);
    }
	
	@Override
	public void onSceneLoaded() {
    	L.log("LoadingActivity::onSceneLoaded");
        
    	AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
    	app.setup.createNewCharacter = false;
    	removeDialog(DIALOG_LOADING);
    	
    	startActivity(new Intent(this, MainActivity.class));
    	this.finish();
	}

    @Override
    protected Dialog onCreateDialog(final int id) {
        switch(id) {
        case DIALOG_LOADING:
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getResources().getText(R.string.dialog_loading_message));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            return dialog;
        }
        return super.onCreateDialog(id);
    }
}
