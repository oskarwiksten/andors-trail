package com.gpl.rpg.AndorsTrail.resource;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.map.TMXMapReader;
import com.gpl.rpg.AndorsTrail.util.Size;

import android.content.res.Resources;

public final class ResourceLoader {

	public static void loadResources(WorldContext world, Resources r) {
    	
        final TileStore tiles = world.tileStore;
        final int mTileSize = tiles.tileSize;
        
        final Size dst_sz1x1 = new Size(mTileSize, mTileSize);
        final Size dst_sz2x2 = new Size(mTileSize*2, mTileSize*2);
        //final Size dst_sz2x3 = new Size(mTileSize*2, mTileSize*3);
        //final Size dst_sz4x3 = new Size(mTileSize*4, mTileSize*3);
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
        loader.prepareTileset(R.drawable.items_tiles, "items_tiles", new Size(14, 30), defaultTileSize);
        /*tiles.iconID_CHAR_HERO = */loader.prepareTileID(R.drawable.char_hero, 0);
        /*tiles.iconID_attackselect = */loader.prepareTileID(R.drawable.map_tiles_1_2, 6+16*5);
        /*tiles.iconID_moveselect = */loader.prepareTileID(R.drawable.map_tiles_1_2, 7+16*5);
        /*tiles.iconID_groundbag = */loader.prepareTileID(R.drawable.map_tiles_2_7, 13+16*0);
    	/*tiles.iconID_boxopened = */loader.prepareTileID(R.drawable.items_tiles, 8+14*29);
        /*tiles.iconID_boxclosed = */loader.prepareTileID(R.drawable.items_tiles, 7+14*29);
        loader.flush();
        
        
        // ========================================================================
        // Load condition types
        loader.prepareTileset(R.drawable.items_tiles, "items_tiles", new Size(14, 30), defaultTileSize);
        world.actorConditionsTypes.initialize(loader, r.getString(R.string.actorconditions_v069));
        world.actorConditionsTypes.initialize(loader, r.getString(R.string.actorconditions_v069_bwm));
        loader.flush();
        
        
        // ========================================================================
        // Load item icons
        loader.prepareTileset(R.drawable.items_tiles, "items_tiles", new Size(14, 30), defaultTileSize);
        world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_money));
        assert(world.itemTypes.getItemTypeByTag("gold") != null);
        assert(world.itemTypes.getItemTypeByTag("gold").id == ItemTypeCollection.ITEMTYPE_GOLD);
        world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_weapons));
        world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_armour));
        if (AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
        	world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_debug));
        } else {
        	world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_rings));
            world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_necklaces));
        	world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_junk));
            world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_food));
            world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_potions));
            world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_animal));
            world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_quest));
            world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_v068));
            world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_v069));
            world.itemTypes.initialize(loader, world.actorConditionsTypes, r.getString(R.string.itemlist_v069_questitems));
        }
        loader.flush();
        
        
        // ========================================================================
        // Load Droplists
        if (AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
        	world.dropLists.initialize(world.itemTypes, r.getString(R.string.droplists_debug));
        } else {
        	world.dropLists.initialize(world.itemTypes, r.getString(R.string.droplists_crossglen));
        	world.dropLists.initialize(world.itemTypes, r.getString(R.string.droplists_crossglen_outside));
        	world.dropLists.initialize(world.itemTypes, r.getString(R.string.droplists_fallhaven));
        	world.dropLists.initialize(world.itemTypes, r.getString(R.string.droplists_wilderness));
        	world.dropLists.initialize(world.itemTypes, r.getString(R.string.droplists_v068));
        }
        
        
        // ========================================================================
        // Load Quests
        if (AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
        	world.quests.initialize(r.getString(R.string.questlist_debug));
        } else {
        	world.quests.initialize(r.getString(R.string.questlist));
        	world.quests.initialize(r.getString(R.string.questlist_nondisplayed));
        	world.quests.initialize(r.getString(R.string.questlist_v068));
        	world.quests.initialize(r.getString(R.string.questlist_v069));
        }
    	

        // ========================================================================
        // Load Conversation
        if (AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
        	world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_debug));
        } else {
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_mikhail));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_crossglen));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_crossglen_gruil));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_crossglen_leonid));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_crossglen_tharal));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_crossglen_leta));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_crossglen_odair));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_jan));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_arcir));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_bucus));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_church));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_athamyr));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_drunk));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_nocmar));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_oldman));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_tavern));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_larcal));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_unnmir));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_gaela));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_vacor));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_unzel));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_wilderness));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_flagstone));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_south));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_signs_pre067));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_thievesguild_1));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_farrik));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_fallhaven_warden));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_umar));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_kaori));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_vilegard_villagers));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_vilegard_erttu));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_vilegard_tavern));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_jolnor));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_alynndir));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_shops));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_ogam));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_foamingflask));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_ambelie));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_foamingflask_guards));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_foamingflask_outsideguard));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_wrye));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_oluag));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_signs_v068));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_maelveon));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_bwm_agent_1));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_bwm_agent_2));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_bwm_agent_3));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_bwm_agent_4));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_bwm_agent_5));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_bwm_agent_6));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_prim_arghest));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_prim_outside));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_prim_inn));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_prim_tavern));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_prim_guthbered));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_blackwater_signs));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_blackwater_harlenn));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_blackwater_upper));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_blackwater_lower));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_blackwater_herec));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_prim_bjorgur));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_prim_fulus));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_prim_merchants));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_blackwater_throdna));
	        world.conversations.initialize(world.itemTypes, world.dropLists, r.getString(R.string.conversationlist_blackwater_kazaul));
        }
        if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
        	world.conversations.verifyData();
        }
        
        // ========================================================================
        // Load monster icons
        //loader.prepareTileset(R.drawable.monsters_armor1, "monsters_armor1", src_sz1x1, defaultTileSize);
        loader.prepareTileset(R.drawable.monsters_demon1, "monsters_demon1", src_sz1x1, dst_sz2x2);
        loader.prepareTileset(R.drawable.monsters_dogs, "monsters_dogs", src_sz7x1, defaultTileSize);
        //loader.prepareTileset(R.drawable.monsters_eye1, "monsters_eye1", src_sz1x1, defaultTileSize);
	    //loader.prepareTileset(R.drawable.monsters_eye2, "monsters_eye2", src_sz1x1, defaultTileSize);
	    //loader.prepareTileset(R.drawable.monsters_eye3, "monsters_eye3", src_sz1x1, defaultTileSize);
	    //loader.prepareTileset(R.drawable.monsters_eye4, "monsters_eye4", src_sz1x1, defaultTileSize);
	    loader.prepareTileset(R.drawable.monsters_ghost1, "monsters_ghost1", src_sz1x1, defaultTileSize);
	    //loader.prepareTileset(R.drawable.monsters_hydra1, "monsters_hydra1", src_sz1x1, dst_sz2x2);
        loader.prepareTileset(R.drawable.monsters_insects, "monsters_insects", src_sz6x1, defaultTileSize);
        loader.prepareTileset(R.drawable.monsters_liches, "monsters_liches", new Size(4, 1), defaultTileSize);
	    loader.prepareTileset(R.drawable.monsters_mage2, "monsters_mage2", src_sz1x1, defaultTileSize);
	    loader.prepareTileset(R.drawable.monsters_mage, "monsters_mage", src_sz1x1, defaultTileSize);
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
        loader.prepareTileset(R.drawable.monsters_zombie1, "monsters_zombie1", src_sz1x1, defaultTileSize);
        //loader.prepareTileset(R.drawable.monsters_zombie2, "monsters_zombie2", src_sz1x1, defaultTileSize);
        //loader.prepareTileset(R.drawable.monsters_dragon1, "monsters_dragon1", src_sz1x1, dst_sz4x3);
        loader.prepareTileset(R.drawable.monsters_rltiles1, "monsters_rltiles1", new Size(20, 8), defaultTileSize);
        loader.prepareTileset(R.drawable.monsters_rltiles2, "monsters_rltiles2", new Size(20, 9), defaultTileSize);
        loader.prepareTileset(R.drawable.karvis_npc, "karvis_npc", new Size(9, 1), defaultTileSize);
        
        if (AndorsTrailApplication.DEVELOPMENT_DEBUGRESOURCES) {
        	world.monsterTypes.initialize(world.dropLists, world.actorConditionsTypes, loader, r.getString(R.string.monsterlist_debug));
        } else {
	        world.monsterTypes.initialize(world.dropLists, world.actorConditionsTypes, loader, r.getString(R.string.monsterlist_crossglen_animals));
	    	world.monsterTypes.initialize(world.dropLists, world.actorConditionsTypes, loader, r.getString(R.string.monsterlist_crossglen_npcs));
	    	world.monsterTypes.initialize(world.dropLists, world.actorConditionsTypes, loader, r.getString(R.string.monsterlist_fallhaven_animals));
	    	world.monsterTypes.initialize(world.dropLists, world.actorConditionsTypes, loader, r.getString(R.string.monsterlist_fallhaven_npcs));
	    	world.monsterTypes.initialize(world.dropLists, world.actorConditionsTypes, loader, r.getString(R.string.monsterlist_wilderness));
	    	world.monsterTypes.initialize(world.dropLists, world.actorConditionsTypes, loader, r.getString(R.string.monsterlist_v068_npcs));
	    	world.monsterTypes.initialize(world.dropLists, world.actorConditionsTypes, loader, r.getString(R.string.monsterlist_v069_monsters));
	    	world.monsterTypes.initialize(world.dropLists, world.actorConditionsTypes, loader, r.getString(R.string.monsterlist_v069_npcs));
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
        	mapReader.read(r, R.xml.debugmap, "debugmap");
        } else {
        	mapReader.read(r, R.xml.home, "home");
            mapReader.read(r, R.xml.crossglen, "crossglen");
	        mapReader.read(r, R.xml.crossglen_farmhouse, "crossglen_farmhouse");
	        mapReader.read(r, R.xml.crossglen_farmhouse_basement, "crossglen_farmhouse_basement");
	        mapReader.read(r, R.xml.crossglen_hall, "crossglen_hall");
	        mapReader.read(r, R.xml.crossglen_smith, "crossglen_smith");
	        mapReader.read(r, R.xml.crossglen_cave, "crossglen_cave");
	        mapReader.read(r, R.xml.wild1, "wild1");
	        mapReader.read(r, R.xml.wild2, "wild2");
	        mapReader.read(r, R.xml.wild3, "wild3");
	        mapReader.read(r, R.xml.jan_pitcave1, "jan_pitcave1");
	        mapReader.read(r, R.xml.jan_pitcave2, "jan_pitcave2");
	        mapReader.read(r, R.xml.jan_pitcave3, "jan_pitcave3");
	        mapReader.read(r, R.xml.fallhaven_nw, "fallhaven_nw");
	        mapReader.read(r, R.xml.snakecave1, "snakecave1");
	        mapReader.read(r, R.xml.snakecave2, "snakecave2");
	        mapReader.read(r, R.xml.snakecave3, "snakecave3");
	        mapReader.read(r, R.xml.wild4, "wild4");
	        mapReader.read(r, R.xml.hauntedhouse1, "hauntedhouse1");
	        mapReader.read(r, R.xml.hauntedhouse2, "hauntedhouse2");
	        mapReader.read(r, R.xml.fallhaven_ne, "fallhaven_ne");
	        mapReader.read(r, R.xml.fallhaven_church, "fallhaven_church");
	        mapReader.read(r, R.xml.fallhaven_barn, "fallhaven_barn");
	        mapReader.read(r, R.xml.fallhaven_potions, "fallhaven_potions");
	        mapReader.read(r, R.xml.fallhaven_gravedigger, "fallhaven_gravedigger");
	        mapReader.read(r, R.xml.fallhaven_clothes, "fallhaven_clothes");
	        mapReader.read(r, R.xml.fallhaven_arcir, "fallhaven_arcir");
	        mapReader.read(r, R.xml.fallhaven_arcir_basement, "fallhaven_arcir_basement");
	        mapReader.read(r, R.xml.fallhaven_athamyr, "fallhaven_athamyr");
	        mapReader.read(r, R.xml.fallhaven_rigmor, "fallhaven_rigmor");
	        mapReader.read(r, R.xml.fallhaven_tavern, "fallhaven_tavern");
	        mapReader.read(r, R.xml.fallhaven_prison, "fallhaven_prison");
	        mapReader.read(r, R.xml.fallhaven_derelict, "fallhaven_derelict");
	        mapReader.read(r, R.xml.fallhaven_nocmar, "fallhaven_nocmar");
	        mapReader.read(r, R.xml.catacombs1, "catacombs1");
	        mapReader.read(r, R.xml.catacombs2, "catacombs2");
	        mapReader.read(r, R.xml.catacombs3, "catacombs3");
	        mapReader.read(r, R.xml.catacombs4, "catacombs4");
	        mapReader.read(r, R.xml.hauntedhouse3, "hauntedhouse3");
	        mapReader.read(r, R.xml.hauntedhouse4, "hauntedhouse4");
	        mapReader.read(r, R.xml.fallhaven_sw, "fallhaven_sw");
	        mapReader.read(r, R.xml.wild5, "wild5");
	        mapReader.read(r, R.xml.wild6, "wild6");
	        mapReader.read(r, R.xml.wild6_house, "wild6_house");
	        mapReader.read(r, R.xml.wild7, "wild7");
	        mapReader.read(r, R.xml.wild8, "wild8");
	        mapReader.read(r, R.xml.wild9, "wild9");
	        mapReader.read(r, R.xml.wild10, "wild10");
	        mapReader.read(r, R.xml.flagstone0, "flagstone0");
	        mapReader.read(r, R.xml.flagstone_inner, "flagstone_inner");
	        mapReader.read(r, R.xml.flagstone_upper, "flagstone_upper");
	        mapReader.read(r, R.xml.flagstone1, "flagstone1");
	        mapReader.read(r, R.xml.flagstone2, "flagstone2");
	        mapReader.read(r, R.xml.flagstone3, "flagstone3");
	        mapReader.read(r, R.xml.flagstone4, "flagstone4");
	        mapReader.read(r, R.xml.wild11, "wild11");
	        mapReader.read(r, R.xml.wild12, "wild12");
	        mapReader.read(r, R.xml.wild11_clearing, "wild11_clearing");
	        mapReader.read(r, R.xml.clearing_level1, "clearing_level1");
	        mapReader.read(r, R.xml.clearing_level2, "clearing_level2");
	        mapReader.read(r, R.xml.fallhaven_se, "fallhaven_se");
	        mapReader.read(r, R.xml.fallhaven_lumberjack, "fallhaven_lumberjack");
	        mapReader.read(r, R.xml.fallhaven_alaun, "fallhaven_alaun");
	        mapReader.read(r, R.xml.fallhaven_storage, "fallhaven_storage");
	        mapReader.read(r, R.xml.fallhaven_farmer, "fallhaven_farmer");
	        mapReader.read(r, R.xml.wild13, "wild13");
	        mapReader.read(r, R.xml.wild14, "wild14");
	        mapReader.read(r, R.xml.wild14_cave, "wild14_cave");
	        mapReader.read(r, R.xml.wild14_clearing, "wild14_clearing");
	        mapReader.read(r, R.xml.wild15, "wild15");
	        mapReader.read(r, R.xml.wild15_house, "wild15_house");
	        mapReader.read(r, R.xml.road1, "road1");
	        mapReader.read(r, R.xml.foaming_flask, "foaming_flask");
	        mapReader.read(r, R.xml.fallhaven_derelict2, "fallhaven_derelict2");
	        mapReader.read(r, R.xml.vilegard_n, "vilegard_n");
	        mapReader.read(r, R.xml.vilegard_s, "vilegard_s");
	        mapReader.read(r, R.xml.vilegard_sw, "vilegard_sw");
	        mapReader.read(r, R.xml.vilegard_ogam, "vilegard_ogam");
	        mapReader.read(r, R.xml.vilegard_chapel, "vilegard_chapel");
	        mapReader.read(r, R.xml.vilegard_tavern, "vilegard_tavern");
	        mapReader.read(r, R.xml.vilegard_armorer, "vilegard_armorer");
	        mapReader.read(r, R.xml.vilegard_smith, "vilegard_smith");
	        mapReader.read(r, R.xml.vilegard_wrye, "vilegard_wrye");
	        mapReader.read(r, R.xml.vilegard_kaori, "vilegard_kaori");
	        mapReader.read(r, R.xml.vilegard_erttu, "vilegard_erttu");
	        mapReader.read(r, R.xml.road2, "road2");
	        mapReader.read(r, R.xml.road3, "road3");
	        mapReader.read(r, R.xml.road4, "road4");
	        mapReader.read(r, R.xml.road4_gargoylecave, "road4_gargoylecave");
	        mapReader.read(r, R.xml.road5, "road5");
	        mapReader.read(r, R.xml.road5_house, "road5_house");
	        mapReader.read(r, R.xml.gargoylecave1, "gargoylecave1");
	        mapReader.read(r, R.xml.gargoylecave2, "gargoylecave2");
	        mapReader.read(r, R.xml.gargoylecave3, "gargoylecave3");
	        mapReader.read(r, R.xml.gargoylecave4, "gargoylecave4");
	        mapReader.read(r, R.xml.blackwater_mountain0, "blackwater_mountain0");
	        mapReader.read(r, R.xml.blackwater_mountain1, "blackwater_mountain1");
	        mapReader.read(r, R.xml.blackwater_mountain2, "blackwater_mountain2");
	        mapReader.read(r, R.xml.blackwater_mountain3, "blackwater_mountain3");
	        mapReader.read(r, R.xml.blackwater_mountain4, "blackwater_mountain4");
	        mapReader.read(r, R.xml.blackwater_mountain5, "blackwater_mountain5");
	        mapReader.read(r, R.xml.blackwater_mountain6, "blackwater_mountain6");
	        mapReader.read(r, R.xml.blackwater_mountain7, "blackwater_mountain7");
	        mapReader.read(r, R.xml.blackwater_mountain8, "blackwater_mountain8");
	        mapReader.read(r, R.xml.blackwater_mountain9, "blackwater_mountain9");
	        mapReader.read(r, R.xml.blackwater_mountain10, "blackwater_mountain10");
	        mapReader.read(r, R.xml.blackwater_mountain11, "blackwater_mountain11");
	        mapReader.read(r, R.xml.blackwater_mountain12, "blackwater_mountain12");
	        mapReader.read(r, R.xml.blackwater_mountain13, "blackwater_mountain13");
	        mapReader.read(r, R.xml.blackwater_mountain14, "blackwater_mountain14");
	        mapReader.read(r, R.xml.blackwater_mountain15, "blackwater_mountain15");
	        mapReader.read(r, R.xml.blackwater_mountain16, "blackwater_mountain16");
	        mapReader.read(r, R.xml.blackwater_mountain17, "blackwater_mountain17");
	        mapReader.read(r, R.xml.blackwater_mountain18, "blackwater_mountain18");
	        mapReader.read(r, R.xml.blackwater_mountain19, "blackwater_mountain19");
	        mapReader.read(r, R.xml.blackwater_mountain20, "blackwater_mountain20");
	        mapReader.read(r, R.xml.blackwater_mountain21, "blackwater_mountain21");
	        mapReader.read(r, R.xml.blackwater_mountain22, "blackwater_mountain22");
	        mapReader.read(r, R.xml.blackwater_mountain23, "blackwater_mountain23");
	        mapReader.read(r, R.xml.blackwater_mountain24, "blackwater_mountain24");
	        mapReader.read(r, R.xml.blackwater_mountain25, "blackwater_mountain25");
	        mapReader.read(r, R.xml.blackwater_mountain26, "blackwater_mountain26");
	        mapReader.read(r, R.xml.blackwater_mountain27, "blackwater_mountain27");
	        mapReader.read(r, R.xml.blackwater_mountain28, "blackwater_mountain28");
	        mapReader.read(r, R.xml.blackwater_mountain29, "blackwater_mountain29");
	        mapReader.read(r, R.xml.blackwater_mountain30, "blackwater_mountain30");
	        mapReader.read(r, R.xml.blackwater_mountain31, "blackwater_mountain31");
	        mapReader.read(r, R.xml.blackwater_mountain32, "blackwater_mountain32");
	        mapReader.read(r, R.xml.blackwater_mountain33, "blackwater_mountain33");
	        mapReader.read(r, R.xml.blackwater_mountain34, "blackwater_mountain34");
	        mapReader.read(r, R.xml.blackwater_mountain35, "blackwater_mountain35");
	        mapReader.read(r, R.xml.blackwater_mountain36, "blackwater_mountain36");
	        mapReader.read(r, R.xml.blackwater_mountain37, "blackwater_mountain37");
	        mapReader.read(r, R.xml.blackwater_mountain38, "blackwater_mountain38");
	        mapReader.read(r, R.xml.blackwater_mountain39, "blackwater_mountain39");
	        mapReader.read(r, R.xml.blackwater_mountain40, "blackwater_mountain40");
	        mapReader.read(r, R.xml.blackwater_mountain41, "blackwater_mountain41");
	        mapReader.read(r, R.xml.blackwater_mountain42, "blackwater_mountain42");
	        mapReader.read(r, R.xml.blackwater_mountain43, "blackwater_mountain43");
	        mapReader.read(r, R.xml.blackwater_mountain44, "blackwater_mountain44");
	        mapReader.read(r, R.xml.blackwater_mountain45, "blackwater_mountain45");
	        mapReader.read(r, R.xml.blackwater_mountain46, "blackwater_mountain46");
	        mapReader.read(r, R.xml.blackwater_mountain47, "blackwater_mountain47");
	        mapReader.read(r, R.xml.blackwater_mountain48, "blackwater_mountain48");
	        mapReader.read(r, R.xml.blackwater_mountain49, "blackwater_mountain49");
	        mapReader.read(r, R.xml.blackwater_mountain50, "blackwater_mountain50");
	        mapReader.read(r, R.xml.blackwater_mountain51, "blackwater_mountain51");
	        mapReader.read(r, R.xml.blackwater_mountain52, "blackwater_mountain52");
	        mapReader.read(r, R.xml.wild0, "wild0");
	        mapReader.read(r, R.xml.crossroads, "crossroads");
	        mapReader.read(r, R.xml.fields0, "fields0");
	        mapReader.read(r, R.xml.fields1, "fields1");
	        mapReader.read(r, R.xml.fields2, "fields2");
	        mapReader.read(r, R.xml.fields3, "fields3");
	        mapReader.read(r, R.xml.fields4, "fields4");
	        mapReader.read(r, R.xml.fields5, "fields5");
	        mapReader.read(r, R.xml.fields6, "fields6");
	        mapReader.read(r, R.xml.fields7, "fields7");
	        mapReader.read(r, R.xml.fields8, "fields8");
	        mapReader.read(r, R.xml.fields9, "fields9");
	        mapReader.read(r, R.xml.fields10, "fields10");
	        mapReader.read(r, R.xml.fields11, "fields11");
	        mapReader.read(r, R.xml.fields12, "fields12");
	        mapReader.read(r, R.xml.houseatcrossroads0, "houseatcrossroads0");
	        mapReader.read(r, R.xml.houseatcrossroads1, "houseatcrossroads1");
	        mapReader.read(r, R.xml.houseatcrossroads2, "houseatcrossroads2");
	        mapReader.read(r, R.xml.houseatcrossroads3, "houseatcrossroads3");
	        mapReader.read(r, R.xml.houseatcrossroads4, "houseatcrossroads4");
	        mapReader.read(r, R.xml.houseatcrossroads5, "houseatcrossroads5");
	        mapReader.read(r, R.xml.loneford1, "loneford1");
	        mapReader.read(r, R.xml.loneford2, "loneford2");
	        mapReader.read(r, R.xml.loneford3, "loneford3");
	        mapReader.read(r, R.xml.loneford4, "loneford4");
	        mapReader.read(r, R.xml.loneford5, "loneford5");
	        mapReader.read(r, R.xml.loneford6, "loneford6");
	        mapReader.read(r, R.xml.loneford7, "loneford7");
	        mapReader.read(r, R.xml.loneford8, "loneford8");
	        mapReader.read(r, R.xml.loneford9, "loneford9");
	        mapReader.read(r, R.xml.loneford10, "loneford10");
	        mapReader.read(r, R.xml.roadbeforecrossroads, "roadbeforecrossroads");
	        mapReader.read(r, R.xml.roadtocarntower0, "roadtocarntower0");
	        mapReader.read(r, R.xml.roadtocarntower1, "roadtocarntower1");
	        mapReader.read(r, R.xml.roadtocarntower2, "roadtocarntower2");
	        mapReader.read(r, R.xml.woodcave0, "woodcave0");
	        mapReader.read(r, R.xml.woodcave1, "woodcave1");
	        mapReader.read(r, R.xml.wild16, "wild16");
	        mapReader.read(r, R.xml.wild17, "wild17");
	        mapReader.read(r, R.xml.gapfiller1, "gapfiller1");
	        mapReader.read(r, R.xml.gapfiller3, "gapfiller3");
	        mapReader.read(r, R.xml.gapfiller4, "gapfiller4");
        }
        
        world.maps.predefinedMaps.addAll(mapReader.transformMaps(loader, world.monsterTypes, world.dropLists));
        mapReader = null;
        
        loader.flush();

        if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
        	world.maps.verifyData(world);
        }
        
        
        // ========================================================================
        // Load effects
        loader.prepareTileset(R.drawable.effect_blood3, "effect_blood3", new Size(8, 2), dst_sz1x1);
        loader.prepareTileset(R.drawable.effect_heal2, "effect_heal2", new Size(8, 2), dst_sz1x1);
        loader.prepareTileset(R.drawable.effect_poison1, "effect_poison1", new Size(8, 2), dst_sz1x1);
        world.visualEffectTypes.initialize(loader);
        loader.flush();
        
        loader = null;
        // ========================================================================
        

        if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
        	world.verifyData();
        }
    }
}
