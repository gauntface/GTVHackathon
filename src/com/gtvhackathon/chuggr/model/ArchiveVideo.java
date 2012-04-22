package com.gtvhackathon.chuggr.model;

import android.os.Bundle;

import java.util.ArrayList;

public class ArchiveVideo {

    private float mAvgRating;
    private String mTitle;
    private String mDescription;
    private String mDate;
    private String mIdentifier;
    private int mDownloads;
    private String mVideoURL;

    public String mThumb;
    public boolean thumbDrawn = false;

    public ArchiveVideo() {
        mAvgRating = 0.0f;
        mTitle = new String();
        mDescription = new String();
        mDate = new String();
        mIdentifier = null;
        mDownloads = -1;
        mThumb = new String();
        mVideoURL = new String();
    }
    
    public void setAvgRating(double rating) {
        mAvgRating = (float) rating;
    }
    
    public double getAvgRating() {
        return mAvgRating;
    }
    
    public void setTitle(String title) {
        mTitle = title;
    }
    
    public String getTitle() {
        return mTitle;
    }
    
    public void setDescription(String description) {
        mDescription = description;
    }
    
    public String getDescription() {
        return mDescription;
    }
    
    public void setDate(String date) {
        mDate = date;
    }
    
    public String getDate() {
        return mDate;
    }
    
    public void setIdentifier(String id) {
        mIdentifier = id;
    }
    
    public String getIdentifier() {
        return mIdentifier;
    }
    
    public void setDownloads(int downloads) {
        mDownloads = downloads;
    }
    
    public int getDownloads() {
        return mDownloads;
    }

    public String getVideoURL() {
        return mVideoURL;
    }

    public void setVideoURL(String mVideoURL) {
        this.mVideoURL = mVideoURL;
    }

    public String getThumb() {
        return mThumb.toString();
    }

    public void setThumb(String mVideoURL) {
        this.mThumb = mVideoURL;
    }


    public Bundle getAsBundle() {
        Bundle b = new Bundle();
        b.putString("title", getTitle());
        b.putString("subtitle", "subtitle...");
        b.putString("description", getDescription());
        b.putString("thumb", mThumb.toString());
        b.putString("source", mVideoURL.toString());
        b.putFloat("rating", mAvgRating);
        return b;
    }


}
