package com.in2world.ccs.service;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.in2world.ccs.Database.SaveData;
import com.in2world.ccs.helper.Config;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.ui.DialerActivity;
import com.in2world.ccs.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.in2world.ccs.server.fcm.FCM.FCM_TITLE;
import static com.in2world.ccs.server.fcm.FCM.FCM_NAME;
import static com.in2world.ccs.server.fcm.FCM.FCM_DATA;
import static com.in2world.ccs.server.fcm.FCM.SIP_READY;
import static com.in2world.ccs.server.fcm.FCM.SIP_RUN;
import static com.in2world.ccs.service.SIP_Service.startAppCheckServices;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServ";
    JSONObject json;
    String TITLE, NAME, DATA = "";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SaveData.init(this);


        Log.d(TAG, "onMessageReceived : From : " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "onMessageReceived: Message data : " + remoteMessage.getData());

            json = new JSONObject(remoteMessage.getData());

            try {
                if (json.has(FCM_TITLE))
                    TITLE = json.getString(FCM_TITLE);
                if (json.has(FCM_NAME))
                    NAME = json.getString(FCM_NAME);
                if (json.has(FCM_DATA))
                    DATA = json.getString(FCM_DATA);

                handleMessage();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "onMessageReceived: JSONException " + e.getMessage());
            }
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        //sendRegistrationToServer(token);
    }


    private void handleMessage() {

        Log.w(TAG, "handleMessage: ");
        if (!ValidationHelper.validString(TITLE))
            return;
        Log.w(TAG, "handleMessage: TITLE " + TITLE);
        if (!ValidationHelper.validString(NAME))
            return;
        Log.w(TAG, "handleMessage: NAME " + NAME);
        if (!ValidationHelper.validString(DATA))
            return;
        Log.w(TAG, "handleMessage: DATA " + DATA);



        switch (TITLE){
            case SIP_RUN:
              //  Intent intentService = new Intent(MyFirebaseMessagingService.this, SIP_Service.class);
               // startService(intentService);
                startAppCheckServices(MyFirebaseMessagingService.this);
                break;
            case SIP_READY:

                if (DialerActivity.active) {
                    Intent pushNotification = new Intent(Config.MAKE_CALL_SIP);
                    pushNotification.putExtra("data", "make_call");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                }

                break;
        }

    }

    private void sendRegistrationToServer(String token) {

    }


}
