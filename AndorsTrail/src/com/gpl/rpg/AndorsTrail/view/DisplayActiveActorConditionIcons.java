package com.gpl.rpg.AndorsTrail.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.listeners.ActorConditionListener;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public final class DisplayActiveActorConditionIcons implements ActorConditionListener {

	private final AndorsTrailPreferences preferences;
	private final TileManager tileManager;
	private final ControllerContext controllers;
	private final WorldContext world;
	private final RelativeLayout activeConditions;
	private final ArrayList<ActiveConditionIcon> currentConditionIcons = new ArrayList<ActiveConditionIcon>();
	private final WeakReference<Context> androidContext;

	public DisplayActiveActorConditionIcons(
			final ControllerContext controllers,
			final WorldContext world,
			Context androidContext,
			RelativeLayout activeConditions) {
		this.controllers = controllers;
		this.world = world;
		this.preferences = controllers.preferences;
		this.tileManager = world.tileManager;
		this.androidContext = new WeakReference<Context>(androidContext);
		this.activeConditions = activeConditions;
	}

	@Override
	public void onActorConditionAdded(Actor actor, ActorCondition condition) {
		if (actor != world.model.player) return;
		ActiveConditionIcon icon = getFirstFreeIcon();
		icon.setActiveCondition(condition);
		icon.show();
	}

	@Override
	public void onActorConditionRemoved(Actor actor, ActorCondition condition) {
		if (actor != world.model.player) return;
		ActiveConditionIcon icon = getIconFor(condition);
		if (icon == null) return;
		icon.hide(true);
	}

	@Override
	public void onActorConditionDurationChanged(Actor actor, ActorCondition condition) {
	}

	@Override
	public void onActorConditionMagnitudeChanged(Actor actor, ActorCondition condition) {
		if (actor != world.model.player) return;
		ActiveConditionIcon icon = getIconFor(condition);
		if (icon == null) return;
		icon.setIconText();
	}

	@Override
	public void onActorConditionRoundEffectApplied(Actor actor, ActorCondition condition) {
		if (actor != world.model.player) return;
		ActiveConditionIcon icon = getIconFor(condition);
		if (icon == null) return;
		icon.pulseAnimate();
	}

	public void unsubscribe() {
		controllers.actorStatsController.actorConditionListeners.remove(this);
		for (ActiveConditionIcon icon : currentConditionIcons) icon.condition = null;
	}

	public void subscribe() {
		for (ActiveConditionIcon icon : currentConditionIcons) icon.hide(false);
		for (ActorCondition condition : world.model.player.conditions) {
			getFirstFreeIcon().setActiveCondition(condition);
		}
		controllers.actorStatsController.actorConditionListeners.add(this);
	}

	private final class ActiveConditionIcon implements AnimationListener {
		public final int id;
		public ActorCondition condition;
		public final ImageView image;
		public final TextView text;
		private final Animation onNewIconAnimation;
		private final Animation onRemovedIconAnimation;
		private final Animation onAppliedEffectAnimation;

		public ActiveConditionIcon(Context context, int id) {
			this.id = id;
			this.image = new ImageView(context);
			this.image.setId(id);
			this.text = new TextView(context);
			this.onNewIconAnimation = AnimationUtils.loadAnimation(context, R.anim.scaleup);
			this.onRemovedIconAnimation = AnimationUtils.loadAnimation(context, R.anim.scaledown);
			this.onAppliedEffectAnimation = AnimationUtils.loadAnimation(context, R.anim.scalebeat);
			this.onRemovedIconAnimation.setAnimationListener(this);

			final Resources res = context.getResources();

			text.setTextColor(res.getColor(android.R.color.white));
			text.setShadowLayer(1, 1, 1, res.getColor(android.R.color.black));
		}

		private void setActiveCondition(ActorCondition condition) {
			this.condition = condition;
			tileManager.setImageViewTile(image, condition.conditionType);
			image.setVisibility(View.VISIBLE);
			setIconText();
		}

		public void setIconText() {
			boolean showMagnitude = (condition.magnitude != 1);
			if (showMagnitude) {
				text.setText(Integer.toString(condition.magnitude));
				text.setVisibility(View.VISIBLE);
			} else {
				text.setVisibility(View.GONE);
			}
		}

		public void hide(boolean useAnimation) {
			if (useAnimation) {
				if (preferences.enableUiAnimations) {
					image.startAnimation(onRemovedIconAnimation);
				} else {
					onAnimationEnd(onRemovedIconAnimation);
				}
			} else {
				image.setVisibility(View.GONE);
				condition = null;
			}
			text.setVisibility(View.GONE);
		}
		public void show() {
			if (!preferences.enableUiAnimations) return;
			image.startAnimation(onNewIconAnimation);
			if (text.getVisibility() == View.VISIBLE) text.startAnimation(onNewIconAnimation);
		}

		public void pulseAnimate() {
			if (!preferences.enableUiAnimations) return;
			image.startAnimation(onAppliedEffectAnimation);
		}

		public boolean isVisible() {
			return condition != null;
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (animation == this.onRemovedIconAnimation) {
				hide(false);
				rearrangeIconsLeftOf(this);
			}
		}

		@Override public void onAnimationRepeat(Animation animation) { }
		@Override public void onAnimationStart(Animation animation) { }
	}

	private void rearrangeIconsLeftOf(ActiveConditionIcon icon) {
		int i = currentConditionIcons.indexOf(icon);
		currentConditionIcons.remove(i);
		currentConditionIcons.add(icon);
		for(; i < currentConditionIcons.size(); ++i) {
			currentConditionIcons.get(i).image.setLayoutParams(getLayoutParamsForIconIndex(i));
		}
	}

	private ActiveConditionIcon getIconFor(ActorCondition condition) {
		for (ActiveConditionIcon icon : currentConditionIcons) {
			if (icon.condition == condition) return icon;
		}
		return null;
	}
	private ActiveConditionIcon getFirstFreeIcon() {
		for (ActiveConditionIcon icon : currentConditionIcons) {
			if (!icon.isVisible()) return icon;
		}
		return addNewActiveConditionIcon();
	}

	private RelativeLayout.LayoutParams getLayoutParamsForIconIndex(int index) {
		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		if (index == 0) {
			layout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		} else {
			layout.addRule(RelativeLayout.LEFT_OF, currentConditionIcons.get(index-1).id);
		}
		return layout;
	}

	private ActiveConditionIcon addNewActiveConditionIcon() {
		int index = currentConditionIcons.size();

		ActiveConditionIcon icon = new ActiveConditionIcon(androidContext.get(), index+1);

		activeConditions.addView(icon.image, getLayoutParamsForIconIndex(index));

		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layout.addRule(RelativeLayout.ALIGN_RIGHT, icon.id);
		layout.addRule(RelativeLayout.ALIGN_BOTTOM, icon.id);
		activeConditions.addView(icon.text, layout);

		currentConditionIcons.add(icon);

		return icon;
	}
}
