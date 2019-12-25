package com.in2world.ccs.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.in2world.ccs.R;
import com.in2world.ccs.RootApplcation;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.tools.GlobalData;

import java.text.ParseException;

import static com.in2world.ccs.tools.GlobalData.CALLING;
import static com.in2world.ccs.tools.GlobalData.CALL_ADDRESS;
import static com.in2world.ccs.tools.GlobalData.CLOSE;
import static com.in2world.ccs.tools.GlobalData.CONNECTED;
import static com.in2world.ccs.tools.GlobalData.HANG_UP;
import static com.in2world.ccs.tools.GlobalData.IN_COMING;
import static com.in2world.ccs.tools.GlobalData.IncomingCallIntent;
import static com.in2world.ccs.tools.GlobalData.OUT_COMING;
import static com.in2world.ccs.tools.GlobalData.READY;
import static com.in2world.ccs.tools.GlobalData.RINGING;
import static com.in2world.ccs.tools.GlobalData.SIP_domain;
import static com.in2world.ccs.tools.GlobalData.SIP_password;
import static com.in2world.ccs.tools.GlobalData.SIP_username;
import static com.in2world.ccs.tools.GlobalData.UPDATE_SETTINGS_DIALOG;
import static com.in2world.ccs.tools.GlobalData.SIP_Manager;
import static com.in2world.ccs.tools.GlobalData.SIP_Audio_Call;
import static com.in2world.ccs.tools.GlobalData.SIP_Profile;
import static com.in2world.ccs.tools.GlobalData.CALL_STATUS;
import static com.in2world.ccs.tools.GlobalData.CALL_NUMBER;
import static com.in2world.ccs.tools.SipErrorCode.getErrorMessage;

