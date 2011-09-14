

function createConversationEditor(obj) {
	var div = $( "#templates #editDialogue" ).clone(true);
	
	var treeDiv = $ ( "#dialogueTree", div );
	treeDiv.dynatree({ 
		title: "Conversation flow" 
		,imagePath: 'img'
	});
	var tree = treeDiv.dynatree("getTree");

	var rootNode = treeDiv.dynatree("getRoot");
	updatePhraseTreeNodesBelow(tree, rootNode, obj);
	
	treeDiv.dynatree({
		onActivate: function(node) {
			onConversationPhraseSelected(div, node.data.model, tree);
		}
	});
	
	tree.activateKey(obj.id);

	return div;
}

function getPhraseByPhraseID(phraseID) {
	if (!phraseID) return;
	return model.dialogue.findById(phraseID);
}

function onConversationPhraseSelected(div, obj, tree) {
	var dialoguePhrase = $( "#dialoguePhrase", div );
	var dialogueReply = $( "#dialogueReply", div );
	var dialoguePhraseReplies = $( "#dialoguePhraseReplies", div );
	dialogueReply.hide();
	dialoguePhrase.hide();
	dialoguePhraseReplies.hide();
	if (!obj) return;
	
	var treeNodeKey = getTreeNodeKey(obj);
	var treeNode = tree.getNodeByKey(treeNodeKey);
	if (!treeNode) {
		treeNode = updatePhraseTreeNodesBelow(tree, tree.getRoot(), obj, true);
	}
	
	treeNode.activateSilently();
	
	applyCommonEditorBindings(div, obj, model.dialogue);
	
	if (obj.isPhrase) {
		buildEditorForPhrase(div, obj, tree, treeNode);
	} else {
		buildEditorForReply(div, obj, tree, treeNode);
	}
}

// ========================================================
//   Set up editor for NPC phrases

function buildEditorForPhrase(div, phrase, tree, treeNode) {
	var dialoguePhrase = $( "#dialoguePhrase", div );
	var dialoguePhraseReplies = $( "#dialoguePhraseReplies", div );
	
	checkboxHidesElement( $( '#hasProgressQuest', dialoguePhrase ), $( '#hasProgressQuestDisplay', dialoguePhrase ), phrase.progressQuest);
	checkboxHidesElement( $( '#hasRewardDroplist', dialoguePhrase ), $( '#hasRewardDroplistDisplay', dialoguePhrase ), phrase.rewardDropListID);
	bindFieldToDataStore( $( "#progressQuest", dialoguePhrase ), model.quests);
	bindFieldToDataStore( $( "#rewardDropListID", dialoguePhrase ), model.droplists);
	
	var rebuildChildNodes = function() {
		updatePhraseReplyTreeNodesBelow(tree, treeNode, phrase);
	}
	var reloadReplyTable = function() {
		applyTableEditor({
			table: $( "#replies", dialoguePhraseReplies ),
			array: phrase.replies, 
			templateFunction: function() { return createReplyForPhrase(phrase); }, 
			onItemSelected: function(obj) {
				onConversationPhraseSelected(div, obj, tree);
			},
			onItemAdded: function(addedObject) {
				rebuildChildNodes();
			}
		});
	}
	reloadReplyTable();
	
	var hasOnlyNextReply = $( '#hasOnlyNextReply', dialoguePhraseReplies );
	checkboxHidesElement( hasOnlyNextReply, $( '#hasOnlyNextReplyDisplay', dialoguePhraseReplies ), phrase.hasOnlyNextReply);
	checkboxShowsElement( hasOnlyNextReply, $( '#hasRepliesDisplay', dialoguePhraseReplies ), !phrase.hasOnlyNextReply);
	
	hasOnlyNextReply.change(function() {
		if ( $(this).attr("checked") ) {
			var nextReply = createReplyForPhrase(phrase);
			nextReply.text = 'N';
			phrase.replies = [ nextReply ];
		} else {
			phrase.replies = [ ];
			reloadReplyTable();
		}
		rebuildChildNodes();
	});
	
	var nextPhraseID = $( "#nextPhraseID", dialoguePhraseReplies );
	nextPhraseID.unbind("change").change(function() {
		phrase.replies[0].nextPhraseID = $( this ).val();
		rebuildChildNodes();
	});
	if (phrase.hasOnlyNextReply) {
		nextPhraseID.val(phrase.replies[0].nextPhraseID);
	}
	
	var phraseID = $( "#id", dialoguePhrase );
	phraseID.change(function() {
		treeNode.data.key = phrase.id;
		rebuildChildNodes();
	});
	
	$( "#followNextReply", dialoguePhraseReplies ).button().unbind('click').click(function() {
		openNextPhrase(nextPhraseID.val(), div, phrase.replies[0], tree);
	});

	$( '#message', dialoguePhrase ).change(function() { treeNode.setTitle( getPhraseNodeText(phrase) ); });
	
	dialoguePhrase.show();
	dialoguePhraseReplies.show();
}

function createReplyForPhrase(phrase) {
	return { 
		id: phrase.id, 
		isPhrase: false,
		phrase: phrase
	};
}

function openNextPhrase(nextPhraseID, div, reply, tree) {
	var createNewPhrase = true;
	var phrase;
	if (nextPhraseID) {
		phrase = getPhraseByPhraseID(nextPhraseID);
		if (phrase) {
			createNewPhrase = false;
		}
	} else {
		nextPhraseID = generatePhraseID(reply.phrase.id);
	}
	
	if (createNewPhrase) {
		phrase = { id: nextPhraseID, isPhrase: true };
		model.dialogue.add(phrase);
	}
	reply.nextPhraseID = nextPhraseID;
	
	var treeNodeKey = getTreeNodeKey(reply.phrase);
	var treeNode = tree.getNodeByKey(treeNodeKey);
	updatePhraseReplyTreeNodesBelow(tree, treeNode, reply.phrase);
	//alert("followNextReply: " + nextPhraseID);
	onConversationPhraseSelected(div, phrase, tree);
}

