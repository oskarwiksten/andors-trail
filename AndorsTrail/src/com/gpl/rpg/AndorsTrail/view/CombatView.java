package com.gpl.rpg.AndorsTrail.view;

import android.R.color;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.ViewContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.CombatController;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.Range;

public final class CombatView extends RelativeLayout {
	private final TextView statusTextView;
	private final Button attackMoveButton;
	private final ImageButton monsterInfo;
	private final RangeBar monsterHealth;
	private final View monsterBar;
	private final View actionBar;
	private final TextView monsterActionText;
	
	private final WorldContext world;
	private final ViewContext view;
	private final Resources res;
	private final AndorsTrailPreferences preferences;
	private final Player player;
	private final Animation displayAnimation;
	private final Animation hideAnimation;

	private Monster currentMonster;
	
	public CombatView(final Context context, AttributeSet attr) {
		super(context, attr);
        AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
        this.world = app.world;
        this.player = world.model.player;
        this.view = app.currentView.get();
        this.preferences = app.preferences;
        this.res = getResources();

        setFocusable(false);
        inflate(context, R.layout.combatview, this);
        this.setBackgroundResource(R.drawable.ui_gradientshape_translucent);
        
        final CombatController c = view.combatController;
        attackMoveButton = (Button) findViewById(R.id.combatview_moveattack);
        attackMoveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				c.executeMoveAttack(0, 0);
			}
		});
        
        Button endTurnButton = (Button) findViewById(R.id.combatview_endturn);
        endTurnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				c.beginMonsterTurn(false);
			}
		});
        Button fleeButton = (Button) findViewById(R.id.combatview_flee);
        fleeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				c.startFlee();
			}
		});
        
        statusTextView = (TextView) findViewById(R.id.combatview_status);
        
        monsterInfo = (ImageButton) findViewById(R.id.combatview_monsterinfo);
        monsterInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Dialogs.showMonsterInfo(view.mainActivity, currentMonster);
			}
		});
        
        monsterHealth = (RangeBar) findViewById(R.id.combatview_monsterhealth);
        monsterHealth.init(R.drawable.ui_progress_health, R.string.combat_monsterhealth);
        monsterBar = findViewById(R.id.combatview_monsterbar);
        actionBar = findViewById(R.id.combatview_actionbar);
        monsterActionText = (TextView) findViewById(R.id.combatview_monsterismoving);
        
        monsterBar.setBackgroundColor(res.getColor(color.transparent));
        actionBar.setBackgroundColor(res.getColor(color.transparent));
        
        displayAnimation = AnimationUtils.loadAnimation(context, R.anim.showcombatbar);
        hideAnimation = AnimationUtils.loadAnimation(context, R.anim.hidecombatbar);
        hideAnimation.setAnimationListener(new AnimationListener() {
			@Override public void onAnimationStart(Animation animation) {}
			@Override public void onAnimationRepeat(Animation animation) {}
			@Override public void onAnimationEnd(Animation arg0) {
				CombatView.this.setVisibility(View.GONE);
			}
		});
    }
	
	public void updateTurnInfo(Monster currentActiveMonster) {
		if (currentActiveMonster != null) {
			actionBar.setVisibility(View.INVISIBLE);
			monsterActionText.setVisibility(View.VISIBLE);
			monsterActionText.setText(res.getString(R.string.combat_monsteraction, currentActiveMonster.getName()));
		} else {
			actionBar.setVisibility(View.VISIBLE);
			monsterActionText.setVisibility(View.GONE);
		}
	}

	private void updateMonsterHealth(Range range) {
	    monsterHealth.update(range);
	}
	private void updatePlayerAP(Range range) {
		statusTextView.setText(res.getString(R.string.combat_status_ap, range.current));
	}
	public void updateCombatSelection(Monster selectedMonster, Coord selectedMovePosition) {
		if (currentMonster != null && currentMonster == selectedMonster) return;
		
		attackMoveButton.setEnabled(true);
		monsterBar.setVisibility(View.INVISIBLE);
		currentMonster = null;
		if (selectedMonster != null) {
			attackMoveButton.setText(res.getString(R.string.combat_attack, player.combatTraits.attackCost));
			monsterBar.setVisibility(View.VISIBLE);
			world.tileManager.setImageViewTile(monsterInfo, selectedMonster);
			updateMonsterHealth(selectedMonster.health);
	        currentMonster = selectedMonster;
		} else if (selectedMovePosition != null) {
			attackMoveButton.setText(res.getString(R.string.combat_move, player.baseTraits.moveCost));
		} else {
			attackMoveButton.setText(res.getString(R.string.combat_attack, player.combatTraits.attackCost));
		}
	}

	public void updateStatus() {
		updatePlayerAP(player.ap);
		if (world.model.uiSelections.selectedMonster != null) {
			updateMonsterHealth(world.model.uiSelections.selectedMonster.health);
		}
		updateCombatSelection(world.model.uiSelections.selectedMonster, world.model.uiSelections.selectedPosition);
	}

	public void show() {
		setVisibility(View.VISIBLE);
    	bringToFront();
    	if (preferences.enableUiAnimations) {
    		startAnimation(displayAnimation);
    	}
	}

	public void hide() {
		if (preferences.enableUiAnimations) {
			startAnimation(hideAnimation);
		} else {
			setVisibility(View.GONE);
		}
	}
}
