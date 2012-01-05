package com.gpl.rpg.AndorsTrail.resource.parsers;

import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
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
		super(25);
		this.itemTraitsParser = new ItemTraitsParser(actorConditionTypes);
		this.droplists = droplists;
		this.tileLoader = tileLoader;
	}
	
	@Override
	public Pair<String, MonsterType> parseRow(String[] parts) {
		final String monsterTypeId = parts[0];
		final int maxHP = ResourceParserUtils.parseInt(parts[5], 1);
		final int maxAP = ResourceParserUtils.parseInt(parts[6], 10);
		final CombatTraits combatTraits = ResourceParserUtils.parseCombatTraits(parts, 8);
		final ItemTraits_OnUse hitEffect = itemTraitsParser.parseItemTraits_OnUse(parts, 18, true);
		final int exp = getExpectedMonsterExperience(combatTraits, hitEffect, maxHP, maxAP);
		return new Pair<String, MonsterType>(monsterTypeId, new MonsterType(
			monsterTypeId
			, parts[2]										// Name
			, parts[3] 										// Tags
			, ResourceParserUtils.parseImageID(tileLoader, parts[1])
			, ResourceParserUtils.parseSize(parts[4], size1x1) //TODO: This could be loaded from the tileset size instead.
			, maxHP 										// HP
			, maxAP											// AP
			, ResourceParserUtils.parseInt(parts[7], 10)		// MoveCost
			, combatTraits
	        , hitEffect
			, exp 											// Exp
			, droplists.getDropList(parts[16]) 				// Droplist
			, ResourceParserUtils.parseNullableString(parts[17]) // PhraseID
			, null 											// Faction
		));
	}
	
	private static float div100(int v) {
		return (float) v / 100f;
	}
	private static int getExpectedMonsterExperience(final CombatTraits t, ItemTraits_OnUse hitEffect, final int maxHP, final int maxAP) {
		if (t == null) return 0;
		final float avgAttackHP  = t.getAttacksPerTurn(maxAP) * div100(t.attackChance) * t.damagePotential.averagef() * (1 + div100(t.criticalChance) * t.criticalMultiplier);
		final float avgDefenseHP = maxHP * (1 + div100(t.blockChance)) + Constants.EXP_FACTOR_DAMAGERESISTANCE * t.damageResistance;
		int attackConditionBonus = 0;
		if (hitEffect != null && hitEffect.addedConditions_target != null && hitEffect.addedConditions_target.length > 0) {
			attackConditionBonus += 50;
		}
		return (int) Math.ceil((avgAttackHP * 3 + avgDefenseHP) * Constants.EXP_FACTOR_SCALING) + attackConditionBonus;
	}
}
