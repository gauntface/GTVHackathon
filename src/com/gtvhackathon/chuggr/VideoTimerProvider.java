package com.gtvhackathon.chuggr;

import android.os.Handler;
import android.os.Message;
import android.widget.VideoView;

import com.gtvhackathon.chuggr.TimerRunnable.TimerProvider;

public class VideoTimerProvider implements TimerProvider {

    private VideoView mVideoView;
    private int[] mEventTimes;
    
    public VideoTimerProvider(VideoView videoView) {
        mVideoView = videoView;
        mEventTimes = new int[]{1,2};
    }

    @Override
    public int getCurrentVideoPosition() {
        if(mVideoView != null) {
            return mVideoView.getCurrentPosition();
        }
        return -1;
    }

    public int getNumberOfEvents() {
        return mEventTimes.length;
    }
    
    @Override
    public int getEventTimeSeconds(int index) {
        return mEventTimes[index];
    }

    public void setVideoView(VideoView videoView) {
        mVideoView = videoView;
    }
    
}
