package com.gpl.rpg.AndorsTrail.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class Range {
	public int max;
	public int current;
	
	public Range() { }
	public Range(Range r) { set(r); }
	public Range(ConstRange r) { set(r); }
	public Range(int max, int current) {
		this.max = max;
		this.current = current;
	}
	
	public void set(Range r) {
		this.max = r.max;
		this.current = r.current;
	}
	public void set(ConstRange r) {
		this.max = r.max;
		this.current = r.current;
	}
	public void set(int max, int current) {
		this.max = max;
		this.current = current;
	}
	public void add(int value, boolean mayOverflow) {
		this.current += value;
		if (!mayOverflow && current > max) current = max;
	}
	public void subtract(int value, boolean mayUnderflow) {
		this.current -= value;
		if (!mayUnderflow && current < 0) current = 0;
	}
	
	public String toString() { return current + "/" + max; }
	public String toMinMaxString() {
		if (isMax()) return Integer.toString(max);
		else return current + "-" + max; 
	}
	public boolean isMax() { return current >= max;	}
	public void setMax() { current = max; }
	public int average() {
		return (max + current) / 2;
	}
	public float averagef() {
		return ((float) max + current) / 2f;
	}
	

	// ====== PARCELABLE ===================================================================

	public Range(DataInputStream src, int fileversion) throws IOException {
		this.max = src.readInt();
		this.current = src.readInt();
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeInt(max);
		dest.writeInt(current);
	}
}
