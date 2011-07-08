package com.gpl.rpg.AndorsTrail.util;

import android.os.Handler;
import android.os.Message;

public final class TimedMessageTask extends Handler {
	private final long interval;
	private final boolean requireIntervalBeforeFirstTick;
	private final Callback callback;
	private long nextTickTime;
	private boolean hasQueuedTick = false;
	
	public TimedMessageTask(Callback callback, long interval, boolean requireIntervalBeforeFirstTick) {
		this.interval = interval;
		this.requireIntervalBeforeFirstTick = requireIntervalBeforeFirstTick;
		this.callback = callback;
		this.nextTickTime = System.currentTimeMillis() + interval;
	}
	
	@Override
    public void handleMessage(Message msg) {
    	if (!hasQueuedTick) return;
    	hasQueuedTick = false;
    	tick();
    }
	
	private void tick() {
		nextTickTime = System.currentTimeMillis() + interval;
		callback.onTick(this);
    }

    private void sleep(long delayMillis) {
    	this.removeMessages(0);
        sendMessageDelayed(obtainMessage(0), delayMillis);
    }
    
    private boolean hasElapsedIntervalTime() {
    	return System.currentTimeMillis() >= nextTickTime;
    }
    
    public void queueAnotherTick() {
    	if (hasQueuedTick) return;
    	hasQueuedTick = true;
    	sleep(interval);
    }
    
    private boolean shouldCauseTickOnStart() {
    	if (requireIntervalBeforeFirstTick) return false;
    	if (hasQueuedTick) return false;
		if (!hasElapsedIntervalTime()) return false;
    	return true;
    }
    
	public void start() {
		if (shouldCauseTickOnStart()) tick();
		queueAnotherTick();
	}

	public void stop() {
    	hasQueuedTick = false;
	}
	
	public interface Callback {
		public void onTick(TimedMessageTask task);
	}
}
