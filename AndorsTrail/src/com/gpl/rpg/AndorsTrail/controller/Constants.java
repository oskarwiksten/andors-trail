package com.gpl.rpg.AndorsTrail.controller;

import java.util.Random;

import com.gpl.rpg.AndorsTrail.util.ConstRange;
import com.gpl.rpg.AndorsTrail.util.Range;

public final class Constants {
	public static final int PERCENT_EXP_LOST_WHEN_DIED = 30;
	public static final int LEVELUP_EFFECT_HEALTH = 5;
	public static final int LEVELUP_EFFECT_ATK_CH = 5;
	public static final int LEVELUP_EFFECT_ATK_DMG = 1;
	public static final int LEVELUP_EFFECT_DEF_CH = 3;
	public static final int MARKET_PRICEFACTOR_PERCENT = 15;
	public static final int MONSTER_AGGRESSION_CHANCE_PERCENT = 15;
	public static final int EXP_FACTOR_DAMAGERESISTANCE = 9;
	public static final float EXP_FACTOR_SCALING = 0.7f;
	public static final int FLEE_FAIL_CHANCE_PERCENT = 20;
	public static final long MINIMUM_INPUT_INTERVAL = 200;

	public static final int MONSTER_MOVEMENT_TURN_DURATION_MS = 1200;
	public static final int ATTACK_ANIMATION_FPS = 10;
	public static final int TICK_DELAY = 500;
	public static final ConstRange monsterWaitTurns = new ConstRange(30,4);
	
	public static final String PREFERENCE_MODEL_LASTRUNVERSION = "lastversion";
	public static final String FILENAME_SAVEGAME_QUICKSAVE = "savegame";
	public static final String FILENAME_SAVEGAME_DIRECTORY = "andors-trail";
	public static final String FILENAME_SAVEGAME_SLOT = FILENAME_SAVEGAME_DIRECTORY + "/savegame";
	
	
	public static final Random rnd = new Random();
	public static boolean roll100(final int chance) { return rnd.nextInt(100) < chance; }
	public static int rollValue(final ConstRange r) { 
		if (r.isMax()) return r.max;
		else return rnd.nextInt(r.max - r.current) + r.current;
	}
	public static int rollValue(final Range r) { 
		if (r.isMax()) return r.max;
		else return rnd.nextInt(r.max - r.current + 1) + r.current;
	}
	public static boolean rollResult(final ConstRange r) { return rnd.nextInt(r.max) < r.current; }
	public static boolean rollResult(final Range r) { return rnd.nextInt(r.max) < r.current; }
}
