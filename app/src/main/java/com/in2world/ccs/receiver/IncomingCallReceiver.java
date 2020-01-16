package com.in2world.ccs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.util.Log;

import com.in2world.ccs.service.SIP_Service;
import com.in2world.ccs.ui.CallActivity;
import com.in2world.ccs.ui.DialerActivity;

import static com.in2world.ccs.tools.GlobalData.IN_COMING;
import static com.in2world.ccs.tools.GlobalData.IsIncomingCaller;
import static com.in2world.ccs.tools.GlobalData.SIP_Audio_Call;
import static com.in2world.ccs.tools.GlobalData.SIP_Manager;
import static com.in2world.ccs.tools.GlobalData.IncomingCallIntent;
import static com.in2world.ccs.tools.GlobalData.CALL_STATUS;
import static com.in2world.ccs.ui.DialerActivity.isInstanceCreated;


public class IncomingCallReceiver extends BroadcastReceiver {


    DialerActivity dialerActivity;
    private static final String TAG = "IncomingCallReceiver";

    /**
     * Processes the incoming call, answers it, and hands it over to the
     * CallActivity.
     *
     * @param context The context under which the receiver is running.
     * @param intent  The intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!IsIncomingCaller) {
            Log.d(TAG, "onReceive: ");
            if (intent.getAction() != null)
                Log.d(TAG, "onReceive:  " + intent.getAction());




          //  DialerActivity.receiveCall(context,intent);
            if (SIP_Service.isInstanceCreated()) {
                SIP_Service.getInstance().receiveCall(intent);
            }
            //IncomingCallIn`w3`33tent = intent;
            //CALL_STATUS = IN_COMING;
            //Intent intentCall = new Intent(context, DialerActivity.class);
            //intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(intentCall);
        }
    }

}