public class CallActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = "CallActivity";

    private EditText editName;
    private LinearLayout layoutReceiver;
    private String username, domain, password;


    public String sipAddress = null;
    private ConstraintLayout layConstraint;
    private Button answer;
    private Button hangup;
    private Button close;
    private TextView callInfo;
    private TextView callName;

    //public SipAudioCall call = null;
    // public IncomingCallReceiver callReceiver;
    private TextView sipLabel;
    private Button btnCall, btnClose;
    private SipAudioCall incomingCall = null;

    private boolean callStatus = false;
    private int callAction = 0;

    BroadcastReceiver incomingCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            try {


                //IncomingCallIntent =  intent;
                //CALL_STATUS = IN_COMING;
                //startActivity(new Intent(CallActivity.this,DialerActivity.class));

                layConstraint.setVisibility(View.VISIBLE);

               SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                    @Override
                    public void onRinging(SipAudioCall call, SipProfile caller) {
                        try {
                            Toast.makeText(context, "onRinging", Toast.LENGTH_SHORT).show();
                            call.answerCall(30);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                   @Override
                   public void onCallBusy(SipAudioCall call) {
                       super.onCallBusy(call);
                       Toast.makeText(context, "onCallBusy", Toast.LENGTH_SHORT).show();
                   }
               };

                callStatus = true;

                Toast.makeText(context, "incomingCallReceiver", Toast.LENGTH_SHORT).show();
                incomingCall = SIP_Manager.takeAudioCall(intent,new SipAudioCall.Listener() {
                    @Override
                    public void onRinging(SipAudioCall call, SipProfile caller) {
                        try {
                            Toast.makeText(context, "onRinging", Toast.LENGTH_SHORT).show();
                            call.answerCall(30);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCallBusy(SipAudioCall call) {
                        super.onCallBusy(call);
                        Toast.makeText(CallActivity.this, "onCallBusy", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onCallBusy: ");
                        updateStatus("onCallBusy.");
                        updateLayout(READY);
                    }

                    @Override
                    public void onChanged(SipAudioCall call) {
                        super.onChanged(call);
                        Toast.makeText(CallActivity.this, "onChanged", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onChanged: ");
                        updateStatus("onChanged.");

                    }

                    @Override
                    public void onCalling(SipAudioCall call) {
                        super.onCalling(call);
                        Toast.makeText(CallActivity.this, "onCalling", Toast.LENGTH_SHORT).show();
                        updateStatus("Calling");
                        Log.d(TAG, "onCalling: ");
                    }
                });


                CALL_STATUS = IN_COMING;
                SIP_Audio_Call = incomingCall;
                updateStatus(SIP_Audio_Call.getPeerProfile().getDisplayName() + " Ringing");
                updateLayout(RINGING);
            } catch (Exception e) {
                if (incomingCall != null) {
                    incomingCall.close();
                }
            }
        }
    };

    private void initView() {




        layConstraint =  findViewById(R.id.lay_constraint);
        answer = (Button) findViewById(R.id.answer);
        hangup = (Button) findViewById(R.id.hangup);
        close = (Button) findViewById(R.id.close);
        callInfo = (TextView) findViewById(R.id.callInfo);
        callName = (TextView) findViewById(R.id.callName);
        btnCall = findViewById(R.id.btn_call);
        btnClose = findViewById(R.id.btn_close);
        sipLabel = findViewById(R.id.sipLabel);
        editName = findViewById(R.id.edit_name);
        layoutReceiver = findViewById(R.id.layout_receiver);
        layoutReceiver.setVisibility(View.VISIBLE);

        ToggleButton pushToTalkButton = findViewById(R.id.pushToTalk);
        pushToTalkButton.setOnTouchListener(this);
        pushToTalkButton.setVisibility(View.GONE);
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
    }


    private void init() {

        updateStatus(RootApplcation.getSipStatus());
        // "Push to talk" can be a serious pain when the screen keeps turning off.
        // Let's prevent that.

    }

    @Override
    public void onStart() {
        super.onStart();
        // When we get back from the preference setting Activity, assume
        // settings have changed, and re-login with new auth info.
    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (SIP_Audio_Call != null) {
//            SIP_Audio_Call.close();
//        }
//        if (incomingCallReceiver != null) {
//            this.unregisterReceiver(incomingCallReceiver);
//        }
//    }





    /**
     * Make an outgoing call.
     */
    public void initiateCall() {

        updateStatus(sipAddress);

            CALL_STATUS = OUT_COMING;
            CALL_NUMBER = sipAddress;
            startActivity(new Intent(CallActivity.this,DialerActivity.class));

    }

    /**
     * Updates the status box at the top of the UI with a messege of your choice.
     *
     * @param status The String to display in the status box.
     */
    public void updateStatus(final String status) {
        // Be a good citizen.  Make sure UI changes fire on the UI thread.
        this.runOnUiThread(new Runnable() {
            public void run() {
                sipLabel.setText(status);
            }
        });
    }

    /**
     * Updates the status box with the SIP address of the current call.
     *
     * @param call The current, active call.
     */
    public void updateStatus(SipAudioCall call) {
        String useName = call.getPeerProfile().getDisplayName();
        if (useName == null) {
            useName = call.getPeerProfile().getUserName();
        }
        updateStatus(useName + "@" + call.getPeerProfile().getSipDomain());
    }

    public void updateLayout(final int status) {

        callAction = status;

        this.runOnUiThread(new Runnable() {
            public void run() {
                switch (status) {
                    case READY:
                        btnCall.setBackgroundColor(getResources().getColor(R.color.btn));
                        btnClose.setBackgroundColor(getResources().getColor(R.color.btn));
                        btnCall.setText("Call");
                        btnClose.setVisibility(View.INVISIBLE);
                        btnCall.setVisibility(View.VISIBLE);
                        break;
                    case CALLING:
                        btnCall.setBackgroundColor(getResources().getColor(R.color.btn));
                        btnClose.setBackgroundColor(getResources().getColor(R.color.btn));
                        btnClose.setVisibility(View.VISIBLE);
                        btnCall.setVisibility(View.VISIBLE);
                        btnCall.setText("Calling");
                        btnClose.setText("Close");
                        break;
                    case RINGING:
                        btnCall.setBackgroundColor(getResources().getColor(R.color.btn));
                        btnClose.setBackgroundColor(getResources().getColor(R.color.btn));
                        btnClose.setVisibility(View.VISIBLE);
                        btnCall.setVisibility(View.VISIBLE);
                        btnCall.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        btnClose.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        btnCall.setText("Answer");
                        btnClose.setText("Denial");
                        break;
                    case CONNECTED:
                        btnCall.setBackgroundColor(getResources().getColor(R.color.btn));
                        btnClose.setBackgroundColor(getResources().getColor(R.color.btn));
                        btnClose.setVisibility(View.VISIBLE);
                        btnCall.setVisibility(View.INVISIBLE);
                        btnCall.setText("CONNECTED");
                        btnClose.setText("END");
                        break;
                    case CLOSE:
                        btnCall.setVisibility(View.VISIBLE);
                        btnCall.setBackgroundColor(getResources().getColor(R.color.btn));
                        btnClose.setBackgroundColor(getResources().getColor(R.color.btn));
                        btnCall.setText("Call");
                        btnClose.setVisibility(View.INVISIBLE);
                        break;


                }
            }
        });
    }

    /**
     * Updates whether or not the user's voice is muted, depending on whether the button is pressed.
     *
     * @param v     The View where the touch event is being fired.
     * @param event The motion to act on.
     * @return boolean Returns false to indicate that the parent view should handle the touch event
     * as it normally would.
     */
    public boolean onTouch(View v, MotionEvent event) {

        Log.d(TAG, "onTouch: ");
        if (SIP_Audio_Call == null) {
            return false;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN && SIP_Audio_Call != null && SIP_Audio_Call.isMuted()) {
            SIP_Audio_Call.toggleMute();
            Toast.makeText(this, "ACTION_DOWN", Toast.LENGTH_SHORT).show();
        } else if (event.getAction() == MotionEvent.ACTION_UP && !SIP_Audio_Call.isMuted()) {
            SIP_Audio_Call.toggleMute();
            Toast.makeText(this, "ACTION_UP", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, UPDATE_SETTINGS_DIALOG, 0, "Account Settings");
        menu.add(0, HANG_UP, 0, "reCreate");


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case UPDATE_SETTINGS_DIALOG:
                updatePreferences();
                break;
            case HANG_UP:
                recreate();
                break;
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CALL_ADDRESS:

                sipAddress = editName.getText().toString();
                if (!ValidationHelper.validString(sipAddress)) {
                    Toast.makeText(this, "enter numberPhone", Toast.LENGTH_SHORT).show();
                } else {
                    initiateCall();
                }
            case UPDATE_SETTINGS_DIALOG:
                return new AlertDialog.Builder(this)
                        .setMessage("Please update your SIP Account Settings.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                updatePreferences();
                            }
                        })
                        .setNegativeButton(
                                android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Noop.
                                    }
                                })
                        .create();
        }
        return null;
    }

    public void updatePreferences() {
//        Intent settingsActivity = new Intent(getBaseContext(),
//                SipSettings.class);
//        startActivity(settingsActivity);

        Toast.makeText(this, "enter your data", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, SipSettingsActivity.class));

    }

    public void Call(View view) throws SipException {


        if (callStatus) {
            // CallActivity wtActivity = (CallActivity) context;
            incomingCall.answerCall(30);
            incomingCall.startAudio();
            incomingCall.setSpeakerMode(true);
//            if (incomingCall.isMuted()) {
//                incomingCall.toggleMute();
//            }
//            incomingCall.toggleMute();
            updateStatus(incomingCall);
            callStatus = true;
            updateLayout(CONNECTED);
            return;
        }

        sipAddress = editName.getText().toString();
        if (!ValidationHelper.validString(sipAddress)) {
            Toast.makeText(this, "enter numberPhone", Toast.LENGTH_SHORT).show();
        } else {

            editName.setText("");
            initiateCall();
        }

    }

    public void Accept(View view) throws SipException {


    }

    public void Denial(View view) throws SipException {

        if (callStatus) {
            // CallActivity wtActivity = (CallActivity) context;
            incomingCall.endCall();
            updateStatus("Ready");
            callStatus = false;
            updateLayout(CLOSE);
            return;
        }

        if (callAction == CONNECTED || (SIP_Audio_Call != null && SIP_Audio_Call.isInCall())) {
            SIP_Audio_Call.close();

            updateLayout(CLOSE);
            return;
        }

        if (callAction == CALLING) {
            //close calling
            if (SIP_Audio_Call != null) {
                SIP_Audio_Call.endCall();
            }
            updateLayout(CLOSE);
            updateStatus("Ready");

            return;
        }


        //  updateLayout(READY);

    }


    SipAudioCall.Listener sipListener = new SipAudioCall.Listener() {
        // Much of the client's interaction with the SIP Stack will
        // happen via listeners.  Even making an outgoing call, don't
        // forget to set up a listener to set things up once the call is established.
        @Override
        public void onCallEstablished(SipAudioCall call) {
            Toast.makeText(CallActivity.this, "onCallEstablished", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCallEstablished: ");
            try {
                call.answerCall(30);
            } catch (Exception e) {
                e.printStackTrace();
            }
            call.startAudio();
            call.setSpeakerMode(true);
            //  call.toggleMute();
            updateStatus(call);
            updateLayout(CONNECTED);
        }
        @Override
        public void onCallEnded(SipAudioCall call) {
            Toast.makeText(CallActivity.this, "onCallEnded", Toast.LENGTH_SHORT).show();
            updateStatus("Ready.");
            updateLayout(READY);
            callStatus = false;
        }
        @Override
        public void onCallBusy(SipAudioCall call) {
            super.onCallBusy(call);
            Toast.makeText(CallActivity.this, "onCallBusy", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCallBusy: ");
            updateStatus("onCallBusy.");
            updateLayout(READY);
        }

        @Override
        public void onChanged(SipAudioCall call) {
            super.onChanged(call);
            Toast.makeText(CallActivity.this, "onChanged", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onChanged: ");
            updateStatus("onChanged.");

        }

        @Override
        public void onCalling(SipAudioCall call) {
            super.onCalling(call);
            Toast.makeText(CallActivity.this, "onCalling", Toast.LENGTH_SHORT).show();
            updateStatus("Calling");
            Log.d(TAG, "onCalling: ");
        }

        @Override
        public void onRinging(SipAudioCall call, SipProfile caller) {
            super.onRinging(call, caller);
            Toast.makeText(CallActivity.this, "onRinging", Toast.LENGTH_SHORT).show();
            updateStatus("Ringing.");
            Log.d(TAG, "onRinging: ");

        }

        @Override
        public void onReadyToCall(SipAudioCall call) {
            super.onReadyToCall(call);

            Toast.makeText(CallActivity.this, "onReadyToCall", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "onReadyToCall: ");
        }

        @Override
        public void onError(SipAudioCall call, int errorCode, String errorMessage) {
            super.onError(call, errorCode, errorMessage);
            Toast.makeText(CallActivity.this, "onError", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onError: errorCode " + errorCode);
            Log.e(TAG, "onError: errorMessage " + errorMessage);
        }

        @Override
        public void onCallHeld(SipAudioCall call) {
            super.onCallHeld(call);
            Toast.makeText(CallActivity.this, "onCallHeld", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCallHeld: ");
        }

        @Override
        public void onRingingBack(SipAudioCall call) {
            super.onRingingBack(call);
            Toast.makeText(CallActivity.this, "onRingingBack", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onRingingBack: ");
        }

    };
}
