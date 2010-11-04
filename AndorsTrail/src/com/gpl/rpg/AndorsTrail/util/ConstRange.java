package com.gpl.rpg.AndorsTrail.util;

public final class ConstRange {
	public final int max;
	public final int current;
	
	public ConstRange(Range r) { 
		this.max = r.max;
		this.current = r.current;
	}
	public ConstRange(ConstRange r) { 
		this.max = r.max;
		this.current = r.current;
	}
	public ConstRange(int max, int current) {
		this.max = max;
		this.current = current;
	}
	
	public String toString() { return current + "/" + max; }
	public String toMinMaxString() {
		if (isMax()) return Integer.toString(max);
		else return current + "-" + max; 
	}
	public boolean isMax() { return max == current;	}
	public int average() {
		return current + (max - current);
	}
}
