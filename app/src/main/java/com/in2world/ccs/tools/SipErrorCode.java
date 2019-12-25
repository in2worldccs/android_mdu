package com.in2world.ccs.tools;

import android.util.Log;

public class SipErrorCode {

    SipErrorCode sipErrorCode;
    private static final String TAG = "SipErrorCode";
    public static final int CLIENT_ERROR = -4;
    public static final int CROSS_DOMAIN_AUTHENTICATION = -44;
    public static final int DATA_CONNECTION_LOST = -10;
    public static final int INVALID_CREDENTIALS = -8;
    public static final int INVALID_REMOTE_URI = -6;
    public static final int IN_PROGRESS = -9;
    public static final int NO_ERROR = 0;
    public static final int PEER_NOT_REACHABLE = -7;
    public static final int SERVER_ERROR = -2;
    public static final int SERVER_UNREACHABLE = -12;
    public static final int SOCKET_ERROR = -1;
    public static final int TIME_OUT = -5;
    public static final int TRANSACTION_TERMINTED = -3;


    public static String getErrorMessage(int errorCode){
        Log.d(TAG, "getErrorMessage: errorCode "+errorCode);
        switch (errorCode){
            case CLIENT_ERROR :
                return "When some error occurs on the device, possibly due to a bug";
            case CROSS_DOMAIN_AUTHENTICATION :
                return "Cross-domain authentication required";
            case DATA_CONNECTION_LOST :
                return "When data connection is lost";
            case INVALID_CREDENTIALS  :
                return "When invalid credentials are provided";
            case INVALID_REMOTE_URI  :
                return "When the remote URI is not valid";
            case IN_PROGRESS  :
                return "The client is in a transaction and cannot initiate a new one";
            case NO_ERROR :
                return "Not an error";
            case PEER_NOT_REACHABLE  :
                return "When the peer is not reachable";
            case SERVER_ERROR  :
                return "When server responds with an error";
            case SERVER_UNREACHABLE :
                return "When the server is not reachable";
            case SOCKET_ERROR  :
                return "When some socket error occurs";
            case TIME_OUT  :
                return "When the transaction gets timed out";
            case TRANSACTION_TERMINTED  :
                return "When transaction is terminated unexpectedly";
            default:
                return "no error";
        }
    }



}
