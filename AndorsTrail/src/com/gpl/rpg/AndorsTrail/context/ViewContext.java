package com.gpl.rpg.AndorsTrail.context;

import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.activity.MainActivity;
import com.gpl.rpg.AndorsTrail.controller.CombatController;
import com.gpl.rpg.AndorsTrail.controller.Controller;
import com.gpl.rpg.AndorsTrail.controller.GameRoundController;
import com.gpl.rpg.AndorsTrail.controller.VisualEffectController;
import com.gpl.rpg.AndorsTrail.controller.ItemController;
import com.gpl.rpg.AndorsTrail.controller.MonsterMovementController;
import com.gpl.rpg.AndorsTrail.controller.MovementController;

public class ViewContext extends WorldContext {
	//Views
	public final MainActivity mainActivity;
	
	//Controllers
	public final Controller controller;
	public final GameRoundController gameRoundController;
	public final CombatController combatController;
	public final VisualEffectController effectController;
	public final ItemController itemController;
	public final MonsterMovementController monsterMovementController;
	public final MovementController movementController;
	
	public final AndorsTrailPreferences preferences;
	
	public ViewContext(AndorsTrailApplication application, MainActivity mainActivity) {
		super(application.world);
		this.mainActivity = mainActivity;
		this.preferences = application.preferences;

		this.controller = new Controller(this);
		this.gameRoundController = new GameRoundController(this);
		this.combatController = new CombatController(this);
		this.effectController = new VisualEffectController(this);
		this.itemController = new ItemController(this);
		this.monsterMovementController = new MonsterMovementController(this);
		this.movementController = new MovementController(this);
	}
}
