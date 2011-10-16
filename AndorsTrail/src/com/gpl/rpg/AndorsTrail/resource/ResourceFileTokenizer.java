package com.gpl.rpg.AndorsTrail.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Pair;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.util.L;

public class ResourceFileTokenizer {
	private static final Pattern rowPattern = Pattern.compile("\\{(.+?)\\};", Pattern.MULTILINE | Pattern.DOTALL);
	private static final String columnSeparator = "\\|";
	private static final String fieldPattern = "([^\\|]*?|\\{\\s*\\{.*?\\}\\s*\\})" + columnSeparator;
	private static String repeat(String s, int count) {
		String result = s;
		for(int i = 1; i < count; ++i) result += s;
		return result;
	}

	
	private final int columns;
	private final Pattern pattern;
	private final String[] parts;
	
	public ResourceFileTokenizer(int columns) {
		this.columns = columns;
		this.pattern = Pattern.compile("^" + repeat(fieldPattern, columns) + "$", Pattern.MULTILINE | Pattern.DOTALL);
		this.parts = new String[columns];
	}
	
	private <T> void tokenizeRows(String input, ArrayList<T> dest, ResourceObjectParser<T> parser) {
		Matcher rowMatcher = rowPattern.matcher(input);
    	while (rowMatcher.find()) {
    		tokenizeRow(rowMatcher.group(1), dest, parser);
    	}
	}
	
	public <T> Collection<String> tokenizeRows(String input, HashMap<String, T> dest, ResourceObjectParser<Pair<String, T>> parser) {
		HashSet<String> ids = new HashSet<String>();
		ArrayList<Pair<String, T>> objects = new ArrayList<Pair<String, T>>();
		tokenizeRows(input, objects, parser);
		
		for (Pair<String, T> o : objects) {
			final String id = o.first;
			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				if (id == null || id.length() <= 0) {
					L.log("WARNING: Entity " + o.second.toString() + " has empty id.");
				} else if (dest.containsKey(id)) {
    				L.log("WARNING: Entity " + id + " is duplicated.");
        		}
    		}
			dest.put(id, o.second);
			ids.add(id);
		}
		return ids;
	}
	
	private <T> void tokenizeRow(String input, ArrayList<T> dest, ResourceObjectParser<T> parser) {
		Matcher groups = pattern.matcher(input);
		if (!groups.find()) return;
		if (groups.groupCount() < columns) return;
		for(int i = 0; i < columns; ++i) {
			parts[i] = groups.group(i + 1);
		}
		T obj = parser.parseRow(parts);
		if (obj != null) dest.add(obj);
	}

	
	private static final Pattern outerPattern = Pattern.compile("^\\{(.*)\\}$", Pattern.MULTILINE | Pattern.DOTALL);
	private static final Pattern innerPattern = Pattern.compile("\\{(.*?)\\}", Pattern.MULTILINE | Pattern.DOTALL);
	
	public <T> void tokenizeArray(String input, ArrayList<T> dest, ResourceObjectParser<T> parser) {
		Matcher matcher = outerPattern.matcher(input);
    	if (!matcher.find()) return;
    	
    	matcher = innerPattern.matcher(matcher.group(1));
    	while (matcher.find()) {
    		tokenizeRow(matcher.group(1), dest, parser);
    	}
	}
	
	public static interface ResourceObjectParser<T> {
		T parseRow(String[] parts);
	}
	
	public static abstract class ResourceParserFor<T> extends ResourceFileTokenizer implements ResourceObjectParser<Pair<String, T>> {
		public ResourceParserFor(int columns) {
			super(columns);
		}

		public Collection<String> parseRows(String input, HashMap<String, T> dest) {
			return tokenizeRows(input, dest, this);
		}
	}
}
