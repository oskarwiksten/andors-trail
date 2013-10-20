package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.script.Requirement;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class MapObject {
	public static enum MapObjectType {
		sign
		,newmap
		,rest
		,keyarea
		,container
		,script
	}

	public static enum MapObjectEvaluationType {
		whenEntering
		,onEveryStep
		,afterEveryRound
		,continuously
	}

	public final CoordRect position;
	public final MapObjectType type;
	public final String id; //placeName on this map or phraseID
	public final String map;
	public final String place;
	public final String group;
	public final Requirement enteringRequirement;
	public final DropList dropList;
	public final MapObjectEvaluationType evaluateWhen;
	public boolean isActive = true;

	private MapObject(
			final CoordRect position
			, final MapObjectType type
			, final String id
			, final String map
			, final String place
			, final String group
			, final Requirement enteringRequirement
			, final DropList dropList
			, final MapObjectEvaluationType evaluateWhen
			) {
		this.position = new CoordRect(position);
		this.type = type;
		this.id = id;
		this.map = map;
		this.place = place;
		this.group = group;
		this.enteringRequirement = enteringRequirement;
		this.dropList = dropList;
		this.evaluateWhen = evaluateWhen;
	}

	public static MapObject createMapSignEvent(final CoordRect position, final String phraseID, final String group) {
		return new MapObject(position, MapObjectType.sign, phraseID, null, null, group, null, null, MapObjectEvaluationType.whenEntering);
	}
	public static MapObject createMapChangeArea(final CoordRect position, final String thisMapTitle, final String destinationMap, final String destinationPlace, final String group) {
		return new MapObject(position, MapObjectType.newmap, thisMapTitle, destinationMap, destinationPlace, group, null, null, MapObjectEvaluationType.whenEntering);
	}
	public static MapObject createRestArea(final CoordRect position, final String placeId, final String group) {
		return new MapObject(position, MapObjectType.rest, placeId, null, null, group, null, null, MapObjectEvaluationType.whenEntering);
	}
	public static MapObject createKeyArea(final CoordRect position, final String phraseID, final Requirement enteringRequirement, final String group) {
		return new MapObject(position, MapObjectType.keyarea, phraseID, null, null, group, enteringRequirement, null, MapObjectEvaluationType.whenEntering);
	}
	public static MapObject createContainerArea(final CoordRect position, final DropList dropList, final String group) {
		return new MapObject(position, MapObjectType.container, null, null, null, group, null, dropList, MapObjectEvaluationType.whenEntering);
	}
	public static MapObject createScriptArea(final CoordRect position, final String phraseID, final MapObjectEvaluationType evaluateWhen, final String group) {
		return new MapObject(position, MapObjectType.script, phraseID, null, null, group, null, null, evaluateWhen);
	}
}
