package com.gpl.rpg.AndorsTrail.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class Size {
	public final int width;
	public final int height;
	public Size(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public String toString() {
		return width + "x" + height;
	}

	public boolean equals(final Size s) {
		return width == s.width && height == s.height;
	}
	public boolean equals(final int w, final int h) {
		return width == w && height == h;
	}


	// ====== PARCELABLE ===================================================================

	public Size(DataInputStream src, int fileversion) throws IOException {
		this.width = src.readInt();
		this.height = src.readInt();
	}

	public void writeToParcel(DataOutputStream dest, int flags) throws IOException {
		dest.writeInt(width);
		dest.writeInt(height);
	}
}
