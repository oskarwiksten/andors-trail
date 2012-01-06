package com.gpl.rpg.AndorsTrail.model.actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class Monster extends Actor {
	public final String monsterTypeID;
	
	public final int millisecondsPerMove;
	public Coord movementDestination = null;
	public long nextActionTime = 0;
	public boolean forceAggressive = false;
	public final CoordRect nextPosition;
	
	public final String phraseID;
	public final int exp;
	public final DropList dropList;
	public final String faction;
	public final int monsterClass;
	
	public Monster(MonsterType monsterType, Coord position) {
		super(monsterType, false);
		this.monsterTypeID = monsterType.id;
		this.position.set(position);
		this.millisecondsPerMove = Constants.MONSTER_MOVEMENT_TURN_DURATION_MS / monsterType.getMovesPerTurn();
		this.nextPosition = new CoordRect(new Coord(), actorTraits.tileSize);
		this.phraseID = monsterType.phraseID;
		this.exp = monsterType.exp;
		this.dropList = monsterType.dropList;
		this.faction = monsterType.faction;
		this.monsterClass = monsterType.monsterClass;
	}

	public void createLoot(Loot container, Player player) {
		int exp = this.exp;
		exp += exp * player.getSkillLevel(SkillCollection.SKILL_MORE_EXP) * SkillCollection.PER_SKILLPOINT_INCREASE_MORE_EXP_PERCENT / 100;
		container.exp += exp;
		if (this.dropList == null) return;
		this.dropList.createRandomLoot(container, player);
	}
	
	public boolean isAgressive() {
		return phraseID == null || forceAggressive;
	}
	
	
	// ====== PARCELABLE ===================================================================

	public static Monster readFromParcel(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		String monsterTypeId = src.readUTF();
		if (fileversion < 20) {
			monsterTypeId = monsterTypeId.replace(' ', '_').replace("\\'", "").toLowerCase();
		}
		MonsterType monsterType = world.monsterTypes.getMonsterType(monsterTypeId);
		
		if (fileversion < 25) return readFromParcel_pre_v0610(src, fileversion, monsterType);
		
		return new Monster(src, world, fileversion, monsterType);
	}

	public Monster(DataInputStream src, WorldContext world, int fileversion, MonsterType monsterType) throws IOException {
		super(src, world, fileversion, false, monsterType);
		this.monsterTypeID = monsterType.id;
		this.millisecondsPerMove = Constants.MONSTER_MOVEMENT_TURN_DURATION_MS / monsterType.getMovesPerTurn();
		this.nextPosition = new CoordRect(new Coord(), actorTraits.tileSize);
		this.phraseID = monsterType.phraseID;
		this.exp = monsterType.exp;
		this.dropList = monsterType.dropList;
		this.forceAggressive = src.readBoolean();
		this.faction = monsterType.faction;
		this.monsterClass = monsterType.monsterClass;
	}

	private static Monster readFromParcel_pre_v0610(DataInputStream src, int fileversion, MonsterType monsterType) throws IOException {
		Coord position = new Coord(src, fileversion);
		Monster m = new Monster(monsterType, position);
		m.ap.current = src.readInt();
		m.health.current = src.readInt();
		if (fileversion >= 12) {
			m.forceAggressive = src.readBoolean();
		}
		return m;
	}

	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeUTF(monsterTypeID);
		super.writeToParcel(dest, flags);
		dest.writeBoolean(forceAggressive);
	}
}
