var ATEditor = (function(ATEditor, utils) {

	function TilesetImage(options) {
		var defaultOptions = {
			numTiles: { x: 1, y: 1 }
			,tileSize: { x: 32, y: 32 }
			,usedFor: []
		};
		_.defaults(options, defaultOptions);
		
		this._name = options.name;
		this._numTiles = options.numTiles;
		this._tileSize = options.tileSize;
		this._usedFor = options.usedFor;
		
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

	var defaultImagePath = utils.getUrlRelativeToBaseSrcDir("AndorsTrail/res/drawable/");
	
	var defaultimage = {
		tilesetName: 'defaultimage', 
		localID: 0, 
		path: ''
	};
	var defaultTileset = new TilesetImage({name: defaultimage.tilesetName });
	
	var _tilesets = {};
	_tilesets[defaultimage.tilesetName] = defaultTileset;
	
	var getTileset = function(name) { 
		var result = _tilesets[name];
		if (!result) { return defaultTileset; }
		return result;
	}

	var parseImageID = function(str) {
		if (!str || str == "") return defaultimage;
		var v = str.split(":");
		if (v.length < 1) return defaultimage;
		return {
			tilesetName: v[0], 
			localID: v[1], 
			path: defaultImagePath
		};
	}
	
	var createImageID = function(tilesetName, localID) {
		if (!tilesetName) return "";
		return tilesetName + ":" + localID;
	}
	
	
	var add = function(tilesetName, numTiles, usedFor, tileSize) { 
		_tilesets[tilesetName] = new TilesetImage({
			name: tilesetName
			,numTiles: numTiles
			,tileSize: tileSize
			,usedFor: usedFor
		}); 
	}
	
	var getTilesetsForSection = function(section) { 
		return _.filter(_tilesets, function(tileset) {
			return _.contains(tileset._usedFor, section);
		});
	}
	var getIconIDsForSection = function(section) { 
		var result = [];
		_.each(getTilesetsForSection(section), function(tileset) {
			for(var i = 0; i < tileset._numTiles.y * tileset._numTiles.x; i++) {
				result.push(createImageID(tileset._name, i));
			}
		});
		return result;
	}
	
	add("actorconditions_1", {x:14, y:8}, [ 'actorcondition' ] );
	add("actorconditions_2", {x:3, y:1}, [ 'actorcondition' ] );
	add("items_armours", {x:14, y:3}, [ 'item' ] );
	add("items_armours_3", {x:10, y:4}, [ 'item' ] );
	add("items_armours_2", {x:7, y:1}, [ 'item' ] );
	add("items_weapons", {x:14, y:6}, [ 'item' ] );
	add("items_weapons_3", {x:13, y:5}, [ 'item' ] );
	add("items_weapons_2", {x:7, y:1}, [ 'item' ] );
	add("items_jewelry", {x:14, y:1}, [ 'item' ] );
	add("items_rings_1", {x:10, y:3}, [ 'item' ] );
	add("items_necklaces_1", {x:10, y:3}, [ 'item' ] );
	add("items_consumables", {x:14, y:5}, [ 'item' ] );
	add("items_books", {x:11, y:1}, [ 'item' ] );
	add("items_misc", {x:14, y:4}, [ 'item' ] );
	add("items_misc_2", {x:20, y:12}, [ 'item' ] );
	add("items_misc_3", {x:20, y:12}, [ 'item' ] );
	add("items_misc_4", {x:20, y:4}, [ 'item' ] );
	add("items_misc_5", {x:9, y:5}, [ 'item' ] );
	add("items_misc_6", {x:9, y:4}, [ 'item' ] );
	add("items_reterski_1", {x:3, y:10}, [ 'item' ] );
	add("items_tometik1", {x:6, y:10}, [ 'item' ] );
	add("items_tometik2", {x:10, y:10}, [ 'item' ] );
	add("items_tometik3", {x:8, y:6}, [ 'item' ] );
	add("monsters_armor1", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_dogs", {x: 7, y:1}, [ 'monster' ] );
	add("monsters_eye1", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_eye2", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_eye3", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_eye4", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_ghost1", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_hydra1", {x: 1, y:1}, [ 'monster' ], {x:64, y:64} );
	add("monsters_insects", {x: 6, y:1}, [ 'monster' ] );
	add("monsters_liches", {x: 4, y:1}, [ 'monster' ] );
	add("monsters_mage", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_mage2", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_man1", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_men", {x: 9, y:1}, [ 'monster' ] );
	add("monsters_men2", {x: 10, y:1}, [ 'monster' ] );
	add("monsters_misc", {x: 12, y:1}, [ 'monster' ] );
	add("monsters_rats", {x: 5, y:1}, [ 'monster' ] );
	add("monsters_rogue1", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_skeleton1", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_skeleton2", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_snakes", {x: 6, y:1}, [ 'monster' ] );
	add("monsters_warrior1", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_wraiths", {x: 3, y:1}, [ 'monster' ] );
	add("monsters_zombie1", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_zombie2", {x: 1, y:1}, [ 'monster' ] );
	add("monsters_karvis1", {x: 2, y:1}, [ 'monster' ] );
	add("monsters_karvis2", {x: 9, y:1}, [ 'monster' ] );
	add("monsters_rltiles1", {x:20, y:8}, [ 'monster' ] );
	add("monsters_rltiles2", {x:20, y:9}, [ 'monster' ] );
	add("monsters_rltiles3", {x:10, y:3}, [ 'monster' ] );
	add("monsters_rltiles4", {x:12, y:4}, [ 'monster' ] );
	add("monsters_redshrike1", {x:7, y:1}, [ 'monster' ] );
	add("monsters_ld1", {x:20, y:12}, [ 'monster' ] );
	add("monsters_ld2", {x:20, y:12}, [ 'monster' ] );
	add("monsters_tometik1", {x:10, y:9}, [ 'monster' ] );
	add("monsters_tometik2", {x:8, y:10}, [ 'monster' ] );
	add("monsters_tometik3", {x:6, y:13}, [ 'monster' ] );
	add("monsters_tometik4", {x:6, y:13}, [ 'monster' ] );
	add("monsters_tometik5", {x:6, y:16}, [ 'monster' ] );
	add("monsters_tometik6", {x:7, y:6}, [ 'monster' ] );
	add("monsters_tometik7", {x:8, y:11}, [ 'monster' ] );
	add("monsters_tometik8", {x:7, y:9}, [ 'monster' ] );
	add("monsters_tometik9", {x:8, y:8}, [ 'monster' ] );
	add("monsters_tometik10", {x:6, y:13}, [ 'monster' ] );
	add("monsters_demon1", {x:1, y:1}, [ 'monster' ], {x:64, y:64} );
	add("monsters_demon2", {x:1, y:1}, [ 'monster' ], {x:64, y:64} );
	add("monsters_cyclops", {x:1, y:1}, [ 'monster' ], {x:64, y:96} );
	
	ATEditor.tilesets = {
		getTileset: getTileset
		,parseImageID: parseImageID
		,createImageID: createImageID
		,getIconIDsForSection: getIconIDsForSection
	};
	return ATEditor;
})(ATEditor, ATEditor.utils);
