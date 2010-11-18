package com.gpl.rpg.AndorsTrail.model.actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Range;

public class Actor {
	public final ActorTraits traits;
	public final Range ap;
	public final Range health;
	public final Coord position;
	public final CoordRect rectPosition;
	
	public Actor(ActorTraits traits) {
		this.traits = traits;
		this.ap = new Range();
		this.health = new Range();
		this.position = new Coord();
		this.rectPosition = new CoordRect(position, traits.tileSize);
		setMaxAP();
		setMaxHP();
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
	
	
	// ====== PARCELABLE ===================================================================

	public Actor(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		this.traits = new ActorTraits(src, world, fileversion);
		this.ap = new Range(src, fileversion);
		this.health = new Range(src, fileversion);
		this.position = new Coord(src, fileversion);
		this.rectPosition = new CoordRect(position, traits.tileSize);
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		traits.writeToParcel(dest, flags);
		ap.writeToParcel(dest, flags);
		health.writeToParcel(dest, flags);
		position.writeToParcel(dest, flags);
	}
}
