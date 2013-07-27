package com.gpl.rpg.AndorsTrailPlaybook.activity;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gpl.rpg.AndorsTrailPlaybook.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrailPlaybook.R;
import com.gpl.rpg.AndorsTrailPlaybook.context.ControllerContext;
import com.gpl.rpg.AndorsTrailPlaybook.context.WorldContext;
import com.gpl.rpg.AndorsTrailPlaybook.model.map.PredefinedMap;

import java.lang.ref.WeakReference;

@SuppressWarnings("unused")
public final class DebugInterface {
	private final ControllerContext controllerContext;
	private final MainActivity mainActivity;
	private final Resources res;
	private final WorldContext world;

	public DebugInterface(ControllerContext controllers, WorldContext world, MainActivity mainActivity) {
		this.controllerContext = controllers;
		this.world = world;
		this.res = mainActivity.getResources();
		this.mainActivity = mainActivity;
	}

	public void addDebugButtons() {
		if (!AndorsTrailApplication.DEVELOPMENT_DEBUGBUTTONS) return;

		addDebugButtons(new DebugButton[] {
			new DebugButton("dmg", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					world.model.player.damagePotential.set(99, 99);
					world.model.player.attackChance = 200;
					world.model.player.attackCost = 1;
					showToast(mainActivity, "DEBUG: damagePotential=99, chance=200%, cost=1", Toast.LENGTH_SHORT);
				}
			})
			/*,new DebugButton("dmg=1", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					world.model.player.traits.combatTraits.set(1, 1);
					mainActivity.updateStatus();
					showToast(mainActivity, "DEBUG: damagePotential=1", Toast.LENGTH_SHORT);
				}
			})*/
			/*,new DebugButton("items", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					world.model.player.inventory.addItem(world.itemTypes.getItemType("elytharan_redeemer"));
					world.model.player.inventory.addItem(world.itemTypes.getItemType("ring_shadow0"));
					world.model.player.inventory.addItem(world.itemTypes.getItemType("shadow_slayer"));
					world.model.player.inventory.addItem(world.itemTypes.getItemType("pot_blind_rage"), 10);
					world.model.player.inventory.addItem(world.itemTypes.getItemType("clouded_rage"));
					world.model.player.inventory.addItem(world.itemTypes.getItemType("pot_fatigue_restore"), 20);
					world.model.player.inventory.addItem(world.itemTypes.getItemType("quickdagger1"));
					world.model.player.inventory.addItem(world.itemTypes.getItemType("bonemeal_potion"));
					world.model.player.inventory.addItem(world.itemTypes.getItemType("calomyran_secrets"));
					world.model.player.inventory.addItem(world.itemTypes.getItemType("tail_caverat"));
					world.model.player.inventory.addItem(world.itemTypes.getItemType("bwm_leather_cap"));
					world.model.player.inventory.addItem(world.itemTypes.getItemType("chaosreaper"));

					mainActivity.updateStatus();
					showToast(mainActivity, "DEBUG: added items", Toast.LENGTH_SHORT);
				}
			})*/
			/*new DebugButton("skills++", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					int N = 1;
					world.model.player.availableSkillIncreases += N * SkillCollection.NUM_SKILLS;
					for(int j = 0; j < N; ++j) {
						for(int i = 0; i < SkillCollection.NUM_SKILLS; ++i) {
							world.model.player.addSkillLevel(i);
						}
					}
					ActorStatsController.recalculatePlayerCombatTraits(world.model.player);
					updateStatus();
					showToast(mainActivity, "DEBUG: all skills raised " + N + " levels", Toast.LENGTH_SHORT);
				}
			})*/
			/*,new DebugButton("bwm", new OnClickListener() {
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

					controllerContext.movementController.placePlayerAt(MapObject.MAPEVENT_NEWMAP, "blackwater_mountain45", "south", 0, 0);
				}
			})*/
			/*,new DebugButton("prim", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					controllerContext.movementController.placePlayerAt(MapObject.MAPEVENT_NEWMAP, "blackwater_mountain29", "south", 0, 0);
				}
			})*/
			/*,new DebugButton("exp+=10000", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					world.model.player.addExperience(10000);
					mainActivity.updateStatus();
					showToast(mainActivity, "DEBUG: given 10000 exp", Toast.LENGTH_SHORT);
				}
			})*/
			,new DebugButton("reset", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					for(PredefinedMap map : world.maps.getAllMaps()) {
						map.resetTemporaryData();
					}
					showToast(mainActivity, "DEBUG: maps respawned", Toast.LENGTH_SHORT);
				}
			})
			,new DebugButton("hp", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					world.model.player.baseTraits.maxHP = 200;
					world.model.player.health.max = world.model.player.baseTraits.maxHP;
					controllerContext.actorStatsController.setActorMaxHealth(world.model.player);
					world.model.player.conditions.clear();
					showToast(mainActivity, "DEBUG: hp set to max", Toast.LENGTH_SHORT);
				}
			})
			/*
			,new DebugButton("cg", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					controllerContext.movementController.placePlayerAt(MapObject.MAPEVENT_NEWMAP, "crossglen", "hall", 0, 0);
				}
			})
			,new DebugButton("vg", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					controllerContext.movementController.placePlayerAt(MapObject.MAPEVENT_NEWMAP, "vilegard_s", "tavern", 0, 0);
				}
			})
			,new DebugButton("cr", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					controllerContext.movementController.placePlayerAt(MapObject.MAPEVENT_NEWMAP, "houseatcrossroads4", "down", 0, 0);
				}
			})
			,new DebugButton("lf", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					controllerContext.movementController.placePlayerAt(MapObject.MAPEVENT_NEWMAP, "loneford9", "south", 0, 0);
				}
			})
			,new DebugButton("fh", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					controllerContext.movementController.placePlayerAt(MapObject.MAPEVENT_NEWMAP, "fallhaven_ne", "clothes", 0, 0);
				}
			})
			,new DebugButton("rc", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					controllerContext.movementController.placePlayerAt(MapObject.MAPEVENT_NEWMAP, "roadtocarntower1", "left3", 0, 0);
				}
			})
			*/

		});
	}

	private void showToast(Context context, String msg, int duration) {
		Toast.makeText(context, msg, duration).show();
	}

	private static class DebugButton {
		public final String text;
		public final OnClickListener listener;
		public DebugButton(String text, OnClickListener listener) {
			this.text = text;
			this.listener = listener;
		}
	}

	private void addDebugButton(DebugButton button, int id, RelativeLayout layout) {
		if (!AndorsTrailApplication.DEVELOPMENT_DEBUGBUTTONS) return;

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, res.getDimensionPixelSize(R.dimen.smalltext_buttonheight));
		if (id == 1)
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		else
			lp.addRule(RelativeLayout.RIGHT_OF, id - 1);
		lp.addRule(RelativeLayout.ABOVE, R.id.main_statusview);
		Button b = new Button(mainActivity);
		b.setText(button.text);
		b.setTextSize(res.getDimension(R.dimen.actionbar_text));
		b.setId(id);
		b.setLayoutParams(lp);
		b.setOnClickListener(button.listener);
		layout.addView(b);
	}

	private void addDebugButtons(DebugButton[] buttons) {
		if (!AndorsTrailApplication.DEVELOPMENT_DEBUGBUTTONS) return;

		if (buttons == null || buttons.length <= 0) return;
		RelativeLayout layout = (RelativeLayout) mainActivity.findViewById(R.id.main_container);

		int id = 1;
		for (DebugButton b : buttons) {
			addDebugButton(b, id, layout);
			++id;
		}
	}
}
