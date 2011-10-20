package com.gpl.rpg.AndorsTrail.util;

import android.os.Handler;
import android.os.Message;

public final class TimedMessageTask extends Handler {
	private final long interval;
	private final boolean requireIntervalBeforeFirstTick;
	private final Callback callback;
	private long nextTickTime;
	private boolean hasQueuedTick = false;
	private boolean isAlive = false;
	
	public TimedMessageTask(Callback callback, long interval, boolean requireIntervalBeforeFirstTick) {
		this.interval = interval;
		this.requireIntervalBeforeFirstTick = requireIntervalBeforeFirstTick;
		this.callback = callback;
		this.nextTickTime = System.currentTimeMillis() + interval;
	}
	
	@Override
    public void handleMessage(Message msg) {
		if (!isAlive) return;
	    if (!hasQueuedTick) return;
    	hasQueuedTick = false;
    	if (tick()) queueAnotherTick();
    }
	
	private boolean tick() {
		nextTickTime = System.currentTimeMillis() + interval;
		return callback.onTick(this);
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
		isAlive = true;
		if (shouldCauseTickOnStart()) tick();
		queueAnotherTick();
	}

	public void stop() {
    	hasQueuedTick = false;
    	isAlive = false;
	}
	
	public interface Callback {
		public boolean onTick(TimedMessageTask task);
	}
}
