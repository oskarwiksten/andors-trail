package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class MapObject {
	public static enum MapObjectType {
		sign
		,newmap
		,rest
		,keyarea
		,container
	}

	public final CoordRect position;
	public final MapObjectType type;
	public final String id; //placeName on this map or phraseID
	public final String map;
	public final String place;
	public final QuestProgress requireQuestProgress;
	public final DropList dropList;
	
	private MapObject(
			final CoordRect position
			, final MapObjectType type
			, final String id
			, final String map
			, final String place
			, final QuestProgress requireQuestProgress
			, final DropList dropList) {
		this.position = new CoordRect(position);
		this.type = type;
		this.id = id;
		this.map = map;
		this.place = place;
		this.requireQuestProgress = requireQuestProgress;
		this.dropList = dropList;
	}
	
	public static MapObject createMapSignEvent(final CoordRect position, final String phraseID) {
		return new MapObject(position, MapObjectType.sign, phraseID, null, null, null, null);
	}
	public static MapObject createNewMapEvent(final CoordRect position, final String thisMapTitle, final String destinationMap, final String destinationPlace) {
		return new MapObject(position, MapObjectType.newmap, thisMapTitle, destinationMap, destinationPlace, null, null);
	}
	public static MapObject createNewRest(final CoordRect position, final String placeId) {
		return new MapObject(position, MapObjectType.rest, placeId, null, null, null, null);
	}
	public static MapObject createNewKeyArea(final CoordRect position, final String phraseID, final QuestProgress requireQuestStage) {
		return new MapObject(position, MapObjectType.keyarea, phraseID, null, null, requireQuestStage, null);
	}
	public static MapObject createNewContainerArea(final CoordRect position, final DropList dropList) {
		return new MapObject(position, MapObjectType.container, null, null, null, null, dropList);
	}
}
