package com.in2world.ccs.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Header;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import com.in2world.ccs.RootApplcation;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.tools.GlobalData;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


import static com.in2world.ccs.helper.Constants.DOMAIN_URL;
import static com.in2world.ccs.helper.Constants.GROUP_ID;
import static com.in2world.ccs.helper.Constants.USER_ID;

import static com.in2world.ccs.tools.GlobalData.TOKEN_VALUE;
import static com.in2world.ccs.tools.GlobalData.isToken;

public class WebService {

    public static final String TAG = "Api";
    private Context context;
    private Result Result;

    public static String RESULT = "result";
    public static String RESPONSE_CODE = "status_code";
    public static String Authorization = "authorization";
    public static String AuthorizationValue = "";
    public static String lang = "ar";
    public static String Disabled = "disabled";

    private RequestAPI mRequestAPI;
    private HashMap<String, Object> mResponse;
    private Map<String, String> mHeader;
    private HashMap<String, String> mRequest;
    public HashMap<String, byte[]> dataMultiPartRequest;
    private OnResponding onResponding;

    //RELEASE URL
    //private final static String DOMAIN_URL = "http://78.141.219.180:3007/api/v1/";

    public enum RequestAPI {
        //Auth
        LOGIN(DOMAIN_URL + "auth/login", Request.Method.POST),

        //Users
        USERS(DOMAIN_URL + "users", Request.Method.GET),
        USER(DOMAIN_URL + "user", Request.Method.GET),
        USER_UPDATE(DOMAIN_URL + "user_update", Request.Method.PUT),

        //Groups
        GROUPS(DOMAIN_URL + "groups", Request.Method.GET),
        GROUP(DOMAIN_URL + "group", Request.Method.GET),
        GROUP_CREATE(DOMAIN_URL + "groups", Request.Method.POST),
        GROUP_UPDATE(DOMAIN_URL + "group_update", Request.Method.PUT);


        private String value;
        private int requestMethod;

        RequestAPI(final String value, final int requestMethod) {
            this.value = value;
            this.requestMethod = requestMethod;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getRequestMethod() {
            return requestMethod;
        }

        @Override
        public String toString() {
            return this.getValue();
        }
    }

    public WebService(Context context, Result Result) {
        this.context = context;
        this.Result = Result;
    }

    public WebService(RequestAPI requestAPI, HashMap<String, String> dataRequest, OnResponding onResponding) {
        this.onResponding = onResponding;
        mRequestAPI = requestAPI;
        mHeader = initializeHeader();
        mRequest = dataRequest;
        mResponse = initializeHashMap();
        if (!ValidationHelper.validObject(mRequest))
            mRequest = new HashMap<>();

        Log.d(TAG, "WebService : mRequestAPI " + mRequestAPI.toString());
        Log.d(TAG, "WebService : mHeader " + mHeader.toString());
        Log.d(TAG, "WebService : mRequest " + mRequest.toString());

        getConnection();
    }

