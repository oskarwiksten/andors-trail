package com.gpl.rpg.AndorsTrail.model.ability;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.scripting.Script;
import com.gpl.rpg.AndorsTrail.scripting.ScriptEngine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ActorCondition {
	public static final int MAGNITUDE_REMOVE_ALL = -99;
	public static final int DURATION_FOREVER = 999;

	public final ActorConditionType conditionType;
	public int magnitude;
	public int duration;
	
	public Script[] scripts;
	public Script[] private_scripts;

	public ActorCondition(
			ActorConditionType conditionType
			, int magnitude
			, int duration
	) {
		this.conditionType = conditionType;
		this.magnitude = magnitude;
		this.duration = duration;
		int length;
		if (conditionType.scripts != null) {
			length = conditionType.scripts.length;
			scripts = new Script[length];
			while (length-->0) {
				scripts[length] = ScriptEngine.instantiateScript(conditionType.scripts[length]);
			}
		}
		if (conditionType.private_scripts != null) {
			length = conditionType.private_scripts.length;
			private_scripts = new Script[length];
			while (length-->0) {
				private_scripts[length] = ScriptEngine.instantiateScript(conditionType.private_scripts[length]);
			}
		}
	}

	public boolean isTemporaryEffect() { return isTemporaryEffect(duration); }
	public static boolean isTemporaryEffect(int duration) {
		return duration != DURATION_FOREVER;
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
