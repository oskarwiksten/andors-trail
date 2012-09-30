package com.gpl.rpg.AndorsTrail.resource.parsers;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.traits.AbilityModifierTraits;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class ResourceParserUtils {
	
	public static int parseImageID(DynamicTileLoader tileLoader, String s) {
	   	String[] parts = s.split(":");
	   	return tileLoader.prepareTileID(parts[0], Integer.parseInt(parts[1]));
	}
	public static ConstRange parseRange(String min, String max) {
		if (   (max == null || max.length() <= 0) 
			&& (min == null || min.length() <= 0) ) {
			return null;
		}
		if (max == null || max.length() <= 0) {
			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				L.log("OPTIMIZE: Unable to parse range with min=" + min + " because max was empty.");
			}
			return null;
		}
		if (min == null || min.length() <= 0) {
			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				L.log("OPTIMIZE: Unable to parse range with max=" + max + " because min was empty.");
			}
			return null;
		}
		
		return new ConstRange(Integer.parseInt(max), Integer.parseInt(min));
	}
	
	private static final Size size1x1 = new Size(1, 1);
	public static Size parseSize(String s, final Size defaultSize) {
		if (s == null || s.length() <= 0) return defaultSize;
		if (s.equals("1x1")) return size1x1;
	   	String[] parts = s.split("x");
	   	if (parts.length < 2) return defaultSize;
	   	return new Size(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}
	
	public static CombatTraits parseCombatTraits(String[] parts, int startIndex) {
		String attackCost = parts[startIndex];
		String attackChance = parts[startIndex + 1];
		String criticalSkill = parts[startIndex + 2];
		String criticalMultiplier = parts[startIndex + 3];
		ConstRange attackDamage = parseRange(parts[startIndex + 4], parts[startIndex + 5]);
		String blockChance = parts[startIndex + 6];
		String damageResistance = parts[startIndex + 7];
		if (       attackCost.length() <= 0 
				&& attackChance.length() <= 0
				&& criticalSkill.length() <= 0
				&& criticalMultiplier.length() <= 0
				&& attackDamage == null
				&& blockChance.length() <= 0
				&& damageResistance.length() <= 0
			) {
			return null;
		} else {
			CombatTraits result = new CombatTraits();
			result.attackCost = parseInt(attackCost, 0);
			result.attackChance = parseInt(attackChance, 0);
			result.criticalSkill = parseInt(criticalSkill, 0);
			result.criticalMultiplier = parseFloat(criticalMultiplier, 0);
			if (attackDamage != null) result.damagePotential.set(attackDamage);
			result.blockChance = parseInt(blockChance, 0);
			result.damageResistance = parseInt(damageResistance, 0);
			return result;
		}
	}
	public static String parseNullableString(String s) {
		if (s == null || s.length() <= 0) return null;
		return s;
	}
	public static int parseInt(String s, int defaultValue) {
		if (s == null || s.length() <= 0) return defaultValue;
		return Integer.parseInt(s);
	}
	public static float parseFloat(String s, int defaultValue) {
		if (s == null || s.length() <= 0) return defaultValue;
		return Float.parseFloat(s);
	}
	public static boolean parseBoolean(String s, boolean defaultValue) {
		if (s == null || s.length() <= 0) return defaultValue;
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (Character.isDigit(s.charAt(0))) {
				if (Integer.parseInt(s) > 1) {
					L.log("WARNING: Tried to parseBoolean on \"" + s + "\".");
				}
			}
		}
		return !s.equals("0") && !s.equals("false");
	}
	public static StatsModifierTraits parseStatsModifierTraits(String[] parts, int startIndex) {
		boolean hasEffect = parseBoolean(parts[startIndex], false);
		if (!hasEffect) return null;
		
		String visualEffectID = parts[startIndex + 1];
		ConstRange boostCurrentHP = parseRange(parts[startIndex + 2], parts[startIndex + 3]);
		ConstRange boostCurrentAP = parseRange(parts[startIndex + 4], parts[startIndex + 5]);
		if (       boostCurrentHP == null 
				&& boostCurrentAP == null
			) {
			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				L.log("OPTIMIZE: Tried to parseStatsModifierTraits , where hasEffect=" + parts[startIndex] + ", but all data was empty.");
			}
			return null;
		} else {
			return new StatsModifierTraits(
					parseInt(visualEffectID, StatsModifierTraits.VISUAL_EFFECT_NONE)
					,boostCurrentHP
					,boostCurrentAP
					);
		}
	}
	
	public static AbilityModifierTraits parseAbilityModifierTraits(String[] parts, int startIndex) {
		String increaseMaxHP = parts[startIndex + 0];
		String increaseMaxAP = parts[startIndex + 1];
		String increaseMoveCost = parts[startIndex + 2];
		String increaseAttackCost = parts[startIndex + 3];
		String increaseAttackChance = parts[startIndex + 4];
		String increaseBlockChance = parts[startIndex + 9];
		String increaseMinDamage = parts[startIndex + 7];
		String increaseMaxDamage = parts[startIndex + 8];
		String increaseCriticalSkill = parts[startIndex + 5];
		String setCriticalMultiplier = parts[startIndex + 6];
		String increaseDamageResistance = parts[startIndex + 10];
		
		if (       increaseMaxHP.length() <= 0 
				&& increaseMaxAP.length() <= 0 
				&& increaseMoveCost.length() <= 0
				&& increaseAttackCost.length() <= 0 
				&& increaseAttackChance.length() <= 0
				&& increaseBlockChance.length() <= 0
				&& increaseMinDamage.length() <= 0
				&& increaseMaxDamage.length() <= 0
				&& increaseCriticalSkill.length() <= 0
				&& setCriticalMultiplier.length() <= 0
				&& increaseDamageResistance.length() <= 0
			) {
			return null;
		} else {
			return new AbilityModifierTraits(
					parseInt(increaseMaxHP, 0)
					,parseInt(increaseMaxAP, 0)
					,parseInt(increaseMoveCost, 0)
					,0 // increaseUseItemCost
					,0 // increaseReequipCost
					,parseInt(increaseAttackCost, 0)
					,parseInt(increaseAttackChance, 0)
					,parseInt(increaseBlockChance, 0)
					,parseInt(increaseMinDamage, 0)
					,parseInt(increaseMaxDamage, 0)
					,parseInt(increaseCriticalSkill, 0)
					,parseFloat(setCriticalMultiplier, 0)
					,parseInt(increaseDamageResistance, 0)
				);
		}
	}
	
	private static final ConstRange zero_or_one = new ConstRange(1, 0);
	private static final ConstRange one = new ConstRange(1, 1);
	private static final ConstRange five = new ConstRange(5, 5);
	private static final ConstRange ten = new ConstRange(10, 10);
	public static ConstRange parseQuantity(String min, String max) {
		if (min.equals("0") && max.equals("1")) return zero_or_one;
		else if (min.equals("1") && max.equals("1")) return one;
		else if (min.equals("5") && max.equals("5")) return five;
		else if (min.equals("10") && max.equals("10")) return ten;
		return parseRange(min, max);
	}
	
	public static final ConstRange always = one;
	private static final ConstRange often = new ConstRange(100, 70);
	private static final ConstRange animalpart = new ConstRange(100, 30);
	private static final ConstRange sometimes = new ConstRange(100, 25);
	private static final ConstRange rare_20 = new ConstRange(100, 20);
	private static final ConstRange rare_10 = new ConstRange(100, 10);
	private static final ConstRange seldom = new ConstRange(100, 5);
	private static final ConstRange unique = new ConstRange(100, 1);
	private static final ConstRange extraordinary = new ConstRange(1000, 1);
	private static final ConstRange legendary = new ConstRange(10000, 1);
	public static ConstRange parseChance(String v) {
		if (v.equals("100")) return always;
		else if (v.equals("70")) return often;
		else if (v.equals("30")) return animalpart;
		else if (v.equals("25")) return sometimes;
		else if (v.equals("20")) return rare_20;
		else if (v.equals("10")) return rare_10;
		else if (v.equals("5")) return seldom;
		else if (v.equals("1")) return unique;
		else if (v.equals("1/1000")) return extraordinary;
		else if (v.equals("1/10000")) return legendary;
		else if (v.indexOf('/') >= 0) {
			int c = v.indexOf('/');
			int a = parseInt(v.substring(0, c), 1);
			int b = parseInt(v.substring(c+1), 100);
			return new ConstRange(b, a);
		}
		else return new ConstRange(100, parseInt(v, 10));
	}
}
