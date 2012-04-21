package com.gtvhackathon.chuggr.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gtvhackathon.chuggr.model.ArchiveVideo;
import com.gtvhackathon.chuggr.runnable.ArchiveDownloadRunnable;
import com.gtvhackathon.chuggr.runnable.ArchiveDownloadRunnable.ArchiveDownloadRunnableListener;

public class ArchiveVideoController {

    private static ArchiveVideoController sArchiveController;

    /**
     * This is nasty - Don't use singletons, BUT we want a speedy demo so just
     * go for it here but change later if anyone wants to
     * 
     * @return
     */
    public static ArchiveVideoController getArchiveViewController() {
        if (sArchiveController == null) {
            sArchiveController = new ArchiveVideoController();
        }

        return sArchiveController;
    }

    private Runnable mDownloadRunnable;
    private ExecutorService mExecutors;
    private ArchiveListener mListener;
    private ArrayList<ArchiveVideo> mVideoList;

    public ArchiveVideoController() {
        mExecutors = Executors.newFixedThreadPool(1);
    }


    public void downloadArchiveData(ArchiveListener listener, String query) {
        mListener = listener;

        if (mDownloadRunnable == null) {
            mDownloadRunnable = new ArchiveDownloadRunnable(new ArchiveDownloadRunnableListener() {

                @Override
                public void onDataDownloaded(ArrayList<ArchiveVideo> videos) {
                    mVideoList = videos;
                    mListener.onDownloadComplete();
                }

            }, query);
            mExecutors.execute(mDownloadRunnable);
        }
    }

    public ArrayList<ArchiveVideo> getVideos(){
             return mVideoList;
    }

    public interface ArchiveListener {
        public void onDownloadComplete();

        public void onError();
    }
}
