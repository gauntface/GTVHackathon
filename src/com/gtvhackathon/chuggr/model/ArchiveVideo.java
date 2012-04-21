package com.gtvhackathon.chuggr.model;

public class ArchiveVideo {

    private double mAvgRating;
    private String mTitle;
    private String mDescription;
    private String mDate;
    private String mIdentifier;
    private int mDownloads;
    
    public ArchiveVideo() {
        mAvgRating = 0.0;
        mTitle = new String();
        mDescription = new String();
        mDate = new String();
        mIdentifier = null;
        mDownloads = -1;
    }
    
    public void setAvgRating(double rating) {
        mAvgRating = rating;
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
    
    public void setDownlads(int downloads) {
        mDownloads = downloads;
    }
    
    public int getDownloads() {
        return mDownloads;
    }
}
