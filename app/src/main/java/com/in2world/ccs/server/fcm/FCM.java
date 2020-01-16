package com.in2world.ccs.server.fcm;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.in2world.ccs.RootApplcation;
import com.in2world.ccs.server.Result;
import com.in2world.ccs.service.SIP_Service;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCM {
    private static final String TAG = "FCM";

    public static final String SIP_RUN = "SIP_RUN";
    public static final String SIP_READY = "SIP_READY";
    public static final String FCM_TITLE = "title";
    public static final String FCM_NAME = "name";
    public static final String FCM_DATA = "data";

    public static String Server_key = "AAAAcAzvFC8:APA91bFM6RhxN4iAEnFqkz3ONbCKqdDGV6Uo5KbDOhlzQILFaLkyqS5qQGB6JRsnNYLq2gjiKwDkD3Vy46Igz_H2hLfg5Owbxn1qR6uJlEgMhgHjGLq_qFRa8f6cZTzA6OM5qtxOd7me";
    public static String token_nokia1 = "e4w6buA2_s4:APA91bFHY3snl4AuwKcJxnDimOzgFV1w37MEeYMirTl3vTKH5ShASqKRUNHY0aSXd4ZL_O62-16FwWyL1_gpVeZWWqRsiDYlvPvBjzAKeLPPQ0BocM8JtjImOMXUcfLo2s--nIR9vFPF";
    public static String token_nokia = "dYrxJEK83tE:APA91bGTjfdIBuHNJjECSfNYwT_wOPsykFjrWBRbiloqjLkbuoFshT9AXU7-CAgsfOCWFhuS2RbkIurdgSCdRD0eRLB81Yxn-SrEeVITrZelRJfmFViCIMcSTIi_UOa9AI8rR_JIq3HX";
    public static String token_samsung = "cQsu7VuSQ7g:APA91bEf4l500DsP-jKZaTGAFVf6gm8XYIsQL3ddbR_zJnGUm_MrnD3PakG5MZ5wcvKTgF49w-n-7HBZAR6UZOcaR7LtxW1HOgThKhWQ38pDJKxukVvNaKbewbZ6fgIXPiBiS2BFijXA";
    public static String token_nokia3 = "eFER3h2BJKw:APA91bG5jKmdHBMBh1tbXo2l7K-3cA4miFbLkCB7UauhLWhBWznzx8YmHwPDYcZX9LEcc9kDwYR40N-8emTNd4cHFioIW5pb6bYHSrt6fiE_VJ2tCM7wW0cRmZ9usgnIto7ggD0Na5cY";
    private Context context;
    private Result Result;

    public FCM(Context context, Result Result) {
        this.context = context;
        this.Result = Result;
    }

    public void pushMessage(String title, String name, String data, String token) {
        final String url = "https://fcm.googleapis.com/fcm/send";
        JSONObject notification_data = new JSONObject();
        try {

            JSONObject dataJSON = new JSONObject();
            dataJSON.put(FCM_TITLE, title);
            dataJSON.put(FCM_NAME, name);
            dataJSON.put(FCM_DATA, data);

            notification_data.put("data", dataJSON);
            notification_data.put("to", token_samsung);

            Log.d(TAG, "pushMessage: data "+notification_data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, notification_data,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: "+response);
                        Result.onResult(response, "pushToken", true, 200, "");

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: "+error);
                        Result.onResult(null, "pushToken", false, error.hashCode(), error.getMessage());

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Server_key);
                params.put("Content-Type", "application/json");

                Log.d(TAG, "getHeaders: params"+params.toString());
                return params;
            }
        };
        RootApplcation.getmRootApplcation().getRequestQueue().add(jsObjRequest);
    }

    public static void sendToCall(Context context){
        new FCM(context, new Result() {
            @Override
            public void onResult(Object object, String function, boolean IsSuccess, int RequestStatus, String MessageStatus) {
                Log.d(TAG, "onResult: IsSuccess "+IsSuccess);
            }
        }).pushMessage(FCM.SIP_READY,"213","13123",FCM.token_samsung);

    }


}
