package com.appaspect.btcrate.ui.splash;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.os.Build;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.appaspect.btcrate.R;
import com.appaspect.btcrate.data.prefs.SharedPreferenceUtils;
import com.appaspect.btcrate.ui.main.SelectCurrencyActivity;
import com.appaspect.btcrate.ui.start.StartActivity;
import com.appaspect.btcrate.utils.AppConstants;


public class Splash_Activity extends AppCompatActivity {

    private long SPLASH_DISPLAY_LENGTH=3000;
    private Timer timer;
    private TimerTask timerTask;
    private String str_User_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O)
        {
            setTheme(R.style.Theme_Transparent_Oreos);
        }
        else
        {

            setTheme(R.style.Theme_Transparent);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }
        }

        setContentView(R.layout.activity_splash);


        if (AppConstants.sharedPreferenceUtils == null)
        {
            AppConstants.sharedPreferenceUtils = SharedPreferenceUtils.getInstance(this);
        }

        str_User_Name= AppConstants.sharedPreferenceUtils.getStringValue(SharedPreferenceUtils.KEY_User_Name,null);

        if (timer != null)
            timer.cancel();

        timerTask = new TimerTask() {
            @Override
            public void run()
            {


                if (timer != null)
                    timer.cancel();

                Intent mainIntent = new Intent(Splash_Activity.this, StartActivity.class);

                if(!TextUtils.isEmpty(str_User_Name))
                {
                     mainIntent = new Intent(Splash_Activity.this, SelectCurrencyActivity.class);
                }

                startActivity(mainIntent);
                finish();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, SPLASH_DISPLAY_LENGTH);

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        if (timer != null)
            timer.cancel();

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null)
            timer.cancel();

        finish();
    }




}
