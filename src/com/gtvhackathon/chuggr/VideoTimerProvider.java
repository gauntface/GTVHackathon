package com.gtvhackathon.chuggr;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.VideoView;

import com.gtvhackathon.chuggr.TimerRunnable.TimerProvider;

import java.util.ArrayList;
import java.util.Vector;

public class VideoTimerProvider implements TimerProvider {

    private VideoView mVideoView;
    ArrayList<XMLParser.timeAndReason> mEventTimesReason;

    public VideoTimerProvider(VideoView videoView, Context context) {
        mVideoView = videoView;
        XMLParser xmlParser = new XMLParser();
        mEventTimesReason = xmlParser.parse(XMLParser.loadFile("test.xml", context));

    }

    @Override
    public int getCurrentVideoPosition() {
        if(mVideoView != null) {
            return mVideoView.getCurrentPosition();
        }
        return -1;
    }



    public int getNumberOfEvents() {
        return mEventTimesReason.size();
    }

    @Override
    public int getEventTimeSeconds(int index) {
        return mEventTimesReason.get(index).time;
    }

    @Override
    public String getEventReason(int index){
        return mEventTimesReason.get(index).reason;
    }

    public void setVideoView(VideoView videoView) {
        mVideoView = videoView;
    }




}
