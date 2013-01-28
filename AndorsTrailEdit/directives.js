var ATEditor = (function(ATEditor, app, $) {
	
	// Copied from http://jsfiddle.net/p69aT/
	//  -> originally from https://groups.google.com/forum/?fromgroups=#!topic/angular/7XVOebG6z6E
	app.directive('fadey', function() {
		return {
			restrict: 'A',
			link: function(scope, elm, attrs) {
				elm = $(elm);
				elm.hide();
				elm.fadeIn();

				scope.destroy = function(complete) {
					elm.slideUp(function() {
						if (complete) {
							complete.apply(scope);
						}
					});
					elm.fadeOut();
				};
			}
		};
	});
	
	
	// Copied from 
	//  http://www.codeproject.com/Articles/464939/Angular-JS-Using-Directives-to-Create-Custom-Attri
	app.directive('ngDsFade', function () {
		return function(scope, element, attrs) {
			element.css('display', 'none');
			scope.$watch(attrs.ngDsFade, function(value) {
				if (value) {
					element.fadeIn(400);
				} else {
					element.fadeOut(300);
				}
			});
		};
	});


	app.directive('ngTileImage', function () {
		return function(scope, element, attrs) {
			element.css('display', 'none');
			scope.$watch(attrs.ngDsFade, function(value) {
				if (value) {
					element.fadeIn(400);
				} else {
					element.fadeOut(300);
				}
			});
		};
	});
	
	return ATEditor;
})(ATEditor, ATEditor.app, jQuery);
