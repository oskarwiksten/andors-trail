package com.gpl.rpg.AndorsTrailPlaybook.controller.listeners;

import com.gpl.rpg.AndorsTrailPlaybook.controller.VisualEffectController.VisualEffectAnimation;

public interface VisualEffectFrameListener {
	void onNewAnimationFrame(VisualEffectAnimation animation, int tileID, int textYOffset);
	void onAnimationCompleted(VisualEffectAnimation animation);
}
