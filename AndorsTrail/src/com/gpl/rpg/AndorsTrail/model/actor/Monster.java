package com.gpl.rpg.AndorsTrail.model.actor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class Monster extends Actor {
	public final MonsterType monsterType;
	
	public final int millisecondsPerMove;
	public Coord movementDestination = null;
	public long nextActionTime = 0;
	public boolean forceAggressive = false;
	public final CoordRect nextPosition;
	
	public Monster(MonsterType monsterType, Coord position) {
		super(monsterType, false);
		this.monsterType = monsterType;
		this.position.set(position);
		this.millisecondsPerMove = Constants.MONSTER_MOVEMENT_TURN_DURATION_MS / monsterType.getMovesPerTurn();
		this.nextPosition = new CoordRect(new Coord(), traits.tileSize);
	}
	
	public void createLoot(Loot container, Player player) {
		int exp = monsterType.exp;
		exp += exp * player.getSkillLevel(Skills.SKILL_MORE_EXP) * Skills.PER_SKILLPOINT_INCREASE_MORE_EXP_PERCENT / 100;
		container.exp += exp;
		if (monsterType.dropList == null) return;
		monsterType.dropList.createRandomLoot(container, player);
	}
	
	public boolean isAgressive() {
		return monsterType.phraseID == null || forceAggressive;
	}
	
	
	// ====== PARCELABLE ===================================================================

	public static Monster readFromParcel(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		String monsterTypeId = src.readUTF();
		if (fileversion < 20) {
			monsterTypeId = monsterTypeId.replace(' ', '_').replace("\\'", "").toLowerCase();
		}
		MonsterType monsterType = world.monsterTypes.getMonsterType(monsterTypeId);
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
		dest.writeUTF(monsterType.id);
		position.writeToParcel(dest, flags);
		dest.writeInt(ap.current);
		dest.writeInt(health.current);
		dest.writeBoolean(forceAggressive);
	}
}
