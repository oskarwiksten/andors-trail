package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.model.script.Requirement;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class ReplaceableMapSection {
	public boolean isApplied = false;
	public final CoordRect replacementArea;
	public final MapSection replaceLayersWith;
	public final Requirement replacementRequirement;

	public ReplaceableMapSection(
			CoordRect replacementArea,
			MapSection replaceLayersWith,
			Requirement replacementRequirement) {
		this.replacementArea = replacementArea;
		this.replaceLayersWith = replaceLayersWith;
		this.replacementRequirement = replacementRequirement;
	}

	public void apply(MapSection dest) {
		dest.replaceLayerContentsWith(replaceLayersWith, replacementArea);
		isApplied = true;
	}
}
