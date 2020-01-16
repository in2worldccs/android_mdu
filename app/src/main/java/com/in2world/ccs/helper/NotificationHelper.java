package com.in2world.ccs.helper;

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
import android.net.sip.SipException;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.in2world.ccs.receiver.CallerReceiver;
import com.in2world.ccs.ui.DialerActivity;
import com.in2world.ccs.ui.MainActivity;
import com.in2world.ccs.R;

import java.util.Random;

import static com.in2world.ccs.service.SIP_Service.incomingCall;
import static com.in2world.ccs.tools.GlobalData.CALL_STATUS;
import static com.in2world.ccs.tools.GlobalData.CONNECTED;
import static com.in2world.ccs.tools.GlobalData.IN_COMING;
import static com.in2world.ccs.tools.GlobalData.IN_COMING_START;
import static com.in2world.ccs.tools.GlobalData.IsIncomingCaller;


/**
 * Created by in2world on 9/10/2017.
 */

public class NotificationHelper {

    NotificationHelper notificationHelper;
    public static int NOTIFICATION_ID = 1;
    public static int NOTIFICATION_ID_CALL = 2;
    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    private static final String TAG = "NotificationHelper";

    public static void showNotification(Context context, String title, String body, String data, Boolean isRandom) {

        if (isRandom) {
            Random r = new Random();
            NOTIFICATION_ID = r.nextInt(500 - 1) + 65;
        }

        Log.e("Log NOTIFICATION_ID : ", NOTIFICATION_ID + "");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.mipmap.ic_launcher)
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

    public static void callNotification(Context context, String title, String body, String data) {


        Log.e("Log NOTIFICATION_ID : ", NOTIFICATION_ID + "");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(title)
                .setContentText(title + body)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (!ValidationHelper.validObject(notificationManager))
            return;


        if (data.equals("start")) {
            notificationBuilder.setContentIntent(answerIntent(context, "openScreen", ""));
            notificationBuilder.addAction(R.drawable.ic_call_answer, "Answer", answerIntent(context, "answer", ""));
            notificationBuilder.addAction(R.drawable.ic_call_hange_up, "Hang up", hangeUpIntent(context, "", ""));

        } else {
            notificationBuilder.setContentIntent(answerIntent(context, "openScreenWithCallStarted", ""));
            notificationBuilder.addAction(R.drawable.ic_call_answer, "End call", answerIntent(context, "endCall", ""));
        }


        /* WATER_REMINDER_NOTIFICATION_ID allows you to update or cancel the notification later on */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Call";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Call ccs", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);

            notificationBuilder.setChannelId(channelId);
        }
        notificationManager.notify(NOTIFICATION_ID_CALL, notificationBuilder.build());

    }

    private static PendingIntent contentIntent(Context context, String title, String body) {

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

    private static PendingIntent answerIntent(Context context, String title, String body) {
        Log.e(TAG, "answerIntent: ");
    /*    CALL_STATUS = IN_COMING_START;
        Intent intentCall = new Intent(context, DialerActivity.class);
        intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!IsIncomingCaller)
            context.startActivity(intentCall);
        return PendingIntent.getActivity(
                context,
                NOTIFICATION_ID_CALL,
                intentCall,
                0);*/

        Intent answerIntent = new Intent(context, CallerReceiver.class);
        answerIntent.setAction(title);
        return PendingIntent.getBroadcast(context, 0, answerIntent, 0);

    }

    private static PendingIntent hangeUpIntent(Context context, String title, String body) {
        Log.e(TAG, "hangeUpIntent: ");

    /*    if (!ValidationHelper.validObject(incomingCall)) {
           return null;
          }

        try {
            incomingCall.endCall();

            // finishDialerActivity();
        } catch (SipException e) {
            Log.e(TAG, "onClick: SipException " + e.getMessage());
            e.printStackTrace();
        }*/


        Intent hangeUpIntent = new Intent(context, CallerReceiver.class);
        hangeUpIntent.setAction("hangeUp");
        return PendingIntent.getBroadcast(context, 0, hangeUpIntent, 0);

    }

    public static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
        return largeIcon;
    }


    public static void cancelNotification(Context context, int notification_id) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager == null) return;
        mNotificationManager.cancel(notification_id);
    }


}