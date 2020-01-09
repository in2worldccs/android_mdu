package com.in2world.ccs.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.sip.SipManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.in2world.ccs.R;
import com.in2world.ccs.RootApplcation;
import com.in2world.ccs.helper.Config;
import com.in2world.ccs.helper.PermissionHelper;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.server.Result;
import com.in2world.ccs.server.fcm.FCM;

import java.util.Timer;
import java.util.TimerTask;

import static android.app.Notification.GROUP_ALERT_SUMMARY;
import static com.in2world.ccs.helper.NotificationHelper.largeIcon;
import static com.in2world.ccs.tools.GlobalData.SIP_Manager;

public class SIP_Service extends IntentService {

    private static final String TAG = "SIP_Service";
    private Context context = null;
    private Timer timer;
    ImageView imageView;
    private WindowManager windowManager;

    public static int KEEPUS_NOTIFICATION_ID = 101;
    public static int SYNC_NOTIFICATION_ID = 102;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    private static SIP_Service instance = null;

    public static boolean isInstanceCreated() {
        return instance != null;
    }//met


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {

        }
        /* We want this service to continue running until it is explicitly
         * stopped, so return sticky.
         */
        return START_STICKY;
    }

    public SIP_Service() {
        super("SIP_Service");
        setIntentRedelivery(true);

        if (mRegistrationBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        if (ValidationHelper.validObject(timer)) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        context = getApplicationContext();
        instance = this;
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.SIP_Broadcast));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Waking up mobile if it is sleeping
            //    WakeLocker.acquire(context);
            // do something
            //   WakeLocker.release();

            // wakeLock.acquire(10*60*1000L /*10 minutes*/);
            Log.d(TAG, "Wakelock acquired");

            showNotification();
        }


        RootApplcation.getmRootApplcation().init(this);


        registrationBroadcastReceiver();
        timer = new Timer("AmaanAppLockerServices");
        timer.schedule(updateTask, 0, 3000);
    }


    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            startJob();
        }
    };


    /**
     * start Job in Backgraung
     */
    private void startJob() {
        Log.d(TAG, "startJob: " + System.currentTimeMillis());
    }

    public static String CHANNEL_ID = "ccsServiceChannel";

    private void showNotification() {
        CHANNEL_ID = context.getResources().getString(R.string.cssServiceChannel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
            chan.setLightColor(ContextCompat.getColor(context, R.color.btn));
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            @SuppressLint("WrongConstant")
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(largeIcon(context))
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                    .setGroup(context.getResources().getString(R.string.app_name))
                    .setGroupSummary(false)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setOngoing(true)
                    .setContentText("Ready to receive calls")
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();

            startForeground(KEEPUS_NOTIFICATION_ID, notification);
        }
    }


    public static void startAppCheckServices(Context context) {
        Log.d(TAG, " startSIPServices: ");


        Intent intentService = new Intent(context, SIP_Service.class);
        context.stopService(intentService);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            //lower then Oreo, just start the service.
            context.startService(intentService);
        }
    }


    public static void stopAppCheckServices(Context context) {
        Log.d(TAG, "stopSIPServices: ");

        Intent intentService = new Intent(context, SIP_Service.class);
        context.stopService(intentService);
    }

    private void registrationBroadcastReceiver() {

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: ");
                if (intent.getAction().equals(Config.SIP_Broadcast)) {
                    // new push message is received


                    Log.d(TAG, "onCreate: ValidationHelper " + ValidationHelper.validObject(SIP_Manager));
                    if (ValidationHelper.validObject(SIP_Manager)) {
                    }

                }
            }
        };
    }

}
