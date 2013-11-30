package com.gpl.rpg.AndorsTrail.util;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ListOfListeners<T> {
	private final ArrayList<WeakReference<T>> listeners = new ArrayList<WeakReference<T>>();

	public final void add(T listener) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			for (WeakReference<T> ref : listeners) {
				if (ref.get() == listener) {
					throw new IndexOutOfBoundsException("FAIL: listener added twice to ListOfListeners.");
				}
			}
		}
		listeners.add(new WeakReference<T>(listener));
	}
	public final void remove(T listenerToRemove) {
		for (int i = listeners.size()-1; i >= 0; --i) {
			T listener = listeners.get(i).get();
			if (listener == null || listener == listenerToRemove) {
				listeners.remove(i);
			}
		}
	}
	public final void clear() {
		listeners.clear();
	}

	protected final void callAllListeners(Function<T> e) {
		for (int i = listeners.size()-1; i >= 0; --i) {
			T listener = listeners.get(i).get();
			if (listener == null) listeners.remove(i);
			else e.call(listener);
		}
	}
	protected final <Arg1> void callAllListeners(Function1<T, Arg1> e, Arg1 arg) {
		for (int i = listeners.size()-1; i >= 0; --i) {
			T listener = listeners.get(i).get();
			if (listener == null) listeners.remove(i);
			else e.call(listener, arg);
		}
	}
	protected final <Arg1, Arg2> void callAllListeners(Function2<T, Arg1, Arg2> e, Arg1 arg1, Arg2 arg2) {
		for (int i = listeners.size()-1; i >= 0; --i) {
			T listener = listeners.get(i).get();
			if (listener == null) listeners.remove(i);
			else e.call(listener, arg1, arg2);
		}
	}
	protected final <Arg1, Arg2, Arg3> void callAllListeners(Function3<T, Arg1, Arg2, Arg3> e, Arg1 arg1, Arg2 arg2, Arg3 arg3) {
		for (int i = listeners.size()-1; i >= 0; --i) {
			T listener = listeners.get(i).get();
			if (listener == null) listeners.remove(i);
			else e.call(listener, arg1, arg2, arg3);
		}
	}

	protected static interface Function<Listener> {
		public void call(Listener listener);
	}
	protected static interface Function1<Listener, Arg1> {
		public void call(Listener listener, Arg1 arg1);
	}
	protected static interface Function2<Listener, Arg1, Arg2> {
		public void call(Listener listener, Arg1 arg1, Arg2 arg2);
	}
	protected static interface Function3<Listener, Arg1, Arg2, Arg3> {
		public void call(Listener listener, Arg1 arg1, Arg2 arg2, Arg3 arg3);
	}
}