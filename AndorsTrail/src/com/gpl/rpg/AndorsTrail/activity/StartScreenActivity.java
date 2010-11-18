package com.gpl.rpg.AndorsTrail.activity;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.WorldSetup;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.Player;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StartScreenActivity extends Activity {
	private boolean hasExistingGame = false;
	private Button startscreen_continue;
	private Button startscreen_newgame;
	private TextView startscreen_currenthero;
	private EditText startscreen_enterheroname;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.startscreen);

        startscreen_currenthero = (TextView) findViewById(R.id.startscreen_currenthero);
        startscreen_enterheroname = (EditText) findViewById(R.id.startscreen_enterheroname);
        startscreen_enterheroname.setImeOptions(EditorInfo.IME_ACTION_DONE);

        startscreen_continue = (Button) findViewById(R.id.startscreen_continue);
        startscreen_continue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				continueGame(false, null);
			}
		});
        
        startscreen_newgame = (Button) findViewById(R.id.startscreen_newgame);
        startscreen_newgame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (hasExistingGame) {
					comfirmNewGame();
				} else {
					createNewGame();
				}
			}
		});
        
        Button b = (Button) findViewById(R.id.startscreen_about);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(StartScreenActivity.this, AboutActivity.class));
			}
		});
        
        b = (Button) findViewById(R.id.startscreen_quit);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//comfirmQuit();
				StartScreenActivity.this.finish();
			}
		});
        
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        app.setup.startResourceLoader(getResources());
        
        if (AndorsTrailApplication.DEVELOPMENT_QUICKSTART) {
        	continueGame(true, "Debug player");
        }
    }
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		ModelContainer model = new ModelContainer();
		hasExistingGame = model.quickload(getSharedPreferences(ModelContainer.PREFERENCE_MODEL_QUICKSAVE, MODE_PRIVATE));
        
		setButtonState(model.player);
		
		if (isNewVersion()) {
			Dialogs.showNewVersion(this);
		}
	}
	
	private boolean isNewVersion() {
		final String v = "lastversion";
		SharedPreferences s = getSharedPreferences(ModelContainer.PREFERENCE_MODEL_LASTRUNVERSION, MODE_PRIVATE);
		int lastversion = s.getInt(v, 0);
		if (lastversion >= AndorsTrailApplication.CURRENT_VERSION) return false;
		Editor e = s.edit();
		e.putInt(v, AndorsTrailApplication.CURRENT_VERSION);
		e.commit();
		return true;
	}


	private void setButtonState(final Player player) {
		startscreen_continue.setEnabled(hasExistingGame);
        startscreen_newgame.setEnabled(true);
        if (hasExistingGame) {
        	startscreen_currenthero.setText(getResources().getString(R.string.startscreen_currenthero, player.traits.name, player.level));
        	startscreen_enterheroname.setText(player.traits.name);
        	startscreen_enterheroname.setVisibility(View.GONE);
        } else {
        	startscreen_currenthero.setText(R.string.startscreen_enterheroname);
        	startscreen_enterheroname.setVisibility(View.VISIBLE);
        }
	}

	private void continueGame(boolean createNewCharacter, String name) {
		final WorldSetup setup = AndorsTrailApplication.getApplicationFromActivity(this).setup;
		setup.createNewCharacter = createNewCharacter;
		setup.newHeroName = name;
        startActivity(new Intent(this, LoadingActivity.class));
	}

	private void createNewGame() {
		String name = startscreen_enterheroname.getText().toString().trim();
		if (name == null || name.length() <= 0) {
			Toast.makeText(this, R.string.startscreen_enterheroname, Toast.LENGTH_SHORT).show();
			return;
		}
		continueGame(true, name);
	}
	
	private void comfirmNewGame() {
		new AlertDialog.Builder(this)
        .setTitle(R.string.startscreen_newgame)
        .setMessage(R.string.startscreen_newgame_confirm)
        .setIcon(android.R.drawable.ic_delete)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//continueGame(true);
				hasExistingGame = false;
				setButtonState(null);
			}
		})
        .setNegativeButton(android.R.string.cancel, null)
        .create().show(); 
	}

	/*
	private void comfirmQuit() {
		new AlertDialog.Builder(this)
        .setTitle(R.string.dialog_confirmexit_title)
        .setMessage(R.string.dialog_confirmexit_message)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				StartScreenActivity.this.finish();
			}
		})
        .setNegativeButton(android.R.string.cancel, null)
        .create().show(); 
	}
	*/
}
