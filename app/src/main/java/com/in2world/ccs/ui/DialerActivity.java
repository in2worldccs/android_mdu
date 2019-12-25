package com.in2world.ccs.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.in2world.ccs.R;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.tools.SipStateCode;

import static com.in2world.ccs.tools.GlobalData.CALLING;
import static com.in2world.ccs.tools.GlobalData.CALL_STATUS;
import static com.in2world.ccs.tools.GlobalData.CLOSE;
import static com.in2world.ccs.tools.GlobalData.CONNECTED;
import static com.in2world.ccs.tools.GlobalData.IN_COMING;
import static com.in2world.ccs.tools.GlobalData.IncomingCallIntent;
import static com.in2world.ccs.tools.GlobalData.KEY_SIP_domain;
import static com.in2world.ccs.tools.GlobalData.OUT_COMING;
import static com.in2world.ccs.tools.GlobalData.READY;
import static com.in2world.ccs.tools.GlobalData.RINGING;
import static com.in2world.ccs.tools.GlobalData.SIP_Manager;
import static com.in2world.ccs.tools.GlobalData.CALL_NUMBER;
import static com.in2world.ccs.tools.GlobalData.SIP_Profile;
import static com.in2world.ccs.tools.GlobalData.SIP_domain;
import static com.in2world.ccs.tools.SipStateCode.ENDING_CALL;
import static com.in2world.ccs.tools.SipStateCode.IN_CALL;
import static com.in2world.ccs.tools.SipStateCode.READY_TO_CALL;
import static com.in2world.ccs.tools.SipStateCode.toString;
import static com.in2world.ccs.tools.SipStateCode.getStateName;

public class DialerActivity extends AppCompatActivity {


    private static final String TAG = "DialerActivity";
    private TextView callName, callInfo;
    private Button answer, close, hangup;

    private SipAudioCall incomingCall = null;
    private SipAudioCall outcomingCall = null;

    private SipAudioCall sipAudioCall = null;

    private LinearLayout settingCall;
    private LinearLayout lyHold;
    private ImageView hold;
    private LinearLayout lyMute;
    private ImageView mute;
    private LinearLayout lySpeaker;
    private ImageView speaker;
    private boolean SpeakerMode = false;
    private boolean localHold = false;
    private boolean localMute = false;

    private void initView() {

        callName = findViewById(R.id.callName);
        callInfo = findViewById(R.id.callInfo);
        answer = findViewById(R.id.answer);
        close = findViewById(R.id.close);
        hangup = findViewById(R.id.hangup);
        settingCall = findViewById(R.id.setting_call);
        lyHold = findViewById(R.id.ly_hold);
        hold = findViewById(R.id.hold);
        lyMute = findViewById(R.id.ly_mute);
        mute = findViewById(R.id.mute);
        lySpeaker = findViewById(R.id.ly_speaker);
        speaker = findViewById(R.id.speaker);

        if (!ValidationHelper.validObject(SIP_Manager)) {
            Toast.makeText(this, "SIP_Manager is null", Toast.LENGTH_SHORT).show();
            finish();
        }

        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);

