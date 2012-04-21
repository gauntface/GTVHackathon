package com.gtvhackathon.chuggr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        ((Button)findViewById(R.id.btnSciFi)).setOnClickListener(this);


        gridView = (GridView) findViewById(R.id.gridview);

        //gridView.setAdapter(new VideoAdapter(this, videos));
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
            case R.id.btnSciFi:
                new DownloadFilmInfoAsyncTask().execute("Sci-Fi");
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
        public ArrayList<ArchiveVideo> videos = new ArrayList<ArchiveVideo>();

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

            final ImageView iv = (ImageView) layout.findViewById(R.id.videoThumbnail);
            new ImageViewLoadAsyncTask().execute(iv, videos.get(position).getThumb());

            ((TextView)layout.findViewById(R.id.videoTitle)).setText(videos.get(position).getTitle());

            return layout;
        }

    }









    private class ImageViewLoadAsyncTask extends AsyncTask<Object, Integer, Integer> {
        private Bitmap bmp;
        private ImageView iv;

        protected Integer doInBackground(Object... objects) {
            HttpURLConnection con=null;
            try{

                iv = (ImageView) objects[0];
                String url = (String) objects[1];

                URL ulrn = new URL(url);
                con = (HttpURLConnection)ulrn.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                publishProgress(1);
            }catch(Exception e){
                Log.e(MainActivity.this.getClass().toString(),e.toString());
            }
            finally { if (con != null) { con.disconnect(); } }
            return 0;
        }

        protected void onProgressUpdate(Integer... progress) {
            // Remove spinner...
            if (null != bmp)
                iv.setImageBitmap(bmp);
            else {
                iv.setImageResource(R.drawable.default_thumb2);
            }
        }
    }

    private class DownloadFilmInfoAsyncTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... strings) {
            // Chuck spinner on there...

            mArchiveVideoController = ArchiveVideoController.getArchiveViewController();
            mArchiveVideoController.downloadArchiveData(new ArchiveListener() {

                @Override
                public void onSingleItemDownload() {
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
            findViewById(R.id.hintScreen).setVisibility(View.GONE);
            ((ProgressBar)findViewById(R.id.progressMain)).setVisibility(View.VISIBLE);
            // Add spinner
        }

        protected void onPostExecute(Long result) {
            // Remove spinner...

        }

        protected void onProgressUpdate(Integer... progress) {
            ((ProgressBar)findViewById(R.id.progressMain)).setVisibility(View.GONE);

            if (gridView.getAdapter()==null){
                gridView.setAdapter(new VideoAdapter(MainActivity.this, mArchiveVideoController.getVideos()));
            }
            else{
                ((VideoAdapter)gridView.getAdapter()).videos = mArchiveVideoController.getVideos();
                ((VideoAdapter) gridView.getAdapter()).notifyDataSetChanged();
            }

            //gridView.setAdapter(new VideoAdapter(MainActivity.this, mArchiveVideoController.getVideos()));

        }


    }




}
