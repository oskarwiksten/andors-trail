package com.gpl.rpg.AndorsTrail.model.actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.listeners.ActorConditionListeners;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Range;

public class Actor {
	public final ActorTraits baseTraits;
	public final CombatTraits combatTraits;
	public final Range ap;
	public final Range health;
	public final Coord position;
	public final CoordRect rectPosition;
	public final ArrayList<ActorCondition> conditions = new ArrayList<ActorCondition>();
	public final ActorConditionListeners conditionListener = new ActorConditionListeners();
	public final boolean isPlayer;
	public final boolean isImmuneToCriticalHits;
	
	public Actor(ActorTraits baseTraits, boolean isPlayer, boolean isImmuneToCriticalHits) {
		this.combatTraits = new CombatTraits(baseTraits.baseCombatTraits);
		this.baseTraits = baseTraits;
		this.ap = new Range(baseTraits.maxAP, baseTraits.maxAP);
		this.health = new Range(baseTraits.maxHP, baseTraits.maxHP);
		this.position = new Coord();
		this.rectPosition = new CoordRect(position, baseTraits.tileSize);
		this.isPlayer = isPlayer;
		this.isImmuneToCriticalHits = isImmuneToCriticalHits;
	}
	
	public int getAttacksPerTurn() { return combatTraits.getAttacksPerTurn(baseTraits.maxAP); }
	
	public boolean isDead() {
		return health.current <= 0;
	}
	public void setMaxAP() {
		ap.setMax();
	}
	public void setMaxHP() {
		health.setMax();
	}
	public String getName() { return baseTraits.name; }
	
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
	
	public void resetStatsToBaseTraits() {
		combatTraits.set(baseTraits.baseCombatTraits);
		health.set(baseTraits.maxHP, health.current);
		ap.set(baseTraits.maxAP, ap.current);
		baseTraits.moveCost = baseTraits.baseMoveCost;
	}


	
	// ====== PARCELABLE ===================================================================

	public Actor(DataInputStream src, WorldContext world, int fileversion, boolean isPlayer, boolean isImmuneToCriticalHits, ActorTraits baseTraits) throws IOException {
		this.isPlayer = isPlayer;
		this.isImmuneToCriticalHits = isImmuneToCriticalHits;
		
		CombatTraits combatTraits = null;
		boolean readCombatTraits = true;
		if (fileversion >= 25) readCombatTraits = src.readBoolean();
		if (readCombatTraits) combatTraits = new CombatTraits(src, fileversion);
		
		this.baseTraits = isPlayer ? new ActorTraits(src, world, fileversion) : baseTraits;
		if (!readCombatTraits) combatTraits = new CombatTraits(this.baseTraits.baseCombatTraits);
		this.combatTraits = combatTraits;

		this.ap = new Range(src, fileversion);
		this.health = new Range(src, fileversion);
		this.position = new Coord(src, fileversion);
		this.rectPosition = new CoordRect(position, this.baseTraits.tileSize);
		if (fileversion <= 16) return;
		final int n = src.readInt();
		for(int i = 0; i < n ; ++i) {
			conditions.add(new ActorCondition(src, world, fileversion));
		}
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		if (this.combatTraits.equals(baseTraits.baseCombatTraits)) {
			dest.writeBoolean(false);
		} else {
			dest.writeBoolean(true);
			combatTraits.writeToParcel(dest, flags);
		}
		if (isPlayer) baseTraits.writeToParcel(dest, flags);
		ap.writeToParcel(dest, flags);
		health.writeToParcel(dest, flags);
		position.writeToParcel(dest, flags);
		dest.writeInt(conditions.size());
		for (ActorCondition c : conditions) {
			c.writeToParcel(dest, flags);
		}
	}
}
