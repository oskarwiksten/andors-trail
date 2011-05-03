
function createQuestEditor(obj) {
	var div = $( "#templates #editQuest" ).clone(true);
	applyCommonEditorBindings(div, obj, model.quests);
	if (!obj.stages) obj.stages = [];
	var array = obj.stages;
	var createNewStage = function() {
		var nextProgress;
		if (array.length > 0) { nextProgress = parseInt(array[array.length - 1].progress) + 10; }
		if (!nextProgress) { nextProgress = 10; }
		return { progress: nextProgress };
	};
	applyTableEditor({
		table: $( "#stages", div ),
		dialog: questlogDialog, 
		array: array, 
		templateFunction: createNewStage
	});
	return div;
}

