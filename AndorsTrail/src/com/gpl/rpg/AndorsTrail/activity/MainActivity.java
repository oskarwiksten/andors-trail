package com.gpl.rpg.AndorsTrail.activity;

import java.lang.ref.WeakReference;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.WorldSetup;
import com.gpl.rpg.AndorsTrail.WorldSetup.OnSceneLoadedListener;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.view.CombatView;
import com.gpl.rpg.AndorsTrail.view.MainView;
import com.gpl.rpg.AndorsTrail.view.StatusView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnSceneLoadedListener {

    private static final int DIALOG_LOADING = 1;
    
    public static final int INTENTREQUEST_HEROINFO = 1;
    public static final int INTENTREQUEST_MONSTERENCOUNTER = 2;
    public static final int INTENTREQUEST_ITEMINFO = 3;
    public static final int INTENTREQUEST_CONVERSATION = 4;
    public static final int INTENTREQUEST_SHOP = 5;
    public static final int INTENTREQUEST_LEVELUP = 6;   
	
    private ViewContext view;
    private WorldContext world;
    
    public MainView mainview;
    public StatusView statusview;
    public CombatView combatview;
	
	private static final int NUM_MESSAGES = 3;
	private final String[] messages = new String[NUM_MESSAGES];
	private TextView statusText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.log("onCreate");
        
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        this.world = app.world;
        
        //Debug.startMethodTracing(ICICLE_KEY);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        showDialog(DIALOG_LOADING);
        app.setup.startCharacterSetup(this);
    }
    	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case INTENTREQUEST_HEROINFO:
			statusview.update();
			combatview.updatePlayerAP(world.model.player.ap);
			break;
		case INTENTREQUEST_MONSTERENCOUNTER:
			if (resultCode == Activity.RESULT_OK) {
				view.combatController.enterCombat();
			} else {
				view.combatController.exitCombat();
			}
			break;
		case INTENTREQUEST_CONVERSATION:
			statusview.update();
			break;
		}
	}
    
    @Override
	public void onSceneLoaded() {
    	L.log("onSceneLoaded");
        
    	AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
    	this.view = new ViewContext(app, this);
    	app.currentView = new WeakReference<ViewContext>(this.view);
    	app.setup.createNewCharacter = false;
        
        setContentView(R.layout.main);
        mainview = (MainView) findViewById(R.id.main_mainview);
        statusview = (StatusView) findViewById(R.id.main_statusview);
        combatview = (CombatView) findViewById(R.id.main_combatview);
        
		statusText = (TextView) findViewById(R.id.statusview_statustext);
		statusText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearMessages();
			}
		});
		clearMessages();
		
        if (world.model.uiSelections.isInCombat) {
        	view.combatController.enterCombat();
        	view.combatController.setCombatSelection(world.model.uiSelections.selectedMonster, world.model.uiSelections.selectedPosition);
        }

        view.controller.resume();

        removeDialog(DIALOG_LOADING);
        
        if (AndorsTrailApplication.DEVELOPMENT_DEBUGBUTTONS) {
        	addDebugButtons(new DebugButton[] {
    			new DebugButton("Add monster", new OnClickListener() {
		    		@Override
					public void onClick(View arg0) {
						MonsterType type = world.monsterTypes.getMonsterType("Winged demon");
						world.model.currentMap.TEST_spawnInArea(world.model.currentMap.spawnAreas[0], type);
					}
				})
    			,new DebugButton("dmg=99", new OnClickListener() {
		    		@Override
					public void onClick(View arg0) {
		    			world.model.player.traits.damagePotential.set(99, 99);
		    			world.model.player.traits.attackChance = 200;
		    			world.model.player.traits.attackCost = 1;
		    			Toast.makeText(MainActivity.this, "DEBUG: damagePotential=99, chance=200%, cost=1", Toast.LENGTH_SHORT).show();
					}
				})
    			,new DebugButton("dmg=1", new OnClickListener() {
		    		@Override
					public void onClick(View arg0) {
		    			world.model.player.traits.damagePotential.set(1, 1);
		    			Toast.makeText(MainActivity.this, "DEBUG: damagePotential=1", Toast.LENGTH_SHORT).show();
					}
				})
    			,new DebugButton("exp+=100", new OnClickListener() {
		    		@Override
					public void onClick(View arg0) {
		    			world.model.player.addExperience(100);
		    			statusview.update();
					}
				})
    			,new DebugButton("gc+=10", new OnClickListener() {
		    		@Override
					public void onClick(View arg0) {
		    			world.model.player.inventory.gold += 10;
		    			statusview.update();
					}
				})
        	});
        }
    }
    
    private class DebugButton {
    	public final String text;
    	public final OnClickListener listener;
		public DebugButton(String text, OnClickListener listener) {
			this.text = text;
			this.listener = listener;
		}
    }
    
    private void addDebugButton(DebugButton button, int id, RelativeLayout layout) {
    	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 30);
    	if (id == 1) 
    		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    	else
    		lp.addRule(RelativeLayout.RIGHT_OF, id - 1);
    	lp.addRule(RelativeLayout.ABOVE, R.id.main_statusview);
    	Button b = new Button(this);
    	b.setText(button.text);
    	b.setTextSize(getResources().getDimension(R.dimen.smalltext));
    	b.setId(id);
    	b.setOnClickListener(button.listener);
        layout.addView(b, lp);
    }
    
    private void addDebugButtons(DebugButton[] buttons) {
    	if (buttons == null || buttons.length <= 0) return;
    	RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_container);
    	
    	int id = 1;
    	for (DebugButton b : buttons) {
    		addDebugButton(b, id, layout);
    		++id;
    	}
    }
    
	@Override
    protected void onPause() {
        super.onPause();
        L.log("onPause");
        view.controller.pause();
        
        SharedPreferences p = getSharedPreferences(ModelContainer.PREFERENCE_MODEL_QUICKSAVE, MODE_PRIVATE);
        SharedPreferences.Editor e = p.edit();
        world.model.quicksave(e);
        e.commit();
        
        WorldSetup.saveWorld(world, getApplicationContext());
        /*
        Bundle b = new Bundle();
    	b.putParcelable(ModelContainer.PREFERENCE_MODEL_SAVE, world.model);
    	onSaveInstanceState(b);
    	p = getSharedPreferences(ModelContainer.PREFERENCE_MODEL_SAVE, MODE_PRIVATE);
        e = p.edit();
        world.model.save(e);
        e.commit();
        */
    	
        //Debug.stopMethodTracing();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        L.log("onResume");
        if (!AndorsTrailApplication.getApplicationFromActivity(this).setup.isSceneReady) return;

        view.controller.resume();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.exit_to_menu)
		.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
		.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				//Dialogs.showConfirmExit(MainActivity.this, view);
				MainActivity.this.finish();
				return true;
			}
		});
		menu.add(R.string.menu_pause)
		.setIcon(android.R.drawable.ic_media_pause)
		.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Dialogs.showPaused(MainActivity.this, view);
				return true;
			}
		}).setEnabled(false);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.getItem(1).setEnabled(world.model.uiSelections.isTicking);
		return super.onPrepareOptionsMenu(menu);
	}
	
	public void redrawAll(int why) {
		this.mainview.redrawAll(why);
	}
	public void redrawTile(final Coord pos, int why) {
		this.mainview.redrawTile(pos, why);
	}
	public void message(String msg) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < NUM_MESSAGES-1; ++i) {
			messages[i] = messages[i + 1];
			if (messages[i].length() > 0) {
				sb.append(messages[i]);
				sb.append('\n');
			}
		}
		messages[NUM_MESSAGES-1] = msg;
		sb.append(msg);
		statusText.setText(sb.toString());
		statusText.setVisibility(View.VISIBLE);
	}
	public void clearMessages() {
		for(int i = 0; i < NUM_MESSAGES; ++i) {
			messages[i] = "";
		}
		statusText.setVisibility(View.GONE);
	}

}
