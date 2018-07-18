package com.gpl.rpg.AndorsTrail.savegames;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.controller.Constants;
import com.gpl.rpg.AndorsTrail.model.ModelContainer;
import com.gpl.rpg.AndorsTrail.util.L;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Savegames {
	public static final int SLOT_QUICKSAVE = 0;

	public static enum LoadSavegameResult {
		success
		, unknownError
		, savegameIsFromAFutureVersion
	}

	public static boolean saveWorld(WorldContext world, Context androidContext, int slot, String displayInfo) {
		try {
			// Create the savegame in a temporary memorystream first to ensure that the savegame can
			// be created correctly. We don't want to trash the user's file unneccessarily if there is an error.
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			saveWorld(world, bos, displayInfo);
			byte[] savegame = bos.toByteArray();
			bos.close();

			FileOutputStream fos = getOutputFile(androidContext, slot);
			fos.write(savegame);
			fos.close();
			return true;
		} catch (IOException e) {
			L.log("Error saving world: " + e.toString());
			return false;
		}
	}
	public static LoadSavegameResult loadWorld(WorldContext world, ControllerContext controllers, Context androidContext, int slot) {
		try {
			FileInputStream fos = getInputFile(androidContext, slot);
			LoadSavegameResult result = loadWorld(androidContext.getResources(), world, controllers, fos);
			fos.close();
			return result;
		} catch (IOException e) {
			if (AndorsTrailApplication.DEVELOPMENT_DEBUGMESSAGES) {
				L.log("Error loading world: " + e.toString());
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				L.log("Load error: " + sw.toString());
			}
			return LoadSavegameResult.unknownError;
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

	public static void saveWorld(WorldContext world, OutputStream outStream, String displayInfo) throws IOException {
		DataOutputStream dest = new DataOutputStream(outStream);
		final int flags = 0;
		FileHeader.writeToParcel(dest, world.model.player.getName(), displayInfo);
		world.maps.writeToParcel(dest, world, flags);
		world.model.writeToParcel(dest, flags);
		dest.close();
	}

	public static LoadSavegameResult loadWorld(Resources res, WorldContext world, ControllerContext controllers, InputStream inState) throws IOException {
		DataInputStream src = new DataInputStream(inState);
		final FileHeader header = new FileHeader(src);
		if (header.fileversion > AndorsTrailApplication.CURRENT_VERSION) return LoadSavegameResult.savegameIsFromAFutureVersion;

		world.maps.readFromParcel(src, world, controllers, header.fileversion);
		world.model = new ModelContainer(src, world, controllers, header.fileversion);
		src.close();

		onWorldLoaded(res, world, controllers);

		return LoadSavegameResult.success;
	}

	private static void onWorldLoaded(Resources res, WorldContext world, ControllerContext controllers) {
		controllers.actorStatsController.recalculatePlayerStats(world.model.player);
		controllers.mapController.resetMapsNotRecentlyVisited();
		controllers.movementController.prepareMapAsCurrentMap(world.model.currentMap, res, false);
		controllers.gameRoundController.resetRoundTimers();
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
	public static List<Integer> getUsedSavegameSlots() {
		try {
			final List<Integer> result = new ArrayList<Integer>();
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
			Collections.sort(result);
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public static final class FileHeader {
		public final int fileversion;
		public final String playerName;
		public final String displayInfo;

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
