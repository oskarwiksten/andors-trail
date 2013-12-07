package com.gpl.rpg.AndorsTrail.util;

public final class ByteUtils {
	public static String toHexString(byte[] bytes) { return toHexString(bytes, bytes.length); }
	public static String toHexString(byte[] bytes, int numBytes) {
		if (bytes == null) return "";
		if (bytes.length == 0) return "";
		final int len = Math.min(numBytes, bytes.length);
		StringBuilder result = new StringBuilder(len * 2);
		for (int i = 0; i < len; i++) {
			String h = Integer.toHexString(0xFF & bytes[i]);
			if (h.length() < 2) result.append('0');
			result.append(h);
		}
		return result.toString();
	}

	public static void xorArray(byte[] array, byte[] mask) {
		final int len = Math.min(array.length, mask.length);
		for(int i = 0; i < len; ++i) {
			array[i] ^= mask[i];
		}
	}
}
