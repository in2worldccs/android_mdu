package com.in2world.ccs.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipException;
import android.util.Log;

import com.in2world.ccs.helper.NotificationHelper;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.ui.DialerActivity;

import static com.in2world.ccs.service.SIP_Service.incomingCall;
import static com.in2world.ccs.service.SIP_Service.outcomingCall;
import static com.in2world.ccs.tools.GlobalData.CALL_STATUS;
import static com.in2world.ccs.tools.GlobalData.IN_COMING;
import static com.in2world.ccs.tools.GlobalData.IN_COMING_START;
import static com.in2world.ccs.tools.GlobalData.IsIncomingCaller;

public class CallerReceiver extends BroadcastReceiver {
    private static final String TAG = "CallerReceiver";
    String action = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");

         if (!ValidationHelper.validObject(intent.getAction())) return;


        Log.d(TAG, "onReceive: action "+intent.getAction());

        action = intent.getAction();

        if (!ValidationHelper.validString(action))
            return;

        if (action.equals("answer")) {

            CALL_STATUS = IN_COMING_START;
            Intent intentCall = new Intent(context, DialerActivity.class);
            intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (!IsIncomingCaller)
                context.startActivity(intentCall);


            NotificationHelper.callNotification(context,"calling","Incoming recive ...","end");

        } else if (action.equals("hangeUp")) {
            if (!ValidationHelper.validObject(incomingCall)) {
                return;
            }

            try {
                incomingCall.endCall();

                NotificationHelper.cancelNotification(context,NotificationHelper.NOTIFICATION_ID_CALL);

                // finishDialerActivity();
            } catch (SipException e) {
                Log.e(TAG, "onClick: SipException " + e.getMessage());
                e.printStackTrace();
            }
        }else if (action.equals("endCall")){

            try {
                if (ValidationHelper.validObject(incomingCall))
                    incomingCall.endCall();

                if (ValidationHelper.validObject(outcomingCall))
                    outcomingCall.endCall();

                NotificationHelper.cancelNotification(context,NotificationHelper.NOTIFICATION_ID_CALL);
            } catch (SipException e) {
                Log.e(TAG, "onClick: SipException " + e.getMessage());
                e.printStackTrace();
            }
        }else if (action.equals("openScreen")){
            CALL_STATUS = IN_COMING;
            Intent intentCall = new Intent(context, DialerActivity.class);
            intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (!IsIncomingCaller)
                context.startActivity(intentCall);

        }else if (action.equals("openScreenWithCallStarted")){
            CALL_STATUS = IN_COMING_START;
            Intent intentCall = new Intent(context, DialerActivity.class);
            intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (!IsIncomingCaller)
                context.startActivity(intentCall);
        }


    }
}
