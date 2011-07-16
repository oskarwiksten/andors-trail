package com.gpl.rpg.AndorsTrail.view;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public final class TraitsInfoView extends TableLayout {
	private final TableRow traitsinfo_attack_row1;
	private final TableRow traitsinfo_attack_row2;
	private final TableRow traitsinfo_attack_row3;
	private final TableRow traitsinfo_critical_row1;
	private final TableRow traitsinfo_critical_row2;
	private final TableRow traitsinfo_defense_row1;
	private final TableRow traitsinfo_defense_row2;
	private final TextView traitsinfo_attack_cost;
	private final TextView traitsinfo_attack_chance;
	private final TextView traitsinfo_attack_damage;
	private final TextView traitsinfo_criticalhit_chance;
	private final TextView traitsinfo_criticalhit_multiplier;
	private final TextView traitsinfo_defense_chance;
	private final TextView traitsinfo_defense_damageresist;
	
	public TraitsInfoView(Context context, AttributeSet attr) {
		super(context, attr);
        setFocusable(false);
        inflate(context, R.layout.traitsinfoview, this);
        
        traitsinfo_attack_row1 = (TableRow) findViewById(R.id.traitsinfo_attack_row1);
        traitsinfo_attack_row2 = (TableRow) findViewById(R.id.traitsinfo_attack_row2);
        traitsinfo_attack_row3 = (TableRow) findViewById(R.id.traitsinfo_attack_row3);
        traitsinfo_critical_row1 = (TableRow) findViewById(R.id.traitsinfo_critical_row1);
        traitsinfo_critical_row2 = (TableRow) findViewById(R.id.traitsinfo_critical_row2);
        traitsinfo_defense_row1 = (TableRow) findViewById(R.id.traitsinfo_defense_row1);
        traitsinfo_defense_row2 = (TableRow) findViewById(R.id.traitsinfo_defense_row2);
        traitsinfo_attack_cost = (TextView) findViewById(R.id.traitsinfo_attack_cost);
        traitsinfo_attack_chance = (TextView) findViewById(R.id.traitsinfo_attack_chance);
        traitsinfo_attack_damage = (TextView) findViewById(R.id.traitsinfo_attack_damage);
        traitsinfo_criticalhit_chance = (TextView) findViewById(R.id.traitsinfo_criticalhit_chance);
        traitsinfo_criticalhit_multiplier = (TextView) findViewById(R.id.traitsinfo_criticalhit_multiplier);
        traitsinfo_defense_chance = (TextView) findViewById(R.id.traitsinfo_defense_chance);
        traitsinfo_defense_damageresist = (TextView) findViewById(R.id.traitsinfo_defense_damageresist);
    }

	public void update(CombatTraits traits) {
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
		if (traits != null && traits.hasCriticalChanceEffect()) {
			traitsinfo_critical_row1.setVisibility(View.VISIBLE);
			traitsinfo_criticalhit_chance.setText(Integer.toString(traits.criticalChance) + "%");
		} else {
			traitsinfo_critical_row1.setVisibility(View.GONE);
		}
		if (traits != null && traits.hasCriticalMultiplierEffect()) {
			traitsinfo_critical_row2.setVisibility(View.VISIBLE);
			traitsinfo_criticalhit_multiplier.setText(Float.toString(traits.criticalMultiplier));
		} else {
			traitsinfo_critical_row2.setVisibility(View.GONE);
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
}
