// Copyright 2011 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.gtvhackathon.chuggr;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.animation.*;
import android.widget.*;
import com.gtvhackathon.chuggr.TimerRunnable.TimerListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

// The VideoPlayerActivity which sets up a video view, attaches a MediaControler to it.
// Communication is via private intent.  This could use the MediaPlayer API's but that would
// add a lot of complexity.

public class VideoPlayerActivity extends Activity
        implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, TimerListener, MediaPlayer.OnPreparedListener {
    
    public static final String TAG = "VPActivity";

    public VideoView mVideoView = null;
    private LayoutParams mDefaultVideoViewSize;

    private VideoTimerProvider mTimeProvider;
    private TimerRunnable mTimerRunnable;
    private ExecutorService mExecutor;
    //private EventPopupWindow mEventPopup;
    private View mEventPopupView;
    private GTVServer gtvserver;

    // Handle AudioFocus issues
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch(focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.i(C.TAG, "AF Gain");
                if (mVideoView != null)
                    mVideoView.resume();
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                Log.i(C.TAG, "AF Loss");
                if (mVideoView != null)
                    mVideoView.stopPlayback();
                mVideoView = null;
                this.finish(); // Let's move on.
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.i(C.TAG, "AF Transient");
                if (mVideoView != null)
                    mVideoView.pause();
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(C.TAG, "done.");
        mVideoView = null;
        this.finish();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(C.TAG, "IO Error e=" + what + " x=" + extra);
        return false; // Will call onCompletion
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.gtvserver = new GTVServer(this);

        this.gtvserver.sendEvent("Connected Thanks");

        // Request Audio Focus
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.e(C.TAG, "Can't get AudioFocus " + result);
            this.finish(); // Just give up.
        }

        setContentView(R.layout.details);
        final Intent i = getIntent();
        final Bundle b = i.getBundleExtra("com.gtvhackathon.chuggr");

        ((TextView) findViewById(R.id.videoTitle)).setText(b.getString("title"));
        ((TextView) findViewById(R.id.videoDescription)).setText(b.getString("description"));
        ((RatingBar) findViewById(R.id.ratingBar1)).setRating(b.getFloat("rating"));
        mVideoView = (VideoView) findViewById(R.id.videoView1);
        mVideoView.setVideoPath(b.getString("source"));
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnPreparedListener(this);

        //findViewById(R.id.progressVideo).setVisibility(View.GONE);

        MediaController mc = new MediaController(this, true);

        mc.setMediaPlayer(mVideoView);
        mc.setAnchorView(mVideoView);
        mVideoView.setMediaController(mc);
        mVideoView.requestFocus();
        mVideoView.start();
        mDefaultVideoViewSize = mVideoView.getLayoutParams();

        // Make it so that we can click the video view and make it zoom
        // Note - this code isn't currently running, but if you were to get a
        // click it does the right thing.
        mVideoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                LayoutParams lp = v.getLayoutParams();
                if (lp.width != LayoutParams.MATCH_PARENT) {
                    lp.width = LayoutParams.MATCH_PARENT;
                    lp.height = LayoutParams.MATCH_PARENT;
                } else {
                    lp = mDefaultVideoViewSize;
                }
                v.setLayoutParams(lp);
            }
        });
        
        mEventPopupView = findViewById(R.id.event_popup);
        mTimeProvider = new VideoTimerProvider(mVideoView, this);
        
        mExecutor = Executors.newFixedThreadPool(1);
        mTimerRunnable = new TimerRunnable(mTimeProvider, this);
        mExecutor.execute(mTimerRunnable);

        Handler handler;
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AnimationSet set = new AnimationSet(false);

                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new DecelerateInterpolator()); //add this
                fadeOut.setDuration(1500);

                Animation pullUp = new TranslateAnimation(0.0f,0.0f,0.0f,-300f);
                pullUp.setInterpolator(new DecelerateInterpolator());
                pullUp.setDuration(1500);

                set.addAnimation(pullUp);
                set.addAnimation(fadeOut);
                ((View)findViewById(R.id.controlsLayout)).startAnimation(set);

                try {
                    Thread.sleep(1500);
                    // Do some stuff
                } catch (Exception e) {
                    e.getLocalizedMessage();
                }
                findViewById(R.id.controlsLayout).setVisibility(View.GONE);

            }
        };
        handler.postDelayed(runnable, 8000);

    }

    @Override
    public void onPause() {
        super.onPause();
        mTimeProvider.setVideoView(null);
        mTimerRunnable.kill();
    }

    @Override
    public void onEventTriggered(int eventIndex, String reason) {
        Log.v(C.TAG, "onEventTriggered() for event = "+eventIndex);
        eventAnimateBeerTrigger(eventIndex, reason);
        if (this.gtvserver!=null){
            Log.e("xx","Sending "+reason);
            this.gtvserver.sendEvent(reason);
        }
    }

    public void eventAnimateBeerTrigger(int eventIndex, String text){
        if (text==null) text="Drink!";
        new AnimationBeerAsyncTask().execute(text);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        findViewById(R.id.progressVideo).setVisibility(View.GONE);
    }


    private class AnimationBeerAsyncTask extends AsyncTask<String, Integer, Integer> {

        private String mString = "";

        protected Integer doInBackground(String... strings) {

            mString=strings[0];

            publishProgress(1);

            try {
                Thread.sleep(3000);
                // Do some stuff
            } catch (Exception e) {
                e.getLocalizedMessage();
            }

            publishProgress(3);

            return 0;
        }


        protected void onProgressUpdate(Integer... progress) {

            AnimationSet animation = new AnimationSet(false); //change to false

            ((TextView)mEventPopupView.findViewById(R.id.textDrink)).setText(mString);

            if (progress[0]==1){
                mEventPopupView.setVisibility(View.VISIBLE);

                AnimationSet as = new AnimationSet(false);

                Animation fadeIn = new AlphaAnimation(0, 1);
                Animation zoom = new ScaleAnimation(0.5f,1.0f,0.5f,1.0f,350f,250f);
                zoom.setInterpolator(new DecelerateInterpolator());
                zoom.setDuration(1000);
                zoom.setStartOffset(500);

                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                fadeIn.setDuration(1000);


                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new DecelerateInterpolator()); //add this
                fadeOut.setDuration(1000);
                fadeOut.setStartOffset(2000);


                as.addAnimation(fadeIn);
                as.addAnimation(zoom);
                as.addAnimation(fadeOut);

                mEventPopupView.startAnimation(as);
            }
            else if (progress[0]==3){
                mEventPopupView.setVisibility(View.GONE);
            }

            return;
        }

    }


}
