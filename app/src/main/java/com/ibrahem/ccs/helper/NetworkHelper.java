package com.ibrahem.ccs.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ibrahem on 9/16/2018.
 */

public class NetworkHelper {


    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }




    public static String getConnectivityStatusString(Context context) {

        int conn = NetworkHelper.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkHelper.TYPE_WIFI) {
            //status = "Wifi enabled";
            status="Internet connection available";
        } else if (conn == NetworkHelper.TYPE_MOBILE) {
            //status = "Mobile data enabled";
            status="Internet connection available";
        } else if (conn == NetworkHelper.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

    /**
     * @param context
     * @return
     */
    //check NetWork
    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null
                && networkInfo.isConnected()
                && networkInfo.getState().equals(NetworkInfo.State.CONNECTED);
    }
}
