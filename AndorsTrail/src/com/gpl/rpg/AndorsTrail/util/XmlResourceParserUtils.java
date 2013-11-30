package com.gpl.rpg.AndorsTrail.util;

import android.content.res.XmlResourceParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public final class XmlResourceParserUtils {

	public static interface TagHandler {
		void handleTag(XmlResourceParser xrp, String tagName) throws XmlPullParserException, IOException;
	}

	public static void readCurrentTagUntilEnd(XmlResourceParser xrp, TagHandler handler) throws XmlPullParserException, IOException {
		String outerTagName = xrp.getName();
		String tagName;
		int eventType;
		while ((eventType = xrp.next()) != XmlResourceParser.END_DOCUMENT) {
			if (eventType == XmlResourceParser.START_TAG) {
				tagName = xrp.getName();
				handler.handleTag(xrp, tagName);
			} else if (eventType == XmlResourceParser.END_TAG) {
				tagName = xrp.getName();
				if (tagName.equals(outerTagName)) return;
			}
		}
	}
}
