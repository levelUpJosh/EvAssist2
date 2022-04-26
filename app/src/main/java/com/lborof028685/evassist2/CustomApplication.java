package com.lborof028685.evassist2;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class CustomApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
