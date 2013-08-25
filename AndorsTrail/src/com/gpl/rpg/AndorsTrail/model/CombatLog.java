package com.gpl.rpg.AndorsTrail.model;

import java.util.LinkedList;
import java.util.ListIterator;

public final class CombatLog {
	private final LinkedList<String> messages = new LinkedList<String>();
	private static final int MAX_COMBAT_LOG_LENGTH = 100;
	private static final String newCombatSession = "--";

	public CombatLog() { }

	public void append(String msg) {
		while (messages.size() >= MAX_COMBAT_LOG_LENGTH) messages.removeFirst();
		messages.addLast(msg);
	}

	public void appendCombatEnded() {
		if (messages.isEmpty()) return;
		if (messages.getLast().equals(newCombatSession)) return;
		append(newCombatSession);
	}

	public String getLastMessages() {
		if (messages.isEmpty()) return "";
		StringBuilder sb = new StringBuilder(100);
		ListIterator<String> it = messages.listIterator(messages.size());
		sb.append(it.previous());
		int i = 1;
		while (it.hasPrevious() && i++ < 3) {
			String s = it.previous();
			if (s.equals(newCombatSession)) break;
			sb.insert(0, '\n').insert(0, s);
		}
		return sb.toString();
	}

	public String[] getAllMessages() {
		return messages.toArray(new String[messages.size()]);
	}
}