    public WebService(RequestAPI requestAPI, HashMap<String, String> dataRequest, HashMap<String, byte[]> dataPartRequest, OnResponding onResponding) {
        this.onResponding = onResponding;
        mRequestAPI = requestAPI;
        mHeader = initializeHeader();
        mRequest = dataRequest;
        mResponse = initializeHashMap();
        dataMultiPartRequest = dataPartRequest;
        if (!ValidationHelper.validObject(mRequest))
            mRequest = new HashMap<>();

        Log.d(TAG, "WebService : mRequestAPI " + mRequestAPI.toString());
        Log.d(TAG, "WebService : mHeader " + mHeader.toString());
        Log.d(TAG, "WebService : mRequestFile " + mRequest.toString());
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(mRequestAPI.getRequestMethod(),
                mRequestAPI.getValue(), new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.w(TAG, "onResponse url : " + mRequestAPI.getValue());
                Log.w(TAG, "onResponse response : " + resultResponse);
                Log.w(TAG, "onResponse statusCode : 200");
                mResponse.put(RESULT, resultResponse);
                sendRequest();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                Log.e(TAG, "Error: " + error.getMessage());
                Log.e(TAG, "Error Message: " + message);
                try {
                    mResponse.put(RESPONSE_CODE, error.networkResponse.statusCode);
                } catch (Exception e) {
                    Log.e(TAG, "onErrorResponse: RESPONSE_CODE "+e.getMessage());
                }


                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        Log.d(TAG + "Result Error:", res.toString());
                        mResponse.put(RESULT, response);

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
                sendRequest();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (mHeader != null)
                    return mHeader;
                else return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (mRequest != null)
                    return mRequest;
                else return super.getParams();
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Log.d(TAG, "getByteData: ");
                HashMap<String, DataPart> dataPartHashMap = new HashMap<>();
                for (String key : dataMultiPartRequest.keySet())
                    dataPartHashMap.put(key, new DataPart(key + ".png", dataMultiPartRequest.get(key), "image/png"));
                return dataPartHashMap;
            }

            @Override
            protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
                Log.d(TAG, "parseNetworkResponse: ");
                Log.d(TAG, "parseNetworkResponse: response " + response.toString());
                Log.d(TAG, "parseNetworkResponse: statusCode " + response.statusCode);
                mResponse.put(RESPONSE_CODE, response.statusCode);
                return super.parseNetworkResponse(response);
            }
        };


        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RootApplcation.getmRootApplcation().addToRequestQueue(multipartRequest);

    }



    private void getConnection() {
        handlerUrl();

        Log.d(TAG, "getConnection: url "+getMethodName(mRequestAPI.getRequestMethod())+"  "+mRequestAPI.getValue());
        StringRequest strReq = new StringRequest(mRequestAPI.getRequestMethod(),
                mRequestAPI.getValue(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mResponse.put(RESULT, response);
                Log.d(TAG, "onResponse : json " + mResponse.get(RESPONSE_CODE));
                Log.d(TAG, "onResponse : code " + mResponse.get(RESULT));
                sendRequest();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = "no error";
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                Log.e(TAG, "onErrorResponse : error " +message);
                try {
                    mResponse.put(RESPONSE_CODE, error.networkResponse.statusCode);
                } catch (Exception e) {
                    Log.e(TAG, "onErrorResponse: e "+e.getMessage() );
                }
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    try {

                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        Log.d(TAG, "onErrorResponse: HttpHeaderParser "+res.toString());
                        Log.d(TAG, "onErrorResponse:  statusCode "+response.statusCode);
                        mResponse.put(RESPONSE_CODE, response.statusCode);
                        mResponse.put(RESULT, res);

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                        Log.e(TAG, "onErrorResponse: UnsupportedEncodingException  "+e1.getMessage() );
                    }
                }
                Log.d(TAG, "onErrorResponse : url " + mRequestAPI.getValue());
                Log.d(TAG, "onErrorResponse : code " + mResponse.get(RESPONSE_CODE));
                Log.d(TAG, "onErrorResponse : json " + mResponse.get(RESULT));
                Log.e(TAG, "onErrorResponse : massage " + error.getMessage());
                sendRequest();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (mRequest != null)
                    return mRequest;
                else
                    return super.getParams();
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (mHeader != null)
                    return mHeader;
                return super.getHeaders();
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d(TAG, "parseNetworkResponse: ");
                Log.d(TAG, "parseNetworkResponse: response " + response.toString());
                Log.d(TAG, "parseNetworkResponse: statusCode " + response.statusCode);
                mResponse.put(RESPONSE_CODE, response.statusCode);
                return super.parseNetworkResponse(response);
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RootApplcation.getmRootApplcation().addToRequestQueue(strReq);
    }


    private void handlerUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        if (mRequestAPI.getValue().equals(RequestAPI.USER.getValue())) {
            if (!mRequest.containsKey(USER_ID))
                return;
            stringBuilder.append(DOMAIN_URL+"users/");
            stringBuilder.append(mRequest.get(USER_ID));
            mRequestAPI.setValue(stringBuilder.toString());
        }if (mRequestAPI.getValue().equals(RequestAPI.USER_UPDATE.getValue())) {
            if (!mRequest.containsKey(USER_ID))
                return;
            stringBuilder.append(DOMAIN_URL+"users/");
            stringBuilder.append(mRequest.get(USER_ID));
            mRequestAPI.setValue(stringBuilder.toString());
        }if (mRequestAPI.getValue().equals(RequestAPI.GROUP.getValue())) {
            if (!mRequest.containsKey(GROUP_ID))
                return;
            stringBuilder.append(DOMAIN_URL+"groups/");
            stringBuilder.append(mRequest.get(GROUP_ID));
            mRequestAPI.setValue(stringBuilder.toString());
        }if (mRequestAPI.getValue().equals(RequestAPI.GROUP_UPDATE.getValue())) {
            if (!mRequest.containsKey(GROUP_ID))
                return;
            stringBuilder.append(DOMAIN_URL+"groups/");
            stringBuilder.append(mRequest.get(GROUP_ID));
            mRequestAPI.setValue(stringBuilder.toString());
        }
        Log.d(TAG, "handlerUrl: mURL "+stringBuilder.toString());
    }


    private Map<String, String> initializeHeader() {
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        //header.put("lang", "en");
        if (isToken())
            header.put(Authorization,TOKEN_VALUE);
        return header;
    }

    private HashMap<String, Object> initializeHashMap() {
        HashMap<String, Object> result = new HashMap();
        result.put(RESPONSE_CODE, 0);
        result.put(RESULT, null);
        return result;
    }

    private void sendRequest() {

        switch ((int) mResponse.get(RESPONSE_CODE)) {
            case NO_INTERNET:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.NO_CONNECTION, mResponse);
                break;
            case OK:
                onResponding.onResponding(mRequestAPI, true, StatusConnection.SUCCESS, mResponse);
                break;
            case CREATED:
                onResponding.onResponding(mRequestAPI, true, StatusConnection.CREATED, mResponse);
                break;
            case BAD_REQUEST:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.BAD_REQUEST, mResponse);
                break;
            case UNAUTHORIZED:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.UNAUTHORIZED, mResponse);
                break;
            case FORBIDDEN:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.FORBIDDEN, mResponse);
                break;
            case NOT_FOUND:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.NOT_FOUND, mResponse);
                break;
            case CONFLICT:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.CREATED, mResponse);
                break;
            case VALIDATION_FAILED:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.VALIDATION_FAILED, mResponse);
                break;
            case SERVER_ERROR:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.INTERNAL_SERVER_ERROR, mResponse);
                break;
            default:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.OTHER, mResponse);
        }
    }


    private static final int NO_INTERNET =  0;
    private static final int OK =  200;
    private static final int CREATED =  201;
    private static final int ACCEPTED =  202;
    private static final int NO_CONTENT =  204;
    private static final int NOT_MODIFIED =  304;
    private static final int BAD_REQUEST =  400;
    private static final int UNAUTHORIZED =  401;
    private static final int FORBIDDEN =  403;
    private static final int NOT_FOUND =  404;
    private static final int UNSUPPORTED_ACTION =  405;
    private static final int CONFLICT =  409;
    private static final int VALIDATION_FAILED =  422;
    private static final int SERVER_ERROR = 500;

    public enum StatusConnection {
        SUCCESS,
        CREATED,
        BAD_REQUEST,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        UNSUPPORTED_ACTION,
        CONFLICT,
        VALIDATION_FAILED,
        NO_PRIVILEGE,
        NO_CONNECTION,
        INTERNAL_SERVER_ERROR,
        OTHER;
    }


    private String getMethodName(int method){
        switch (method){
            case 0:return "GET";
            case 1:return "POST";
            case 2:return "PUT";
            case 3:return "DELETE";
            default: return "NULL";
        }
    }


    public interface OnResponding {
        void onResponding(RequestAPI requestAPI, boolean IsSuccess, StatusConnection statusConnection, HashMap<String, Object> objectResult);
    }

}
