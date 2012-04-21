package com.gtvhackathon.chuggr;

import android.app.Activity;
import android.content.Intent;

import java.net.URI;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VideoActivity extends Activity implements View.OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ((Button)findViewById(R.id.btnVideo)).setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnVideo:
                    playVideo();
                break;
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
