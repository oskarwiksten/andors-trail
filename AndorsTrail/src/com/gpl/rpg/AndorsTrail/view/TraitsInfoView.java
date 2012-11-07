package com.gpl.rpg.AndorsTrail.view;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.ActorTraits;
import com.gpl.rpg.AndorsTrail.util.Range;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TraitsInfoView extends TableLayout {
	private final TableRow traitsinfo_attack_row1;
	private final TableRow traitsinfo_attack_row2;
	private final TableRow traitsinfo_attack_row3;
	private final TableRow traitsinfo_critical_row1;
	private final TableRow traitsinfo_critical_row2;
	private final TableRow traitsinfo_critical_row3;
	private final TableRow traitsinfo_defense_row1;
	private final TableRow traitsinfo_defense_row2;
	private final TextView traitsinfo_attack_cost;
	private final TextView traitsinfo_attack_chance;
	private final TextView traitsinfo_attack_damage;
	private final TextView traitsinfo_criticalhit_skill;
	private final TextView traitsinfo_criticalhit_multiplier;
	private final TextView traitsinfo_criticalhit_effectivechance;
	private final TextView traitsinfo_defense_chance;
	private final TextView traitsinfo_defense_damageresist;
	
	public TraitsInfoView(Context context, AttributeSet attr) {
		this(context, attr, R.layout.traitsinfoview);
	}
	
	public TraitsInfoView(Context context, AttributeSet attr, int layoutResourceID) {
		super(context, attr);
        setFocusable(false);
        inflate(context, layoutResourceID, this);
        
        traitsinfo_attack_row1 = (TableRow) findViewById(R.id.traitsinfo_attack_row1);
        traitsinfo_attack_row2 = (TableRow) findViewById(R.id.traitsinfo_attack_row2);
        traitsinfo_attack_row3 = (TableRow) findViewById(R.id.traitsinfo_attack_row3);
        traitsinfo_critical_row1 = (TableRow) findViewById(R.id.traitsinfo_critical_row1);
        traitsinfo_critical_row2 = (TableRow) findViewById(R.id.traitsinfo_critical_row2);
        traitsinfo_critical_row3 = (TableRow) findViewById(R.id.traitsinfo_critical_row3);
        traitsinfo_defense_row1 = (TableRow) findViewById(R.id.traitsinfo_defense_row1);
        traitsinfo_defense_row2 = (TableRow) findViewById(R.id.traitsinfo_defense_row2);
        traitsinfo_attack_cost = (TextView) findViewById(R.id.traitsinfo_attack_cost);
        traitsinfo_attack_chance = (TextView) findViewById(R.id.traitsinfo_attack_chance);
        traitsinfo_attack_damage = (TextView) findViewById(R.id.traitsinfo_attack_damage);
        traitsinfo_criticalhit_skill = (TextView) findViewById(R.id.traitsinfo_criticalhit_skill);
        traitsinfo_criticalhit_multiplier = (TextView) findViewById(R.id.traitsinfo_criticalhit_multiplier);
        traitsinfo_criticalhit_effectivechance = (TextView) findViewById(R.id.traitsinfo_criticalhit_effectivechance);
        traitsinfo_defense_chance = (TextView) findViewById(R.id.traitsinfo_defense_chance);
        traitsinfo_defense_damageresist = (TextView) findViewById(R.id.traitsinfo_defense_damageresist);
    }

	public void update(Actor actor) {
		update(
			actor.getAttackCost()
			,actor.getAttackChance()
			,actor.getDamagePotential()
			,actor.getCriticalSkill()
			,actor.getCriticalMultiplier()
			,actor.getBlockChance()
			,actor.getDamageResistance());
	}
	
	public void update(
			int attackCost
			,int attackChance
			,Range damagePotential
			,int criticalSkill
			,float criticalMultiplier
			,int blockChance
			,int damageResistance
		) { 
		if (attackCost != 0) {
			traitsinfo_attack_row1.setVisibility(View.VISIBLE);
			traitsinfo_attack_cost.setText(Integer.toString(attackCost));
		} else {
			traitsinfo_attack_row1.setVisibility(View.GONE);
		}
		if (attackChance != 0) {
			traitsinfo_attack_row2.setVisibility(View.VISIBLE);
			traitsinfo_attack_chance.setText(Integer.toString(attackChance) + "%");
		} else {
			traitsinfo_attack_row2.setVisibility(View.GONE);
		}
		if (damagePotential.max != 0) {
			traitsinfo_attack_row3.setVisibility(View.VISIBLE);
			traitsinfo_attack_damage.setText(damagePotential.toMinMaxString());
		} else {
			traitsinfo_attack_row3.setVisibility(View.GONE);
		}
		if (criticalSkill != 0) {
			traitsinfo_critical_row1.setVisibility(View.VISIBLE);
			traitsinfo_criticalhit_skill.setText(Integer.toString(criticalSkill));
		} else {
			traitsinfo_critical_row1.setVisibility(View.GONE);
		}
		if (criticalMultiplier != 0 && criticalMultiplier != 1) {
			traitsinfo_critical_row2.setVisibility(View.VISIBLE);
			traitsinfo_criticalhit_multiplier.setText(Float.toString(criticalMultiplier));
		} else {
			traitsinfo_critical_row2.setVisibility(View.GONE);
		}
		if (criticalSkill != 0 && criticalMultiplier != 0 && criticalMultiplier != 1) {
			traitsinfo_critical_row3.setVisibility(View.VISIBLE);
			traitsinfo_criticalhit_effectivechance.setText(Integer.toString(Actor.getEffectiveCriticalChance(criticalSkill)) + "%");
		} else {
			traitsinfo_critical_row3.setVisibility(View.GONE);
		}
		if (blockChance != 0) {
			traitsinfo_defense_row1.setVisibility(View.VISIBLE);
			traitsinfo_defense_chance.setText(Integer.toString(blockChance) + "%");
		} else {
			traitsinfo_defense_row1.setVisibility(View.GONE);
		}
		if (damageResistance != 0) {
			traitsinfo_defense_row2.setVisibility(View.VISIBLE);
			traitsinfo_defense_damageresist.setText(Integer.toString(damageResistance));
		} else {
			traitsinfo_defense_row2.setVisibility(View.GONE);
		}
	}
	
	/*
	public void update(ActorTraits traits) {
		if (traits != null && traits.attackCost != 0) {
			traitsinfo_attack_row1.setVisibility(View.VISIBLE);
			traitsinfo_attack_cost.setText(Integer.toString(traits.attackCost));
		} else {
			traitsinfo_attack_row1.setVisibility(View.GONE);
		}
		if (traits != null && traits.hasAttackChanceEffect()) {
			traitsinfo_attack_row2.setVisibility(View.VISIBLE);
			traitsinfo_attack_chance.setText(Integer.toString(traits.attackChance) + "%");
		} else {
			traitsinfo_attack_row2.setVisibility(View.GONE);
		}
		if (traits != null && traits.hasAttackDamageEffect()) {
			traitsinfo_attack_row3.setVisibility(View.VISIBLE);
			traitsinfo_attack_damage.setText(traits.damagePotential.toMinMaxString());
		} else {
			traitsinfo_attack_row3.setVisibility(View.GONE);
		}
		if (traits != null && traits.hasCriticalSkillEffect()) {
			traitsinfo_critical_row1.setVisibility(View.VISIBLE);
			traitsinfo_criticalhit_skill.setText(Integer.toString(traits.criticalSkill));
		} else {
			traitsinfo_critical_row1.setVisibility(View.GONE);
		}
		if (traits != null && traits.hasCriticalMultiplierEffect()) {
			traitsinfo_critical_row2.setVisibility(View.VISIBLE);
			traitsinfo_criticalhit_multiplier.setText(Float.toString(traits.criticalMultiplier));
		} else {
			traitsinfo_critical_row2.setVisibility(View.GONE);
		}
		if (traits != null && traits.hasCriticalAttacks()) {
			traitsinfo_critical_row3.setVisibility(View.VISIBLE);
			traitsinfo_criticalhit_effectivechance.setText(Integer.toString(traits.getEffectiveCriticalChance()) + "%");
		} else {
			traitsinfo_critical_row3.setVisibility(View.GONE);
		}
		if (traits != null && traits.hasBlockEffect()) {
			traitsinfo_defense_row1.setVisibility(View.VISIBLE);
			traitsinfo_defense_chance.setText(Integer.toString(traits.blockChance) + "%");
		} else {
			traitsinfo_defense_row1.setVisibility(View.GONE);
		}
		if (traits != null && traits.damageResistance != 0) {
			traitsinfo_defense_row2.setVisibility(View.VISIBLE);
			traitsinfo_defense_damageresist.setText(Integer.toString(traits.damageResistance));
		} else {
			traitsinfo_defense_row2.setVisibility(View.GONE);
		}
	}
	*/
}
