package com.gpl.rpg.AndorsTrail.resource;

import java.util.regex.Pattern;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.CombatTraits;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.map.TMXMapReader;
import com.gpl.rpg.AndorsTrail.util.ConstRange;
import com.gpl.rpg.AndorsTrail.util.L;
import com.gpl.rpg.AndorsTrail.util.Size;

import android.content.res.Resources;

public final class ResourceLoader {

	public static void loadResources(WorldContext world, Resources r) {
    	
        final TileStore tiles = world.tileStore;
        tiles.displayTileSize = DynamicTileLoader.measureBitmapWidth(r, R.drawable.equip_body); // Should be 32 on regular size.
        L.log("displayTileSize=" + tiles.displayTileSize);
        final int mTileSize = tiles.displayTileSize;
        L.log("mTileSize=" + mTileSize);
        
        //final Size dst_sz2x2 = new Size(mTileSize*2, mTileSize*2);
        //final Size dst_sz2x3 = new Size(mTileSize*2, mTileSize*3);
        //final Size dst_sz4x3 = new Size(mTileSize*4, mTileSize*3);
        final Size dst_sz1x1 = new Size(mTileSize, mTileSize);
        final Size defaultTileSize = dst_sz1x1;
        final Size src_sz1x1 = new Size(1, 1);
        final Size src_sz6x1 = new Size(6, 1);
        final Size src_sz7x1 = new Size(7, 1);
        final Size src_mapTileSize = new Size(16, 8);
        final Size src_mapTileSize7 = new Size(16, 7);
        
        DynamicTileLoader loader = new DynamicTileLoader(tiles, r);
        
        // ========================================================================
        // Load various ui icons
        loader.prepareTileset(R.drawable.char_hero, "char_hero", src_sz1x1, defaultTileSize);
        loader.prepareTileset(R.drawable.map_tiles_1_2, "map_tiles_1_2", src_mapTileSize, defaultTileSize);
        loader.prepareTileset(R.drawable.map_tiles_2_7, "map_tiles_2_7", src_mapTileSize, defaultTileSize);
        loader.prepareTileset(R.drawable.map_tiles_1_6, "map_tiles_1_6", src_mapTileSize, defaultTileSize);
        /*tiles.iconID_CHAR_HERO = */loader.getTileID(R.drawable.char_hero, 0);
        /*tiles.iconID_attackselect = */loader.getTileID(R.drawable.map_tiles_1_2, 6+16*5);
        /*tiles.iconID_moveselect = */loader.getTileID(R.drawable.map_tiles_1_2, 7+16*5);
        /*tiles.iconID_groundbag = */loader.getTileID(R.drawable.map_tiles_2_7, 13+16*0);
        /*tiles.iconID_mapsign = */loader.getTileID(R.drawable.map_tiles_1_6, 1+16*3);
        loader.flush();
        
        // ========================================================================
        // Load item icons
        loader.prepareTileset(R.drawable.items_tiles, "items_tiles", new Size(14, 30), defaultTileSize);
        world.itemTypes.initialize(loader, r.getString(R.string.itemlist_money));
        assert(world.itemTypes.getItemTypeByTag("gold") != null);
        assert(world.itemTypes.getItemTypeByTag("gold").id == ItemTypeCollection.ITEMTYPE_GOLD);
        world.itemTypes.initialize(loader, r.getString(R.string.itemlist_weapons));
        world.itemTypes.initialize(loader, r.getString(R.string.itemlist_armour));
        if (AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
        	world.itemTypes.initialize(loader, r.getString(R.string.itemlist_debug));
        } else {
        	world.itemTypes.initialize(loader, r.getString(R.string.itemlist_rings));
            world.itemTypes.initialize(loader, r.getString(R.string.itemlist_necklaces));
        	world.itemTypes.initialize(loader, r.getString(R.string.itemlist_junk));
            world.itemTypes.initialize(loader, r.getString(R.string.itemlist_food));
            world.itemTypes.initialize(loader, r.getString(R.string.itemlist_potions));
            world.itemTypes.initialize(loader, r.getString(R.string.itemlist_animal));
            world.itemTypes.initialize(loader, r.getString(R.string.itemlist_quest));
        }
        loader.flush();
        
        world.dropLists.initialize(world.itemTypes);

        // ========================================================================
        // Conversation
        if (AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
        	world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_debug));
        } else {
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_mikhail));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_crossglen));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_crossglen_gruil));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_crossglen_leonid));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_crossglen_tharal));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_crossglen_leta));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_crossglen_odair));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_jan));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_fallhaven));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_fallhaven_arcir));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_fallhaven_bucus));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_fallhaven_church));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_fallhaven_athamyr));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_fallhaven_drunk));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_fallhaven_nocmar));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_fallhaven_oldman));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_fallhaven_tavern));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_fallhaven_larcal));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_fallhaven_unnmir));
	        world.conversations.initialize(world.itemTypes, r.getString(R.string.conversationlist_fallhaven_gaela));
        }
        if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
        	world.conversations.verifyData();
        }
        
        // ========================================================================
        // Load monster icons
        //loader.prepareTileset(R.drawable.monsters_armor1, "monsters_armor1", src_sz1x1, defaultTileSize);
        //loader.prepareTileset(R.drawable.monsters_demon1, "monsters_demon1", src_sz1x1, dst_sz2x2);
        //loader.prepareTileset(R.drawable.monsters_demon2, "monsters_demon2", src_sz1x1, defaultTileSize);
	    loader.prepareTileset(R.drawable.monsters_dogs, "monsters_dogs", src_sz7x1, defaultTileSize);
        //loader.prepareTileset(R.drawable.monsters_dragons, "monsters_dragons", src_sz7x1, defaultTileSize);
        //loader.prepareTileset(R.drawable.monsters_eye1, "monsters_eye1", src_sz1x1, defaultTileSize);
	    //loader.prepareTileset(R.drawable.monsters_eye2, "monsters_eye2", src_sz1x1, defaultTileSize);
	    //loader.prepareTileset(R.drawable.monsters_eye3, "monsters_eye3", src_sz1x1, defaultTileSize);
	    //loader.prepareTileset(R.drawable.monsters_eye4, "monsters_eye4", src_sz1x1, defaultTileSize);
	    loader.prepareTileset(R.drawable.monsters_ghost1, "monsters_ghost1", src_sz1x1, defaultTileSize);
	    loader.prepareTileset(R.drawable.monsters_ghost2, "monsters_ghost2", src_sz1x1, defaultTileSize);
	    //loader.prepareTileset(R.drawable.monsters_hydra1, "monsters_hydra1", src_sz1x1, dst_sz2x2);
        loader.prepareTileset(R.drawable.monsters_insects, "monsters_insects", src_sz6x1, defaultTileSize);
        loader.prepareTileset(R.drawable.monsters_liches, "monsters_liches", new Size(4, 1), defaultTileSize);
	    loader.prepareTileset(R.drawable.monsters_mage2, "monsters_mage2", src_sz1x1, defaultTileSize);
	    //loader.prepareTileset(R.drawable.monsters_mage3, "monsters_mage3", src_sz1x1, defaultTileSize);
	    //loader.prepareTileset(R.drawable.monsters_mage4, "monsters_mage4", src_sz1x1, defaultTileSize);
	    //loader.prepareTileset(R.drawable.monsters_mage, "monsters_mage", src_sz1x1, defaultTileSize);
        loader.prepareTileset(R.drawable.monsters_man1, "monsters_man1", src_sz1x1, defaultTileSize);
        loader.prepareTileset(R.drawable.monsters_men, "monsters_men", new Size(9, 1), defaultTileSize);
        loader.prepareTileset(R.drawable.monsters_men2, "monsters_men2", new Size(10, 1), defaultTileSize);
	    loader.prepareTileset(R.drawable.monsters_misc, "monsters_misc", new Size(12, 1), defaultTileSize);
    	loader.prepareTileset(R.drawable.monsters_rats, "monsters_rats", new Size(5, 1), defaultTileSize);
        loader.prepareTileset(R.drawable.monsters_rogue1, "monsters_rogue1", src_sz1x1, defaultTileSize);
        loader.prepareTileset(R.drawable.monsters_skeleton1, "monsters_skeleton1", src_sz1x1, defaultTileSize);
        loader.prepareTileset(R.drawable.monsters_skeleton2, "monsters_skeleton2", src_sz1x1, defaultTileSize);
        loader.prepareTileset(R.drawable.monsters_snakes, "monsters_snakes", src_sz6x1, defaultTileSize);
        //loader.prepareTileset(R.drawable.monsters_cyclops, "monsters_cyclops", src_sz1x1, dst_sz2x3);
        loader.prepareTileset(R.drawable.monsters_warrior1, "monsters_warrior1", src_sz1x1, defaultTileSize);
        //loader.prepareTileset(R.drawable.monsters_wraiths, "monsters_wraiths", new Size(3, 1), defaultTileSize);
        //loader.prepareTileset(R.drawable.monsters_zombie1, "monsters_zombie1", src_sz1x1, defaultTileSize);
        //loader.prepareTileset(R.drawable.monsters_zombie2, "monsters_zombie2", src_sz1x1, defaultTileSize);
        //loader.prepareTileset(R.drawable.monsters_dragon1, "monsters_dragon1", src_sz1x1, dst_sz4x3);
        
        if (AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
        	world.monsterTypes.initialize(world.dropLists, loader, r.getString(R.string.monsterlist_debug));
        	world.monsterTypes.initialize(world.dropLists, loader, r.getString(R.string.monsterlist_misc));
        } else {
	        world.monsterTypes.initialize(world.dropLists, loader, r.getString(R.string.monsterlist_crossglen_animals));
	    	world.monsterTypes.initialize(world.dropLists, loader, r.getString(R.string.monsterlist_crossglen_npcs));
	    	world.monsterTypes.initialize(world.dropLists, loader, r.getString(R.string.monsterlist_fallhaven_animals));
	    	world.monsterTypes.initialize(world.dropLists, loader, r.getString(R.string.monsterlist_fallhaven_npcs));
        }
        loader.flush();

        if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
        	world.monsterTypes.verifyData(world);
        }
        
        // ========================================================================
        // Load map icons
        loader.prepareTileset(R.drawable.map_tiles_1_1, "map_tiles_1_1.png", src_mapTileSize, defaultTileSize);
        if (!AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
	        loader.prepareTileset(R.drawable.map_tiles_1_2, "map_tiles_1_2.png", src_mapTileSize, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_1_3, "map_tiles_1_3.png", src_mapTileSize, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_1_4, "map_tiles_1_4.png", src_mapTileSize, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_1_5, "map_tiles_1_5.png", src_mapTileSize, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_1_6, "map_tiles_1_6.png", src_mapTileSize, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_1_7, "map_tiles_1_7.png", src_mapTileSize, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_1_8, "map_tiles_1_8.png", src_mapTileSize7, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_2_1, "map_tiles_2_1.png", src_mapTileSize, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_2_2, "map_tiles_2_2.png", src_mapTileSize, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_2_3, "map_tiles_2_3.png", src_mapTileSize, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_2_4, "map_tiles_2_4.png", src_mapTileSize, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_2_5, "map_tiles_2_5.png", src_mapTileSize, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_2_6, "map_tiles_2_6.png", src_mapTileSize, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_2_7, "map_tiles_2_7.png", src_mapTileSize, defaultTileSize);
	        loader.prepareTileset(R.drawable.map_tiles_2_8, "map_tiles_2_8.png", src_mapTileSize7, defaultTileSize);
        }
        TMXMapReader mapReader = new TMXMapReader();
        
        if (AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
        	mapReader.read(r.getXml(R.xml.debugmap), "debugmap");
        } else {
	        mapReader.read(r.getXml(R.xml.home), "home");
            mapReader.read(r.getXml(R.xml.crossglen), "crossglen");
	        mapReader.read(r.getXml(R.xml.crossglen_farmhouse), "crossglen_farmhouse");
	        mapReader.read(r.getXml(R.xml.crossglen_farmhouse_basement), "crossglen_farmhouse_basement");
	        mapReader.read(r.getXml(R.xml.crossglen_hall), "crossglen_hall");
	        mapReader.read(r.getXml(R.xml.crossglen_smith), "crossglen_smith");
	        mapReader.read(r.getXml(R.xml.crossglen_cave), "crossglen_cave");
	        mapReader.read(r.getXml(R.xml.wild1), "wild1");
	        mapReader.read(r.getXml(R.xml.wild2), "wild2");
	        mapReader.read(r.getXml(R.xml.wild3), "wild3");
	        mapReader.read(r.getXml(R.xml.jan_pitcave1), "jan_pitcave1");
	        mapReader.read(r.getXml(R.xml.jan_pitcave2), "jan_pitcave2");
	        mapReader.read(r.getXml(R.xml.jan_pitcave3), "jan_pitcave3");
	        mapReader.read(r.getXml(R.xml.fallhaven_nw), "fallhaven_nw");
	        mapReader.read(r.getXml(R.xml.snakecave1), "snakecave1");
	        mapReader.read(r.getXml(R.xml.snakecave2), "snakecave2");
	        mapReader.read(r.getXml(R.xml.snakecave3), "snakecave3");
	        mapReader.read(r.getXml(R.xml.wild4), "wild4");
	        mapReader.read(r.getXml(R.xml.hauntedhouse1), "hauntedhouse1");
	        mapReader.read(r.getXml(R.xml.hauntedhouse2), "hauntedhouse2");
	        mapReader.read(r.getXml(R.xml.fallhaven_ne), "fallhaven_ne");
	        mapReader.read(r.getXml(R.xml.fallhaven_church), "fallhaven_church");
	        mapReader.read(r.getXml(R.xml.fallhaven_barn), "fallhaven_barn");
	        mapReader.read(r.getXml(R.xml.fallhaven_potions), "fallhaven_potions");
	        mapReader.read(r.getXml(R.xml.fallhaven_gravedigger), "fallhaven_gravedigger");
	        mapReader.read(r.getXml(R.xml.fallhaven_clothes), "fallhaven_clothes");
	        mapReader.read(r.getXml(R.xml.fallhaven_arcir), "fallhaven_arcir");
	        mapReader.read(r.getXml(R.xml.fallhaven_arcir_basement), "fallhaven_arcir_basement");
	        mapReader.read(r.getXml(R.xml.fallhaven_athamyr), "fallhaven_athamyr");
	        mapReader.read(r.getXml(R.xml.fallhaven_rigmor), "fallhaven_rigmor");
	        mapReader.read(r.getXml(R.xml.fallhaven_tavern), "fallhaven_tavern");
	        mapReader.read(r.getXml(R.xml.fallhaven_prison), "fallhaven_prison");
	        mapReader.read(r.getXml(R.xml.fallhaven_derelict), "fallhaven_derelict");
	        mapReader.read(r.getXml(R.xml.fallhaven_nocmar), "fallhaven_nocmar");
	        mapReader.read(r.getXml(R.xml.catacombs1), "catacombs1");
	        mapReader.read(r.getXml(R.xml.catacombs2), "catacombs2");
	        mapReader.read(r.getXml(R.xml.catacombs3), "catacombs3");
	        mapReader.read(r.getXml(R.xml.catacombs4), "catacombs4");
	        mapReader.read(r.getXml(R.xml.hauntedhouse3), "hauntedhouse3");
	        mapReader.read(r.getXml(R.xml.hauntedhouse4), "hauntedhouse4");
        }
        
        world.maps.predefinedMaps.addAll(mapReader.transformMaps(loader, world.monsterTypes));
        mapReader = null;
        
        loader.flush();

        if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
        	world.maps.verifyData(world);
        }
        
        
        // ========================================================================
        // Load effects
        loader.prepareTileset(R.drawable.effect_blood3, "effect_blood3", new Size(8, 2), dst_sz1x1);
        world.effectTypes.initialize(loader);
        loader.flush();
        
        loader = null;
        // ========================================================================
        

        if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
        	world.verifyData();
        }
    }
    
	public static final Pattern rowPattern = Pattern.compile("\\{(.+?)\\};", Pattern.MULTILINE | Pattern.DOTALL);
    public static final String columnSeparator = "\\|";
	public static int parseImage(DynamicTileLoader tileLoader, String s) {
	   	String[] parts = s.split(":");
	   	return tileLoader.getTileID(parts[0], Integer.parseInt(parts[1]));
	}
	public static ConstRange parseRange(String s) {
		if (s == null || s.length() <= 0) return null;
	   	String[] parts = s.split("-");
	   	if (parts.length < 2) {
	   		int val = Integer.parseInt(s);
	   		return new ConstRange(val, val);
	   	} else {
	   		return new ConstRange(Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
	   	}
	}
	public static Size parseSize(String s, final Size defaultSize) {
		if (s == null || s.length() <= 0) return defaultSize;
	   	String[] parts = s.split("x");
	   	if (parts.length < 2) return defaultSize;
	   	return new Size(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}
	public static CombatTraits parseCombatTraits(String[] parts, int startIndex) {
		String AtkCost = parts[startIndex];
		String AtkPct = parts[startIndex + 1];
		String CritPct = parts[startIndex + 2];
		String CritMult = parts[startIndex + 3];
		String DMG = parts[startIndex + 4];
		String BlkPct = parts[startIndex + 5];
		String DMG_res = parts[startIndex + 6];
		if (       AtkCost.length() <= 0 
				&& AtkPct.length() <= 0
				&& CritPct.length() <= 0
				&& CritMult.length() <= 0
				&& DMG.length() <= 0
				&& BlkPct.length() <= 0
				&& DMG_res.length() <= 0
			) {
			return null;
		} else {
			CombatTraits result = new CombatTraits();
			result.attackCost = parseInt(AtkCost, 0);
			result.attackChance = parseInt(AtkPct, 0);
			result.criticalChance = parseInt(CritPct, 0);
			result.criticalMultiplier = parseInt(CritMult, 0);
			ConstRange r = parseRange(DMG);
			if (r != null) result.damagePotential.set(r);
			result.blockChance = parseInt(BlkPct, 0);
			result.damageResistance = parseInt(DMG_res, 0);
			return result;
		}
	}
	public static int parseInt(String s, int defaultValue) {
		if (s == null || s.length() <= 0) return defaultValue;
		return Integer.parseInt(s);
	}

}
