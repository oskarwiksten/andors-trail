package com.gpl.rpg.AndorsTrail.model.map;

import com.gpl.rpg.AndorsTrail.model.script.Requirement;
import com.gpl.rpg.AndorsTrail.util.CoordRect;

public final class ReplaceableMapSection {
	public boolean isApplied = false;
	public boolean isActive = true;
	public final CoordRect replacementArea;
	public final MapSection replaceLayersWith;
	public final Requirement replacementRequirement;
	public final String group;

	public ReplaceableMapSection(
			CoordRect replacementArea,
			MapSection replaceLayersWith,
			Requirement replacementRequirement,
			String group) {
		this.replacementArea = replacementArea;
		this.replaceLayersWith = replaceLayersWith;
		this.replacementRequirement = replacementRequirement;
		this.group = group;
	}

	public void apply(MapSection dest) {
		dest.replaceLayerContentsWith(replaceLayersWith, replacementArea);
		isApplied = true;
	}
}
