package com.gpl.rpg.AndorsTrail.resource.parsers;

import java.util.ArrayList;

import android.util.Pair;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.model.item.DropList;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.item.DropList.DropItem;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer;
import com.gpl.rpg.AndorsTrail.resource.ResourceFileTokenizer.ResourceParserFor;
import com.gpl.rpg.AndorsTrail.util.L;

public final class DropListParser extends ResourceParserFor<DropList> {

	private final ResourceFileTokenizer droplistItemResourceTokenizer = new ResourceFileTokenizer(4);
	private final ResourceObjectParser<DropItem> dropItemParser;
	
	public DropListParser(final ItemTypeCollection itemTypes) {
		super(2);
		this.dropItemParser = new ResourceObjectParser<DropItem>() {
			@Override
			public DropItem parseRow(String[] parts) {
				return new DropItem(
						itemTypes.getItemType(parts[0]) 						// Itemtype
						, ResourceParserUtils.parseChance(parts[3]) 				// Chance
						, ResourceParserUtils.parseQuantity(parts[1], parts[2]) 	// Quantity
					);
			}
		};
	}

	@Override
	public Pair<String, DropList> parseRow(String[] parts) {
		// [id|items[itemID|quantity_Min|quantity_Max|chance|]|];
		
		String droplistID = parts[0];
		
		final ArrayList<DropItem> items = new ArrayList<DropItem>();
		droplistItemResourceTokenizer.tokenizeArray(parts[1], items, dropItemParser);				
		DropItem[] items_ = items.toArray(new DropItem[items.size()]);
		
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (items_.length <= 0) {
				L.log("OPTIMIZE: Droplist \"" + droplistID + "\" has no dropped items.");
			}
		}
		return new Pair<String, DropList>(droplistID, new DropList(items_));
	}
}
