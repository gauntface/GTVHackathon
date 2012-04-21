package com.gtvhackathon.chuggr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.net.URI;
import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class MainActivity extends Activity implements View.OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Add content based on category selected:
        addContentFromCategory();

        GridView g = (GridView) findViewById(R.id.gridview);
        g.setAdapter(new VideoAdapter(this));
        g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                playVideo((Video) adapterView.getItemAtPosition(i));
            }
        });

    }

    private void addContentFromCategory() {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.contentView);

        Context c = getApplicationContext();
        LayoutInflater mInflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);

//        for (int i=0; i<3; i++){
//            View v;
//            v = mInflater.inflate(R.layout.video_thumb, contentLayout, false);
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    playVideo();
//                }
//            });
//            contentLayout.addView(v);
//        }
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
            videos.add( new Video(uriThumb, uriVideo, "Video Title", "Subtitle", "Description"));
            videos.add( new Video(uriThumb, uriVideo, "Video Title", "Subtitle", "Description"));
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

//            layout.setOnClickListener(new MyOnClickListener(position));

            ((TextView)layout.findViewById(R.id.videoTitle)).setText("Video " + position);

            return layout;
        }

//        class MyOnClickListener implements View.OnClickListener
//        {
//            private final int position;
//
//            public MyOnClickListener(int position)
//            {
//                this.position = position;
//            }
//
//            @Override
//            public void onClick(View v)
//            {
//               playVideo(videos.get(this.position));
//            }
//        }


    }






}
