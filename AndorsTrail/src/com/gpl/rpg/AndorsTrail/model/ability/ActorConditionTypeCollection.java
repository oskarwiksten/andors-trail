package com.gpl.rpg.AndorsTrail.model.ability;

import java.util.ArrayList;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.VisualEffectCollection;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.ability.traits.AbilityModifierTraits;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
import com.gpl.rpg.AndorsTrail.util.L;

public class ActorConditionTypeCollection {
	private final ArrayList<ActorConditionType> conditionTypes = new ArrayList<ActorConditionType>();
	
	public ActorConditionType getActorConditionType(String conditionTypeID) {
		for (ActorConditionType t : conditionTypes) {
			if (t.conditionTypeID.equals(conditionTypeID)) return t;
		}
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			L.log("WARNING: Cannot find ActorConditionType \"" + conditionTypeID + "\".");
		}
		return null;
	}
	
	public void initialize(DynamicTileLoader tileLoader) {
		CombatTraits t = new CombatTraits();
		t.attackChance = 5;
		conditionTypes.add(new ActorConditionType("bless", "Bless", tileLoader.prepareTileID("items_tiles", 13+22*14), false, null, null, new AbilityModifierTraits(0, 0, 0, t)));
		
		t = new CombatTraits();
		t.damagePotential.set(2, 2);
		conditionTypes.add(new ActorConditionType("str", "Strength", tileLoader.prepareTileID("items_tiles", 0+25*14), false, null, null, new AbilityModifierTraits(0, 0, 0, t)));
		
		conditionTypes.add(new ActorConditionType("regen", "Regeneration", tileLoader.prepareTileID("items_tiles", 7+22*14), false, new StatsModifierTraits(VisualEffectCollection.EFFECT_RESTORE_HP, new ConstRange(1, 1), null), null, null));
		conditionTypes.add(new ActorConditionType("poison", "Poison", tileLoader.prepareTileID("items_tiles", 4+24*14), true, new StatsModifierTraits(VisualEffectCollection.EFFECT_POISON, new ConstRange(-1, -1), null), null, null));
	}
}
