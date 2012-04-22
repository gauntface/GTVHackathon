package com.gtvhackathon.chuggr;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.VideoView;

import com.gtvhackathon.chuggr.TimerRunnable.TimerProvider;

import java.util.Vector;

public class VideoTimerProvider implements TimerProvider {

    private VideoView mVideoView;
    private int[] mEventTimes;
    private String[] mEventResons;


    public VideoTimerProvider(VideoView videoView, Context context) {
        mVideoView = videoView;
        XMLParser xmlParser = new XMLParser();
        Vector<XMLParser.DrinkEvent> xventTimes = xmlParser.parse(XMLParser.loadFile("test.xml", context));
        mEventTimes = xventTimes.get(0).times;
        mEventResons = xventTimes.get(0).getDrinkReasons();
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

    @Override
    public String getEventReason(int index){
        return mEventResons[index];
    }

    public void setVideoView(VideoView videoView) {
        mVideoView = videoView;
    }




}
