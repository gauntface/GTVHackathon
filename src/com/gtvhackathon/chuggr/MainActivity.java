package com.gtvhackathon.chuggr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gtvhackathon.chuggr.model.ArchiveVideo;

import com.gtvhackathon.chuggr.controller.ArchiveVideoController;
import com.gtvhackathon.chuggr.controller.ArchiveVideoController.ArchiveListener;

public class MainActivity extends Activity implements View.OnClickListener {
    
    private ArchiveVideoController mArchiveVideoController;
    private GridView gridView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ((Button)findViewById(R.id.btnHorror)).setOnClickListener(this);
        ((Button)findViewById(R.id.btnComedy)).setOnClickListener(this);

        ArrayList<ArchiveVideo> videos = new ArrayList<ArchiveVideo>();

        Uri uriVideo = Uri.parse("http://www.stealthcopter.com/video1.mp4");
        //"android.resource://com.gtvhackathon.chuggr/raw/video1");
        Uri uriThumb = Uri.parse("android.resource://com.gtvhackathon.chuggr/" + R.drawable.ic_launcher);

        ArchiveVideo video = new ArchiveVideo();
        video.setTitle("Sab");
        video.setThumb("");
        video.setVideoURL("http://archive.org/download/Black_Sabbath_US_trailer/Black_Sabbath_US_trailer.mp4");
        video.setDescription("Video description....");


        videos.add(video);

        ArchiveVideo video2 = new ArchiveVideo();
        video2.setTitle("Xmen");
        video2.setThumb("");
        video2.setVideoURL("http://www.videodetective.net/player.aspx?cmd=6&fmt=4&customerid=699923&videokbrate=80&publishedid=244133");
        video2.setDescription("Video description....");

        videos.add(video2);

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new VideoAdapter(this, videos));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                playVideo((ArchiveVideo) adapterView.getItemAtPosition(i));
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnHorror:
                new DownloadFilmInfoAsyncTask().execute("Horror");
                break;
            case R.id.btnComedy:
                new DownloadFilmInfoAsyncTask().execute("Comedy");
                break;
        }
    }




    private void playVideo(ArchiveVideo vid) {
        final Bundle b = vid.getAsBundle();
        final Intent i = new Intent("com.gtvhackathon.chuggr");
        i.putExtra("com.gtvhackathon.chuggr", b);
        startActivity(i);
    }


    public class VideoAdapter extends BaseAdapter {
        private Context mContext;
        ArrayList<ArchiveVideo> videos = new ArrayList<ArchiveVideo>();

        public VideoAdapter(Context c, ArrayList<ArchiveVideo> videos) {
            mContext = c;

                for (ArchiveVideo video : videos){
                    this.videos.add(video);
                }
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

            ((TextView)layout.findViewById(R.id.videoTitle)).setText(videos.get(position).getTitle());

            return layout;
        }

    }






    private class DownloadFilmInfoAsyncTask extends AsyncTask<String, Integer, Integer> {
//        private ArrayList<ArchiveVideo> videos;

        protected Integer doInBackground(String... strings) {
            // Chuck spinner on there...

            mArchiveVideoController = ArchiveVideoController.getArchiveViewController();
            mArchiveVideoController.downloadArchiveData(new ArchiveListener() {

                @Override
                public void onDownloadComplete() {
                    publishProgress(1);
                }

                @Override
                public void onError() {
                    // On Error - Ooops

                    // Remove spinner...
                }

            }, strings[0]);


            return null;
        }

        protected void onPreExecute(){
            // Remove current
            gridView.setAdapter(null);
            ((ProgressBar)findViewById(R.id.progressMain)).setVisibility(View.VISIBLE);
               // Add spinner
        }

        protected void onPostExecute(Long result) {
            // Remove spinner...

        }

        protected void onProgressUpdate(Integer... progress) {
            ((ProgressBar)findViewById(R.id.progressMain)).setVisibility(View.GONE);

            ArrayList<ArchiveVideo> temp = mArchiveVideoController.getVideos();

            if (temp!=null){
                gridView.setAdapter(new VideoAdapter(MainActivity.this, mArchiveVideoController.getVideos()));
            }
            else Log.e(MainActivity.this.getClass().toString(), "NULL");

        }


    }




}
