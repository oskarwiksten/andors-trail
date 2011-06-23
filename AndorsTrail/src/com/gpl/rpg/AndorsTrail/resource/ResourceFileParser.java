package com.gpl.rpg.AndorsTrail.resource;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail.model.ability.traits.AbilityModifierTraits;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnEquip;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Size;

public class ResourceFileParser {
	private static final Pattern rowPattern = Pattern.compile("\\{(.+?)\\};", Pattern.MULTILINE | Pattern.DOTALL);
	private static final String columnSeparator = "\\|";
	private static String repeat(String s, int count) {
		String result = s;
		for(int i = 1; i < count; ++i) result += s;
		return result;
	}
	
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
	public static Size parseSize(String s, final Size defaultSize) {
		if (s == null || s.length() <= 0) return defaultSize;
	   	String[] parts = s.split("x");
	   	if (parts.length < 2) return defaultSize;
	   	return new Size(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}
	public static CombatTraits parseCombatTraits(String[] parts, int startIndex) {
		String attackCost = parts[startIndex];
		String attackChance = parts[startIndex + 1];
		String criticalChance = parts[startIndex + 2];
		String criticalMultiplier = parts[startIndex + 3];
		ConstRange attackDamage = parseRange(parts[startIndex + 4], parts[startIndex + 5]);
		String blockChance = parts[startIndex + 6];
		String damageResistance = parts[startIndex + 7];
		if (       attackCost.length() <= 0 
				&& attackChance.length() <= 0
				&& criticalChance.length() <= 0
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
			result.criticalChance = parseInt(criticalChance, 0);
			result.criticalMultiplier = parseInt(criticalMultiplier, 0);
			if (attackDamage != null) result.damagePotential.set(attackDamage);
			result.blockChance = parseInt(blockChance, 0);
			result.damageResistance = parseInt(damageResistance, 0);
			return result;
		}
	}
	public static int parseInt(String s, int defaultValue) {
		if (s == null || s.length() <= 0) return defaultValue;
		return Integer.parseInt(s);
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
		boolean hasEffect = parseBoolean(parts[startIndex], false);
		if (!hasEffect) return null;
		
		String boostMaxHP = parts[startIndex + 1];
		String boostMaxAP = parts[startIndex + 2];
		String moveCostPenalty = parts[startIndex + 3];
		CombatTraits combatTraits = parseCombatTraits(parts, startIndex + 4);
		
		if (       boostMaxHP.length() <= 0 
				&& boostMaxAP.length() <= 0 
				&& moveCostPenalty.length() <= 0
				&& combatTraits == null
			) {
			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				L.log("OPTIMIZE: Tried to parseAbilityModifierTraits , where hasEffect=" + parts[startIndex] + ", but all data was empty.");
			}
			return null;
		} else {
			return new AbilityModifierTraits(
					parseInt(boostMaxHP, 0)
					,parseInt(boostMaxAP, 0)
					,parseInt(moveCostPenalty, 0)
					,combatTraits
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
	
	private static final ConstRange always = one;
	private static final ConstRange often = new ConstRange(100, 70);
	private static final ConstRange animalpart = new ConstRange(100, 30);
	private static final ConstRange seldom = new ConstRange(100, 25);
	private static final ConstRange very_seldom = new ConstRange(100, 5);
	private static final ConstRange unique = new ConstRange(100, 1);
	public static ConstRange parseChance(String v) {
		if (v.equals("100")) return always;
		else if (v.equals("70")) return often;
		else if (v.equals("30")) return animalpart;
		else if (v.equals("25")) return seldom;
		else if (v.equals("5")) return very_seldom;
		else if (v.equals("1")) return unique;
		else if (v.indexOf('/') >= 0) {
			int c = v.indexOf('/');
			int a = parseInt(v.substring(0, c), 1);
			int b = parseInt(v.substring(c+1), 100);
			return new ConstRange(b, a);
		}
		else return new ConstRange(100, parseInt(v, 10));
	}
	
	private static ActorConditionEffect parseActorConditionEffect(ActorConditionTypeCollection actorConditionTypes, String[] parts, boolean includeDuration) {
		if (includeDuration) {
			return new ActorConditionEffect(
					actorConditionTypes.getActorConditionType(parts[0])
					, parseInt(parts[1], ActorCondition.MAGNITUDE_REMOVE_ALL)
					, parseInt(parts[2], ActorCondition.DURATION_FOREVER)
					, parseChance(parts[3])
				);
		} else {
			return new ActorConditionEffect(
					actorConditionTypes.getActorConditionType(parts[0])
					, parseInt(parts[1], 1)
					, ActorCondition.DURATION_FOREVER
					, always
				);
		}
	}
	
	private static class ActorConditionTypeArrayAppender implements ResourceObjectFieldParser {
		private final ActorConditionTypeCollection actorConditionTypes;
		private final ArrayList<ActorConditionEffect> dest;
		private final boolean includeDuration;
		public ActorConditionTypeArrayAppender(ActorConditionTypeCollection actorConditionTypes, ArrayList<ActorConditionEffect> dest, boolean includeDuration) {
			this.actorConditionTypes = actorConditionTypes;
			this.dest = dest;
			this.includeDuration = includeDuration;
		}
		@Override
		public void matchedRow(String[] parts) {
			ActorConditionEffect a = parseActorConditionEffect(actorConditionTypes, parts, includeDuration);
			if (a != null) dest.add(a);
		}
	}
	private static final ResourceObjectTokenizer tokenize4Fields = new ResourceObjectTokenizer(4);
	public static ItemTraits_OnUse parseItemTraits_OnUse(final ActorConditionTypeCollection actorConditionTypes, String[] parts, int startIndex, boolean parseTargetConditions) {
		boolean hasEffect = parseBoolean(parts[startIndex], false);
		if (!hasEffect) return null;
		
		ConstRange boostCurrentHP = parseRange(parts[startIndex + 1], parts[startIndex + 2]);
		ConstRange boostCurrentAP = parseRange(parts[startIndex + 3], parts[startIndex + 4]);
		final ArrayList<ActorConditionEffect> addedConditions_source = new ArrayList<ActorConditionEffect>();
		final ArrayList<ActorConditionEffect> addedConditions_target = new ArrayList<ActorConditionEffect>();
		ResourceObjectArrayTokenizer.tokenize(parts[startIndex + 5], tokenize4Fields, new ActorConditionTypeArrayAppender(actorConditionTypes, addedConditions_source, true));
		if (parseTargetConditions) {
			ResourceObjectArrayTokenizer.tokenize(parts[startIndex + 6], tokenize4Fields, new ActorConditionTypeArrayAppender(actorConditionTypes, addedConditions_target, true));
		}
		if (       boostCurrentHP == null 
				&& boostCurrentAP == null
				&& addedConditions_source.isEmpty()
				&& addedConditions_target.isEmpty()
			) {
			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				L.log("OPTIMIZE: Tried to parseItemTraits_OnUse , where hasEffect=" + parts[startIndex] + ", but all data was empty.");
			}
			return null;
		} else {
			return new ItemTraits_OnUse(
					boostCurrentHP
					,boostCurrentAP
					,listToArray(addedConditions_source)
					,listToArray(addedConditions_target)
					);
		}
	}
	
	private static final ResourceObjectTokenizer tokenize2Fields = new ResourceObjectTokenizer(2);
	public static ItemTraits_OnEquip parseItemTraits_OnEquip(final ActorConditionTypeCollection actorConditionTypes, String[] parts, int startIndex) {
		boolean hasEffect = parseBoolean(parts[startIndex], false);
		if (!hasEffect) return null;
		
		String boostMaxHP = parts[startIndex + 1];
		String boostMaxAP = parts[startIndex + 2];
		String moveCostPenalty = parts[startIndex + 3];
		CombatTraits combatTraits = parseCombatTraits(parts, startIndex + 4);
		final ArrayList<ActorConditionEffect> addedConditions = new ArrayList<ActorConditionEffect>();
		ResourceObjectArrayTokenizer.tokenize(parts[startIndex + 12], tokenize2Fields, new ActorConditionTypeArrayAppender(actorConditionTypes, addedConditions, false));
		
		if (       boostMaxHP.length() <= 0 
				&& boostMaxAP.length() <= 0
				&& moveCostPenalty.length() <= 0
				&& combatTraits == null
				&& addedConditions.isEmpty()
			) {
			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				L.log("OPTIMIZE: Tried to parseItemTraits_OnEquip , where hasEffect=" + parts[startIndex] + ", but all data was empty.");
			}
			return null;
		} else {
			return new ItemTraits_OnEquip(
					parseInt(boostMaxHP, 0)
					,parseInt(boostMaxAP, 0)
					,parseInt(moveCostPenalty, 0)
					,combatTraits
					,listToArray(addedConditions)
					);
		}
	}
	
	public static ActorConditionEffect[] listToArray(ArrayList<ActorConditionEffect> list) {
		if (list.isEmpty()) return null;
		return list.toArray(new ActorConditionEffect[list.size()]);
	}
	
	public static class ResourceObjectTokenizer {
		private static final String fieldPattern = "([^\\|]*?|\\{\\s*\\{.*?\\}\\s*\\})" + columnSeparator;
		
		private final int columns;
		private final Pattern pattern;
		private final String[] parts;
		public ResourceObjectTokenizer(int columns) {
			this.columns = columns;
			this.pattern = Pattern.compile("^" + repeat(fieldPattern, columns) + "$", Pattern.MULTILINE | Pattern.DOTALL);
			this.parts = new String[columns];
		}
		public void tokenizeRows(String input, ResourceObjectFieldParser parser) {
			Matcher rowMatcher = rowPattern.matcher(input);
	    	while (rowMatcher.find()) {
	    		tokenizeRow(rowMatcher.group(1), parser);
	    	}
		}
		public void tokenizeRow(String input, ResourceObjectFieldParser parser) {
			Matcher groups = pattern.matcher(input);
			if (!groups.find()) return;
			if (groups.groupCount() < columns) return;
			for(int i = 0; i < columns; ++i) {
				parts[i] = groups.group(i + 1);
			}
			parser.matchedRow(parts);
		}
	}
	
	public static class ResourceObjectArrayTokenizer {
		private static final Pattern outerPattern = Pattern.compile("^\\{(.*)\\}$", Pattern.MULTILINE | Pattern.DOTALL);
		private static final Pattern innerPattern = Pattern.compile("\\{(.*?)\\}", Pattern.MULTILINE | Pattern.DOTALL);
		
		public static void tokenize(String input, ResourceObjectTokenizer objectTokenizer, ResourceObjectFieldParser parser) {
			Matcher matcher = outerPattern.matcher(input);
	    	if (!matcher.find()) return;
	    	
	    	matcher = innerPattern.matcher(matcher.group(1));
	    	while (matcher.find()) {
	    		objectTokenizer.tokenizeRow(matcher.group(1), parser);
	    	}
		}
	}
	
	public interface ResourceObjectFieldParser {
		void matchedRow(String[] parts);
	}
}
