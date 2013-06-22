package com.gpl.rpg.AndorsTrail.util;

public final class ByteUtils {
	public static String toHexString(byte[] bytes) {
		if (bytes == null) return "";
		if (bytes.length == 0) return "";
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String h = Integer.toHexString(0xFF & bytes[i]);
			if (h.length() < 2) result.append('0');
			result.append(h);
		}
		return result.toString();
	}
}
