var ATEditor = (function(ATEditor, app, tilesets, $) {
	
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
			var scale = attrs.ngTileImageScale;
			if (!scale) scale = 1;
			
			scope.$watch(attrs.ngTileImage, function(value) {
				var img = tilesets.parseImageID(value);
				var tileset = tilesets.getTileset(img.tilesetName);
				var c = tileset.localIDToCoords(img.localID);
				element.css({
					"background-image": "url(" +img.path + img.tilesetName + ".png)", 
					"background-position": (-c.x)*scale+"px " + (-c.y)*scale+"px",
					"width": tileset._tileSize.x * scale + "px",
					"height": tileset._tileSize.y * scale + "px",
					"cursor": "pointer"
				});
				if (scale && (scale != 1)) {
					element.css({
						"background-size": 
							tileset._tileSize.x * tileset._numTiles.x * scale + "px "
							+ tileset._tileSize.y * tileset._numTiles.y * scale + "px "
					});
				}
			});
		};
	});
	
	app.directive('ngSelectImage', function () {
		return {
			link : function(scope, element, attrs) {
				function openDialog() {
					var element = angular.element('#selectIconModal');
					var ctrl = element.controller();
					ctrl.startSelecting(attrs.ngSelectImage, function(iconID) {
						element.modal('hide');
						var s = attrs.ngSelectImageDest || 'iconID';
						eval("scope." + s + "='" + iconID + "'");
					});
					element.modal('show');
				}
				element.bind('click', openDialog);
			}
		}
	});
	
	// http://jsfiddle.net/ag5zC/22/
	app.directive('treenode', function ($compile) {
		var link;
		return {
			restrict: 'E',
			terminal: true,
			scope: { node: '=', onclick: '=' },
			link: function (scope, element, attrs) {
				if(!link) {
					link = $compile(element.html());
				}
				element.replaceWith(link(scope.$new(), function(clone) { }));
			}
		}
	});
	
		  
	return ATEditor;
})(ATEditor, ATEditor.app, ATEditor.tilesets, jQuery);
