package com.gpl.rpg.AndorsTrail.util;

// Should really use android.util.Pair<> instead, but it is not available for API level 4 (Android 1.6).
public final class Pair<T1, T2> {
	public final T1 first;
	public final T2 second;
	public Pair(T1 a, T2 b) {
		this.first = a;
		this.second = b;
	}
}
