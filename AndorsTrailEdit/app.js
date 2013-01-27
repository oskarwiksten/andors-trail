var app = (function(controllers) {
	return angular
		.module('ateditor', [])
		.config(['$routeProvider', function($routeProvider) {
			$routeProvider
				.when('/actorcondition/edit/:id', {templateUrl: 'edit_actorcondition.html', controller: controllers.ActorConditionController})
				.when('/quest/edit/:id', {templateUrl: 'edit_quest.html', controller: controllers.QuestController})
				.when('/item/edit/:id', {templateUrl: 'edit_item.html', controller: controllers.ItemController})
				.when('/droplist/edit/:id', {templateUrl: 'edit_droplist.html', controller: controllers.DropListController})
				.when('/dialogue/edit/:id', {templateUrl: 'edit_dialogue.html', controller: controllers.DialogueController})
				.when('/monster/edit/:id', {templateUrl: 'edit_monster.html', controller: controllers.MonsterController})
				.when('/itemcategory/edit/:id', {templateUrl: 'edit_itemcategory.html', controller: controllers.ItemCategoryController});
		}]);
})(controllers);
