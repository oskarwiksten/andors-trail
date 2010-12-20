package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class MapObject {
	public static final int MAPEVENT_SIGN = 1;
	public static final int MAPEVENT_NEWMAP = 2;
	public static final int MAPEVENT_REST = 3;
	public static final int MAPEVENT_KEYAREA = 4;
	
	public final CoordRect position;
	public final int type;
	public final String text;
	public final String title;
	public final String map;
	public final String place;
	public final QuestProgress questProgress;
	
	private MapObject(final CoordRect position, final int type, final String title, final String text, final String map, final String place, final QuestProgress questProgress) {
		this.position = new CoordRect(position);
		this.type = type;
		this.title = title;
		this.text = text;
		this.map = map;
		this.place = place;
		this.questProgress = questProgress;
	}
	
	public static MapObject createMapSignEvent(final CoordRect position, final String title, final String text, final QuestProgress applyQuestStage) {
		return new MapObject(position, MAPEVENT_SIGN, title, text, null, null, applyQuestStage);
	}
	public static MapObject createNewMapEvent(final CoordRect position, final String thisMapTitle, final String destinationMap, final String destinationPlace) {
		return new MapObject(position, MAPEVENT_NEWMAP, thisMapTitle, null, destinationMap, destinationPlace, null);
	}
	public static MapObject createNewRest(final CoordRect position) {
		return new MapObject(position, MAPEVENT_REST, null, null, null, null, null);
	}
	public static MapObject createNewKeyArea(final CoordRect position, final String text, final QuestProgress requireQuestStage) {
		return new MapObject(position, MAPEVENT_KEYAREA, null, text, null, null, requireQuestStage);
	}
}
