var ATEditor = (function(ATEditor, model, _) {

	function DialogueShowTreeController($scope, $routeParams) {
		$scope.datasource = model.dialogue;
		$scope.rootPhrase = $scope.datasource.findById($routeParams.id);
		$scope.onclick = function(node) {
			ATEditor.navigationFunctions.editObjId(model.dialogue, node.id);
		};
		
		function buildSingleTreeNode(type, id, text, children) {
			return {
				id: id || ""
				,text: text || ""
				,type: type
				,children: children || []
			};
		}
		
		function singleChild(n) { return n ? [n] : []; }
		
		function buildPhraseTree(phrase, visitedPhrases) {
			if (!phrase) { return; }
			if (visitedPhrases[phrase.id]) {
				return buildSingleTreeNode("loop", phrase.id, phrase.message);
			}
			visitedPhrases[phrase.id] = true;
			
			var children;
			if (phrase.hasOnlyNextReply) {
				var nextNode = model.dialogue.findById(phrase.nextPhraseID);
				children = singleChild(buildPhraseTree(nextNode, visitedPhrases));
			} else {
				children = _.map(phrase.replies, function(reply) {
					var replyChild;
					if (reply.nextPhraseID.length == 1) {
						replyChild = buildSingleTreeNode("action");
					} else {
						var nextNode = model.dialogue.findById(reply.nextPhraseID);
						replyChild = buildPhraseTree(nextNode, visitedPhrases);
					}
					
					if (!reply.text) { 
						return replyChild || buildSingleTreeNode("reply", phrase.id, "(no text)");
					}
					
					return buildSingleTreeNode("reply", phrase.id, reply.text, singleChild(replyChild));
				});
			}
			
			var phraseText = phrase.message || "(conditional evaluation)";
			return buildSingleTreeNode("phrase", phrase.id, phraseText, children);
		}
		
		$scope.node = buildPhraseTree($scope.rootPhrase, {});
	};
	
	ATEditor.controllers = ATEditor.controllers || {};
	ATEditor.controllers.DialogueShowTreeController = DialogueShowTreeController;

	return ATEditor;
})(ATEditor, ATEditor.model, _);
