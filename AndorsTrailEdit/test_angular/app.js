angular
	.module('oskar1', [])
    .config(['$routeProvider', function($routeProvider) {
		$routeProvider
			.when('/item/:id', {templateUrl: 'detail.html', controller: DetailCtrl});
	}]);
