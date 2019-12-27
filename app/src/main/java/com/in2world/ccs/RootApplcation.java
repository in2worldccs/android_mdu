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

import com.in2world.ccs.Database.SaveData;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.receiver.IncomingCallReceiver;
import com.in2world.ccs.tools.GlobalData;

import java.text.ParseException;

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
    public static RootApplcation mRootApplcation;

    @Override
    public void onCreate() {
        super.onCreate();
        setmRootApplcation(this);
        SaveData.init(this);
        init(this);
    }


    public  void init(Context context) {
        if (!checkMyData()) {
            return;
        }

        Log.d(TAG, "init: username " + SIP_username);
        Log.d(TAG, "init: domain " + SIP_domain);
        Log.d(TAG, "init: password " + SIP_password);

        // Set up the intent filter.  This will be used to fire an
        // IncomingCallReceiver when someone calls the SIP address used by this
        // application.
        registerReceiver(context);

        initializeSIPManager(context);
    }

    private void registerReceiver(Context context){
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.SipDemo.INCOMING_CALL");
        IncomingCallReceiver callReceiver = new IncomingCallReceiver();
        context.registerReceiver(callReceiver, filter);
    }

    private  void initializeSIPManager(Context context) {
        if (!ValidationHelper.validObject(SIP_Manager)) {
            SIP_Manager = SipManager.newInstance(context);
        }
        initializeSip(context);
    }
    /**
     * Logs you into your SIP provider, registering this device as the location to
     * send SIP calls to for your SIP address.
     */
    private static void initializeSip(Context context) {

        if (SIP_Manager == null) {
            return;
        }
        if (SIP_Profile != null) {
            closeLocalProfile();
        }
        if (!ValidationHelper.validString(SIP_username) ||
                !ValidationHelper.validString(SIP_domain) ||
                !ValidationHelper.validString(SIP_password)) {
            return;
        }

        try {
            SipProfile.Builder builder = new SipProfile.Builder(SIP_username, SIP_domain);
            builder.setPassword(SIP_password);
            SIP_Profile = builder.build();



            Intent i = new Intent();
            i.setAction("android.SipDemo.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, Intent.FILL_IN_DATA);
            SIP_Manager.open(SIP_Profile, pi,null);

            // This listener must be added AFTER manager.open is called,
            // Otherwise the methods aren't guaranteed to fire.


            Log.d(TAG, "initializeLocalProfile: manager " + (SIP_Manager != null));
            SIP_Manager.setRegistrationListener(SIP_Profile.getUriString(), new SipRegistrationListener() {
                public void onRegistering(String localProfileUri) {
                    setSipStatus("Registering with SIP Server...");
                }

                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    setSipStatus("Ready");
                }

                public void onRegistrationFailed(String localProfileUri, int errorCode,
                                                 String errorMessage) {
                    Log.w(TAG, "onRegistrationFailed: localProfileUri " + localProfileUri);
                    Log.w(TAG, "onRegistrationFailed: errorCode " + getErrorMessage(errorCode));
                    Log.w(TAG, "onRegistrationFailed: errorMessage " + errorMessage);
                    setSipStatus("Error , "+getErrorMessage(errorCode));
                }
            });
        } catch (ParseException pe) {
            Log.e(TAG, "initializeLocalProfile: "+pe.getMessage() );
        } catch (SipException se) {
            Log.e(TAG, "initializeLocalProfile: "+se.getMessage() );
        }
    }

    /**
     * Closes out your local profile, freeing associated objects into memory
     * and unregistering your device from the server.
     */
    public static void closeLocalProfile() {
        if (!ValidationHelper.validObject(SIP_Profile)) {
            return;
        }
        try {
        } catch (Exception ee) {
            Log.e(TAG , " Failed to close local profile. ", ee);
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
}
