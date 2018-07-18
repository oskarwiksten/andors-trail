var ATEditor = (function(ATEditor, model, utils, _, $) {
	
	var http;
	var resources = {};
	
	function loadUrlFromGit(relativeUrl, successCallback, errorCallback) {
		var url = utils.getUrlRelativeToBaseSrcDir(relativeUrl);
		http.get(url)
		.success(function(data, status, headers, config) {
			successCallback(data);
		}).error(function(data, status, headers, config) {
			if(errorCallback) { errorCallback(status); }
		});
	}

	function addExampleModelItems() {
		var _import = function(section, data) {
			ATEditor.importExport.importDataObjects(section, data);
		};
		
		_import(model.quests, [
			{id: "testQuest", name: "Test quest", showInLog: 1, stages: [ { progress: 10, logText: "Stage 10"} , { progress: 20, logText: "Stage 20", finishesQuest: 1 } ] }
			]);
	}
		
	function init($http) {
		http = $http;
		
		// http://andors-trail.googlecode.com/git/AndorsTrail/res/values/loadresources.xml
		loadUrlFromGit("AndorsTrail/res/values/loadresources.xml", function(data) {
			var parseListOfResourceFiles = function(xmlname, section) {
				var items = [];
				resources[section.id] = items;
				$(data).find("array[name='" + xmlname + "'] item").each(function(idx, f) {
					var match = (/^@.*\/(.+)$/g).exec($(f).text());
					if (match) {
						items.push(match[1] + ".json");
					}
				});
			};
			parseListOfResourceFiles("loadresource_itemcategories", model.itemCategories);
			parseListOfResourceFiles("loadresource_actorconditions", model.actorConditions);
			parseListOfResourceFiles("loadresource_items", model.items);
			parseListOfResourceFiles("loadresource_droplists", model.droplists);
			parseListOfResourceFiles("loadresource_quests", model.quests);
			parseListOfResourceFiles("loadresource_conversationlists", model.dialogue);
			parseListOfResourceFiles("loadresource_monsters", model.monsters);
			
			_.each(resources['itemcategory'], function(file) {
				loadUrlFromGit("AndorsTrail/res/raw/" + file, function(data) {
					ATEditor.importExport.importDataObjects(model.itemCategories, data);
				});
			});
		});
		
		loadUrlFromGit("AndorsTrail/res/raw/actorconditions_v069.json", function(data) {
			ATEditor.importExport.importDataObjects(model.actorConditions, data);
		});
		loadUrlFromGit("AndorsTrail/res/raw/conversationlist_mikhail.json", function(data) {
			ATEditor.importExport.importDataObjects(model.dialogue, data);
		});
		loadUrlFromGit("AndorsTrail/res/raw/droplists_crossglen.json", function(data) {
			ATEditor.importExport.importDataObjects(model.droplists, data);
		});
		loadUrlFromGit("AndorsTrail/res/raw/monsterlist_crossglen_animals.json", function(data) {
			ATEditor.importExport.importDataObjects(model.monsters, data);
		});
		loadUrlFromGit("AndorsTrail/res/raw/itemlist_v069_2.json", function(data) {
			ATEditor.importExport.importDataObjects(model.items, data);
		});
		
		addExampleModelItems(model);
	}
	
	ATEditor.exampleData = {
		init: init
		,resources: resources
		,loadUrlFromGit: loadUrlFromGit
	};
	
	return ATEditor;
})(ATEditor, ATEditor.model, ATEditor.utils, _, jQuery);
