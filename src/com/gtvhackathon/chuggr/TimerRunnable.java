package com.gtvhackathon.chuggr;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TimerRunnable implements Runnable {

    private final static String TAG = "TimerRunnable";
    private final static int MIN_THREAD_SLEEP_TIME = 50;
    private final static int MSG_ON_EVENT_TRIGGERED = 0;
    
    private boolean mIsRunning;
    private TimerProvider mProvider;
    private TimerListener mListener;
    private Handler mHandler;
    private String r = "";
    public TimerRunnable(TimerProvider provider, TimerListener listener) {
        mIsRunning = false;
        mProvider = provider;
        mListener = listener;
        
        mHandler = new Handler() {
            
            public void handleMessage(Message msg) {
                switch(msg.what) {
                case MSG_ON_EVENT_TRIGGERED:
                    mListener.onEventTriggered(msg.arg1, r);
                    break;
                }
            }
            
        };
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
            r = mProvider.getEventReason(currentEventIndex);
            millisecondsPosition = mProvider.getCurrentVideoPosition();
            if(millisecondsPosition != -1) {
                if(millisecondsPosition< currentEventMilliseconds) {
                    // We need to sleep for a while
                    waitFor(millisecondsPosition - currentEventMilliseconds);
                } else {
                    Message msg = Message.obtain(mHandler, MSG_ON_EVENT_TRIGGERED);
                    msg.arg1 = currentEventIndex;
                    mHandler.sendMessage(msg);
                    
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
        public String getEventReason(int index);
        public int getEventTimeSeconds(int index);
        public int getNumberOfEvents();
    }
    
    public interface TimerListener {
        
        public void onEventTriggered(int eventIndex, String reason);
        
    }
    
}
