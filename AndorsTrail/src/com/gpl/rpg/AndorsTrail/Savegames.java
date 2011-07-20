package com.gpl.rpg.AndorsTrail;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Environment;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.ActorStatsController;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.controller.MovementController;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.util.L;

public final class Savegames {
	public static final int SLOT_QUICKSAVE = 0;
	
	public static final int LOAD_RESULT_SUCCESS = 0;
	public static final int LOAD_RESULT_UNKNOWN_ERROR = 1;
	public static final int LOAD_RESULT_FUTURE_VERSION = 2;
    
    public static boolean saveWorld(WorldContext world, Context androidContext, int slot, String displayInfo) {
    	try {
    		// Create the savegame in a temporary memorystream first to ensure that the savegame can
    		// be created correctly. We don't want to trash the user's file unneccessarily if there is an error.
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    	saveWorld(world, bos, displayInfo);
	    	byte[] savegame = bos.toByteArray();
	    	bos.close();
	    	bos = null;
	    	
	    	FileOutputStream fos = getOutputFile(androidContext, slot);
	    	fos.write(savegame);
	    	fos.close();
	    	return true;
    	} catch (IOException e) {
    		L.log("Error saving world: " + e.toString());
    		return false;
    	}
    }
    public static int loadWorld(WorldContext world, Context androidContext, int slot) {
    	try {
	    	FileInputStream fos = getInputFile(androidContext, slot);
	    	int result = loadWorld(world, fos);
	    	fos.close();
	    	return result;
    	} catch (IOException e) {
    		L.log("Error loading world: " + e.toString());
	    	return LOAD_RESULT_UNKNOWN_ERROR;
    	}
    }
    
    private static FileOutputStream getOutputFile(Context androidContext, int slot) throws IOException {
    	if (slot == SLOT_QUICKSAVE) {
    		return androidContext.openFileOutput(Constants.FILENAME_SAVEGAME_QUICKSAVE, Context.MODE_PRIVATE);
    	} else {
    		ensureSavegameDirectoryExists();
	    	return new FileOutputStream(getSlotFile(slot));
    	}
    }
    private static void ensureSavegameDirectoryExists() {
    	File root = Environment.getExternalStorageDirectory();
		File dir = new File(root, Constants.FILENAME_SAVEGAME_DIRECTORY);
		if (!dir.exists()) dir.mkdir();
    }
    private static FileInputStream getInputFile(Context androidContext, int slot) throws IOException {
    	if (slot == SLOT_QUICKSAVE) {
    		return androidContext.openFileInput(Constants.FILENAME_SAVEGAME_QUICKSAVE);
    	} else {
    		return new FileInputStream(getSlotFile(slot));
    	}
    }
    private static File getSlotFile(int slot) {
    	File root = getSavegameDirectory();
		return new File(root, Constants.FILENAME_SAVEGAME_FILENAME_PREFIX + slot);
    }
    
    private static File getSavegameDirectory() {
    	File root = Environment.getExternalStorageDirectory();
		return new File(root, Constants.FILENAME_SAVEGAME_DIRECTORY);
    }
	
	private static void saveWorld(WorldContext world, OutputStream outStream, String displayInfo) throws IOException {
    	DataOutputStream dest = new DataOutputStream(outStream);
    	final int flags = 0;
    	FileHeader.writeToParcel(dest, world.model.player.traits.name, displayInfo);
    	world.maps.writeToParcel(dest, flags);
    	world.model.writeToParcel(dest, flags);
    	dest.close();
    }
    private static int loadWorld(WorldContext world, InputStream inState) throws IOException {
    	DataInputStream src = new DataInputStream(inState);
    	final FileHeader header = new FileHeader(src);
    	if (header.fileversion > AndorsTrailApplication.CURRENT_VERSION) return LOAD_RESULT_FUTURE_VERSION;
    	
    	world.maps.readFromParcel(src, world, header.fileversion);
    	world.model = new ModelContainer(src, world, header.fileversion);
    	src.close();
    	
    	ActorStatsController.recalculatePlayerCombatTraits(world.model.player);
		MovementController.moveBlockedActors(world);
    	
    	return LOAD_RESULT_SUCCESS;
    }
    
	public static FileHeader quickload(Context androidContext, int slot) {
		try {
			if (slot != SLOT_QUICKSAVE) {
				File f = getSlotFile(slot);
				if (!f.exists()) return null;
			}
			FileInputStream fos = getInputFile(androidContext, slot);
	    	DataInputStream src = new DataInputStream(fos);
	    	final FileHeader header = new FileHeader(src);
	    	src.close();
	    	fos.close();
	    	return header;
		} catch (Exception e) {
			return null;
		}
	}
	
	private static final Pattern savegameFilenamePattern = Pattern.compile(Constants.FILENAME_SAVEGAME_FILENAME_PREFIX + "(\\d+)");
	public static Set<Integer> getUsedSavegameSlots(Context androidContext) {
		try {
			final HashSet<Integer> result = new HashSet<Integer>();
			getSavegameDirectory().listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File f, String filename) {
					Matcher m = savegameFilenamePattern.matcher(filename);
					if (m != null && m.matches()) {
						result.add(Integer.parseInt(m.group(1)));
						return true;
					}
					return false;
				}
			});
			return result;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final class FileHeader {
		public final int fileversion;
		public final String playerName;
		public final String displayInfo;
		
		public FileHeader(String playerName, String displayInfo) {
			this.fileversion = AndorsTrailApplication.CURRENT_VERSION;
			this.playerName = playerName;
			this.displayInfo = displayInfo;
		}
		
		public String describe() {
			return playerName + ", " + displayInfo;
		}

			
		// ====== PARCELABLE ===================================================================

		public FileHeader(DataInputStream src) throws IOException {
			int fileversion = src.readInt();
	    	if (fileversion == 11) fileversion = 5; // Fileversion 5 had no version identifier, but the first byte was 11.
	    	this.fileversion = fileversion;
	    	if (fileversion >= 14) { // Before fileversion 14 (0.6.7), we had no file header.
	    		this.playerName = src.readUTF();
	    		this.displayInfo = src.readUTF();
	    	} else {
	    		this.playerName = null;
	    		this.displayInfo = null;
	    	}
		}
		
		public static void writeToParcel(DataOutputStream dest, String playerName, String displayInfo) throws IOException {
			dest.writeInt(AndorsTrailApplication.CURRENT_VERSION);
	    	dest.writeUTF(playerName);
	    	dest.writeUTF(displayInfo);
		}
	}
}
