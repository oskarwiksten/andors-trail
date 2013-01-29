var ATEditor = (function(ATEditor, tilesets) {

	this.setImage = function(imageElem, imageID, scale) {
		if (!scale) scale = 1;
		var img = tilesets.parseImageID(imageID);
		var tilesetImage = tilesets.getTileset(img.name);
		var c = tilesetImage.localIDToCoords(img.localID);
		imageElem.css({
			"background-image": "url(" +img.path + img.name + ".png)", 
			"background-position": (-c.x)*scale+"px " + (-c.y)*scale+"px",
			"width": tilesetImage._tileSize.x * scale + "px",
			"height": tilesetImage._tileSize.y * scale + "px",
			"cursor": "pointer"
		});
		if (scale && (scale !== 1)) {
			imageElem.css({
				"background-size": 
					tilesetImage._tileSize.x * tilesetImage._numTiles.x * scale + "px "
					+ tilesetImage._tileSize.y * tilesetImage._numTiles.y * scale + "px "
			});
		}
	}
	
	ATEditor.iconselector = {
		setImage: setImage
	};
	return ATEditor;
})(ATEditor, ATEditor.tilesets);
