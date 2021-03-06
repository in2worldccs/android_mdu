package com.in2world.ccs.tools;

import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;

import com.in2world.ccs.Database.SaveData;
import com.in2world.ccs.helper.ValidationHelper;

public class GlobalData {

    GlobalData globalData;

    public static String KEY_SIP_username = "namePref";
    public static String KEY_SIP_domain = "domainPref";
    public static String KEY_SIP_password = "passPref";
    public static String SIP_username = null;
    public static String SIP_domain = null;
    public static String SIP_password = null;

    public static final int CALL_ADDRESS = 1;
    public static final int SET_AUTH_INFO = 2;
    public static final int UPDATE_SETTINGS_DIALOG = 3;
    public static final int HANG_UP = 4;


    public static final int IN_COMING = 100;
    public static final int OUT_COMING = 101;
    public static final int READY = 1;
    public static final int CALLING = 2;
    public static final int RINGING = 3;
    public static final int CONNECTED = 4;
    public static final int CLOSE = 5;


    public static String SIP_STATUS = "Not Ready";
    public static SipProfile SIP_Profile = null;
    public static SipAudioCall SIP_Audio_Call = null;
    public static SipManager SIP_Manager = null;
    public static int CALL_STATUS = -1;
    public static String CALL_NUMBER = "0000";
    public static Intent IncomingCallIntent = null;


    public static boolean checkMyData() {

        if (ValidationHelper.validString(SIP_username) &&
                ValidationHelper.validString(SIP_domain) &&
                ValidationHelper.validString(SIP_password))
            return true;


        SIP_username = SaveData.getInstance().getString(KEY_SIP_username);
        SIP_domain = SaveData.getInstance().getString(KEY_SIP_domain);
        SIP_password = SaveData.getInstance().getString(KEY_SIP_password);

        if (!ValidationHelper.validString(SIP_username)) {
            return false;
        }
        if (!ValidationHelper.validString(SIP_domain)) {
            return false;
        }
        if (!ValidationHelper.validString(SIP_password)) {
            return false;
        }

        return true;
    }


}