function generatePhraseID(previousPhraseID) {
	var suffix;
	var n = 1;
	
	var match = (/^(.*\D)(\d+)$/g).exec(previousPhraseID);
	if (match) {
		suffix = match[1];
		n = parseInt(match[2]) + 1;
	} else {
		suffix = previousPhraseID + "_";
	}
	
	for (var i = n; i < 1000; ++i) {
		var phraseID = suffix + i;
		if (!getPhraseByPhraseID(phraseID)) return phraseID;
	}
}

// ========================================================
//   Set up editor for replies

function buildEditorForReply(div, reply, tree, treeNode) {
	var dialogueReply = $( "#dialogueReply", div );
	
	checkboxHidesElement( $( '#requiresItems', dialogueReply ), $( '#requiresItemsDisplay', dialogueReply ), reply.requires_itemID);
	checkboxHidesElement( $( '#requiresQuest', dialogueReply ), $( '#requiresQuestDisplay', dialogueReply ), reply.requires_Progress);
	bindFieldToDataStore( $( "#requires_itemID", dialogueReply ), model.items);
	bindFieldToDataStore( $( "#requires_Progress", dialogueReply ), model.quests);
	
	var replyLeadsTo = $( "#replyLeadsTo", dialogueReply );
	replyLeadsTo.val(reply.nextPhraseID);
	if (!replyLeadsTo.val()) { replyLeadsTo.val(""); }
	replyLeadsTo.change(function() { nextPhraseID.val( $(this).val() ).change(); });
	changeHidesElement(replyLeadsTo, $( "#nextPhraseIDDisplay", dialogueReply ) , function() { return replyLeadsTo.val() == ''; } );
	
	var nextPhraseID = $( "#nextPhraseID", dialogueReply );
	nextPhraseID.change(function() {
		updatePhraseTreeNodesBelow(tree, treeNode, getPhraseByPhraseID(reply.nextPhraseID) );
	});
	
	$( "#followReply", dialogueReply ).button().unbind('click').click(function() {
		openNextPhrase(nextPhraseID.val(), div, reply, tree);
	});
	
	$( '#text', dialogueReply ).change(function() { treeNode.setTitle( getReplyNodeText(reply) ); });
	
	dialogueReply.show();
}


// ========================================================
//   Tree node key generators

	function getTreeNodeKey(obj) {
		if (!obj) return "";
		if (obj.isPhrase) return obj.id;
		return getTreeNodeReplyKey(obj);
	}

	function getTreeNodeReplyKey(obj) {
		var idx = 0;
		for (var i = 0; i < obj.phrase.replies.length; ++i) {
			if (obj.phrase.replies[i] == obj) { 
				idx = i;
				break;
			}
		}
		return getTreeNodeReplyKeyIndex(obj, idx);
	}

	function getTreeNodeReplyKeyIndex(obj, idx) {
		return getTreeNodeKey(obj.phrase) + "__reply_" + idx;
	}

		
// ========================================================
//   Tree node title generators
	
	function getPhraseNodeText(phrase) {
		return phrase.message ? shortenString(phrase.message, 30) : "(no phrase text)";
	}

	function getReplyNodeText(reply) {
		return reply.text ? shortenString(reply.text, 30) : "(no reply text)";
	}

// ========================================================
//   Tree-building functions

	// (re)Build a NPC phrase node
	function updatePhraseTreeNodesBelow(tree, parent, phrase, keepExisting) {
		if (!keepExisting) { parent.removeChildren(); }
		
		if (!phrase) return; 
		
		phrase.isPhrase = true;
		var key = getTreeNodeKey(phrase);

		if (tree.getNodeByKey(key)) {
			parent.addChild({
				title: '(conversation loop)'
				,model: phrase
			});
			return;
		}
		
		var phraseNode = parent.addChild({
			title: getPhraseNodeText(phrase)
			,key: key
			,model: phrase
			,icon: 'phrase.png'
		});
		
		if (!phrase.replies) phrase.replies = [];
		updatePhraseReplyTreeNodesBelow(tree, phraseNode, phrase, phrase.replies);
		
		phraseNode.expand(true);
		return phraseNode;
	}

	// (re)Build all nodes below a NPC phrase (i.e. rebuild all reply nodes)
	function updatePhraseReplyTreeNodesBelow(tree, phraseNode, phrase) {
		phraseNode.removeChildren();
		
		if (!phrase.replies) phrase.replies = [];

		if (phrase.replies.length == 1) {
			var singleReply = phrase.replies[0];
			if (singleReply.text == 'N') {
				phrase.hasOnlyNextReply = true;
				updatePhraseTreeNodesBelow(tree, phraseNode, getPhraseByPhraseID(singleReply.nextPhraseID) );
				return;
			}
		}
		
		phrase.replies.forEach(function(reply, idx) {
			jQuery.extend(reply, createReplyForPhrase(phrase));
			var key = getTreeNodeReplyKeyIndex(reply, idx);
			var replyNode = phraseNode.addChild({
				title: getReplyNodeText(reply)
				,key: key
				,model: reply
				,icon: 'reply.png'
			});
			if (reply.nextPhraseID) {
				updatePhraseTreeNodesBelow(tree, replyNode, getPhraseByPhraseID(reply.nextPhraseID) );
			}
			replyNode.expand(true);
		});
	}
