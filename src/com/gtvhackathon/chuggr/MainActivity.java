package com.gtvhackathon.chuggr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

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
        ((Button)findViewById(R.id.btnDrama)).setOnClickListener(this);

        gridView = (GridView) findViewById(R.id.gridview);

        //gridView.setAdapter(new VideoAdapter(this, videos));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArchiveVideo video  = (ArchiveVideo) adapterView.getItemAtPosition(i);
                if (video.getVideoURL().isEmpty()){
                    Toast.makeText(MainActivity.this,"NOT READY YET :(",Toast.LENGTH_SHORT).show();
                }
                else{
                    playVideo((ArchiveVideo) adapterView.getItemAtPosition(i));
                }


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
            case R.id.btnDrama:
                new DownloadFilmInfoAsyncTask().execute("Drama");
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
            this.videos = videos;
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


            if (!videos.get(position).thumbDrawn){
                iv.setImageResource(R.drawable.default_thumb2);
                new ImageViewLoadAsyncTask().execute(iv, videos.get(position));
                videos.get(position).thumbDrawn = true;
            }

            ((TextView)layout.findViewById(R.id.videoTitle)).setText(videos.get(position).getTitle());

            return layout;
        }

    }









    private class ImageViewLoadAsyncTask extends AsyncTask<Object, Integer, Integer> {
        private Bitmap bmp;
        private ImageView iv;

        public void getVideoThumbAndUrl(ArchiveVideo video) {

            String url = "http://archive.org/download/"+video.getIdentifier()+"/"+video.getIdentifier()+"_files.xml";

            //initialize
            InputStream inputStream = null;

            //http post
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
            } catch(Exception e) {
                Log.e(C.TAG, "Error in http connection " + e.toString());
            }

            String responseString = "";
            //convert response to string
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                inputStream.close();
                responseString = sb.toString();
            }catch(Exception e){
                Log.e(C.TAG, "Error converting result "+e.toString());
            }

            // Inspect Response string
            // Split by "<file name=\"

            video.setVideoURL("");
            video.setThumb("");

            String tmp[]  = responseString.split("<file name");
            for (String s:tmp){
                String tmp2[] = s.split(" source=");
                String s3 = tmp2[0];
                if (tmp2[0].contains(".mp4")){
                    s3=s3.replace("=","");
                    s3=s3.replace("\"","");
                    //Log.e(C.TAG, "MP4: "+s3);
                    video.setVideoURL("http://archive.org/download/"+video.getIdentifier()+"/"+s3);
                }
                else if (tmp2[0].contains(".jpg")){
                    s3=s3.replace("=","");
                    s3=s3.replace("\"","");
                    //Log.e(C.TAG, "JPG: "+s3);
                    video.setThumb("http://archive.org/download/"+video.getIdentifier()+"/"+s3);
                }

            }
        }

        protected Integer doInBackground(Object... objects) {

            ArchiveVideo video = (ArchiveVideo) objects[1];

            getVideoThumbAndUrl(video);

            HttpURLConnection con=null;
            try{

                iv = (ImageView) objects[0];
                String url = (String) video.getThumb();

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
            if (null != bmp) iv.setImageBitmap(bmp);
        }
    }

    private class DownloadFilmInfoAsyncTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... strings) {
            // Chuck spinner on there...
            ArchiveVideoController.nullifyVideoController();
            mArchiveVideoController = ArchiveVideoController.getArchiveViewController();
            mArchiveVideoController.downloadArchiveData(new ArchiveListener() {

                @Override
                public void onSingleItemDownload() {
                    publishProgress(1);
                }

                @Override
                public void onError() {
                    // On Error - Ooops
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
                ArrayList<ArchiveVideo> tmp = mArchiveVideoController.getVideos();
                gridView.setAdapter(new VideoAdapter(MainActivity.this, tmp ));
            }
            else{
                ((VideoAdapter)gridView.getAdapter()).videos = mArchiveVideoController.getVideos();
                ((VideoAdapter) gridView.getAdapter()).notifyDataSetChanged();
            }

            //gridView.setAdapter(new VideoAdapter(MainActivity.this, mArchiveVideoController.getVideos()));

        }


    }




}
