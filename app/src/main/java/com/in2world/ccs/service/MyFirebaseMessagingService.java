package com.in2world.ccs.service;

import android.content.Intent;
import android.net.sip.SipManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.in2world.ccs.Database.SaveData;
import com.in2world.ccs.R;
import com.in2world.ccs.helper.Config;
import com.in2world.ccs.helper.MessageHelper;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.module.Response;
import com.in2world.ccs.server.Result;
import com.in2world.ccs.server.WebService;
import com.in2world.ccs.server.fcm.FCM;
import com.in2world.ccs.socket.SocketIO;
import com.in2world.ccs.tools.GlobalData;
import com.in2world.ccs.ui.DialerActivity;
import com.in2world.ccs.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Objects;

import static com.in2world.ccs.helper.Constants.USER_ID;
import static com.in2world.ccs.server.WebService.RESULT;
import static com.in2world.ccs.server.WebService.StatusConnection.BAD_REQUEST;
import static com.in2world.ccs.server.WebService.StatusConnection.INTERNAL_SERVER_ERROR;
import static com.in2world.ccs.server.WebService.StatusConnection.NO_CONNECTION;
import static com.in2world.ccs.server.WebService.StatusConnection.UNAUTHORIZED;
import static com.in2world.ccs.server.WebService.StatusConnection.VALIDATION_FAILED;
import static com.in2world.ccs.server.fcm.FCM.FCM_TITLE;
import static com.in2world.ccs.server.fcm.FCM.FCM_NAME;
import static com.in2world.ccs.server.fcm.FCM.FCM_DATA;
import static com.in2world.ccs.server.fcm.FCM.SIP_READY;
import static com.in2world.ccs.server.fcm.FCM.SIP_RUN;
import static com.in2world.ccs.server.fcm.FCM.sendToCall;
import static com.in2world.ccs.service.SIP_Service.startSIPServices;
import static com.in2world.ccs.tools.GlobalData.PROFILE_KEY;
import static com.in2world.ccs.tools.GlobalData.READY;
import static com.in2world.ccs.tools.GlobalData.SIP_Manager;
import static com.in2world.ccs.tools.GlobalData.TOKEN_KEY;
import static com.in2world.ccs.tools.GlobalData.TOKEN_VALUE;
import static com.in2world.ccs.tools.GlobalData.checkMyData;
import static com.in2world.ccs.tools.GlobalData.isProfile;
import static com.in2world.ccs.tools.GlobalData.isToken;
import static com.in2world.ccs.tools.GlobalData.mProfile;
import static com.in2world.ccs.tools.GlobalData.mUser;
import static com.in2world.ccs.tools.GlobalData.saveDataSIP;

public class MyFirebaseMessagingService extends FirebaseMessagingService implements WebService.OnResponding {

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
        sendRegistrationToServer(token);
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


        if (!ValidationHelper.validObject(mUser)) {
            HashMap<String, String> params = new HashMap<>();
            params.put(USER_ID, "" + DATA);
            new WebService(WebService.RequestAPI.USER, params, this);
        }


        switch (TITLE){
            case SIP_RUN:
                if (!SIP_Service.isInstanceCreated())
                    startSIPServices(MyFirebaseMessagingService.this);
                else if(!ValidationHelper.validObject(SIP_Manager)){
                    startSIPServices(MyFirebaseMessagingService.this);
                }else {
                    if (ValidationHelper.validObject(mUser))
                        sendToCall(MyFirebaseMessagingService.this);
                }
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
        Log.i(TAG, "sendRegistrationToServer: token : "+token);
        if(isProfile()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("fcm_token", token);
            mProfile.setFcmToken(token);
            new WebService(WebService.RequestAPI.USER_UPDATE, params, this);
        }
    }
    @Override
    public void onResponding(WebService.RequestAPI requestAPI, boolean IsSuccess, WebService.StatusConnection statusConnection, HashMap<String, Object> objectResult) {
        Log.w(TAG, "onResponding: requestAPI " + requestAPI.toString());
        Log.w(TAG, "onResponding: statusConnection " + statusConnection);
        Log.w(TAG, "onResponding: dataResult " + objectResult.toString());
        try {
            if (requestAPI.equals(WebService.RequestAPI.USER)) {
                if (IsSuccess) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());
                    if (result.getResult() == Response.SUCCESS) {
                        TOKEN_VALUE = result.getDataResponse().getToken();
                        SaveData.getInstance().saveString(TOKEN_KEY, TOKEN_VALUE);
                        mUser = result.getDataResponse().getUser();
                        isProfile();
                        if (TITLE.equals(SIP_RUN)){
                            sendToCall(MyFirebaseMessagingService.this);
                        }
                    }
                } else if (statusConnection == NO_CONNECTION) {
                    Toast.makeText(this, getResources().getString(R.string.NO_CONNECTION), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == BAD_REQUEST) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == UNAUTHORIZED) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == VALIDATION_FAILED) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == INTERNAL_SERVER_ERROR) {
                    Toast.makeText(this, "حدث خطأ في الخادم ... جاري الأصلاح LOGIN ", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onResponding: Exception " + e.getMessage());
            Log.d(TAG, "onResponding: Exception getLocalizedMessage " + e.getLocalizedMessage());
            MessageHelper.AppDialog(this, R.string.error_connection, e.getMessage());
        }
    }
}
