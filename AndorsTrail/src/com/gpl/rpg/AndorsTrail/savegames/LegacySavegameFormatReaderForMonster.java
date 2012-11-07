package com.gpl.rpg.AndorsTrail.savegames;

import java.io.DataInputStream;
import java.io.IOException;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer;
import com.gpl.rpg.AndorsTrail.savegames.LegacySavegameFormatReaderForPlayer.LegacySavegameData_Actor;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Range;

public class LegacySavegameFormatReaderForMonster {
	public static Monster readFromParcel_pre_v25(DataInputStream src, int fileversion, MonsterType monsterType) throws IOException {
		Monster m = new Monster(monsterType);
		m.position.set(new Coord(src, fileversion));
		m.ap.current = src.readInt();
		m.health.current = src.readInt();
		if (fileversion >= 12) {
			if (src.readBoolean()) m.forceAggressive();
		}
		return m;
	}
	
	/*
	public static Monster readFromParcel_pre_v33(DataInputStream src, WorldContext world, int fileversion, MonsterType monsterType) throws IOException {
		LegacySavegameData_Monster savegameData = readMonsterDataPreV33(src, world, fileversion, monsterType);
		return new Monster(savegameData, monsterType);
	}
	
	private static LegacySavegameData_Monster readMonsterDataPreV33(DataInputStream src, WorldContext world, int fileversion, MonsterType monsterType) throws IOException {
		LegacySavegameData_Monster result = new LegacySavegameData_Monster();
		result.isImmuneToCriticalHits = monsterType.isImmuneToCriticalHits();
		
		boolean readCombatTraits = true;
		if (fileversion >= 25) readCombatTraits = src.readBoolean();
		if (readCombatTraits) {
			result.attackCost = src.readInt();
			result.attackChance = src.readInt();
			result.criticalSkill = src.readInt();
			if (fileversion <= 20) {
				result.criticalMultiplier = src.readInt();
			} else {
				result.criticalMultiplier = src.readFloat();
			}
			result.damagePotential = new Range(src, fileversion);
			result.blockChance = src.readInt();
			result.damageResistance = src.readInt();
		}
		
		result.iconID = monsterType.iconID;
		result.tileSize = monsterType.tileSize;
		result.maxAP = monsterType.maxAP;
		result.maxHP = monsterType.maxHP;
		result.name = monsterType.name;
		result.moveCost = monsterType.moveCost;
		
		result.baseAttackCost = monsterType.attackCost;
		result.baseAttackChance = monsterType.attackChance;
		result.baseCriticalSkill = monsterType.criticalSkill;
		result.baseCriticalMultiplier = monsterType.criticalMultiplier;
		result.baseDamagePotential = new Range(monsterType.damagePotential);
		result.baseBlockChance = monsterType.blockChance;
		result.baseDamageResistance = monsterType.damageResistance;
		result.baseMoveCost = monsterType.moveCost;
		
		if (!readCombatTraits) {
			result.attackCost = result.baseAttackCost;
			result.attackChance = result.baseAttackChance;
			result.criticalSkill = result.baseCriticalSkill;
			result.criticalMultiplier = result.baseCriticalMultiplier;
			result.damagePotential = result.baseDamagePotential;
			result.blockChance = result.baseBlockChance;
			result.damageResistance = result.baseDamageResistance;
		}
		
		result.ap = new Range(src, fileversion);
		result.health = new Range(src, fileversion);
		result.position = new Coord(src, fileversion);
		result.rectPosition = new CoordRect(result.position, result.tileSize);
		if (fileversion > 16) {
			final int n = src.readInt();
			for(int i = 0; i < n; ++i) {
				result.conditions.add(new ActorCondition(src, world, fileversion));
			}
		}

		result.forceAggressive = src.readBoolean();
		if (fileversion >= 31) {
			if (src.readBoolean()) {
				result.shopItems = new ItemContainer(src, world, fileversion);
			}
		}
		
		return result;
	}
	
	public static final class LegacySavegameData_Monster extends LegacySavegameData_Actor {
		// from Monster
		public boolean forceAggressive;
		public ItemContainer shopItems;
	}*/
}
