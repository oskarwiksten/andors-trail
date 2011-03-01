package com.gpl.rpg.AndorsTrail.model.ability;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.gpl.rpg.AndorsTrail.context.WorldContext;

public class ActorCondition {
	public static final int MAGNITUDE_REMOVE_ALL = -99;
	public static final int DURATION_FOREVER = 999;
	
	public final ActorConditionType conditionType;
	public final int magnitude;
	public int duration;
	
	public ActorCondition(ActorConditionType conditionType, int magnitude, int duration) {
		this.conditionType = conditionType;
		this.magnitude = magnitude;
		this.duration = duration;
	}
	
	public boolean isTemporaryEffect() { return isTemporaryEffect(duration); }
	public static boolean isTemporaryEffect(int duration) {
		return duration != DURATION_FOREVER;
	}

	public String describeEffect() {
		return describeEffect(conditionType, magnitude, duration);
	}
	public static String describeEffect(ActorConditionType conditionType, int magnitude, int duration) {
		StringBuilder sb = new StringBuilder(conditionType.name);
		if (magnitude > 1) {
			sb.append(" x");
			sb.append(magnitude); 
		}
		if (isTemporaryEffect(duration)) {
			sb.append(" (");
			sb.append(duration);
			sb.append(')');
		}
		return sb.toString();
	}
	

	// ====== PARCELABLE ===================================================================

	public ActorCondition(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		final String conditionTypeID = src.readUTF();
		this.conditionType = world.actorConditionsTypes.getActorConditionType(conditionTypeID);
		this.magnitude = src.readInt();
		this.duration = src.readInt();
	}
	
	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeUTF(conditionType.conditionTypeID);
		dest.writeInt(magnitude);
		dest.writeInt(duration);
	}
}
