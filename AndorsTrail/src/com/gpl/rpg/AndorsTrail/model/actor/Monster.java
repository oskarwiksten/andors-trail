package com.gpl.rpg.AndorsTrail.model.actor;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer;
import com.gpl.rpg.AndorsTrail.model.item.Loot;
import com.gpl.rpg.AndorsTrail.savegames.LegacySavegameFormatReaderForMonster;
import com.gpl.rpg.AndorsTrail.util.Coord;
import com.gpl.rpg.AndorsTrail.util.CoordRect;
import com.gpl.rpg.AndorsTrail.util.Range;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class Monster extends Actor {

	public Coord movementDestination = null;
	public long nextActionTime = 0;
	public final CoordRect nextPosition;

	private boolean forceAggressive = false;
	private ItemContainer shopItems = null;

	private final MonsterType monsterType;

	public Monster(MonsterType monsterType) {
		super(monsterType.tileSize, false, monsterType.isImmuneToCriticalHits());
		this.monsterType = monsterType;
		this.setIconID( monsterType.iconID );
		this.nextPosition = new CoordRect(new Coord(), monsterType.tileSize);
		resetStatsToBaseTraits();
		this.getAp().setMax();
		this.getHealth().setMax();
	}

	public void resetStatsToBaseTraits() {
		this.name = monsterType.name;
		this.getAp().setMax( monsterType.maxAP );
		this.getHealth().setMax( monsterType.maxHP );
		this.setMoveCost( monsterType.moveCost );
		this.setAttackCost( monsterType.attackCost );
		this.setAttackChance( monsterType.attackChance );
		this.setCriticalSkill( monsterType.criticalSkill );
		this.setCriticalMultiplier( monsterType.criticalMultiplier );
		if (monsterType.damagePotential != null) this.getDamagePotential().set(monsterType.damagePotential);
		else this.getDamagePotential().set(0, 0);
		this.setBlockChance(monsterType.blockChance);
		this.setDamageResistance(monsterType.damageResistance);
		this.setOnHitEffects(monsterType.onHitEffects);
	}

	public DropList getDropList() { return monsterType.dropList; }
	public int getExp() { return monsterType.exp; }
	public String getPhraseID() { return monsterType.phraseID; }
	public String getMonsterTypeID() { return monsterType.id; }
	public String getFaction() { return monsterType.faction; }
	public MonsterType.MonsterClass getMonsterClass() { return monsterType.monsterClass; }
	public MonsterType.AggressionType getMovementAggressionType() { return monsterType.aggressionType; }

	public void createLoot(Loot container, Player player) {
		int exp = this.getExp();
		exp += exp * player.getSkillLevel(SkillCollection.SkillID.moreExp) * SkillCollection.PER_SKILLPOINT_INCREASE_MORE_EXP_PERCENT / 100;
		container.exp += exp;
		DropList dropList = getDropList();
		if (dropList == null) return;
		dropList.createRandomLoot(container, player);
	}
	public ItemContainer getShopItems(Player player) {
		if (shopItems != null) return shopItems;
		Loot loot = new Loot();
		shopItems = loot.items;
		getDropList().createRandomLoot(loot, player);
		return shopItems;
	}
	public void resetShopItems() {
		this.shopItems = null;
	}
	public boolean isAdjacentTo(Player p) {
		return this.getRectPosition().isAdjacentTo(p.getPosition());
	}

	public boolean isAgressive() {
		return getPhraseID() == null || forceAggressive;
	}

	public void forceAggressive() {
		forceAggressive = true;
	}


	// ====== PARCELABLE ===================================================================

	public static Monster newFromParcel(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		String monsterTypeId = src.readUTF();
		if (fileversion < 20) {
			monsterTypeId = monsterTypeId.replace(' ', '_').replace("\\'", "").toLowerCase();
		}
		MonsterType monsterType = world.monsterTypes.getMonsterType(monsterTypeId);

		if (fileversion < 25) return LegacySavegameFormatReaderForMonster.newFromParcel_pre_v25(src, fileversion, monsterType);

		return new Monster(src, world, fileversion, monsterType);
	}

	private Monster(DataInputStream src, WorldContext world, int fileversion, MonsterType monsterType) throws IOException {
		this(monsterType);

		boolean readCombatTraits = true;
		if (fileversion >= 25) readCombatTraits = src.readBoolean();
		if (readCombatTraits) {
			this.setAttackCost( src.readInt() );
			this.setAttackChance( src.readInt() );
			this.setCriticalSkill( src.readInt() );
			if (fileversion <= 20) {
				this.setCriticalMultiplier( src.readInt() );
			} else {
				this.setCriticalMultiplier( src.readFloat() );
			}
			this.getDamagePotential().set(new Range(src, fileversion));
			this.setBlockChance(src.readInt());
			this.setDamageResistance(src.readInt());
		}

		this.getAp().readFromParcel(src, fileversion);
		this.getHealth().readFromParcel(src, fileversion);
		this.getPosition().readFromParcel(src, fileversion);
		if (fileversion > 16) {
			final int numConditions = src.readInt();
			for(int i = 0; i < numConditions; ++i) {
				getConditions().add(new ActorCondition(src, world, fileversion));
			}
		}

		if (fileversion >= 34) {
			this.setMoveCost( src.readInt() );
		}

		this.forceAggressive = src.readBoolean();
		if (fileversion >= 31) {
			if (src.readBoolean()) {
				this.shopItems = ItemContainer.newFromParcel(src, world, fileversion);
			}
		}
	}

	public void writeToParcel(DataOutputStream dest) throws IOException {
		dest.writeUTF(getMonsterTypeID());
		if (getAttackCost() == monsterType.attackCost
				&& getAttackChance() == monsterType.attackChance
				&& getCriticalSkill() == monsterType.criticalSkill
				&& getCriticalMultiplier() == monsterType.criticalMultiplier
				&& getDamagePotential().equals(monsterType.damagePotential)
				&& getBlockChance() == monsterType.blockChance
				&& getDamageResistance() == monsterType.damageResistance
				) {
			dest.writeBoolean(false);
		} else {
			dest.writeBoolean(true);
			dest.writeInt(getAttackCost());
			dest.writeInt(getAttackChance());
			dest.writeInt(getCriticalSkill());
			dest.writeFloat(getCriticalMultiplier());
			getDamagePotential().writeToParcel(dest);
			dest.writeInt(getBlockChance());
			dest.writeInt(getDamageResistance());
		}
		getAp().writeToParcel(dest);
		getHealth().writeToParcel(dest);
		getPosition().writeToParcel(dest);
		dest.writeInt(getConditions().size());
		for (ActorCondition c : getConditions()) {
			c.writeToParcel(dest);
		}
		dest.writeInt(getMoveCost());

		dest.writeBoolean(forceAggressive);
		if (shopItems != null) {
			dest.writeBoolean(true);
			shopItems.writeToParcel(dest);
		} else {
			dest.writeBoolean(false);
		}
	}
}
