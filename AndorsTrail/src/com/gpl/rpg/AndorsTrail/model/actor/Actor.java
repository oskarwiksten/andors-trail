package com.gpl.rpg.AndorsTrail.model.actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Range;

public class Actor {
	public final ActorTraits traits;
	public final Range ap;
	public final Range health;
	public final Coord position;
	public final CoordRect rectPosition;
	public final ArrayList<ActorCondition> conditions = new ArrayList<ActorCondition>();
	public final boolean isPlayer;
	
	public Actor(ActorTraits traits, boolean isPlayer) {
		this.traits = traits;
		this.ap = new Range();
		this.health = new Range();
		this.position = new Coord();
		this.rectPosition = new CoordRect(position, traits.tileSize);
		this.isPlayer = isPlayer;
		setMaxAP();
		setMaxHP();
	}
	
	public boolean isDead() {
		return health.current <= 0;
	}
	public void setMaxAP() {
		ap.set(traits.maxAP, traits.maxAP);
	}
	public void setMaxHP() {
		health.set(traits.maxHP, traits.maxHP);
	}
	
	public boolean useAPs(int cost) {
		if (ap.current < cost) return false;
		ap.subtract(cost, false);
		return true;
	}
	
	public boolean hasCondition(final String conditionTypeID) {
		for (ActorCondition c : conditions) {
			if (c.conditionType.conditionTypeID.equals(conditionTypeID)) return true;
		}
		return false;
	}
	
	public void resetStatsToBaseTraits() {
		traits.set(traits.baseCombatTraits);
		health.set(traits.maxHP, health.current);
		ap.set(traits.maxAP, ap.current);
		traits.moveCost = traits.baseMoveCost;
	}

	
	// ====== PARCELABLE ===================================================================

	public Actor(DataInputStream src, WorldContext world, int fileversion, boolean isPlayer) throws IOException {
		this.isPlayer = isPlayer;
		this.traits = new ActorTraits(src, world, fileversion);
		this.ap = new Range(src, fileversion);
		this.health = new Range(src, fileversion);
		this.position = new Coord(src, fileversion);
		this.rectPosition = new CoordRect(position, traits.tileSize);
		if (fileversion <= 16) return;
		final int n = src.readInt();
		for(int i = 0; i < n ; ++i) {
			conditions.add(new ActorCondition(src, world, fileversion));
		}
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		traits.writeToParcel(dest, flags);
		ap.writeToParcel(dest, flags);
		health.writeToParcel(dest, flags);
		position.writeToParcel(dest, flags);
		dest.writeInt(conditions.size());
		for (ActorCondition c : conditions) {
			c.writeToParcel(dest, flags);
		}
	}
}
