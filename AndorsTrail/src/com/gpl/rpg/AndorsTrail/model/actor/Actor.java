package com.gpl.rpg.AndorsTrail.model.actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.FloatMath;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.listeners.ActorConditionListeners;
import com.gpl.rpg.AndorsTrail.savegames.LegacySavegameFormatReaderForPlayer.LegacySavegameData_Actor;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Range;
import com.gpl.rpg.AndorsTrail.util.Size;

public class Actor {
	public final int iconID;
	public final Size tileSize;
	public final Coord position = new Coord();
	public final CoordRect rectPosition;
	public final boolean isPlayer;
	private final boolean isImmuneToCriticalHits;
	protected String name;
	
	// TODO: Should be privates
	public final Range ap = new Range();
	public final Range health = new Range();
	public final ArrayList<ActorCondition> conditions = new ArrayList<ActorCondition>();
	public final ActorConditionListeners conditionListener = new ActorConditionListeners();
	public int moveCost;
	public int attackCost;
	public int attackChance;
	public int criticalSkill;
	public float criticalMultiplier;
	public final Range damagePotential = new Range();
	public int blockChance;
	public int damageResistance;
	public ItemTraits_OnUse[] onHitEffects;
	
	public Actor(int iconID, Size tileSize, boolean isPlayer, boolean isImmuneToCriticalHits) {
		this.iconID = iconID;
		this.tileSize = tileSize;
		this.rectPosition = new CoordRect(this.position, this.tileSize);
		this.isPlayer = isPlayer;
		this.isImmuneToCriticalHits = isImmuneToCriticalHits;
	}
	
	public boolean isImmuneToCriticalHits() { return isImmuneToCriticalHits; }
	public String getName() { return name; }
	public int getMaxAP() { return ap.max; }
	public int getMaxHP() { return health.max; }
	public int getMoveCost() { return moveCost; }
	public int getAttackCost() { return attackCost; }
	public int getAttackChance() { return attackChance; }
	public int getCriticalSkill() { return criticalSkill; }
	public float getCriticalMultiplier() { return criticalMultiplier; }
	public Range getDamagePotential() { return damagePotential; }
	public int getBlockChance() { return blockChance; }
	public int getDamageResistance() { return damageResistance; }
	public ItemTraits_OnUse[] getOnHitEffects() { return onHitEffects; }
	public List<ItemTraits_OnUse> getOnHitEffectsAsList() { return onHitEffects == null ? null : Arrays.asList(onHitEffects); }
	
	public boolean hasAttackChanceEffect_() { return getAttackChance() != 0; }
	public boolean hasAttackDamageEffect_() { return getDamagePotential().max != 0; }
	public boolean hasBlockEffect_() { return getBlockChance() != 0; }
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
		return health.current <= 0;
	}
	public void setMaxAP() {
		ap.setMax();
	}
	public void setMaxHP() {
		health.setMax();
	}
	
	public boolean useAPs(int cost) {
		if (ap.current < cost) return false;
		ap.subtract(cost, false);
		return true;
	}
	public boolean hasAPs(int cost) {
		return ap.current >= cost;
	}
	
	public boolean hasCondition(final String conditionTypeID) {
		for (ActorCondition c : conditions) {
			if (c.conditionType.conditionTypeID.equals(conditionTypeID)) return true;
		}
		return false;
	}

	
	// ====== PARCELABLE ===================================================================

	/*
	public Actor(DataInputStream src, WorldContext world, int fileversion, boolean isPlayer, boolean isImmuneToCriticalHits, Size tileSize, ActorTraits baseTraits) throws IOException {
		this.isPlayer = isPlayer;
		this.isImmuneToCriticalHits = isImmuneToCriticalHits;
		
		CombatTraits combatTraits = null;
		boolean readCombatTraits = src.readBoolean();
		if (readCombatTraits) combatTraits = new CombatTraits(src, fileversion);
		
		this.baseTraits = isPlayer ? new ActorTraits(src, world, fileversion) : baseTraits;
		this.name = src.readUTF();
		this.iconID = baseTraits.iconID;
		this.tileSize = tileSize;
		
		if (!readCombatTraits) combatTraits = new CombatTraits(this.baseTraits);
		this.combatTraits = combatTraits;

		this.ap = new Range(src, fileversion);
		this.health = new Range(src, fileversion);
		this.position = new Coord(src, fileversion);
		this.rectPosition = new CoordRect(position, this.tileSize);
		final int numConditions = src.readInt();
		for(int i = 0; i < numConditions; ++i) {
			conditions.add(new ActorCondition(src, world, fileversion));
		}
	}
	
	public Actor(LegacySavegameData_Actor savegameData, boolean isPlayer) {
		this.isPlayer = isPlayer;
		this.isImmuneToCriticalHits = savegameData.isImmuneToCriticalHits;
		this.baseTraits = new ActorTraits(savegameData);
		this.name = savegameData.name;
		this.iconID = savegameData.iconID;
		this.tileSize = savegameData.tileSize;
		this.combatTraits = new CombatTraits(savegameData);
		this.ap = savegameData.ap;
		this.health = savegameData.health;
		this.position = savegameData.position;
		this.rectPosition = savegameData.rectPosition;
		this.conditions.addAll(savegameData.conditions);
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		if (this.combatTraits.isSameValuesAs(baseTraits)) {
			dest.writeBoolean(false);
		} else {
			dest.writeBoolean(true);
			combatTraits.writeToParcel(dest, flags);
		}
		dest.writeUTF(name);
		if (isPlayer) baseTraits.writeToParcel(dest, flags);
		ap.writeToParcel(dest, flags);
		health.writeToParcel(dest, flags);
		position.writeToParcel(dest, flags);
		dest.writeInt(conditions.size());
		for (ActorCondition c : conditions) {
			c.writeToParcel(dest, flags);
		}
	}*/
}
