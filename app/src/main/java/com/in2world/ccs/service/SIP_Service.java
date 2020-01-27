package com.in2world.ccs.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.net.sip.SipSession;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.in2world.ccs.R;
import com.in2world.ccs.RootApplcation;
import com.in2world.ccs.helper.Config;
import com.in2world.ccs.helper.NotificationHelper;
import com.in2world.ccs.helper.PermissionHelper;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.receiver.IncomingCallReceiver;
import com.in2world.ccs.server.Result;
import com.in2world.ccs.server.fcm.FCM;
import com.in2world.ccs.tools.GlobalData;
import com.in2world.ccs.tools.SipStateCode;
import com.in2world.ccs.ui.DialerActivity;

import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Notification.GROUP_ALERT_SUMMARY;
import static com.in2world.ccs.RootApplcation.closeLocalProfile;
import static com.in2world.ccs.RootApplcation.setSipStatus;
import static com.in2world.ccs.helper.Config.SIP_INCAMING_CALLER;
import static com.in2world.ccs.helper.NotificationHelper.largeIcon;
import static com.in2world.ccs.server.fcm.FCM.sendToCall;
import static com.in2world.ccs.tools.GlobalData.CALL_NUMBER;
import static com.in2world.ccs.tools.GlobalData.CALL_STATUS;
import static com.in2world.ccs.tools.GlobalData.IN_COMING;
import static com.in2world.ccs.tools.GlobalData.IncomingCallIntent;
import static com.in2world.ccs.tools.GlobalData.IsIncomingCaller;
import static com.in2world.ccs.tools.GlobalData.SIP_Manager;
import static com.in2world.ccs.tools.GlobalData.SIP_Profile;
import static com.in2world.ccs.tools.GlobalData.SIP_domain;
import static com.in2world.ccs.tools.GlobalData.SIP_password;
import static com.in2world.ccs.tools.GlobalData.SIP_username;
import static com.in2world.ccs.tools.GlobalData.checkMyData;
import static com.in2world.ccs.tools.GlobalData.mProfile;
import static com.in2world.ccs.tools.GlobalData.mUser;
import static com.in2world.ccs.tools.SipErrorCode.getErrorMessage;

public class SIP_Service extends IntentService {

    private static final String TAG = "SIP_Service";
    private Context context = null;
    private Timer timer;
    ImageView imageView;
    private WindowManager windowManager;

    public static int KEEPUS_NOTIFICATION_ID = 101;
    public static int SYNC_NOTIFICATION_ID = 102;
    private BroadcastReceiver mRegistrationBroadcastReceiver, IncomingCallReceiver;
    public static SipAudioCall incomingCall = null;
    public static SipAudioCall outcomingCall = null;


    private IncomingCallReceiver callReceiver = null;
    private static SIP_Service instance = null;
    public static Ringtone ringtone;

    public static boolean isInstanceCreated() {
        return instance != null;
    }//met

    public static SIP_Service getInstance() {
        return instance;
    }

    public static void setInstance(SIP_Service instance) {
        SIP_Service.instance = instance;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {

        }
        /* We want this service to continue running until it is explicitly
         * stopped, so return sticky.
         */
        return START_STICKY;
    }

    public SIP_Service() {
        super("SIP_Service");
        setIntentRedelivery(true);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        closeLocalProfile();
        if (ValidationHelper.validObject(timer)) {
            timer.cancel();
            timer = null;
        }

        if (callReceiver != null) {
            this.unregisterReceiver(callReceiver);
            callReceiver = null;
        }
        //your end servce code
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        context = getApplicationContext();
        setInstance(this);
        //registerReceiver(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Waking up mobile if it is sleeping
            //    WakeLocker.acquire(context);
            // do something
            //   WakeLocker.release();

            // wakeLock.acquire(10*60*1000L /*10 minutes*/);
            Log.d(TAG, "Wakelock acquired");

            showNotification();
        }


        init(this);


