package com.gtvhackathon.chuggr;

import android.util.Log;

public class TimerRunnable implements Runnable {

    private final static String TAG = "TimerRunnable";
    
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
        while(mIsRunning) {
            millisecondsPosition = mProvider.getVideoPosition();
            Log.v(TAG, "millisecondsPosition = "+millisecondsPosition);
            try {
                // TODO: Tweak to use the time for the next event
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public boolean isRunning() {
        return mIsRunning;
    }
    
    public void kill() {
        mIsRunning = false;
    }
    
    public interface TimerProvider {
        
        public int getVideoPosition();
        
    }
    
    public interface TimerListener {
        
        public void onEventTriggered();
        
    }
    
}
