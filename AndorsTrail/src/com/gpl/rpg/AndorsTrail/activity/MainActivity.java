package com.gpl.rpg.AndorsTrail.activity;

import java.lang.ref.WeakReference;

import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.Savegames;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.CombatController;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer.ItemEntry;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.view.CombatView;
import com.gpl.rpg.AndorsTrail.view.MainView;
import com.gpl.rpg.AndorsTrail.view.QuickButton.QuickButtonContextMenuInfo;
import com.gpl.rpg.AndorsTrail.view.QuickitemView;
import com.gpl.rpg.AndorsTrail.view.StatusView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public final class MainActivity extends Activity {

    public static final int INTENTREQUEST_HEROINFO = 1;
    public static final int INTENTREQUEST_MONSTERENCOUNTER = 2;
    public static final int INTENTREQUEST_ITEMINFO = 3;
    public static final int INTENTREQUEST_CONVERSATION = 4;
    public static final int INTENTREQUEST_SHOP = 5;
    public static final int INTENTREQUEST_LEVELUP = 6;
    public static final int INTENTREQUEST_PREFERENCES = 7;
    public static final int INTENTREQUEST_SAVEGAME = 8;
	
    private ViewContext view;
    private WorldContext world;
    
    public MainView mainview;
    public StatusView statusview;
    public CombatView combatview;
    public QuickitemView quickitemview;
    public LinearLayout activeConditions;
	
	private static final int NUM_MESSAGES = 3;
	private final String[] messages = new String[NUM_MESSAGES];
	private TextView statusText;
	public WeakReference<Toast> lastToast = null;
	private ContextMenuInfo lastSelectedMenu = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.log("onCreate");
    	//Debug.startMethodTracing(ICICLE_KEY);
        
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
        this.world = app.world;
        this.view = new ViewContext(app, this);
    	app.currentView = new WeakReference<ViewContext>(this.view);
    	AndorsTrailApplication.setWindowParameters(this, app.preferences);
        
        setContentView(R.layout.main);
        mainview = (MainView) findViewById(R.id.main_mainview);
        statusview = (StatusView) findViewById(R.id.main_statusview);
        combatview = (CombatView) findViewById(R.id.main_combatview);
        quickitemview = (QuickitemView) findViewById(R.id.main_quickitemview);
        activeConditions = (LinearLayout) findViewById(R.id.statusview_activeconditions);
        
		statusText = (TextView) findViewById(R.id.statusview_statustext);
		statusText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearMessages();
			}
		});
		clearMessages();
		
        if (AndorsTrailApplication.DEVELOPMENT_DEBUGBUTTONS) {
        	addDebugButtons(new DebugButton[] {
    			/*new DebugButton("Add monster", new OnClickListener() {
		    		@Override
					public void onClick(View arg0) {
		    			final String name = "winged_demon";
						MonsterType type = world.monsterTypes.getMonsterType(name);
						if (type == null) {
							Toast.makeText(MainActivity.this, "Cannot find monster type \"" + name + "\", unable to spawn.", Toast.LENGTH_LONG).show();
						} else {
							world.model.currentMap.TEST_spawnInArea(world.model.currentMap.spawnAreas[0], type);
						}
					}
				})
    			,*/new DebugButton("dmg=99", new OnClickListener() {
		    		@Override
					public void onClick(View arg0) {
		    			world.model.player.traits.damagePotential.set(99, 99);
		    			world.model.player.traits.attackChance = 200;
		    			world.model.player.traits.attackCost = 1;
		    			updateStatus();
		    			Toast.makeText(MainActivity.this, "DEBUG: damagePotential=99, chance=200%, cost=1", Toast.LENGTH_SHORT).show();
					}
				})/*
    			,new DebugButton("dmg=1", new OnClickListener() {
		    		@Override
					public void onClick(View arg0) {
		    			world.model.player.traits.damagePotential.set(1, 1);
		    			updateStatus();
		    			Toast.makeText(MainActivity.this, "DEBUG: damagePotential=1", Toast.LENGTH_SHORT).show();
					}
				})*/
    			/*,new DebugButton("items", new OnClickListener() {
		    		@Override
					public void onClick(View arg0) {
		    			ItemType itemType = world.itemTypes.getItemTypeByTag("elytharan_redeemer");
		    			world.model.player.inventory.addItem(itemType);
		    			itemType = world.itemTypes.getItemTypeByTag("ring_shadow0");
		    			world.model.player.inventory.addItem(itemType);
		    			itemType = world.itemTypes.getItemTypeByTag("shadow_slayer");
		    			world.model.player.inventory.addItem(itemType);
		    			updateStatus();
		    			Toast.makeText(MainActivity.this, "DEBUG: added items", Toast.LENGTH_SHORT).show();
					}
				})
    			,new DebugButton("bwm", new OnClickListener() {
		    		@Override
					public void onClick(View arg0) {
		    			Player player = world.model.player;
		    			player.addQuestProgress(new QuestProgress("bwm_agent", 1));
		    			player.addQuestProgress(new QuestProgress("bwm_agent", 5));
		    			player.addQuestProgress(new QuestProgress("bwm_agent", 10));
		    			player.addQuestProgress(new QuestProgress("bwm_agent", 20));
		    			player.addQuestProgress(new QuestProgress("bwm_agent", 25));
		    			player.addQuestProgress(new QuestProgress("bwm_agent", 30));
		    			player.addQuestProgress(new QuestProgress("bwm_agent", 40));
		    			player.addQuestProgress(new QuestProgress("bwm_agent", 50));
		    			player.addQuestProgress(new QuestProgress("bwm_agent", 60));
		    			
	    				view.movementController.placePlayerAt(MapObject.MAPEVENT_NEWMAP, "blackwater_mountain45", "south", 0, 0);
					}
				})
    			,new DebugButton("prim", new OnClickListener() {
		    		@Override
					public void onClick(View arg0) {
		    			view.movementController.placePlayerAt(MapObject.MAPEVENT_NEWMAP, "blackwater_mountain29", "south", 0, 0);
					}
				})*/
    			,new DebugButton("hp=max", new OnClickListener() {
		    		@Override
					public void onClick(View arg0) {
		    			world.model.player.health.setMax();
		    			updateStatus();
		    			Toast.makeText(MainActivity.this, "DEBUG: hp set to max", Toast.LENGTH_SHORT).show();
					}
				})
        	});
        }
		quickitemview.setVisibility(View.GONE);
        quickitemview.registerForContextMenu(this);
    	quickitemview.refreshQuickitems();
        
    }
    	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case INTENTREQUEST_HEROINFO:
			combatview.updatePlayerAP(world.model.player.ap);
			quickitemview.refreshQuickitems();
			break;
		case INTENTREQUEST_MONSTERENCOUNTER:
			if (resultCode == Activity.RESULT_OK) {
				view.combatController.enterCombat(CombatController.BEGIN_TURN_PLAYER);
			} else {
				view.combatController.exitCombat(false);
			}
			break;
		case INTENTREQUEST_CONVERSATION:
			if (resultCode == ConversationActivity.ACTIVITYRESULT_ATTACK) {
				final Coord p = world.model.player.nextPosition;
				Monster m = world.model.currentMap.getMonsterAt(p);
				if (m == null) return; //Shouldn't happen.
				m.forceAggressive = true;
				view.combatController.setCombatSelection(m, p);
				view.combatController.enterCombat(CombatController.BEGIN_TURN_PLAYER);
			} else if (resultCode == ConversationActivity.ACTIVITYRESULT_REMOVE) {
				final Coord p = world.model.player.nextPosition;
				Monster m = world.model.currentMap.getMonsterAt(p);
				if (m == null) return;
				world.model.currentMap.remove(m);
				redrawAll(MainView.REDRAW_ALL_MONSTER_KILLED);
			}
			break;
		case INTENTREQUEST_PREFERENCES:
			AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(this);
	        AndorsTrailPreferences.read(this, app.preferences);
	        world.tileStore.updatePreferences(app.preferences);
			break;
		case INTENTREQUEST_SAVEGAME:
			if (resultCode != Activity.RESULT_OK) break;
			final int slot = data.getIntExtra("slot", 1);
			if (save(slot)) {
				Toast.makeText(this, getResources().getString(R.string.menu_save_gamesaved, slot), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, R.string.menu_save_failed, Toast.LENGTH_LONG).show();
			}
			break;
		}
	}
    
    private boolean save(int slot) {
    	final Player player = world.model.player;
    	return Savegames.saveWorld(world, this, slot, getString(R.string.savegame_currenthero_displayinfo, player.level, player.totalExperience, player.inventory.gold));
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
        view.gameRoundController.pause();
        
        save(Savegames.SLOT_QUICKSAVE);
    	
        //Debug.stopMethodTracing();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        L.log("onResume");
        if (!AndorsTrailApplication.getApplicationFromActivity(this).setup.isSceneReady) return;

        view.gameRoundController.resume();

        if (world.model.uiSelections.isInCombat) {
        	view.combatController.setCombatSelection(world.model.uiSelections.selectedMonster, world.model.uiSelections.selectedPosition);
        	view.combatController.enterCombat(CombatController.BEGIN_TURN_CONTINUE);
        }
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
		menu.add(R.string.menu_save)
		.setIcon(android.R.drawable.ic_menu_save)
		.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Dialogs.showSave(MainActivity.this, view);
				return true;
			}
		});
		menu.add(R.string.menu_settings)
		.setIcon(android.R.drawable.ic_menu_preferences)
		.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Dialogs.showPreferences(MainActivity.this);
				return true;
			}
		});
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if(quickitemview.isQuickButtonId(v.getId())){
			createQuickButtonMenu(menu, v, menuInfo);
		}
		lastSelectedMenu = null;
	}

	private void createQuickButtonMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		menu.add(Menu.NONE, R.id.quick_menu_unassign, Menu.NONE, R.string.inventory_unassign);
		SubMenu assignMenu = menu.addSubMenu(Menu.NONE, R.id.quick_menu_assign, Menu.NONE, R.string.inventory_assign);
		for(int i=0; i<world.model.player.inventory.items.size(); ++i){
			ItemEntry itemEntry = world.model.player.inventory.items.get(i);
			if(itemEntry.itemType.isUsable())
				assignMenu.add(R.id.quick_menu_assign_group, i, Menu.NONE, itemEntry.itemType.name);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		QuickButtonContextMenuInfo menuInfo;
		if(item.getGroupId() == R.id.quick_menu_assign_group){
			menuInfo = (QuickButtonContextMenuInfo) lastSelectedMenu;
			view.itemController.setQuickItem(world.model.player.inventory.items.get(item.getItemId()).itemType, menuInfo.index);
			return true;
		}
		switch(item.getItemId()){
		case R.id.quick_menu_unassign:
			menuInfo = (QuickButtonContextMenuInfo) item.getMenuInfo();
			view.itemController.setQuickItem(null, menuInfo.index);
			break;
		case R.id.quick_menu_assign:
			menuInfo = (QuickButtonContextMenuInfo) item.getMenuInfo();
			lastSelectedMenu = menuInfo;
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}
	
	public void updateStatus() {
		statusview.updateStatus();
		statusview.updateActiveConditions(this, activeConditions);
		quickitemview.refreshQuickitems();
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

	public void showToast(String msg, int duration) {
		Toast t = null;
		if (lastToast != null) t = lastToast.get();
		if (t == null) {
			t = Toast.makeText(this, msg, duration);
			lastToast = new WeakReference<Toast>(t);
		} else {
			t.setText(msg);
			t.setDuration(duration);
		}
		t.show();
	}

}
