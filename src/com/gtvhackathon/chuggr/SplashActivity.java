package com.gtvhackathon.chuggr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class SplashActivity extends Activity {

    public static final String SHAREDPREFNAME = "COM_CHUGGR";
    private final int SPLASH_DISPLAY_LENGHT = 1650;
    private boolean started_app = false;
    private Handler handler;
    private Runnable runnable;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // Display the disclaimer if they haven't read it before...
        final SharedPreferences mPrefs = getSharedPreferences(SHAREDPREFNAME,0);
        Boolean agree = mPrefs.getBoolean("agree_terms",false);

        if (!agree){

            final Dialog dialog = new Dialog(SplashActivity.this);
            dialog.setContentView(R.layout.disclaimer);
            dialog.setTitle("Terms and Conditions");
            dialog.setCancelable(true);

            Button confirm = (Button) dialog.findViewById(R.id.btnConfirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPrefs.edit().putBoolean("agree_terms",true).commit();
                    createHandlerAndRunnable();
                    dialog.dismiss();
                }
            });
            dialog.show();

        }
        else{
            createHandlerAndRunnable();
        }
        // Time out splash

    }


    public void createHandlerAndRunnable(){
        /* New Handler to start the Menu-Activity 
        * and close this Splash-Screen after some seconds.*/
        handler = new Handler();
        runnable = new Runnable(){
            @Override
            public void run() {
                makegoaway();
            }
        };
        handler.postDelayed(runnable, SPLASH_DISPLAY_LENGHT);
    }

    public void makegoaway(){
        /* Create an Intent that will start the Menu-Activity. */
        if (!started_app){ // added to prevent running two copies when user clicks to skip
            // and when time runs out for splash
            Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
            SplashActivity.this.startActivity(mainIntent);
            SplashActivity.this.finish();
            started_app=true;
        }
    }


    @Override
    protected void onPause(){
        super.onPause();
        if (handler!=null){
            handler.removeCallbacks(runnable);
        }
    }
    

}
