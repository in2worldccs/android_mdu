package com.ibrahem.ccs.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.ibrahem.ccs.ui.MainActivity;
import com.ibrahem.ccs.R;

import java.util.Random;


/**
 * Created by ibrahem on 9/10/2017.
 */

public class NotificationHelper {

    private static int NOTIFICATION_ID = 1;
    /**
     * This pending intent id is used to uniquely reference the pending intent
     */

    private static final String TAG = "NotificationHelper";

    public static void showNotification(Context context,String title , String body , Boolean isRandom) {

        if (isRandom) {
            Random r = new Random();
            NOTIFICATION_ID = r.nextInt(500 - 1) + 65;
        }
        Log.e("Log NOTIFICATION_ID : ", NOTIFICATION_ID + "");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(body)
                .setContentIntent(contentIntent(context, title, body))
                .setDefaults(NotificationCompat.DEFAULT_SOUND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (!ValidationHelper.validObject(notificationManager))
            return;

        /* WATER_REMINDER_NOTIFICATION_ID allows you to update or cancel the notification later on */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "amman_notification_calls";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Amaan Channel Hint", NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

            notificationBuilder.setChannelId(channelId);
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

    }

    private static PendingIntent contentIntent(Context context, String title,String body) {

        Intent intent;
        intent = new Intent(context, MainActivity.class);
        intent.putExtra("title", "");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                intent,
                0);
    }



    public static Bitmap largeIcon(Context context) {
            Resources res = context.getResources();
            Bitmap largeIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
            return largeIcon;
        }


    public static void playNotificationSound(Context mContext) {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}