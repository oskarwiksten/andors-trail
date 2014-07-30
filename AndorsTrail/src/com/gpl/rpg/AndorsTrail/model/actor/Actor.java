package com.gpl.rpg.AndorsTrail.model.actor;

import android.util.FloatMath;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Range;
import com.gpl.rpg.AndorsTrail.util.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Actor {
	private int iconID;
	private final Size tileSize;
	private final Coord position = new Coord();
	private final CoordRect rectPosition;
	private final boolean isPlayer;
	private final boolean isImmuneToCriticalHits;
	protected String name;

	private final Range ap = new Range();
	private final Range health = new Range();
	private final ArrayList<ActorCondition> conditions = new ArrayList<ActorCondition>();

	private int moveCost;
	private int attackCost;
	private int attackChance;
	private int criticalSkill;
	private float criticalMultiplier;
	private final Range damagePotential = new Range();
	// TODO: Should be privates
	private int blockChance;
	public int damageResistance;
	public ItemTraits_OnUse[] onHitEffects;

	public Actor(
			Size tileSize
			, boolean isPlayer
			, boolean isImmuneToCriticalHits
	) {
		this.tileSize = tileSize;
		this.rectPosition = new CoordRect(this.position, this.tileSize);
		this.isPlayer = isPlayer;
		this.isImmuneToCriticalHits = isImmuneToCriticalHits;
	}

	public boolean isImmuneToCriticalHits() { return isImmuneToCriticalHits; }
	public int getIconID() { return iconID; }
	public void setIconID(int iconID) { this.iconID = iconID; }
	public Size getTileSize() { return tileSize; }
	public Coord getPosition() { return position;}
	public CoordRect getRectPosition() { return rectPosition; }
	public boolean isPlayer() { return isPlayer; }
	public String getName() { return name; }
	public Range getAp() { return ap; }
	public Range getHealth() { return health;}
	public ArrayList<ActorCondition> getConditions() { return conditions; }
	public int getCurrentAP() { return ap.getCurrent(); }
	public int getMaxAP() { return ap.getMax(); }
	public int getCurrentHP() { return health.getCurrent(); }
	public int getMaxHP() { return health.getMax(); }
	public int getMoveCost() { return moveCost; }
	public void setMoveCost(int moveCost) { this.moveCost = moveCost; }
	public int getAttackCost() { return attackCost; }
	public void setAttackCost(int attackCost) { this.attackCost = attackCost; }
	public int getAttackChance() { return attackChance; }
	public void setAttackChance(int attackChance) { this.attackChance = attackChance; }
	public int getCriticalSkill() { return criticalSkill; }
	public void setCriticalSkill(int criticalSkill) { this.criticalSkill = criticalSkill; }
	public float getCriticalMultiplier() { return criticalMultiplier; }
	public void setCriticalMultiplier(float criticalMultiplier) { this.criticalMultiplier = criticalMultiplier; }
	public Range getDamagePotential() { return damagePotential; }
	public int getBlockChance() { return blockChance; }
	public void setBlockChance(int blockChance) {this.blockChance = blockChance;}
	public int getDamageResistance() { return damageResistance; }
	public ItemTraits_OnUse[] getOnHitEffects() { return onHitEffects; }
	public List<ItemTraits_OnUse> getOnHitEffectsAsList() { return onHitEffects == null ? null : Arrays.asList(onHitEffects); }

	public boolean hasCriticalSkillEffect() { return getCriticalSkill() != 0; }
	public boolean hasCriticalMultiplierEffect() { float m = getCriticalMultiplier(); return m != 0 && m != 1; }
	public boolean hasCriticalAttacks() { return hasCriticalSkillEffect() && hasCriticalMultiplierEffect(); }

	public int getAttacksPerTurn() { return (int) Math.floor(getMaxAP() / getAttackCost()); }
	public int getEffectiveCriticalChance() { return getEffectiveCriticalChance(getCriticalSkill()); }
	public static int getEffectiveCriticalChance(int criticalSkill) {
		if (criticalSkill <= 0) return 0;
		int v = (int) (-5 + 2 * FloatMath.sqrt(5*criticalSkill));
		if (v < 0) return 0;
		return v;
	}

	public boolean isDead() {
		return health.getCurrent() <= 0;
	}

	public boolean hasAPs(int cost) {
		return ap.getCurrent() >= cost;
	}

	public boolean hasCondition(final String conditionTypeID) {
		for (ActorCondition c : conditions) {
			if (c.conditionType.conditionTypeID.equals(conditionTypeID)) return true;
		}
		return false;
	}
}
