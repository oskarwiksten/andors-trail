package com.gpl.rpg.AndorsTrail.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.WorldSetup;
import com.gpl.rpg.AndorsTrail.WorldSetup.OnResourcesLoadedListener;
import com.gpl.rpg.AndorsTrail.WorldSetup.OnSceneLoadedListener;
import com.gpl.rpg.AndorsTrail.savegames.Savegames;

public final class LoadingActivity extends Activity implements OnResourcesLoadedListener, OnSceneLoadedListener {

	private WorldSetup setup;
	private ProgressDialog progressDialog;

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
		progressDialog = ProgressDialog.show(this, null, getString(R.string.dialog_loading_message));
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
		progressDialog.dismiss();
		startActivity(new Intent(this, MainActivity.class));
		this.finish();
	}

	@Override
	public void onSceneLoadFailed(Savegames.LoadSavegameResult loadResult) {
		progressDialog.dismiss();
		if (loadResult == Savegames.LoadSavegameResult.savegameIsFromAFutureVersion) {
			showLoadingFailedDialog(R.string.dialog_loading_failed_incorrectversion);
		} else {
			showLoadingFailedDialog(R.string.dialog_loading_failed_message);
		}
	}

	private void showLoadingFailedDialog(int messageResourceID) {
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
		d.show();
	}
}
