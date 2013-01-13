package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.savegames.Savegames;
import com.gpl.rpg.AndorsTrail.WorldSetup;
import com.gpl.rpg.AndorsTrail.WorldSetup.OnResourcesLoadedListener;
import com.gpl.rpg.AndorsTrail.WorldSetup.OnSceneLoadedListener;

public final class LoadingActivity extends Activity implements OnResourcesLoadedListener, OnSceneLoadedListener {

    private static final int DIALOG_LOADING = 1;
    private static final int DIALOG_LOADING_FAILED = 2;
    private static final int DIALOG_LOADING_WRONGVERSION = 3;
    private WorldSetup setup;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        app.setWindowParameters(this);
        this.setup = app.getWorldSetup();
    }
	
	@Override
    public void onResume() {
		super.onResume();
		showDialog(DIALOG_LOADING);
        setup.setOnResourcesLoadedListener(this);
	}
	
	@Override
    public void onPause() {
		super.onPause();
		setup.setOnResourcesLoadedListener(null);
		setup.removeOnSceneLoadedListener(this);
	}
	
	@Override
	public void onResourcesLoaded() {
		setup.startCharacterSetup(this);
	}
	
	@Override
	public void onSceneLoaded() {
    	removeDialog(DIALOG_LOADING);
    	startActivity(new Intent(this, MainActivity.class));
    	this.finish();
	}
	
	@Override
	public void onSceneLoadFailed(int loadResult) {
    	removeDialog(DIALOG_LOADING);
    	if (loadResult == Savegames.LOAD_RESULT_FUTURE_VERSION) {
    		showDialog(DIALOG_LOADING_WRONGVERSION);	
    	} else {
    		showDialog(DIALOG_LOADING_FAILED);
    	}
	}

	private Dialog createLoadingFailedDialog(int messageResourceID) {
		Dialog d = new AlertDialog.Builder(this)
	        .setTitle(R.string.dialog_loading_failed_title)
	        .setMessage(messageResourceID)
	        .setNeutralButton(android.R.string.ok, null)
	        .create();
	    d.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				LoadingActivity.this.finish();
			}
		});
	    return d;
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
        case DIALOG_LOADING_FAILED:
        	return createLoadingFailedDialog(R.string.dialog_loading_failed_message); 
        case DIALOG_LOADING_WRONGVERSION:
        	return createLoadingFailedDialog(R.string.dialog_loading_failed_incorrectversion); 
        }
        return super.onCreateDialog(id);
    }
}
