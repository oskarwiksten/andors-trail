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
	
	add("actorconditions_1", {x:14, y:8}, [ 'conditions' ] );
	add("actorconditions_2", {x:3, y:1}, [ 'conditions' ] );
	add("items_armours", {x:14, y:3}, [ 'items' ] );
	add("items_armours_3", {x:10, y:4}, [ 'items' ] );
	add("items_armours_2", {x:7, y:1}, [ 'items' ] );
	add("items_weapons", {x:14, y:6}, [ 'items' ] );
	add("items_weapons_3", {x:13, y:5}, [ 'items' ] );
	add("items_weapons_2", {x:7, y:1}, [ 'items' ] );
	add("items_jewelry", {x:14, y:1}, [ 'items' ] );
	add("items_rings_1", {x:10, y:3}, [ 'items' ] );
	add("items_necklaces_1", {x:10, y:3}, [ 'items' ] );
	add("items_consumables", {x:14, y:5}, [ 'items' ] );
	add("items_books", {x:11, y:1}, [ 'items' ] );
	add("items_misc", {x:14, y:4}, [ 'items' ] );
	add("items_misc_2", {x:20, y:12}, [ 'items' ] );
	add("items_misc_3", {x:20, y:12}, [ 'items' ] );
	add("items_misc_4", {x:20, y:4}, [ 'items' ] );
	add("monsters_armor1", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_demon1", {x: 1, y:1}, [ 'monsters' ], {x:64, y:64} );
	add("monsters_dogs", {x: 7, y:1}, [ 'monsters' ] );
	add("monsters_eye1", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_eye2", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_eye3", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_eye4", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_ghost1", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_hydra1", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_insects", {x: 6, y:1}, [ 'monsters' ] );
	add("monsters_liches", {x: 4, y:1}, [ 'monsters' ] );
	add("monsters_mage", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_mage2", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_man1", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_men", {x: 9, y:1}, [ 'monsters' ] );
	add("monsters_men2", {x: 10, y:1}, [ 'monsters' ] );
	add("monsters_misc", {x: 12, y:1}, [ 'monsters' ] );
	add("monsters_rats", {x: 5, y:1}, [ 'monsters' ] );
	add("monsters_rogue1", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_skeleton1", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_skeleton2", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_snakes", {x: 6, y:1}, [ 'monsters' ] );
	add("monsters_cyclops", {x: 1, y:1}, [ 'monsters' ], {x:64, y:96} );
	add("monsters_warrior1", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_wraiths", {x: 3, y:1}, [ 'monsters' ] );
	add("monsters_zombie1", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_zombie2", {x: 1, y:1}, [ 'monsters' ] );
	add("monsters_karvis1", {x: 2, y:1}, [ 'monsters' ] );
	add("monsters_karvis2", {x: 9, y:1}, [ 'monsters' ] );
	add("monsters_rltiles1", {x:20, y:8}, [ 'monsters' ] );
	add("monsters_rltiles2", {x:20, y:9}, [ 'monsters' ] );
	add("monsters_rltiles3", {x:10, y:3}, [ 'monsters' ] );
	add("monsters_redshrike1", {x:6, y:1}, [ 'monsters' ] );
	add("monsters_ld1", {x:20, y:12}, [ 'monsters' ] );
	add("monsters_ld2", {x:20, y:12}, [ 'monsters' ] );
	
	ATEditor.tilesets = {
		getTileset: getTileset
		,parseImageID: parseImageID
		,createImageID: createImageID
	};
	return ATEditor;
})(ATEditor, ATEditor.utils);
