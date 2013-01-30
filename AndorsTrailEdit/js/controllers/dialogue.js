var ATEditor = (function(ATEditor, model, defaults, _) {

	function DialogueController($scope, $routeParams) {
		$scope.datasource = model.dialogue;
		$scope.rootPhrase = $scope.datasource.findById($routeParams.id);
		$scope.phrase = $scope.rootPhrase;
		$scope.reply = null;
		
		function rebuildTree(rootPhrase) {
			console.log("rebuilding tree for " + rootPhrase.id);
			rootPhrase.tree = rootPhrase.tree || {};
			rootPhrase.tree.dirty = true;	
			rebuildPhraseTree(rootPhrase, {});
		}
		
		function rebuildPhraseTree(phrase, visitedPhraseIDs) {
			if (visitedPhraseIDs[phrase.id]) { 
				var phraseNode = {};
				phraseNode.image = 'imgphrase.png';
				phraseNode.text = "(conversation loop)";
				phraseNode.phrase = phrase;
				phraseNode.reply = null;
				phraseNode.children = [];
				return phraseNode; 
			}
			visitedPhraseIDs[phrase.id] = true;
			if (_.keys(visitedPhraseIDs).length > 1000) { return {}; }
			
			if (phrase.tree && !phrase.tree.dirty) { return phrase.tree; }
			console.log("Constructing " + phrase.id);
			var phraseNode = {};
			phraseNode.dirty = false;
			phraseNode.image = 'imgphrase.png';
			phraseNode.text = phrase.message || '(no text)';
			phraseNode.phrase = phrase;
			phraseNode.reply = null;
			phraseNode.children = [];
			phrase.tree = phraseNode;
			
			if (phrase.hasOnlyNextReply) {
				var nextPhrase = model.dialogue.findById(phrase.nextPhraseID);
				if (nextPhrase) {
					var childNode = rebuildPhraseTree(nextPhrase, visitedPhraseIDs);
					phraseNode.children = [ childNode ];
				}
			} else {
				_.each(phrase.replies, function(reply) {
					var replyNode = {}
					replyNode.image = 'imgreply.png';
					replyNode.text = reply.text || '(no text)';
					replyNode.phrase = phrase;
					replyNode.reply = reply;
					replyNode.children = [];
					phraseNode.children.push(replyNode);
					if (reply.nextPhraseID) {
						var nextPhrase = model.dialogue.findById(reply.nextPhraseID);
						if (nextPhrase) {
							var childNode = rebuildPhraseTree(nextPhrase, visitedPhraseIDs);
							replyNode.children.push(childNode);
						}
					}
				});
			}
			return phraseNode;
		}
		
		$scope.onclick = function(node) {
			$scope.phrase = node.phrase;
			$scope.reply = node.reply;
		};
		
		$scope.$watch('phrase.message'
						+' + phrase.nextPhraseID'
						+' + phrase.hasOnlyNextReply'
						+' + reply.text'
						+' + reply.nextPhraseID'
			, function() { 
				rebuildTree($scope.phrase);
				$scope.node = $scope.rootPhrase.tree;
			});
		
		$scope.refreshTree = function() {
			function setDirty(node) {
				if (node) {
					node.dirty = true;
					_.each(node.children, function(c) {
						setDirty(c);
					});
				}
			}
			setDirty($scope.rootPhrase.tree);
			rebuildTree($scope.rootPhrase);
		};
		
		$scope.removeReward = function(phrase, reward) {
			var idx = phrase.rewards.indexOf(reward);
			phrase.rewards.splice(idx, 1);
		};
		$scope.addReward = function(phrase) {
			phrase.rewards.push({});
		};
		$scope.followNextReply = function(nextPhraseID) {
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
})(ATEditor, ATEditor.model, ATEditor.defaults, _);
