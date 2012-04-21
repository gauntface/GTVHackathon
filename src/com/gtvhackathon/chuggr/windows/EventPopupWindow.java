package com.gtvhackathon.chuggr.windows;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gtvhackathon.chuggr.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

public class EventPopupWindow extends android.widget.PopupWindow {
    
    private final static int POP_UP_DURATION = 1500;
    
    private ExecutorService mExecutor;
    
    public EventPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        
        mExecutor = Executors.newFixedThreadPool(1);
        mExecutor.execute(new Runnable() {
            
            @Override
            public void run() {
                
                try {
                    Thread.sleep(POP_UP_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // Bleurgh - Should tidy this up
                    if(EventPopupWindow.this.isShowing()) {
                        EventPopupWindow.this.dismiss();
                    }
                }
                
            }
        });
    }
    
}
