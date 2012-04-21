package com.gtvhackathon.chuggr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.gtvhackathon.chuggr.controller.ArchiveVideoController;

public class MainActivity extends Activity implements View.OnClickListener {
    
    private ArchiveVideoController mArchiveVideoController;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        GridView g = (GridView) findViewById(R.id.gridview);
        g.setAdapter(new VideoAdapter(this));
        g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                playVideo((Video) adapterView.getItemAtPosition(i));
            }
        });

        mArchiveVideoController = ArchiveVideoController.getArchiveViewController();
        mArchiveVideoController.downloadArchiveData(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
        }
    }

    private void playVideo(Video vid) {
        final Bundle b = vid.getAsBundle();
        final Intent i = new Intent("com.gtvhackathon.chuggr");
        i.putExtra("com.gtvhackathon.chuggr", b);
        startActivity(i);
    }


    public class VideoAdapter extends BaseAdapter {
        private Context mContext;
        ArrayList<Video> videos = new ArrayList<Video>();

        public VideoAdapter(Context c) {
            mContext = c;

            Uri uriVideo = Uri.parse("http://www.stealthcopter.com/video1.mp4");
            //"android.resource://com.gtvhackathon.chuggr/raw/video1");
            Uri uriThumb = Uri.parse("android.resource://com.gtvhackathon.chuggr/" + R.drawable.ic_launcher);
            videos.add( new Video(uriThumb, uriVideo, "Video Title", "Subtitle", "Description"));
            videos.add( new Video(uriThumb, uriVideo, "Video Title", "Subtitle", "Description"));
            videos.add( new Video(uriThumb, uriVideo, "Video Title", "Subtitle", "Description"));
            uriVideo = Uri.parse("http://archive.org/download/Black_Sabbath_US_trailer/Black_Sabbath_US_trailer.mp4");

            videos.add( new Video(uriThumb, uriVideo, "Archive.org vid", "Subtitle", "Description"));
            uriVideo = Uri.parse("http://www.videodetective.net/player.aspx?cmd=6&fmt=4&customerid=699923&videokbrate=80&publishedid=244133");

            videos.add( new Video(uriThumb, uriVideo, "IA VIDEO", "Subtitle", "Description"));
        }

        @Override
        public int getCount() {
            return videos.size();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Object getItem(int i) {
            return videos.get(i);  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public long getItemId(int i) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        // create a new ButtonView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layout;
            LayoutInflater li = LayoutInflater.from(mContext);

            if (convertView == null) {  // if it's not recycled, initialize some attributes
                layout = (LinearLayout) li.inflate(R.layout.video_thumb, null);
            } else {
                layout = (LinearLayout) convertView;
            }

            ((TextView)layout.findViewById(R.id.videoTitle)).setText(videos.get(position).mTitle);

            return layout;
        }

    }
}
