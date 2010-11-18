package com.gpl.rpg.AndorsTrail.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.activity.MainActivity;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.CombatController;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.Range;

public final class CombatView extends FrameLayout {
	private final TextView statusTextView;
	private final Button attackMoveButton;
	private final ImageButton monsterInfo;
	private final RangeBar monsterHealth;
	private final View monsterBar;
	private final View actionBar;
	private final TextView monsterActionText;
	
	private final WorldContext world;
	private final ViewContext view;

	private MonsterType currentMonsterType;
	public CombatView(final Context context, AttributeSet attr) {
		super(context, attr);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
        this.world = app.world;
        this.view = app.currentView.get();

        setFocusable(false);
        inflate(context, R.layout.combatview, this);
        this.setBackgroundResource(R.drawable.ui_gradientshape);
        
        final CombatController c = view.combatController;
        attackMoveButton = (Button) findViewById(R.id.combatview_moveattack);
        attackMoveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				c.executeMoveAttack();
			}
		});
        
        Button endTurnButton = (Button) findViewById(R.id.combatview_endturn);
        endTurnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				c.endPlayerTurn();
			}
		});
        Button endCombatButton = (Button) findViewById(R.id.combatview_endcombat);
        endCombatButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (c.canExitCombat()) {
					c.exitCombat(true);
				} else {
					((MainActivity) context).message(getResources().getString(R.string.combat_cannotexitcombat));
				}
			}
		});
        
        statusTextView = (TextView) findViewById(R.id.combatview_status);
        
        monsterInfo = (ImageButton) findViewById(R.id.combatview_monsterinfo);
        monsterInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Dialogs.showMonsterInfo(view.mainActivity, currentMonsterType.id);
			}
		});
        
        monsterHealth = (RangeBar) findViewById(R.id.combatview_monsterhealth);
        monsterHealth.init(R.drawable.ui_progress_health, R.string.combat_monsterhealth);
        monsterBar = findViewById(R.id.combatview_monsterbar);
        actionBar = findViewById(R.id.combatview_actionbar);
        monsterActionText = (TextView) findViewById(R.id.combatview_monsterismoving);
    }
	
	public void updateTurnInfo(Monster currentActiveMonster) {
		if (currentActiveMonster != null) {
			actionBar.setVisibility(View.INVISIBLE);
			monsterActionText.setVisibility(View.VISIBLE);
			monsterActionText.setText(getResources().getString(R.string.combat_monsteraction, currentActiveMonster.traits.name));
		} else {
			actionBar.setVisibility(View.VISIBLE);
			monsterActionText.setVisibility(View.GONE);
		}
	}

	public void updateMonsterHealth(Range range) {
	    monsterHealth.update(range);
	}
	public void updatePlayerAP(Range range) {
		statusTextView.setText(getResources().getString(R.string.combat_status_ap, range.current));
	}
	public void updateCombatSelection(Monster selectedMonster, Coord selectedMovePosition) {
		attackMoveButton.setEnabled(true);
		monsterBar.setVisibility(View.INVISIBLE);
		currentMonsterType = null;
		if (selectedMonster != null) {
			attackMoveButton.setText(getResources().getString(R.string.combat_attack, world.model.player.traits.attackCost));
			monsterBar.setVisibility(View.VISIBLE);
			monsterInfo.setImageBitmap(world.tileStore.bitmaps[selectedMonster.traits.iconID]);
	        updateMonsterHealth(selectedMonster.health);
			currentMonsterType = selectedMonster.monsterType;
		} else if (selectedMovePosition != null) {
			attackMoveButton.setText(getResources().getString(R.string.combat_move, world.model.player.traits.moveCost));
		} else {
			attackMoveButton.setEnabled(false);
		}
	}
}
