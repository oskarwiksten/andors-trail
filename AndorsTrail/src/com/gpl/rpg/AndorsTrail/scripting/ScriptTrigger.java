package com.gpl.rpg.AndorsTrail.scripting;

public class ScriptTrigger {

	public static enum Categories {
		map(new Events[]{Events.onEnter, Events.onLeave}),
		attack(new Events[]{Events.onTry, Events.onHit, Events.onMiss}),
		item(new Events[]{Events.onEquip, Events.onUse}),
		player(new Events[]{Events.onReceivedHit, Events.onKilled, Events.onLevelUp, Events.onSkillUp, Events.onRewardReceived, Events.statsUpdated}),
		actor(new Events[]{Events.onReceivedHit, Events.onKilled, Events.statsUpdated});
		
		private final Events[] allowedEvents;
		
		private Categories(Events[] allowedEvents) {
			this.allowedEvents = allowedEvents;
		}
		
		public boolean isAllowed(Events ev) {
			for (Events e : allowedEvents) {
				if (e.equals(ev)) return true;
			}
			return false;
		}
		
	}
	
	public static enum Events {
		onEnter,
		onLeave,
		onTry,
		onHit,
		onMiss,
		onEquip,
		onUse,
		onLevelUp,
		onSkillUp,
		onRewardReceived,
		onReceivedHit,
		onKilled,
		statsUpdated,
		
	}
	
	public final Categories category;
	public final Events event;
	
	public ScriptTrigger(String category, String event) {
		this.category = Categories.valueOf(category);
		this.event = Events.valueOf(event);
		if (!this.category.isAllowed(this.event)){
			throw new RuntimeException("ERROR : "+event+" is not an event type for category "+category);
		}
	}
	
}
