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
		
		if (!droplists.containsKey(name)) {
			L.log("WARNING: Cannot find droplist \"" + name + "\".");
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
		items.add(new DropItem(itemTypes.getItemTypeByTag("Insect wing"), animalpart, one));
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
		items.add(new DropItem(itemTypes.getItemType(ItemTypeCollection.ITEMTYPE_GOLD), often, new ConstRange(10, 7)));
		items.add(new DropItem(itemTypes.getItemTypeByTag("hair"), animalpart, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("gem1"), seldom, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("meat"), animalpart, one));
		items.add(new DropItem(itemTypes.getItemTypeByTag("boots1"), seldom, one));
		droplists.put("canineboss", new DropList(items));

	}
}
