package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class MapObject {
	public static final int MAPEVENT_SIGN = 1;
	public static final int MAPEVENT_NEWMAP = 2;
	public static final int MAPEVENT_REST = 3;
	public static final int MAPEVENT_KEYAREA = 4;
	public static final int MAPEVENT_CONTAINER = 5;
	
	public final CoordRect position;
	public final int type;
	public final String id; //placeName on this map or phraseID
	public final String map;
	public final String place;
	public final QuestProgress requireQuestProgress;
	public final DropList dropList;
	
	private MapObject(final CoordRect position, final int type, final String id, final String map, final String place, final QuestProgress requireQuestProgress, final DropList dropList) {
		this.position = new CoordRect(position);
		this.type = type;
		this.id = id;
		this.map = map;
		this.place = place;
		this.requireQuestProgress = requireQuestProgress;
		this.dropList = dropList;
	}
	
	public static MapObject createMapSignEvent(final CoordRect position, final String phraseID) {
		return new MapObject(position, MAPEVENT_SIGN, phraseID, null, null, null, null);
	}
	public static MapObject createNewMapEvent(final CoordRect position, final String thisMapTitle, final String destinationMap, final String destinationPlace) {
		return new MapObject(position, MAPEVENT_NEWMAP, thisMapTitle, destinationMap, destinationPlace, null, null);
	}
	public static MapObject createNewRest(final CoordRect position, final String placeId) {
		return new MapObject(position, MAPEVENT_REST, placeId, null, null, null, null);
	}
	public static MapObject createNewKeyArea(final CoordRect position, final String phraseID, final QuestProgress requireQuestStage) {
		return new MapObject(position, MAPEVENT_KEYAREA, phraseID, null, null, requireQuestStage, null);
	}
	public static MapObject createNewContainerArea(final CoordRect position, final DropList dropList) {
		return new MapObject(position, MAPEVENT_CONTAINER, null, null, null, null, dropList);
	}
	
	public boolean shouldHaveDestinationMap() {
		if (type != MAPEVENT_NEWMAP) return false;
		if (id.equals("exit")) return false;
		return true;
	}
}
