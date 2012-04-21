package com.gtvhackathon.chuggr.runnable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gtvhackathon.chuggr.C;
import com.gtvhackathon.chuggr.model.ArchiveVideo;

import android.util.Log;

public class ArchiveDownloadRunnable implements Runnable {

    private ArchiveDownloadRunnableListener mListener;
    private String mQuery;

    public ArchiveDownloadRunnable(ArchiveDownloadRunnableListener listener, String query) {
        mListener = listener;
        mQuery = query;
    }

    @Override
    public void run() {


        ArrayList<ArchiveVideo> videoList = new ArrayList<ArchiveVideo>();
//        String url = "http://archive.org/advancedsearch.php?q=mediatype%3Amovies+AND+subject%3A%22"+mQuery+"%22&fl%5B%5D=avg_rating&fl%5B%5D=description&fl%5B%5D=downloads&fl%5B%5D=identifier&fl%5B%5D=title&sort%5B%5D=downloads+desc&sort%5B%5D=&sort%5B%5D=&rows=50&page=1&output=json&callback=callback&save=yes#raw";
        String url = "http://archive.org/advancedsearch.php?q=mediatype%3Amovies+AND+subject%3A%22"+mQuery+"%22&fl%5B%5D=avg_rating&fl%5B%5D=call_number&fl%5B%5D=collection&fl%5B%5D=contributor&fl%5B%5D=coverage&fl%5B%5D=creator&fl%5B%5D=date&fl%5B%5D=description&fl%5B%5D=downloads&fl%5B%5D=foldoutcount&fl%5B%5D=format&fl%5B%5D=headerImage&fl%5B%5D=identifier&fl%5B%5D=imagecount&fl%5B%5D=language&fl%5B%5D=licenseurl&fl%5B%5D=mediatype&fl%5B%5D=month&fl%5B%5D=num_reviews&fl%5B%5D=oai_updatedate&fl%5B%5D=publicdate&fl%5B%5D=publisher&fl%5B%5D=rights&fl%5B%5D=scanningcentre&fl%5B%5D=source&fl%5B%5D=subject&fl%5B%5D=title&fl%5B%5D=type&fl%5B%5D=volume&fl%5B%5D=week&fl%5B%5D=year&sort%5B%5D=downloads+desc&sort%5B%5D=&sort%5B%5D=&rows=10&page=1&output=json&callback=&save=yes#raw";

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

        
        //try parse the string to a JSON object
        try{
            JSONObject jsonResponse = new JSONObject(responseString);
            JSONObject responseObj = jsonResponse.getJSONObject("response");
            JSONArray docsArray = responseObj.getJSONArray("docs");
            JSONObject docObject;
            
            ArchiveVideo video;
            for(int i = 0; i < docsArray.length(); i++) {
                docObject = docsArray.getJSONObject(i);
                
                video = new ArchiveVideo();
                
                if(docObject.has("title")) {
                    video.setTitle(docObject.getString("title"));
                }
                
                if(docObject.has("description")) {
                    video.setDescription(docObject.getString("description"));
                }
                
                if(docObject.has("identifier")) {
                    video.setIdentifier(docObject.getString("identifier"));
                }
                
                if(docObject.has("date")) {
                    video.setDate(docObject.getString("date"));
                }
                
                if(docObject.has("downloads")) {
                    video.setDownloads(docObject.getInt("downloads"));
                }
                
                if(docObject.has("avg_rating")) {
                    video.setAvgRating(docObject.getDouble("avg_rating"));
                }
                
                videoList.add(video);
            }
        }catch(JSONException e){
            Log.e(C.TAG, "Error parsing data "+e.toString());
        }
        
        for(ArchiveVideo video : videoList) {
            Log.e(this.getClass().toString(), "GETTING MOAR" + video.getIdentifier());
            getVideoThumbAndUrl(video);
            mListener.onSingleItemDownload(video);
        }

    }

    public void getVideoThumbAndUrl(ArchiveVideo video) {
        //mIdentifier


        String url = "http://archive.org/download/"+video.getIdentifier()+"/"+video.getIdentifier()+"_files.xml";
        Log.e(C.TAG, "Getting XML: " + url);

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
                Log.e(C.TAG, "MP4: "+s3);
                video.setVideoURL("http://archive.org/download/"+video.getIdentifier()+"/"+s3);
            }
            else if (tmp2[0].contains(".jpg")){
                s3=s3.replace("=","");
                s3=s3.replace("\"","");
                Log.e(C.TAG, "JPG: "+s3);
                video.setThumb("http://archive.org/download/"+video.getIdentifier()+"/"+s3);
            }

        }

        // http://archive.org/download/LastMinuteCreativeSolutions_12/Random.mp4

        // Use response string
//        if (responseString.contains(video.getIdentifier() + ".mp4")){
//              //http://archive.org/download/LastMinuteCreativeSolutions_12/LastMinuteCreativeSolutions_12_files.xml
//              video.setVideoURL("ia600500.us.archive.org/1/items/"+video.getIdentifier()+"/"+video.getIdentifier()+".avi");
//            Log.e(C.TAG, "x"+video.getVideoURL());
//            video.setThumb("");
//        }


    }
    
    public interface ArchiveDownloadRunnableListener {
        
        //public void onDataDownloaded(ArrayList<ArchiveVideo> videos);

        public void onSingleItemDownload(ArchiveVideo video);
    }
    
}
