
function EditorTabs(div) {
	var mainTabs;
	var nextTabID;
	var tabInfos = [];
	var editorTypes = [];

	// =====================================
	// Private methods
	
	var closeTab = function(tabID, index) {
		tabInfos[tabID] = null;
		mainTabs.tabs( "remove", index );
	};
	
	var closeCurrentTab = function() {
		var index = mainTabs.tabs('option', 'selected');
		var tab = findTabFromIndex(index);
		var tabID = $(tab).data("tabID");
		if (!tabID) { return; }
		closeTab(tabID, index);
	};

	var findTab 			= function(tabID) { return mainTabs.find('ul li a[href="#tabs-' + tabID + '"]').parent(); }
	var findTabFromIndex	= function(tabIndex) { return mainTabs.find('li')[tabIndex]; }
	var findTabIndex 		= function(tab) { return $( "li", mainTabs ).index(tab); }

	var addTab = function(title, tabInfo) {
		var tabID = nextTabID;
		tabInfos[tabID] = tabInfo;
		mainTabs.tabs( "add", "#tabs-" + tabID, title );
		mainTabs.tabs( "select", -1 );
		var tab = findTab(tabID);
		tab.find( ".ui-icon-close" ).click(function() {
			var index = findTabIndex(tab);
			closeTab(tabID, index);
		});
		tab.data("tabID", tabID);
		nextTabID++;
	};

	var findTabIDOfObject = function(obj) {
		for (var i = 1; i < tabInfos.length; ++i) {
			if (tabInfos[i] && tabInfos[i].obj == obj) {
				return i;
			}
		}
		return -1;
	};
	
	var createObjectEditor = function(tabInfo) {
		var creator = editorTypes[tabInfo.objectType];
		if (!creator) {
			alert("unknown objectType: " + tabInfo.objectType);
			return;
		}
		return creator(tabInfo.obj);
	};
	
	
	// =====================================
	// Public methods
	
	this.registerEditorType = function(objectType, editorCreator) {
		editorTypes[objectType] = editorCreator;
	};
	
	this.renameTabForObject = function(obj, name) {
		var tabID = findTabIDOfObject(obj);
		if (!tabID) return;
		findTab(tabID).find("a").text(name);
	};

	this.openTabForObject = function(obj, objectType, title) {
		var tabID = findTabIDOfObject(obj);
		if (tabID > 0) {
			var index = findTabIndex(findTab(tabID));
			mainTabs.tabs( "select", index );
			return;
		}
		addTab(title, {obj: obj, objectType: objectType});
	};
	
	mainTabs = div.tabs({
		tabTemplate: "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close'>Remove Tab</span></li>",
		add: function( event, ui ) {
			var editor = createObjectEditor( tabInfos[nextTabID] );
			$( ui.panel ).append( editor );
			mainTabs.tabs('select', ui.index);
		}
	});
	mainTabs.find( ".ui-tabs-nav" ).sortable({ axis: "x" });
	nextTabID = mainTabs.size() + 1;
	
	div.keydown(function(e) {
		if (e.keyCode == 27 /* ESC */ ) {  closeCurrentTab(); }
	});
}
