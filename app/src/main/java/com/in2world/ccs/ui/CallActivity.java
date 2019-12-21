package com.in2world.ccs.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.in2world.ccs.helper.ValidationHelper;

import java.text.ParseException;

import static com.in2world.ccs.tools.GlobalData.CALLING;
import static com.in2world.ccs.tools.GlobalData.CALL_ADDRESS;
import static com.in2world.ccs.tools.GlobalData.CLOSE;
import static com.in2world.ccs.tools.GlobalData.CONNECTED;
import static com.in2world.ccs.tools.GlobalData.HANG_UP;
import static com.in2world.ccs.tools.GlobalData.READY;
import static com.in2world.ccs.tools.GlobalData.RINGING;
import static com.in2world.ccs.tools.GlobalData.SIP_domain;
import static com.in2world.ccs.tools.GlobalData.SIP_password;
import static com.in2world.ccs.tools.GlobalData.SIP_username;
import static com.in2world.ccs.tools.GlobalData.UPDATE_SETTINGS_DIALOG;
import static com.in2world.ccs.tools.SipErrorCode.getErrorMessage;

public class CallActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = "CallActivity";

    private EditText editName;
    private LinearLayout layoutReceiver;
    private String username, domain, password;


    public String sipAddress = null;

    public SipManager manager = null;
    public SipProfile me = null;
    public SipAudioCall call = null;
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
                incomingCall = manager.takeAudioCall(intent,new SipAudioCall.Listener() {
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
                call = incomingCall;
                updateStatus(call.getPeerProfile().getDisplayName() + " Ringing");
                updateLayout(RINGING);
            } catch (Exception e) {
                if (incomingCall != null) {
                    incomingCall.close();
                }
            }
        }
    };

    private void initView() {

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        // username = prefs.getString("namePref", "");
        //domain = prefs.getString("domainPref", "");
        //password = prefs.getString("passPref", "");

        username = SIP_username;
        domain = SIP_domain;
        password = SIP_password;

        Log.d(TAG, "init: username " + username);
        Log.d(TAG, "init: domain " + domain);
        Log.d(TAG, "init: password " + password);

        // Set up the intent filter.  This will be used to fire an
        // IncomingCallReceiver when someone calls the SIP address used by this
        // application.

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.SipDemo.INCOMING_CALL");
        // callReceiver = new IncomingCallReceiver();
        this.registerReceiver(incomingCallReceiver, filter);

        // "Push to talk" can be a serious pain when the screen keeps turning off.
        // Let's prevent that.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initializeManager();
    }

    @Override
    public void onStart() {
        super.onStart();
        // When we get back from the preference setting Activity, assume
        // settings have changed, and re-login with new auth info.
        initializeManager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.close();
        }

        closeLocalProfile();

        if (incomingCallReceiver != null) {
            this.unregisterReceiver(incomingCallReceiver);
        }
    }

    public void initializeManager() {
        if (manager == null) {
            manager = SipManager.newInstance(this);
        }
        initializeLocalProfile();
    }

    /**
     * Logs you into your SIP provider, registering this device as the location to
     * send SIP calls to for your SIP address.
     */
    public void initializeLocalProfile() {
        if (manager == null) {
            return;
        }

        if (me != null) {
            closeLocalProfile();
        }

        if (username.length() == 0 || domain.length() == 0 || password.length() == 0) {
            showDialog(UPDATE_SETTINGS_DIALOG);
            return;
        }

        try {
            SipProfile.Builder builder = new SipProfile.Builder(username, domain);
            builder.setPassword(password);
            me = builder.build();



            Intent i = new Intent();
            i.setAction("android.SipDemo.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
            manager.open(me, pi, null);

            // This listener must be added AFTER manager.open is called,
            // Otherwise the methods aren't guaranteed to fire.


            Log.d(TAG, "initializeLocalProfile: manager " + (manager != null));
            manager.setRegistrationListener(me.getUriString(), new SipRegistrationListener() {
                public void onRegistering(String localProfileUri) {
                    updateStatus("Registering with SIP Server...");
                }

                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    updateStatus("Ready");
                    updateLayout(READY);
                }

                public void onRegistrationFailed(String localProfileUri, int errorCode,
                                                 String errorMessage) {
                    Log.w(TAG, "onRegistrationFailed: localProfileUri " + localProfileUri);
                    Log.w(TAG, "onRegistrationFailed: errorCode " + errorCode);
                    Log.w(TAG, "onRegistrationFailed: errorMessage " + errorMessage);
                    updateStatus("Registration failed. "+getErrorMessage(errorCode));
                }
            });
        } catch (ParseException pe) {
            updateStatus("Connection Error.");
        } catch (SipException se) {
            updateStatus("Connection error.");
        }
    }

    /**
     * Closes out your local profile, freeing associated objects into memory
     * and unregistering your device from the server.
     */
    public void closeLocalProfile() {
        if (manager == null) {
            return;
        }
        try {
            if (me != null) {
                manager.close(me.getUriString());
            }
        } catch (Exception ee) {
            Log.d(TAG + "/onDestroy", "Failed to close local profile.", ee);
        }
    }

    /**
     * Make an outgoing call.
     */
    public void initiateCall() {

        updateStatus(sipAddress);

        try {

            updateLayout(CALLING);

            call = manager.makeAudioCall(me.getUriString(), sipAddress + "@" + domain, sipListener, 30);

        } catch (Exception e) {
            Log.i(TAG, "initiateCall: Error when trying to close manager. " + e);
            if (me != null) {
                try {
                    manager.close(me.getUriString());
                } catch (Exception ee) {
                    Log.i(TAG, "initiateCall: Error when trying to close manager. ", ee);
                    ee.printStackTrace();
                }
            }
            if (call != null) {
                call.close();
            }
        }
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
        if (call == null) {
            return false;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN && call != null && call.isMuted()) {
            call.toggleMute();
            Toast.makeText(this, "ACTION_DOWN", Toast.LENGTH_SHORT).show();
        } else if (event.getAction() == MotionEvent.ACTION_UP && !call.isMuted()) {
            call.toggleMute();
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

        if (callAction == CONNECTED || (call != null && call.isInCall())) {
            call.close();

            updateLayout(CLOSE);
            return;
        }

        if (callAction == CALLING) {
            //close calling
            if (call != null) {
                call.endCall();
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
