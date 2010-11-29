package com.gpl.rpg.AndorsTrail.model.item;

import java.util.ArrayList;
import java.util.HashMap;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.item.DropList.DropItem;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
import com.gpl.rpg.AndorsTrail.util.L;

public final class DropListCollection {
	public static final String DROPLIST_STARTITEMS = "startitems";
	
	private final HashMap<String, DropList> droplists = new HashMap<String, DropList>();
	
	public DropList getDropList(String name) {
		if (name == null || name.length() <= 0) return null;
		
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!droplists.containsKey(name)) {
				L.log("WARNING: Cannot find droplist \"" + name + "\".");
			}
		}
		return droplists.get(name);
	}
	
	public void initialize(ItemTypeCollection itemTypes) {
		ArrayList<DropItem> items = new ArrayList<DropItem>();
		final ConstRange one = new ConstRange(1, 1);
		final ConstRange ten = new ConstRange(10, 10);
		final ConstRange five = new ConstRange(5, 5);
		final ConstRange always = one;
		
		if (AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
			items.clear();
			items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), new ConstRange(100, 100), new ConstRange(10, 0)));
			items.add(new DropItem(itemTypes.getItemType(1), new ConstRange(100, 10), new ConstRange(1, 1)));
			items.add(new DropItem(itemTypes.getItemType(2), new ConstRange(100, 50), new ConstRange(1, 1)));
			droplists.put("list1", new DropList(items));
			
			items.clear();
			for(int i = 1; i <= 20; ++i) {
				items.add(new DropItem(itemTypes.getItemType(i), always, one));
			}
			droplists.put("shop1", new DropList(items));
		}
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), always, new ConstRange(12, 12)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("club1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shirt1"), always, one));
		if (!AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
			items.add(new DropItem(itemTypes.getItemTypeByTag("ring_mikhail"), always, one));
		}
		droplists.put(DROPLIST_STARTITEMS, new DropList(items));
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemTypeByTag("health_minor"), always, ten));
		items.add(new DropItem(itemTypes.getItemTypeByTag("health"), always, ten));
		items.add(new DropItem(itemTypes.getItemTypeByTag("health_major"), always, ten));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring_dmg1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring_dmg2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring_block1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring_atkch1"), always, one));
		droplists.put("shop_priest", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemTypeByTag("apple_green"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("meat"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("meat_cooked"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("Carrot"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("Bread"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("Mushroom"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("Eggs"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("Mead"), always, five));
		droplists.put("shop_food", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemTypeByTag("shirt1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shirt2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("hat1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("hat2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gloves1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gloves2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gloves3"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gloves4"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("boots1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("boots2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("boots3"), always, one));
		droplists.put("shop_clothes", new DropList(items));
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemTypeByTag("club1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("club3"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ironsword0"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("hammer0"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("hammer1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("dagger0"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("dagger1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("dagger2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shortsword1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ironsword1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ironsword2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("broadsword1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("broadsword2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("steelsword1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("axe1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("axe2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("armor1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("armor2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("armor3"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shield1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shield3"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shield4"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shield5"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("boots1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("boots5"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("hat3"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("hat4"), always, one));
		droplists.put("shop_smith", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemTypeByTag("shirt1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shirt2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem1"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem2"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem3"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring_dmg5"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring_dmg6"), always, one));
		droplists.put("shop_thief", new DropList(items));
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), always, new ConstRange(2, 0)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("tail_trainingrat"), always, one));
		droplists.put("trainingrat", new DropList(items));
		
		final ConstRange seldom = new ConstRange(100, 30);
		final ConstRange very_seldom = new ConstRange(100, 5);
		//final ConstRange sometimes = new ConstRange(100, 50);
		final ConstRange often = new ConstRange(100, 70);
		final ConstRange animalpart = seldom;
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), always, new ConstRange(4, 2)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("rat_tail"), animalpart, one));
		droplists.put("rat", new DropList(items));
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), always, ten));
		items.add(new DropItem(itemTypes.getItemTypeByTag("tail_caverat"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem1"), very_seldom, one));
		droplists.put("caveratboss", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(4, 2)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("insectwing"), animalpart, one));
		droplists.put("wasp", new DropList(items));
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(4, 2)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shell"), animalpart, one));
		droplists.put("insect", new DropList(items));
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(6, 3)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem1"), very_seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("meat"), animalpart, one));
		droplists.put("canine", new DropList(items));
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(6, 3)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("meat"), animalpart, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gland"), very_seldom, one));
		droplists.put("snake", new DropList(items));
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(15, 5)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem2"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("health_minor"), seldom, one));
		droplists.put("lich1", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(9, 9)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("dagger_venom"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem3"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("health"), always, one));
		droplists.put("snakemaster", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(10, 7)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("hair"), animalpart, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem1"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("meat"), animalpart, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("boots1"), seldom, one));
		droplists.put("canineboss", new DropList(items));
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(8, 4)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem1"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("claws"), animalpart, one));
		droplists.put("cavecritter", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(12, 4)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem2"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("hammer0"), very_seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("health_minor"), seldom, one));
		droplists.put("cavemonster", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemTypeByTag("neck_irogotu"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring_gandir"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("health"), always, one));
		droplists.put("irogotu", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemTypeByTag("vial_empty1"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem1"), seldom, one));
		droplists.put("haunt", new DropList(items));

		items.clear();
		//TODO: Fill with heartsteel items.
		droplists.put("nocmar", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shirt1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shirt2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem1"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem2"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem3"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("quickdagger1"), always, one));
		droplists.put("ganos", new DropList(items));	
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemTypeByTag("shirt1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shirt2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shirt_dmgresist"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("boots3"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gloves_attack1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gloves_attack2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("jewel_fallhaven"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring_dmg1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring_dmg2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring_block1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("ring_atkch1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("necklace_shield1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("necklace_shield2"), always, one));
		droplists.put("fallhaven_clothes", new DropList(items));	

		items.clear();
		items.add(new DropItem(itemTypes.getItemTypeByTag("vial_empty1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("vial_empty2"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("vial_empty3"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("vial_empty4"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("health_minor"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("health"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("health_major"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("milk"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("rat_tail"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("Radish"), always, five));
		items.add(new DropItem(itemTypes.getItemTypeByTag("Strawberry"), always, five));
		droplists.put("fallhaven_potions", new DropList(items));
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemTypeByTag("bonemeal_potion"), always, ten));
		droplists.put("thoronir", new DropList(items));
		
		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(12, 4)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("club1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("calomyran_secrets"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("milk"), always, one));
		droplists.put("larcal", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(12, 4)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem2"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("health"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("vial_empty2"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gloves1"), very_seldom, one));
		droplists.put("catacombguard", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(5, 1)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem1"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("vial_empty1"), seldom, one));
		droplists.put("catacombrat", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(5, 1)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem1"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("key_luthor"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("health_major"), always, one));
		droplists.put("luthor", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(23, 16)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem2"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("health"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("bone"), animalpart, one));
		droplists.put("skeleton", new DropList(items));

		items.clear();
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(30, 16)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem3"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("health"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("bone"), always, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("shield1"), seldom, one));
		droplists.put("skeletonmaster", new DropList(items));
	}
	
	// Selftest method. Not part of the game logic.
	public boolean verifyExistsDroplist(int itemTypeID) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			for (DropList d : droplists.values()) {
				if (d.contains(itemTypeID)) return true;
			}
		}
		return false;
	}
}
