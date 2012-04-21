package com.gtvhackathon.chuggr;

import android.util.Log;

public class TimerRunnable implements Runnable {

    private final static String TAG = "TimerRunnable";
    private final static int MIN_THREAD_SLEEP_TIME = 50;
    
    private boolean mIsRunning;
    private TimerProvider mProvider;
    private TimerListener mListener;
    
    public TimerRunnable(TimerProvider provider, TimerListener listener) {
        mIsRunning = false;
        mProvider = provider;
        mListener = listener;
    }

    @Override
    public void run() {
        mIsRunning = true;
        
        int millisecondsPosition;
        int noOfEvents = mProvider.getNumberOfEvents();
        int currentEventIndex = 0;
        int currentEventMilliseconds = 0;
        
        while(mIsRunning && currentEventIndex < noOfEvents) {
            currentEventMilliseconds = mProvider.getEventTimeSeconds(currentEventIndex) * 1000;
            millisecondsPosition = mProvider.getCurrentVideoPosition();
            if(millisecondsPosition != -1) {
                Log.v(TAG, "currentEventMilliseconds = "+currentEventMilliseconds);
                Log.v(TAG, "millisecondsPosition = "+millisecondsPosition);
                if(millisecondsPosition< currentEventMilliseconds) {
                    // We need to sleep for a while
                    waitFor(millisecondsPosition - currentEventMilliseconds);
                } else {
                    mListener.onEventTriggered();
                    currentEventIndex++;
                }
                
            }
        }
    }
    
    private void waitFor(int millisecondsToSleep) {
        if(millisecondsToSleep < 0) {
            millisecondsToSleep = MIN_THREAD_SLEEP_TIME;
        }
        try {
            Thread.sleep(millisecondsToSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isRunning() {
        return mIsRunning;
    }
    
    public void kill() {
        mIsRunning = false;
    }
    
    public interface TimerProvider {
        
        public int getCurrentVideoPosition();
        public int getEventTimeSeconds(int index);
        public int getNumberOfEvents();
    }
    
    public interface TimerListener {
        
        public void onEventTriggered();
        
    }
    
}
