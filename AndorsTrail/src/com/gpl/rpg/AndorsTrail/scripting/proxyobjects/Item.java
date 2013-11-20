package com.gpl.rpg.AndorsTrail.scripting.proxyobjects;

import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnEquip;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.scripting.Script;

public class Item {
	
	public String id;
	public String category;
	public ItemReward reward;
	public Script[] privateScripts;
	
	public Item(ItemType type, ItemTraits_OnUse useEffect, ItemTraits_OnEquip equipEffect) {
		this.id = type.id;
		this.category = type.category.id;
		if (useEffect != null) {
			this.reward = new ItemReward(useEffect);
		} else if (equipEffect != null){
			this.reward = new ItemReward(equipEffect);
		}
		this.privateScripts = type.private_scripts;
	}
	
}