        Log.d(TAG, "onCreate: ");
        initView();
    }


    private void init() {

        if (!ValidationHelper.validObject(SIP_Manager))
            finish();


        if (!ValidationHelper.validObject(SIP_Profile))
            finish();


        initCall();

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!ValidationHelper.validObject(incomingCall)) {
                    finish();
                }

                try {
                    incomingCall.answerCall(30);
                    incomingCall.startAudio();
                    incomingCall.setSpeakerMode(false);
                    answer.setVisibility(View.GONE);
                    hangup.setVisibility(View.GONE);
                    close.setVisibility(View.VISIBLE);
                    settingCall.setVisibility(View.VISIBLE);
                    updateStatus(CONNECTED);
                } catch (SipException e) {
                    Log.e(TAG, "onClick: SipException " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        hangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ValidationHelper.validObject(incomingCall)) {
                    finish();
                }

                try {

                    incomingCall.endCall();

                    // finishDialerActivity();
                } catch (SipException e) {
                    Log.e(TAG, "onClick: SipException " + e.getMessage());
                    e.printStackTrace();
                }

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (ValidationHelper.validObject(incomingCall))
                        incomingCall.endCall();

                    if (ValidationHelper.validObject(outcomingCall))
                        outcomingCall.endCall();

                    //finishDialerActivity();
                } catch (SipException e) {
                    Log.e(TAG, "onClick: SipException " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        lyHold.setVisibility(View.GONE);
  /*      lyHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ValidationHelper.validObject(incomingCall))
                    sipAudioCall = incomingCall;

                if (ValidationHelper.validObject(outcomingCall))
                    sipAudioCall = outcomingCall;


                try {
                    if (sipAudioCall.isOnHold()) {
                        sipAudioCall.holdCall(30);
                        hold.setImageDrawable(getDrawable(R.drawable.icon_off_hold));
                    } else {
                        sipAudioCall.holdCall(0);
                        hold.setImageDrawable(getDrawable(R.drawable.icon_on_hold));
                    }
                } catch (SipException e) {
                    e.printStackTrace();
                }
            }
        });*/

        lyMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ValidationHelper.validObject(incomingCall))
                    sipAudioCall = incomingCall;

                if (ValidationHelper.validObject(outcomingCall))
                    sipAudioCall = outcomingCall;


                if (sipAudioCall.isMuted()) {
                    sipAudioCall.toggleMute();
                    mute.setImageDrawable(getDrawable(R.drawable.icon_off_mute));
                } else {
                    sipAudioCall.toggleMute();
                    mute.setImageDrawable(getDrawable(R.drawable.icon_on_mute));
                }
            }
        });

        lySpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ValidationHelper.validObject(incomingCall))
                    sipAudioCall = incomingCall;

                if (ValidationHelper.validObject(outcomingCall))
                    sipAudioCall = outcomingCall;


                if (SpeakerMode) {
                    sipAudioCall.setSpeakerMode(false);
                    SpeakerMode = false;
                    speaker.setImageDrawable(getDrawable(R.drawable.icon_off_speaker));
                } else {
                    sipAudioCall.setSpeakerMode(true);
                    SpeakerMode = true;
                    speaker.setImageDrawable(getDrawable(R.drawable.icon_on_speaker));
                }
            }
        });
    }

    private void initCall() {

        Log.d(TAG, "initCall: CALL_STATUS " + CALL_STATUS);
        switch (CALL_STATUS) {
            case IN_COMING:
                whenInComingCall();
                break;
            case OUT_COMING:
                whenOutComingCall();
                break;
            default:
                finishDialerActivity();
                break;
        }
    }

    private void whenInComingCall() {

        close.setVisibility(View.GONE);

        try {

            Toast.makeText(this, "IncomingCallIntent : " + ValidationHelper.validObject(IncomingCallIntent), Toast.LENGTH_SHORT).show();
            outcomingCall = null;
            incomingCall = SIP_Manager.takeAudioCall(IncomingCallIntent, listener_incoming);
            CALL_NUMBER = incomingCall.getPeerProfile().getUserName();

            Log.d(TAG, "whenInComingCall: Profile " + incomingCall.getPeerProfile().getProfileName());
            Log.d(TAG, "whenInComingCall: Display " + incomingCall.getPeerProfile().getDisplayName());
            Log.d(TAG, "whenInComingCall: User " + incomingCall.getPeerProfile().getUserName());
            updateStatus(RINGING);
            if (ValidationHelper.validString(CALL_NUMBER))
                callName.setText(CALL_NUMBER);

        } catch (SipException e) {
            Log.e(TAG, "whenInComingCall: e " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void whenOutComingCall() {

        try {
            answer.setVisibility(View.GONE);
            hangup.setVisibility(View.GONE);
            close.setVisibility(View.VISIBLE);
            incomingCall = null;
            outcomingCall = SIP_Manager.makeAudioCall(SIP_Profile.getUriString(), CALL_NUMBER + "@" + SIP_domain, listener_outcoming, 30);
            updateStatus(RINGING);
            settingCall.setVisibility(View.VISIBLE);
            if (ValidationHelper.validString(CALL_NUMBER))
                callName.setText(CALL_NUMBER);
        } catch (SipException e) {
            Log.e(TAG, "whenOutComingCall: " + e.getMessage());
            e.printStackTrace();
        }

    }

    SipAudioCall.Listener listener_incoming = new SipAudioCall.Listener() {
        @Override
        public void onRinging(SipAudioCall call, SipProfile caller) {
            Log.d(TAG, "in onRinging: ");
            try {
                Toast.makeText(DialerActivity.this, "onRinging", Toast.LENGTH_SHORT).show();
                call.answerCall(30);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCallBusy(SipAudioCall call) {
            super.onCallBusy(call);
            Log.d(TAG, "in onCallBusy: ");
            Toast.makeText(DialerActivity.this, "Call Busy", Toast.LENGTH_SHORT).show();
            updateStatus(call.getState());
        }

        @Override
        public void onCallEnded(SipAudioCall call) {
            Log.d(TAG, "out onCallEnded: ");
            Log.d(TAG, "out onCallEnded: State " + call.getState());
            updateStatus(call.getState());
        }


        @Override
        public void onChanged(SipAudioCall call) {
            super.onChanged(call);
            Log.d(TAG, "in onChanged: ");
            updateStatus(call.getState());
        }

        @Override
        public void onCalling(SipAudioCall call) {
            super.onCalling(call);
            Log.d(TAG, "in onCalling: ");
            updateStatus(call.getState());
        }


        @Override
        public void onReadyToCall(SipAudioCall call) {
            super.onReadyToCall(call);
            Log.d(TAG, "in onReadyToCall: ");
            updateStatus(call.getState());
        }

        @Override
        public void onError(SipAudioCall call, int errorCode, String errorMessage) {
            super.onError(call, errorCode, errorMessage);
            Log.e(TAG, "in onError: errorCode " + errorCode);
            Log.e(TAG, "in onError: errorMessage " + errorMessage);
            updateStatus(errorCode);
        }

        @Override
        public void onCallHeld(SipAudioCall call) {
            super.onCallHeld(call);
            Log.d(TAG, "in onCallHeld: ");
            updateStatus(call.getState());
        }

        @Override
        public void onRingingBack(SipAudioCall call) {
            super.onRingingBack(call);
            Log.d(TAG, "in onRingingBack: ");
            updateStatus(call.getState());
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CALL_STATUS = -1;
    }


    public void updateStatus(final int status) {

        Log.d(TAG, "updateStatus: status " + status);
        updateLayout(status);

    }


    public void updateLayout(final int status) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                callInfo.setText(getStateName(status));

                if (status == IN_CALL){
                    settingCall.setVisibility(View.VISIBLE);
                }
                if (status == READY_TO_CALL ||
                        status == ENDING_CALL) {
                    settingCall.setVisibility(View.GONE);
                    finishDialerActivity();
                }
            }
        });
    }

    public void finishDialerActivity() {
        CALL_STATUS = -1;
        finish();
    }


    SipAudioCall.Listener listener_outcoming = new SipAudioCall.Listener() {
        @Override
        public void onCallEstablished(SipAudioCall call) {
            Log.d(TAG, "out onCallEstablished:");
            Log.d(TAG, "out onCallEstablished: State " + call.getState());
            call.startAudio();
            call.setSpeakerMode(false);
            updateStatus(call.getState());
        }

        @Override
        public void onCallEnded(SipAudioCall call) {
            Log.d(TAG, "out onCallEnded: ");
            Log.d(TAG, "out onCallEnded: State " + call.getState());
            updateStatus(call.getState());
        }

        @Override
        public void onCallBusy(SipAudioCall call) {
            super.onCallBusy(call);
            Log.d(TAG, "out onCallBusy: ");
            Log.d(TAG, "out onCallBusy: State " + call.getState());
            updateStatus(call.getState());
        }

        @Override
        public void onChanged(SipAudioCall call) {
            super.onChanged(call);
            Log.d(TAG, "out onChanged: ");
            Log.d(TAG, "out onChanged: isInCall" + call.isInCall());
            Log.d(TAG, "out onChanged: State " + call.getState());
            updateStatus(call.getState());
        }

        @Override
        public void onCalling(SipAudioCall call) {
            super.onCalling(call);
            Log.d(TAG, "out onCalling: ");
            updateStatus(call.getState());
        }

        @Override
        public void onRinging(SipAudioCall call, SipProfile caller) {
            super.onRinging(call, caller);
            Log.d(TAG, "out onRinging: ");
            updateStatus(call.getState());
        }

        @Override
        public void onRingingBack(SipAudioCall call) {
            super.onRingingBack(call);
            Log.d(TAG, "out onRingingBack: ");
            updateStatus(call.getState());

        }

        @Override
        public void onCallHeld(SipAudioCall call) {
            super.onCallHeld(call);
            Log.d(TAG, "out onCallHeld: "+call.getState());
            Log.d(TAG, "out onCallHeld: "+call.isOnHold());
            updateStatus(call.getState());
        }


        @Override
        public void onError(SipAudioCall call, int errorCode, String errorMessage) {
            super.onError(call, errorCode, errorMessage);
            Log.d(TAG, "out onError: errorCode " + SipStateCode.toString(errorCode));
            Log.d(TAG, "out onError: errorMessage " + errorMessage);
            updateStatus(errorCode);
        }

        @Override
        public void onReadyToCall(SipAudioCall call) {
            super.onReadyToCall(call);
            Log.d(TAG, "out onReadyToCall: ");
            updateStatus(call.getState());
        }
    };


    public static void sendCall(Context context, String phoneNumber) {
        if (!ValidationHelper.validString(phoneNumber)) {
            Toast.makeText(context, "no number", Toast.LENGTH_SHORT).show();
            return;
        }

        CALL_STATUS = OUT_COMING;
        CALL_NUMBER = phoneNumber;
        context.startActivity(new Intent(context, DialerActivity.class));
    }

    public static void receiveCall(Context context, Intent intent) {
        if (!ValidationHelper.validObject(intent)) {
            Toast.makeText(context, "error in receive call", Toast.LENGTH_SHORT).show();
            return;
        }
        IncomingCallIntent = intent;
        CALL_STATUS = IN_COMING;
        Intent intentCall = new Intent(context, DialerActivity.class);
        intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentCall);
    }

}
