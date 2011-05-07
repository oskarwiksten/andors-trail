
function TilesetImage(name, numTiles, tileSize, tags) {
	this._name = name;
	this._numTiles = numTiles ? numTiles : { x: 1, y: 1 };
	this._tileSize = tileSize ? tileSize : { x: 32, y: 32 };
	this._tags = tags ? tags : [];
	
	this.localIDToCoords = function(localID) {
		return {
			x: (localID % this._numTiles.x) * this._tileSize.x,
			y: Math.floor(localID / this._numTiles.x) * this._tileSize.y
		}
	}
	this.coordsToLocalID = function(x, y) {
		return Math.floor(x / this._tileSize.x) 
				+ this._numTiles.x * Math.floor(y / this._tileSize.y)
	}
}

var defaultimage = {
	name: 'defaultimage', 
	localID: 0, 
	path: ''
};

function ImageSelector(imagePath, dialog) {
	var _tilesets = {};
	_tilesets[""] = new TilesetImage(defaultimage.name);
	_tilesets[defaultimage.name] = _tilesets[""];
	
	var currentInput;

	var get = function(name) { return _tilesets[name]; }

	var parseImageID = function(str) {
		if (!str || str == "") return defaultimage;
		var v = str.split(":");
		if (v.length < 1) return defaultimage;
		return {
			name: v[0], 
			localID: v[1], 
			path: imagePath
		};
	}
	
	var getImageID = function(name, localID) {
		if (!name) return "";
		return name + ":" + localID;
	}
	
	var updateImageFromFormField = function(image, formField) {
		var img = parseImageID(formField.val());
		var tilesetImage = get(img.name);
		if (!tilesetImage) { tilesetImage = get(""); }
		var c = tilesetImage.localIDToCoords(img.localID);
		image.css({
			"background-image": "url(" +img.path + img.name + ".png)", 
			"background-position": (-c.x)+"px " + (-c.y)+"px",
			"width": tilesetImage._tileSize.x + "px",
			"height": tilesetImage._tileSize.y + "px",
			"cursor": "pointer"
		});
	}
	
	this.add = function(tileset) { 
		var name = tileset._name;
		_tilesets[name] = tileset; 
		dialog.append("<img src=\"" + imagePath + name + ".png\" id=\"" + name + "\" style=\"cursor: pointer;\" />");		
		
		dialog.find("#" + name).click(function(e) {
			var x = e.pageX - $(this).offset().left;
			var y = e.pageY - $(this).offset().top;
			var localID = tileset.coordsToLocalID(x, y);
			currentInput.val(getImageID(name, localID));
			currentInput.change(); // Causes the change handler to be run, thus updating the image.
			dialog.dialog("close");
		});
	}
	
	var showImages = function(showTilesetTag) {
		jQuery.each(_tilesets, function(idx, t) {
			if (!idx) return;
			var visible = t._tags.indexOf(showTilesetTag) >= 0;
			$( "#" + idx, dialog ).toggle(visible);
		});
	}
	
	this.imageify = function(img, val, showTilesetTag) {
		val.change(function() { updateImageFromFormField(img, val); });
		img.click(function() {
			currentInput = val;
			showImages(showTilesetTag);
			dialog.dialog("open");
		});
		val.change();
	}
	
	
	dialog.dialog({
		autoOpen: false,
		modal: true,
		width: 600,
		height: 800,
		position: [50,50],
		buttons: {
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		}
	});
}

