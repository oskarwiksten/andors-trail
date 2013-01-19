package com.gpl.rpg.AndorsTrail.resource.parsers.json;

public final class JsonFieldNames {
	public static final class ActorCondition {
		public static final String conditionTypeID = "id";
		public static final String name = "name";
		public static final String iconID = "iconID";
		public static final String category = "category";
		public static final String isStacking = "isStacking";
		public static final String isPositive = "isPositive";
		public static final String roundEffect = "roundEffect";
		public static final String fullRoundEffect = "fullRoundEffect";
		public static final String abilityEffect = "abilityEffect";
	}

	public static final class StatsModifierTraits {
		public static final String visualEffectID = "visualEffectID";
		public static final String increaseCurrentHP = "increaseCurrentHP";
		public static final String increaseCurrentAP = "increaseCurrentAP";
	}

	public static final class AbilityModifierTraits {
		public static final String increaseMaxHP = "increaseMaxHP";
		public static final String increaseMaxAP = "increaseMaxAP";
		public static final String increaseMoveCost = "increaseMoveCost";
		public static final String increaseUseItemCost = "increaseUseItemCost";
		public static final String increaseReequipCost = "increaseReequipCost";
		public static final String increaseAttackCost = "increaseAttackCost";
		public static final String increaseAttackChance = "increaseAttackChance";
		public static final String increaseCriticalSkill = "increaseCriticalSkill";
		public static final String setCriticalMultiplier = "setCriticalMultiplier";
		public static final String increaseAttackDamage = "increaseAttackDamage";
		public static final String increaseBlockChance = "increaseBlockChance";
		public static final String increaseDamageResistance = "increaseDamageResistance";
	}

	public static final class ItemCategory {
		public static final String itemCategoryID = "id";
		public static final String name = "name";
		public static final String actionType = "actionType";
		public static final String inventorySlot = "inventorySlot";
		public static final String size = "size";
	}

	public static final class DropList {
		public static final String dropListID = "id";
		public static final String items = "items";
	}

	public static final class DropItem {
		public static final String itemID = "itemID";
		public static final String quantity = "quantity";
		public static final String chance = "chance";
	}

	public static final class Range {
		public static final String min = "min";
		public static final String max = "max";
	}

	public static final class Phrase {
		public static final String phraseID = "id";
		public static final String message = "message";
		public static final String rewards = "rewards";
		public static final String replies = "replies";
	}

	public static final class Reply {
		public static final String text = "text";
		public static final String nextPhraseID = "nextPhraseID";
		public static final String requires = "requires";
	}

	public static final class ReplyRequires {
		public static final String progress = "progress";
		public static final String item = "item";
	}

	public static final class ReplyRequiresItem {
		public static final String itemID = "itemID";
		public static final String quantity = "quantity";
		public static final String requireType = "requireType";
	}

	public static final class PhraseReward {
		public static final String rewardType = "rewardType";
		public static final String rewardID = "rewardID";
		public static final String value = "value";
	}
}
