package com.gpl.rpg.AndorsTrail.resource.parsers;

import android.util.FloatMath;

import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail.model.actor.ActorTraits;
import com.gpl.rpg.AndorsTrail.model.actor.MonsterType;
import com.gpl.rpg.AndorsTrail.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer.ResourceParserFor;
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
		final String monsterTypeId = parts[0];
		final CombatTraits combatTraits = ResourceParserUtils.parseCombatTraits(parts, 11);
		final ItemTraits_OnUse hitEffect = itemTraitsParser.parseItemTraits_OnUse(parts, 21, true);
		final ActorTraits baseTraits = new ActorTraits(
				ResourceParserUtils.parseImageID(tileLoader, parts[1])
				, ResourceParserUtils.parseSize(parts[4], size1x1) //TODO: This could be loaded from the tileset size instead.
				, combatTraits
				, ResourceParserUtils.parseInt(parts[10], 10)	// MoveCost
				, hitEffect == null ? null : new ItemTraits_OnUse[] { hitEffect }
				);
		baseTraits.name = parts[2];
		baseTraits.maxHP = ResourceParserUtils.parseInt(parts[8], 1);
		baseTraits.maxAP = ResourceParserUtils.parseInt(parts[9], 10);
		
		final int exp = getExpectedMonsterExperience(combatTraits, hitEffect, baseTraits.maxHP, baseTraits.maxAP);
		return new Pair<String, MonsterType>(monsterTypeId, new MonsterType(
			monsterTypeId
			, parts[3] 										// Tags
			, exp 											// Exp
			, droplists.getDropList(parts[19]) 				// Droplist
			, ResourceParserUtils.parseNullableString(parts[20]) 	// PhraseID
			, ResourceParserUtils.parseBoolean(parts[6], false)		// isUnique
			, ResourceParserUtils.parseNullableString(parts[7])		// Faction
			, ResourceParserUtils.parseInt(parts[5], MonsterType.MONSTERCLASS_HUMANOID) // MonsterClass
			, baseTraits
		));
	}
	
	private static float div100(int v) {
		return (float) v / 100f;
	}
	private static int getExpectedMonsterExperience(final CombatTraits t, ItemTraits_OnUse hitEffect, final int maxHP, final int maxAP) {
		if (t == null) return 0;
		final float avgAttackHP  = t.getAttacksPerTurn(maxAP) * div100(t.attackChance) * t.damagePotential.averagef() * (1 + div100(t.criticalSkill) * t.criticalMultiplier);
		final float avgDefenseHP = maxHP * (1 + div100(t.blockChance)) + Constants.EXP_FACTOR_DAMAGERESISTANCE * t.damageResistance;
		int attackConditionBonus = 0;
		if (hitEffect != null && hitEffect.addedConditions_target != null && hitEffect.addedConditions_target.length > 0) {
			attackConditionBonus += 50;
		}
		return (int) FloatMath.ceil((avgAttackHP * 3 + avgDefenseHP) * Constants.EXP_FACTOR_SCALING) + attackConditionBonus;
	}
}
