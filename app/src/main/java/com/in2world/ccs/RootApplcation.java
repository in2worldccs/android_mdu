package com.in2world.ccs;

import android.app.Application;

import com.in2world.ccs.Database.SaveData;

public class RootApplcation extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SaveData.init(this);

    }
}
