package com.gtvhackathon.chuggr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.net.URI;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements View.OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Add content based on category selected:
        addContentFromCategory();



    }

    private void addContentFromCategory() {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.contentView);

        Context c = getApplicationContext();
        LayoutInflater mInflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);

        for (int i=0; i<3; i++){
            View v;
            v = mInflater.inflate(R.layout.video_thumb, contentLayout, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playVideo();
                }
            });
            contentLayout.addView(v);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
        }
    }

    private void playVideo() {
        Uri uriVideo = Uri.parse("http://www.stealthcopter.com/video1.mp4");

//        Uri uriVideo = Uri.parse("android.resource://com.gtvhackathon.chuggr/raw/video1");
        Uri uriThumb = Uri.parse("android.resource://com.gtvhackathon.chuggr/" + R.drawable.ic_launcher);

        Video vid = new Video(uriThumb, uriVideo, "Video Title", "Subtitle", "Description");

        final Bundle b = vid.getAsBundle();
        final Intent i = new Intent("com.gtvhackathon.chuggr");
        i.putExtra("com.gtvhackathon.chuggr", b);
        startActivity(i);
    }
}
