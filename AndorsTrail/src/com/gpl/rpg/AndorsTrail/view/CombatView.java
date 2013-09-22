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
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.CombatController;
import com.gpl.rpg.AndorsTrail.controller.listeners.ActorStatsListener;
import com.gpl.rpg.AndorsTrail.controller.listeners.CombatSelectionListener;
import com.gpl.rpg.AndorsTrail.controller.listeners.CombatTurnListener;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.util.Coord;

public final class CombatView extends RelativeLayout implements CombatSelectionListener, CombatTurnListener, ActorStatsListener {
	private final TextView statusTextView;
	private final Button attackMoveButton;
	private final ImageButton monsterInfo;
	private final RangeBar monsterHealth;
	private final View monsterBar;
	private final View actionBar;
	private final TextView monsterActionText;

	private final WorldContext world;
	private final ControllerContext controllers;
	private final Resources res;
	private final AndorsTrailPreferences preferences;
	private final Player player;
	private final Animation displayAnimation;
	private final Animation hideAnimation;

	private Monster currentMonster;

	public CombatView(final Context context, AttributeSet attr) {
		super(context, attr);
		AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivityContext(context);
		this.world = app.getWorld();
		this.player = world.model.player;
		this.controllers = app.getControllerContext();
		this.preferences = app.getPreferences();
		this.res = getResources();

		setFocusable(false);
		inflate(context, R.layout.combatview, this);
		this.setBackgroundResource(R.drawable.ui_gradientshape_translucent);

		final CombatController c = controllers.combatController;
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
				c.endPlayerTurn();
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
				Dialogs.showMonsterInfo(context, currentMonster);
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

	private void updateTurnInfo(Monster currentActiveMonster) {
		if (currentActiveMonster != null) {
			actionBar.setVisibility(View.INVISIBLE);
			monsterActionText.setVisibility(View.VISIBLE);
			monsterActionText.setText(res.getString(R.string.combat_monsteraction, currentActiveMonster.getName()));
		} else {
			actionBar.setVisibility(View.VISIBLE);
			monsterActionText.setVisibility(View.GONE);
		}
	}

	private void updateMonsterHealth(Monster m) {
		monsterHealth.update(m.getMaxHP(), m.getCurrentHP());
	}
	private void updatePlayerAP() {
		statusTextView.setText(res.getString(R.string.combat_status_ap, player.getCurrentAP()));
	}
	private void updateSelectedMonster(Monster selectedMonster) {
		if (currentMonster != null && currentMonster == selectedMonster) return;

		attackMoveButton.setEnabled(true);
		monsterBar.setVisibility(View.INVISIBLE);
		currentMonster = null;
		if (selectedMonster != null) {
			monsterBar.setVisibility(View.VISIBLE);
			world.tileManager.setImageViewTile(monsterInfo, selectedMonster);
			updateMonsterHealth(selectedMonster);
			currentMonster = selectedMonster;
		}
		updateAttackMoveButtonText(selectedMonster != null);
	}

	private void updateAttackMoveButtonText() {
		updateAttackMoveButtonText(world.model.uiSelections.selectedMonster != null);
	}
	private void updateAttackMoveButtonText(boolean hasSelectedMonster) {
		if (hasSelectedMonster) {
			attackMoveButton.setText(res.getString(R.string.combat_attack, player.getAttackCost()));
		} else {
			attackMoveButton.setText(res.getString(R.string.combat_move, player.getMoveCost()));
		}
	}

	public void updateStatus() {
		updatePlayerAP();
		updateSelectedMonster(world.model.uiSelections.selectedMonster);
	}

	private void show() {
		updateStatus();
		setVisibility(View.VISIBLE);
		bringToFront();
		if (preferences.enableUiAnimations) {
			startAnimation(displayAnimation);
		}
	}

	private void hide() {
		if (preferences.enableUiAnimations) {
			startAnimation(hideAnimation);
		} else {
			setVisibility(View.GONE);
		}
	}

	public void subscribe() {
		controllers.combatController.combatSelectionListeners.add(this);
		controllers.combatController.combatTurnListeners.add(this);
		controllers.actorStatsController.actorStatsListeners.add(this);
	}
	public void unsubscribe() {
		controllers.actorStatsController.actorStatsListeners.remove(this);
		controllers.combatController.combatTurnListeners.remove(this);
		controllers.combatController.combatSelectionListeners.remove(this);
	}

	@Override
	public void onMonsterSelected(Monster m, Coord selectedPosition, Coord previousSelection) {
		updateSelectedMonster(m);
	}

	@Override
	public void onMovementDestinationSelected(Coord selectedPosition, Coord previousSelection) {
		updateSelectedMonster(null);
	}

	@Override
	public void onCombatSelectionCleared(Coord previousSelection) {
		updateSelectedMonster(null);
	}

	@Override
	public void onCombatStarted() {
		show();
		updateTurnInfo(null);
	}

	@Override
	public void onCombatEnded() {
		hide();
	}

	@Override
	public void onNewPlayerTurn() {
		updateTurnInfo(null);
	}

	@Override
	public void onMonsterIsAttacking(Monster m) {
		updateTurnInfo(m);
	}

	@Override
	public void onActorHealthChanged(Actor actor) {
		if (actor == currentMonster) updateMonsterHealth(currentMonster);
	}

	@Override
	public void onActorAPChanged(Actor actor) {
		if (actor == player) updatePlayerAP();
	}

	@Override
	public void onActorAttackCostChanged(Actor actor, int newAttackCost) {
		if (actor == player) updateAttackMoveButtonText();
	}

	@Override
	public void onActorMoveCostChanged(Actor actor, int newMoveCost) {
		if (actor == player) updateAttackMoveButtonText();
	}
}
