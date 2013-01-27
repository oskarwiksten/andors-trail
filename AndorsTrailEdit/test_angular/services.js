angular
	.module('itemsService', ['ngResource'])
	.factory('Item', function($resource) {
		var items = [];
		return $resource('phones/:phoneId.json', {}, {
			query: {method:'GET', params:{phoneId:'phones'}, isArray:true}
		});
});