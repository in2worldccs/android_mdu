package com.in2world.ccs;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.in2world.ccs.Database.SaveData;
import com.in2world.ccs.helper.Config;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.receiver.IncomingCallReceiver;
import com.in2world.ccs.service.MyFirebaseMessagingService;
import com.in2world.ccs.service.SIP_Service;
import com.in2world.ccs.tools.GlobalData;
import com.in2world.ccs.ui.DialerActivity;

import io.fabric.sdk.android.Fabric;

import java.text.ParseException;

import static com.in2world.ccs.server.fcm.FCM.sendToCall;
import static com.in2world.ccs.service.SIP_Service.startSIPServices;
import static com.in2world.ccs.tools.GlobalData.READY;
import static com.in2world.ccs.tools.GlobalData.SIP_Manager;
import static com.in2world.ccs.tools.GlobalData.SIP_Profile;
import static com.in2world.ccs.tools.GlobalData.SIP_domain;
import static com.in2world.ccs.tools.GlobalData.SIP_password;
import static com.in2world.ccs.tools.GlobalData.SIP_username;
import static com.in2world.ccs.tools.GlobalData.UPDATE_SETTINGS_DIALOG;
import static com.in2world.ccs.tools.GlobalData.checkMyData;
import static com.in2world.ccs.tools.SipErrorCode.getErrorMessage;

public class RootApplcation extends Application {

    public static String SIP_STATUS = "Not Ready";

    private static final String TAG = "RootApplcation";
    public static RootApplcation mRootApplcation ;
    public static IncomingCallReceiver callReceiver =  new IncomingCallReceiver();;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        setmRootApplcation(this);
        SaveData.init(this);
     //   init(this);

        SIP_Service.stopSIPServices(this);
        if (!SIP_Service.isInstanceCreated())
            startSIPServices(this);

    }


    public void init(Context context) {
        if (!checkMyData()) {
            return;
        }

        Log.d(TAG, "init: username " + SIP_username);
        Log.d(TAG, "init: domain " + SIP_domain);
        Log.d(TAG, "init: password " + SIP_password);


      //  registerReceiver(context);

        initializeSIPManager(context);
    }

    private void registerReceiver(Context context) {
        Log.d(TAG, "registerReceiver: ");
        //isRegisterReceiver(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.SipDemo.INCOMING_CALL");
        callReceiver = new IncomingCallReceiver();
        context.registerReceiver(callReceiver, filter);
    }
    public void isRegisterReceiver(Context context) {
        Log.d(TAG, "isRegisterReceiver: ");

        if (callReceiver != null) {
            context.unregisterReceiver(callReceiver);
        }
    }
    private void initializeSIPManager(Context context) {
        Log.d(TAG, "initializeSIPManager: ");
        if (!ValidationHelper.validObject(SIP_Manager)) {
            SIP_Manager = SipManager.newInstance(context);
        }
        Log.d(TAG, "initializeSIPManager: " + ValidationHelper.validObject(SIP_Manager));

        initializeSip(context);
    }

    /**
     * Logs you into your SIP provider, registering this device as the location to
     * send SIP calls to for your SIP address.
     */
    private static void initializeSip(final Context context) {

        Log.d(TAG, "initializeSip: 0");
        if (SIP_Manager == null) {
            SIP_Manager = SipManager.newInstance(context);
            if (SIP_Manager == null) {
                return;
            }
        }

        Log.d(TAG, "initializeSip: 1");
        closeLocalProfile();
        Log.d(TAG, "initializeSip: 2");
        if (!ValidationHelper.validString(SIP_username) ||
                !ValidationHelper.validString(SIP_domain) ||
                !ValidationHelper.validString(SIP_password)) {
            return;
        }

        Log.d(TAG, "initializeSip: 3");
        try {
            SipProfile.Builder builder = new SipProfile.Builder(SIP_username, SIP_domain);
            builder.setPassword(SIP_password);
            //builder.setAuthUserName(auth_username);
            //builder.setOutboundProxy(outbound_proxy)
            SIP_Profile = builder.build();

            Log.d(TAG, "initializeSip: 6");

            Intent i = new Intent();
            i.setAction("android.SipDemo.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, Intent.FILL_IN_DATA);
            SIP_Manager.open(SIP_Profile, pi, null);

            // This listener must be added AFTER manager.open is called,
            // Otherwise the methods aren't guaranteed to fire.


            Log.d(TAG, "initializeLocalProfile: manager " + (SIP_Manager != null));
            SIP_Manager.setRegistrationListener(SIP_Profile.getUriString(), new SipRegistrationListener() {
                public void onRegistering(String localProfileUri) {
                    setSipStatus("Registering with SIP Server...");
                    Log.d(TAG, "onRegistering: Registering with SIP Server...");
                }

                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    setSipStatus("Ready");
                    Log.d(TAG, "onRegistering: Ready");


                    Log.d(TAG, "onRegistrationDone: isInstanceCreated "+SIP_Service.isInstanceCreated());
                    if (SIP_Service.isInstanceCreated()) {
//                        Intent pushNotification = new Intent(Config.SIP_Broadcast);
//                        pushNotification.putExtra("data", "make_call");
//                        LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification);

                        sendToCall(context);
                    }
                }
                public void onRegistrationFailed(String localProfileUri, int errorCode,
                                                 String errorMessage) {
                    Log.w(TAG, "onRegistrationFailed: localProfileUri " + localProfileUri);
                    Log.w(TAG, "onRegistrationFailed: errorCode " + getErrorMessage(errorCode));
                    Log.w(TAG, "onRegistrationFailed: errorMessage " + errorMessage);
                    setSipStatus("Error , " + getErrorMessage(errorCode));
                }
            });
        } catch (ParseException pe) {
            Log.e(TAG, "initializeLocalProfile: " + pe.getMessage());
        } catch (SipException se) {
            Log.e(TAG, "initializeLocalProfile: " + se.getMessage());
        }
    }

    /**
     * Closes out your local profile, freeing associated objects into memory
     * and unregistering your device from the server.
     */
    public static void closeLocalProfile() {
        Log.d(TAG, "closeLocalProfile: ");
        if (SIP_Manager == null) {
            return;
        }
        Log.d(TAG, "closeLocalProfile: 1");
        try {
            if (SIP_Profile == null) {
                SipProfile.Builder builder = new SipProfile.Builder(SIP_username, SIP_domain);
                builder.setPassword(SIP_password);
                SIP_Profile = builder.build();
            }

            SIP_Manager.close(SIP_Profile.getUriString());
            Log.w(TAG, "closeLocalProfile: Done DoneDoneDoneDone "+SIP_Profile.getUriString());
        } catch (Exception ee) {
            Log.e(TAG,"onDestroy Failed to close local profile.", ee);
        }
    }

    public static String getSipStatus() {
        return SIP_STATUS;
    }

    public static void setSipStatus(String sipStatus) {
        SIP_STATUS = sipStatus;
    }

    public static RootApplcation getmRootApplcation() {
        return mRootApplcation;
    }

    public static void setmRootApplcation(RootApplcation mRootApplcation) {
        RootApplcation.mRootApplcation = mRootApplcation;
    }

    private RequestQueue requestQueue;

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

}