        registrationBroadcastReceiver();
        timer = new Timer("SipServices");
        timer.schedule(updateTask, 0, 3000);
    }


    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            startJob();
        }
    };


    /**
     * start Job in Backgraung
     */
    private void startJob() {
        Log.d(TAG, "startJob: " + System.currentTimeMillis());
    }

    public static String CHANNEL_ID = "ccsServiceChannel";

    private void showNotification() {
        CHANNEL_ID = context.getResources().getString(R.string.cssServiceChannel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
            chan.setLightColor(ContextCompat.getColor(context, R.color.btn));
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            @SuppressLint("WrongConstant")
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(largeIcon(context))
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                    .setGroup(context.getResources().getString(R.string.app_name))
                    .setGroupSummary(false)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setOngoing(true)
                    .setContentText("Ready to receive calls")
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();

            startForeground(KEEPUS_NOTIFICATION_ID, notification);
        }
    }


    public static void startSIPServices(Context context) {
        Log.d(TAG, " startSIPServices: ");


        Intent intentService = new Intent(context, SIP_Service.class);
        context.stopService(intentService);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            //lower then Oreo, just start the service.
            context.startService(intentService);
        }
    }


    public static void stopSIPServices(Context context) {
        Log.d(TAG, "stopSIPServices: ");
        Intent intentService = new Intent(context, SIP_Service.class);
        context.stopService(intentService);
    }

    private void registrationBroadcastReceiver() {

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: ");
                if (intent.getAction().equals(Config.SIP_Broadcast)) {
                    // new push message is received
                    Log.d(TAG, "onCreate: ValidationHelper " + ValidationHelper.validObject(SIP_Manager));
                    if (ValidationHelper.validObject(SIP_Manager)) {
                        DialerActivity.receiveCall(context, intent);
                    }

                }
            }
        };
        IncomingCallReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: " + intent.getAction());
                Log.d(TAG, "onCreate: ValidationHelper " + ValidationHelper.validObject(SIP_Manager));
                if (ValidationHelper.validObject(SIP_Manager)) {
                    DialerActivity.receiveCall(context, intent);
                }
            }
        };
    }


    private void registerReceiver() {
        Log.d(TAG, "registerReceiver: ");
        //isRegisterReceiver(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SIP_INCAMING_CALLER);
        // filter.addAction(Config.SIP_Broadcast);
        callReceiver = new IncomingCallReceiver();
        //registerReceiver(callReceiver,filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(IncomingCallReceiver, filter);
    }


    public void init(Context context) {
        if (!checkMyData()) {
            return;
        }

        Log.d(TAG, "init: username " + SIP_username);
        Log.d(TAG, "init: domain " + SIP_domain);
        Log.d(TAG, "init: password " + SIP_password);


        registerReceiver(context);
        if (!ValidationHelper.validObject(SIP_Manager)) {
            SIP_Manager = SipManager.newInstance(context);
        }
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
           // SIP_Manager.createSipSession(SIP_Profile,null);
            SIP_Manager.open(SIP_Profile, pi, null);
            // This listener must be added AFTER manager.open is called,
            // Otherwise the methods aren't guaranteed to fire.
            Log.d(TAG, "initializeLocalProfile: SIP_Profile " + SIP_Profile.getUriString());
            Log.d(TAG, "initializeLocalProfile: manager " + (SIP_Manager != null));
            SIP_Manager.setRegistrationListener(SIP_Profile.getUriString(), new SipRegistrationListener() {
                public void onRegistering(String localProfileUri) {
                    setSipStatus("Registering with SIP Server..." + localProfileUri);
                    Log.d(TAG, "onRegistering: Registering with SIP Server...");
                }
                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    setSipStatus("Ready");
                    Log.d(TAG, "onRegistering: Ready");


                    Log.d(TAG, "onRegistrationDone: isInstanceCreated " + SIP_Service.isInstanceCreated());

                    if(!ValidationHelper.validObject(mUser)) return;
                    new FCM(context, new Result() {
                        @Override
                        public void onResult(Object object, String function, boolean IsSuccess, int RequestStatus, String MessageStatus) {
                            Log.d(TAG, "onResult: IsSuccess "+IsSuccess);
                        }
                    }).pushMessage(FCM.SIP_READY,mProfile.getUsername(),""+GlobalData.mUser.getId(), GlobalData.mUser.getFcmToken());
                }
                public void onRegistrationFailed(String localProfileUri, int errorCode,
                                                 String errorMessage) {
                    Log.w(TAG, "onRegistrationFailed: localProfileUri " + localProfileUri);
                    Log.w(TAG, "onRegistrationFailed: errorCode " + getErrorMessage(errorCode));
                    Log.w(TAG, "onRegistrationFailed: errorMessage " + errorMessage);
                    setSipStatus("Error , " + getErrorMessage(errorCode));
                }
            });
        } catch (ParseException | SipException pe) {
            stopSIPServices(context);
            Log.e(TAG, "initializeLocalProfile: " + pe.getMessage());
        }
    }

    /**
     * Closes out your local profile, freeing associated objects into memory
     * and unregistering your device from the server.
     */
    public static void closeLocalProfile() {
        Log.d(TAG, "closeLocalProfile: ");
        if (SIP_Manager == null)
            return;

        Log.d(TAG, "closeLocalProfile: 1SIP_username "+SIP_username);
        Log.d(TAG, "closeLocalProfile: 1SIP_domain "+SIP_domain);
        try {
            if (SIP_Profile == null) {
                SipProfile.Builder builder = new SipProfile.Builder(SIP_username, SIP_domain);
                builder.setPassword(SIP_password);
                SIP_Profile = builder.build();
            }
            try {
                //SIP_Manager.unregister(SIP_Profile, null);
                SIP_Manager.close(SIP_Profile.getUriString());
                Log.w(TAG, "closeLocalProfile: Done DoneDoneDoneDone " + SIP_Profile.getUriString());
            }catch (SipException sipException){
                Log.e(TAG, "SipService is dead and is restarting... "+ sipException.getMessage());
            }
        } catch (Exception ee) {
            Log.e(TAG, "onDestroy Failed to close local profile "+ ee.getMessage());

        }
    }


    private void registerReceiver(Context context) {
        Log.d(TAG, "registerReceiver: ");
        //isRegisterReceiver(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.SipDemo.INCOMING_CALL");
        callReceiver = new IncomingCallReceiver();
        this.registerReceiver(callReceiver, filter);
    }


    public void receiveCall(Intent intent) {
        Log.d(TAG, "receiveCall: ");
        IncomingCallIntent= intent;
        playNotificationSound(this);
        whenInComingCall();
        CALL_STATUS = IN_COMING;
        NotificationHelper.callNotification(this,incomingCall.getPeerProfile().getUserName()," is Calling","start");
        //  DialerActivity.receiveCall(this, intent);
    }


    public void whenInComingCall(){
        try {
            if (!ValidationHelper.validObject(IncomingCallIntent))
                return;
            outcomingCall =null;
            incomingCall = SIP_Manager.takeAudioCall(IncomingCallIntent, listener_incoming);
        } catch (SipException e) {
            e.printStackTrace();
            Log.e(TAG, "whenInComingCall: incomingCall "+e.getMessage());
        }

    }

    public void whenOutComingCall() {
        try {
            incomingCall = null;
            Log.d(TAG, "whenOutComingCall: SIP_Profile "+SIP_Profile.getUriString());
            Log.d(TAG, "whenOutComingCall: peer "+CALL_NUMBER + "@" + SIP_domain);
            outcomingCall = SIP_Manager.makeAudioCall(SIP_Profile.getUriString(), CALL_NUMBER + "@" + SIP_domain, listener_outcoming, 30);
        } catch (SipException e) {
            Log.e(TAG, "whenOutComingCall: outcomingCall" + e.getMessage());
            e.printStackTrace();
        }


    }



    SipAudioCall.Listener listener_incoming = new SipAudioCall.Listener() {
        @Override
        public void onRinging(SipAudioCall call, SipProfile caller) {
            Log.d(TAG, "in onRinging: ");
            try {
                call.answerCall(30);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCallBusy(SipAudioCall call) {
            super.onCallBusy(call);
            Log.d(TAG, "in onCallBusy: ");
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

    private void updateStatus(int state) {

        Log.d(TAG, "updateStatus: "+state);
        if (DialerActivity.isInstanceCreated())
            DialerActivity.getInstance().updateLayout(state);

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
            Log.d(TAG, "out onCallHeld: " + call.getState());
            Log.d(TAG, "out onCallHeld: " + call.isOnHold());
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

    public void playNotificationSound(Context mContext) {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notes_of_the_optimistic");
            ringtone = RingtoneManager.getRingtone(mContext, alarmSound);
            // ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  void stopNotificationSound() {
        if (ringtone != null)
            ringtone.stop();
    }

}
