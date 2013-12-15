package com.gpl.rpg.AndorsTrail.scripting.interpreter;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.ActorCondition;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.actor.Actor;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail.model.map.MapObject;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail.model.quest.QuestProgress;
import com.gpl.rpg.AndorsTrail.scripting.proxyobjects.Item;
import com.gpl.rpg.AndorsTrail.util.ConstRange;

public class ATSMethodCall extends ATSValueReference {

	public enum ObjectMethod {
		worldGetMap {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				return ((WorldContext)targetInstance).maps.findPredefinedMap((String)parameters[0].evaluate(context));
			}
		},
		mapActivateGroup {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				//TODO move this logic in the controller as activateGroup(PredefinedMap map, String group) 
				String group = (String) parameters[0].evaluate(context);
				for (MapObject o : ((PredefinedMap)targetInstance).eventObjects) {
					if (o.group.equals(group)) {
						context.controllers.mapController.activateMapObject((PredefinedMap) targetInstance, o);
					}
				}
				return null;
			}
		},
		mapDeactivateGroup {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				//TODO move this logic in the controller as deactivateGroup(PredefinedMap map, String group)
				String group = (String) parameters[0].evaluate(context);
				for (MapObject o : ((PredefinedMap)targetInstance).eventObjects) {
					if (o.group.equals(group)) {
						context.controllers.mapController.deactivateMapObject(o);
					}
				}
				return null;
			}
		},
		actorAddCondition {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				context.controllers.actorStatsController.rollForConditionEffect(((Actor)targetInstance), new ActorConditionEffect(context.world.actorConditionsTypes.getActorConditionType((String)parameters[0].evaluate(context)), ((Double)parameters[1].evaluate(context)).intValue(), ((Double)parameters[2].evaluate(context)).intValue(), new ConstRange(100, ((Double)parameters[3].evaluate(context)).intValue()) ));
				return null;
			}
		},
		actorClearCondition {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				context.controllers.actorStatsController.rollForConditionEffect(((Actor)targetInstance), new ActorConditionEffect(context.world.actorConditionsTypes.getActorConditionType((String)parameters[0].evaluate(context)), ActorCondition.MAGNITUDE_REMOVE_ALL, 1, new ConstRange(100, ((Double)parameters[1].evaluate(context)).intValue()) ));
				return null;
			}
		},
		getItemCount {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				return ((Player)targetInstance).inventory.getItemQuantity((String) parameters[0].evaluate(context));
			}
		},
		giveItem {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				((Player)targetInstance).inventory.addItem(context.world.itemTypes.getItemType((String) parameters[0].evaluate(context)), ((Double) parameters[1].evaluate(context)).intValue());
				return null;
			}
		},
		removeItem {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				((Player)targetInstance).inventory.removeItem((String) parameters[0].evaluate(context), ((Double) parameters[1].evaluate(context)).intValue());
				return null;
			}
		},
		getItemInSlot {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				ItemType type = ((Player)targetInstance).inventory.getItemTypeInWearSlot(Inventory.WearSlot.valueOf((String) parameters[0].evaluate(context)));
				return new Item(type, null, type.effects_equip); //TODO better "object" handling
			}
		},
		equipItemInSlot {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				ItemType type = context.world.itemTypes.getItemType((String) parameters[0].evaluate(context));
				context.controllers.itemController.equipItem(type, Inventory.WearSlot.valueOf((String) parameters[1].evaluate(context)));
				return null;
			}
		},
		unequipSlot {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				//TODO Throws a systematic NPE until unequipSlot stops using ItemType...
				context.controllers.itemController.unequipSlot(null, Inventory.WearSlot.valueOf((String) parameters[1].evaluate(context)));
				return null;
			}
		},
		hasQuestProgress {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				return ((Player)targetInstance).hasExactQuestProgress((String) parameters[0].evaluate(context), ((Double) parameters[1].evaluate(context)).intValue());
			}
		},
		addQuestProgress {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				return ((Player)targetInstance).addQuestProgress(new QuestProgress((String) parameters[0].evaluate(context), ((Double) parameters[1].evaluate(context)).intValue()));
			}
		},
		addExperience {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				context.controllers.actorStatsController.addExperience(((Double)parameters[0].evaluate(context)).intValue());
				return null;
			}
		},
		addAlignment {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				((Player)targetInstance).addAlignment((String) parameters[0].evaluate(context), ((Double) parameters[1].evaluate(context)).intValue());
				return null;
			}
		},
		getAlignment {
			@Override
			public Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance) {
				return Double.valueOf(((Player)targetInstance).getAlignment((String) parameters[0].evaluate(context)));
			}
		};
		
		public abstract Object evaluate(ScriptContext context, ATSValueReference[] parameters, Object targetInstance);
	}
	
	public final ObjectMethod method;
	public final ATSValueReference[] parameters;
	public final ATSValueReference targetInstance;
	
	public ATSMethodCall(ObjectMethod method, ATSValueReference[] parameters, ATSValueReference optionalTargetInstance) {
		this.method = method;
		this.parameters = parameters;
		this.targetInstance = optionalTargetInstance;
	}
	
	@Override
	public void set(ScriptContext context, Object value) {
		throw new RuntimeException("ATScript : Trying to associate a value to a method result ? come on...");
	}

	@Override
	public Object evaluate(ScriptContext context) {
		Object returned = method.evaluate(context, parameters, targetInstance.evaluate(context));
		return next!=null ? next.evaluate(context) : returned;
	}
	

}
