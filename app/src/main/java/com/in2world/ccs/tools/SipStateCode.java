package com.in2world.ccs.tools;

import android.util.Log;

public class SipStateCode {

    SipStateCode sipStateCode;

    private static final String TAG = "SipErrorCode";
    /** When session is ready to initiate a call or transaction. */
    public static final int READY_TO_CALL = 0;
    /** When the registration request is sent out. */
    public static final int REGISTERING = 1;
    /** When the unregistration request is sent out. */
    public static final int DEREGISTERING = 2;
    /** When an INVITE request is received. */
    public static final int INCOMING_CALL = 3;
    /** When an OK response is sent for the INVITE request received. */
    public static final int INCOMING_CALL_ANSWERING = 4;
    /** When an INVITE request is sent. */
    public static final int OUTGOING_CALL = 5;
    /** When a RINGING response is received for the INVITE request sent. */
    public static final int OUTGOING_CALL_RING_BACK = 6;
    /** When a CANCEL request is sent for the INVITE request sent. */
    public static final int OUTGOING_CALL_CANCELING = 7;
    /** When a call is established. */
    public static final int IN_CALL = 8;
    /** When an OPTIONS request is sent. */
    public static final int PINGING = 9;
    /** When ending a call. @hide */
    public static final int ENDING_CALL = 10;
    /** Not defined. */
    public static final int NOT_DEFINED = 101;
    public static final int DECLINED = 603;
    public static final int CONNECTING = 701;

    /**
     * Converts the state to string.
     */
    public static String toString(int state) {
        switch (state) {
            case READY_TO_CALL:
                return "READY_TO_CALL";
            case REGISTERING:
                return "REGISTERING";
            case DEREGISTERING:
                return "DEREGISTERING";
            case INCOMING_CALL:
                return "INCOMING_CALL";
            case INCOMING_CALL_ANSWERING:
                return "INCOMING_CALL_ANSWERING";
            case OUTGOING_CALL:
                return "OUTGOING_CALL";
            case OUTGOING_CALL_RING_BACK:
                return "OUTGOING_CALL_RING_BACK";
            case OUTGOING_CALL_CANCELING:
                return "OUTGOING_CALL_CANCELING";
            case IN_CALL:
                return "IN_CALL";
            case PINGING:
                return "PINGING";
            case CONNECTING:
                return "CONNECTING";
            default:
                return "NOT_DEFINED";
        }
    }
    public static String getStateName(int stateCode){
        Log.d(TAG, "getStateName: stateCode "+stateCode);
        Log.d(TAG, "getStateMessage: "+toString(stateCode));
        switch (stateCode){
            case DEREGISTERING :
                return "DEREGISTERING";
            case INCOMING_CALL :
                return "INCOMING CALL";
            case INCOMING_CALL_ANSWERING :
                return "INCOMING CALL ANSWERING";
            case IN_CALL :
                return "IN CALL";
            case NOT_DEFINED :
                return "NOT DEFINED";
            case OUTGOING_CALL :
                return "OUTGOING CALL";
            case OUTGOING_CALL_CANCELING :
                return "OUTGOING CALL CANCELING";
            case OUTGOING_CALL_RING_BACK :
                return "OUTGOING CALL RING BACK";
            case PINGING :
                return "PINGING";
            case READY_TO_CALL :
                return "READY_TO_CALL";
            case REGISTERING :
                return "REGISTERING";
            case DECLINED :
                return "DECLINED";
            case CONNECTING :
                return "CONNECTING";
            default:
                return "null";
        }
    }
}
