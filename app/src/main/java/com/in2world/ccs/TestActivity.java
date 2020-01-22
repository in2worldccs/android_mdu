package com.in2world.ccs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.in2world.ccs.helper.NotificationHelper;
import com.in2world.ccs.ui.ChatGroupsActivity;
import com.in2world.ccs.ui.ChatUsersActivity;
import com.in2world.ccs.ui.MainActivity;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class TestActivity extends AppCompatActivity {
    private ImageButton forwardbtn, backwardbtn, pausebtn, playbtn;
    private MediaPlayer mPlayer;
    private TextView songName, startTime, songTime;
    private SeekBar songPrgs;
    private static int oTime =0, sTime =0, eTime =0, fTime = 5000, bTime = 5000;
    private Handler hdlr = new Handler();

    NotificationCompat.Builder builder;
//AlmobrmJ2130611ibrahem
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

    }

    public void onText(View view) {
        Toast.makeText(this, "onText", Toast.LENGTH_SHORT).show();
        //showHeadsUpNotification();

        NotificationHelper.callNotification(this,"calling","Incoming recive ...","");
    }



}