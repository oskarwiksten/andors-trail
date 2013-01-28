package com.gpl.rpg.AndorsTrail.resource.parsers;

import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail.model.ability.traits.AbilityModifierTraits;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnEquip;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer.ResourceObjectParser;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
import com.gpl.rpg.AndorsTrail.util.L;

public final class ItemTraitsParser {
	private final ResourceObjectParser<ActorConditionEffect> actorConditionEffectParser_withDuration;
	private final ResourceObjectParser<ActorConditionEffect> actorConditionEffectParser_withoutDuration;
	private final ResourceFileTokenizer tokenize4Fields = new ResourceFileTokenizer(4);
	private final ResourceFileTokenizer tokenize2Fields = new ResourceFileTokenizer(2);
	
	public ItemTraitsParser(final ActorConditionTypeCollection actorConditionTypes) {
		this.actorConditionEffectParser_withDuration = new ResourceObjectParser<ActorConditionEffect>() {
			@Override
			public ActorConditionEffect parseRow(String[] parts) {
				return new ActorConditionEffect(
						actorConditionTypes.getActorConditionType(parts[0])
						, ResourceParserUtils.parseInt(parts[1], ActorCondition.MAGNITUDE_REMOVE_ALL)
						, ResourceParserUtils.parseInt(parts[2], ActorCondition.DURATION_FOREVER)
						, ResourceParserUtils.parseChance(parts[3])
					);
			}
		};
		this.actorConditionEffectParser_withoutDuration = new ResourceObjectParser<ActorConditionEffect>() {
			@Override
			public ActorConditionEffect parseRow(String[] parts) {
				return new ActorConditionEffect(
						actorConditionTypes.getActorConditionType(parts[0])
						, ResourceParserUtils.parseInt(parts[1], 1)
						, ActorCondition.DURATION_FOREVER
						, ResourceParserUtils.always
					);
			}
		};
	}
	
	public ItemTraits_OnUse parseItemTraits_OnUse(String[] parts, int startIndex, boolean parseTargetConditions) {
		boolean hasEffect = ResourceParserUtils.parseBoolean(parts[startIndex], false);
		if (!hasEffect) return null;
		
		ConstRange boostCurrentHP = ResourceParserUtils.parseConstRange(parts[startIndex + 1], parts[startIndex + 2]);
		ConstRange boostCurrentAP = ResourceParserUtils.parseConstRange(parts[startIndex + 3], parts[startIndex + 4]);
		final ArrayList<ActorConditionEffect> addedConditions_source = new ArrayList<ActorConditionEffect>();
		final ArrayList<ActorConditionEffect> addedConditions_target = new ArrayList<ActorConditionEffect>();
		tokenize4Fields.tokenizeArray(parts[startIndex + 5], addedConditions_source, actorConditionEffectParser_withDuration);
		if (parseTargetConditions) {
			tokenize4Fields.tokenizeArray(parts[startIndex + 6], addedConditions_target, actorConditionEffectParser_withDuration);
		}
		if (       boostCurrentHP == null 
				&& boostCurrentAP == null
				&& addedConditions_source.isEmpty()
				&& addedConditions_target.isEmpty()
			) {
			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				L.log("OPTIMIZE: Tried to parseItemTraits_OnUse , where hasEffect=" + parts[startIndex] + ", but all data was empty.");
			}
			return null;
		} else {
			return new ItemTraits_OnUse(
					new StatsModifierTraits(
							StatsModifierTraits.VISUAL_EFFECT_NONE
						,boostCurrentHP
						,boostCurrentAP
					)
					,listToArray(addedConditions_source)
					,listToArray(addedConditions_target)
					);
		}
	}
	
	public ItemTraits_OnEquip parseItemTraits_OnEquip(String[] parts, int startIndex) {
		boolean hasEffect = ResourceParserUtils.parseBoolean(parts[startIndex], false);
		if (!hasEffect) return null;
		
		AbilityModifierTraits stats = ResourceParserUtils.parseAbilityModifierTraits(parts, startIndex + 1);
		final ArrayList<ActorConditionEffect> addedConditions = new ArrayList<ActorConditionEffect>();
		tokenize2Fields.tokenizeArray(parts[startIndex + 12], addedConditions, actorConditionEffectParser_withoutDuration);
		
		if (stats == null && addedConditions.isEmpty()) {
			if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
				L.log("OPTIMIZE: Tried to parseItemTraits_OnEquip , where hasEffect=" + parts[startIndex] + ", but all data was empty.");
			}
			return null;
		} else {
			return new ItemTraits_OnEquip(stats, listToArray(addedConditions));
		}
	}
	
	private static ActorConditionEffect[] listToArray(ArrayList<ActorConditionEffect> list) {
		if (list.isEmpty()) return null;
		return list.toArray(new ActorConditionEffect[list.size()]);
	}
}
