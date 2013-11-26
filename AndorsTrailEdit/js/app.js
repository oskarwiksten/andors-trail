var ATEditor = (function(ATEditor, controllers) {
	
	var htmldir = 'partials/';
	
	ATEditor.app = angular
		.module('ateditor', ['ui.sortable'])
		.config(['$routeProvider', function($routeProvider) {
			$routeProvider
				.when('/actorcondition/edit/:id', {templateUrl: htmldir+'edit_actorcondition.html', controller: controllers.ActorConditionController})
				.when('/quest/edit/:id', {templateUrl: htmldir+'edit_quest.html', controller: controllers.QuestController})
				.when('/item/edit/:id', {templateUrl: htmldir+'edit_item.html', controller: controllers.ItemController})
				.when('/item/table', {templateUrl: htmldir+'table_item.html', controller: controllers.ItemTableController})
				.when('/droplist/edit/:id', {templateUrl: htmldir+'edit_droplist.html', controller: controllers.DropListController})
				.when('/dialogue/edit/:id', {templateUrl: htmldir+'edit_dialogue.html', controller: controllers.DialogueController})
				.when('/dialogue/tree/:id', {templateUrl: htmldir+'tree_dialogue.html', controller: controllers.DialogueShowTreeController})
				.when('/monster/edit/:id', {templateUrl: htmldir+'edit_monster.html', controller: controllers.MonsterController})
				.when('/monster/table', {templateUrl: htmldir+'table_monster.html', controller: controllers.MonsterTableController})
				.when('/itemcategory/edit/:id', {templateUrl: htmldir+'edit_itemcategory.html', controller: controllers.ItemCategoryController})
				.when('/import', {templateUrl: htmldir+'import.html', controller: controllers.ImportController})
				.when('/export', {templateUrl: htmldir+'export.html', controller: controllers.ExportController})
				.when('/start', {templateUrl: htmldir+'start.html'})
				.otherwise({redirectTo: '/start'});
		}]);
	return ATEditor;
})(ATEditor, ATEditor.controllers);
