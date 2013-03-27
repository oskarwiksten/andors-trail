var ATEditor = (function(ATEditor, model, defaults, importExport, _) {

	function DialogueController($scope, $routeParams) {
		$scope.datasource = model.dialogue;
		$scope.rootPhrase = $scope.datasource.findById($routeParams.id);
		$scope.phrase = $scope.rootPhrase;
		$scope.reply = null;
		
		$scope.removeReward = function(phrase, reward) {
			var idx = phrase.rewards.indexOf(reward);
			phrase.rewards.splice(idx, 1);
		};
		$scope.addReward = function(phrase) {
			phrase.rewards.push({});
		};
		$scope.proceedToPhrase = function(obj, prop) {
			var phraseId = obj[prop];
			if (phraseId) { 
				var nextPhrase = model.dialogue.findById(phraseId);
				if (nextPhrase) {
					ATEditor.navigationFunctions.editObjId(model.dialogue, phraseId);
					return;
				}
			} else {
				phraseId = $scope.phrase.id; 
			}
			var newPhrase = model.dialogue.addNew(phraseId);
			importExport.prepareObjectsForEditor(model.dialogue, [ newPhrase ]);
			newPhrase.hasOnlyNextReply = true;
			
			phraseId = newPhrase.id;
			obj[prop] = phraseId;
			
			ATEditor.navigationFunctions.editObjId(model.dialogue, phraseId);
		};
		$scope.selectReply = function(reply) {
			$scope.reply = reply;
		};
		$scope.removeReply = function(phrase, reply) {
			var idx = phrase.replies.indexOf(reply);
			phrase.replies.splice(idx, 1);
			if ($scope.reply === reply) { $scope.reply = null; }
		};
		$scope.addReply = function(phrase) {
			var reply = {};
			defaults.addDefaults('reply', reply);
			phrase.replies.push(reply);
			$scope.reply = reply;
		};
	};
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.DialogueController = DialogueController;

	return ATEditor;
})(ATEditor, ATEditor.model, ATEditor.defaults, ATEditor.importExport, _);
