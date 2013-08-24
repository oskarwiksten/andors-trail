package com.gpl.rpg.AndorsTrail.controller;

public final class AttackResult {
	public final boolean isHit;
	public final boolean isCriticalHit;
	public final int damage;
	public final boolean targetDied;
	public AttackResult(boolean isHit, boolean isCriticalHit, int damage, boolean targetDied) {
		this.isHit = isHit;
		this.isCriticalHit = isCriticalHit;
		this.damage = damage;
		this.targetDied = targetDied;
	}
	public static final AttackResult MISS = new AttackResult(false, false, 0, false);
}
