var ListOfItems = function(options) {
	var o = {
		idField: 'id'
	};
	angular.extend(o, options);

	this.items = [];
	this.findById = function(id) { 
		for (var i = 0; i < this.items.length; ++i) {
			var item = this.items[i];
			if (item[o.idField] == id) return item;
		}
	};
};

var Model = { 
	items: new ListOfItems() 
};

Model.items.items = [{id: 'a', name: 'test1'}, {id: 'b', name: 'test2'}];

function ListCtrl($scope, $routeParams) {
    $scope.items = Model.items.items;
	$scope.add = function() {
		
	};
}


function DetailCtrl($scope, $routeParams) {
	alert("newing DetailCtrl");
    $scope.item = Model.items.findById($routeParams.id);
}
