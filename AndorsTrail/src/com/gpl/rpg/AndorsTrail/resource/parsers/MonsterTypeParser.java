package com.gpl.rpg.AndorsTrail.resource.parsers;

import android.util.FloatMath;

import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer.ResourceParserFor;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
import com.gpl.rpg.AndorsTrail.util.Pair;
import com.gpl.rpg.AndorsTrail.util.Size;

public final class MonsterTypeParser extends ResourceParserFor<MonsterType> {

	private final Size size1x1 = new Size(1, 1);
	private final DropListCollection droplists;
	private final ItemTraitsParser itemTraitsParser;
	private final DynamicTileLoader tileLoader;
	
	public MonsterTypeParser(final DropListCollection droplists, final ActorConditionTypeCollection actorConditionTypes, final DynamicTileLoader tileLoader) {
		super(28);
		this.itemTraitsParser = new ItemTraitsParser(actorConditionTypes);
		this.droplists = droplists;
		this.tileLoader = tileLoader;
	}
	
	@Override
	public Pair<String, MonsterType> parseRow(String[] parts) {
		final ItemTraits_OnUse hitEffect = itemTraitsParser.parseItemTraits_OnUse(parts, 21, true);
		int maxHP = ResourceParserUtils.parseInt(parts[8], 1);
		int maxAP = ResourceParserUtils.parseInt(parts[9], 10);
		int attackCost = ResourceParserUtils.parseInt(parts[11], 10);
		int attackChance = ResourceParserUtils.parseInt(parts[12], 0);
		ConstRange damagePotential = ResourceParserUtils.parseConstRange(parts[15], parts[16]);
		int criticalSkill = ResourceParserUtils.parseInt(parts[13], 0);
		float criticalMultiplier = ResourceParserUtils.parseFloat(parts[14], 0);
		int blockChance = ResourceParserUtils.parseInt(parts[17], 0);
		int damageResistance = ResourceParserUtils.parseInt(parts[18], 0);
		
		final int exp = getExpectedMonsterExperience(attackCost, attackChance, damagePotential, criticalSkill, criticalMultiplier, blockChance, damageResistance, hitEffect, maxHP, maxAP);
		
		final String monsterTypeId = parts[0];
		return new Pair<String, MonsterType>(monsterTypeId, new MonsterType(
			monsterTypeId
			, parts[2]										// Name
			, parts[3] 										// SpawnGroup
			, exp 											// Exp
			, droplists.getDropList(parts[19]) 				// Droplist
			, ResourceParserUtils.parseNullableString(parts[20]) 	// PhraseID
			, ResourceParserUtils.parseBoolean(parts[6], false)		// isUnique
			, ResourceParserUtils.parseNullableString(parts[7])		// Faction
			, ResourceParserUtils.parseInt(parts[5], MonsterType.MONSTERCLASS_HUMANOID) // MonsterClass
			, ResourceParserUtils.parseSize(parts[4], size1x1) //TODO: This could be loaded from the tileset size instead.
			, ResourceParserUtils.parseImageID(tileLoader, parts[1]) // IconID
			, maxAP
			, maxHP
			, ResourceParserUtils.parseInt(parts[10], 10)	// MoveCost
			, attackCost
			, attackChance
			, criticalSkill
			, criticalMultiplier
			, damagePotential
			, blockChance
			, damageResistance
			, hitEffect == null ? null : new ItemTraits_OnUse[] { hitEffect }
		));
	}
	
	private static float div100(int v) {
		return (float) v / 100f;
	}
	private static int getExpectedMonsterExperience(
			int attackCost,
			int attackChance,
			ConstRange damagePotential,
			int criticalSkill,
			float criticalMultiplier,
			int blockChance,
			int damageResistance,
			ItemTraits_OnUse hitEffect, 
			final int maxHP, 
			final int maxAP) {
		final float averageDamage = damagePotential != null ? damagePotential.averagef() : 0;
		final float avgAttackHP  = getAttacksPerTurn(maxAP, attackCost) * div100(attackChance) * averageDamage * (1 + div100(criticalSkill) * criticalMultiplier);
		final float avgDefenseHP = maxHP * (1 + div100(blockChance)) + Constants.EXP_FACTOR_DAMAGERESISTANCE * damageResistance;
		int attackConditionBonus = 0;
		if (hitEffect != null && hitEffect.addedConditions_target != null && hitEffect.addedConditions_target.length > 0) {
			attackConditionBonus += 50;
		}
		return (int) FloatMath.ceil((avgAttackHP * 3 + avgDefenseHP) * Constants.EXP_FACTOR_SCALING) + attackConditionBonus;
	}
	private static int getAttacksPerTurn(int maxAP, int attackCost) {
		return (int) Math.floor(maxAP / attackCost);
	}
}
