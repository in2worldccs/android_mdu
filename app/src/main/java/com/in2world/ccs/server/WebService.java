package com.in2world.ccs.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import static com.in2world.ccs.tools.GlobalData.TOKEN_VALUE;

public class WebService {

    public static final String TAG = "Api";
    private Context context;
    private Result Result;

    public static String RESULT = "result";
    public static String RESPONSE_CODE = "status_code";
    public static String Authorization = "Authorization";
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
    private final static String DOMAIN_URL = "http://ibrahemayyad.ga:3007/api/v1/";

    public enum RequestAPI {

        //Auth
        LOGIN(DOMAIN_URL + "auth/login", Request.Method.POST),


        //Users
        USERS(DOMAIN_URL + "users", Request.Method.POST),
        USER(DOMAIN_URL + "USER", Request.Method.GET);

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
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                VolleyLog.d(TAG, "Error Message: " + message);
                try {
                    mResponse.put(RESPONSE_CODE, error.networkResponse.statusCode);
                } catch (Exception e) {

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
        String newURL = mRequestAPI.getValue();


       handlerUrl(newURL);


        StringRequest strReq = new StringRequest(mRequestAPI.getRequestMethod(),
                newURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mResponse.put(RESULT, response);
                VolleyLog.d(TAG, "onResponse : json " + mResponse.get(RESPONSE_CODE));
                VolleyLog.d(TAG, "onResponse : code " + mResponse.get(RESULT));
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
                try {
                    mResponse.put(RESPONSE_CODE, error.networkResponse.statusCode);
                } catch (Exception e) {

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



                VolleyLog.d(TAG, "onErrorResponse : url " + mRequestAPI.getValue());
                VolleyLog.d(TAG, "onErrorResponse : code " + mResponse.get(RESPONSE_CODE));
                VolleyLog.d(TAG, "onErrorResponse : json " + mResponse.get(RESULT));
                VolleyLog.e(TAG, "onErrorResponse : massage " + error.getMessage());
                VolleyLog.e(TAG, "onErrorResponse : error " +message);

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
                else return super.getHeaders();
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d(TAG, "parseNetworkResponse: ");
                Log.d(TAG, "parseNetworkResponse: response " + response.toString());
                Log.d(TAG, "parseNetworkResponse: statusCode " + response.statusCode);
                mResponse.put(RESPONSE_CODE, response.statusCode);
                return super.parseNetworkResponse(response);
            }

//            @Override
//            public String getBodyContentType() {
//                return "application/json";
//            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RootApplcation.getmRootApplcation().addToRequestQueue(strReq);
    }


    private void handlerUrl(String mURL) {
        StringBuilder stringBuilder = new StringBuilder();
        if (mRequestAPI.getValue().equals(RequestAPI.USER.getValue())) {
            if (!mRequest.containsKey("user_id"))
                return;
            mURL = DOMAIN_URL + "users";
            stringBuilder.append("/");
            stringBuilder.append(mRequest.get("user_id"));
            mURL += stringBuilder.toString();
            mRequestAPI.setValue(mURL);
        }
        Log.d(TAG, "handlerUrl: mURL "+mURL);
    }


   /* public WebService(RequestAPI requestAPI, HashMap<String, String> dataRequest, OnResponding onResponding) {
        this.onResponding = onResponding;
        mRequestAPI = requestAPI;
        mHeader = initializeHeader();
        mRequest = dataRequest;
        mResponse = initializeHashMap();
        if (!ValidationHelper.validObject(mRequest))
            mRequest = new HashMap<>();

        Log.d(TAG ,"WebService : mRequestAPI " + mRequestAPI.toString());
        Log.d(TAG ,"WebService : mHeader " + mHeader.toString());
        Log.d(TAG ,"WebService : mRequest " + mRequest.toString());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("message", "Message");

        //Simply put a byte[] to the params, AQuery will detect it and treat it as a multi-part post
        byte[] data = getImageData();
        params.put("source", data);

        //Alternatively, put a File or InputStream instead of byte[]
        //File file = getImageFile();
        //params.put("source", file);

        AQuery aq = new AQuery(getApplicationContext());
        aq.auth(handle).ajax(url, params, JSONObject.class, this, "photoCb");
    }*/


    private Map<String, String> initializeHeader() {
        Map<String, String> header = new HashMap<String, String>();
        header.put("Accept", "application/json");
        header.put("lang", "en");
        if (GlobalData.IS_TOKEN())
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
            case -101:
            case 0:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.NO_CONNECTION, mResponse);
                break;
            case 200:
                onResponding.onResponding(mRequestAPI, true, StatusConnection.SUCCESS, mResponse);
                break;
            case 201:
                onResponding.onResponding(mRequestAPI, true, StatusConnection.CREATED, mResponse);
                break;
            case 400:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.BAD_REQUEST, mResponse);
                break;
            case 401:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.UNAUTHORIZED, mResponse);
                break;
            case 403:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.FORBIDDEN, mResponse);
            case 404:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.NOT_FOUND, mResponse);
                break;
            case 409:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.CREATED, mResponse);
                break;
            case 422:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.VALIDATION_FAILED, mResponse);
                break;
            case 500:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.INTERNAL_SERVER_ERROR, mResponse);
                break;
            default:
                onResponding.onResponding(mRequestAPI, false, StatusConnection.OTHER, mResponse);
        }
    }

    /*
    * const Status = {
        OK: 200,
        CREATED: 201,
        ACCEPTED: 202,
        NO_CONTENT: 204,
        NOT_MODIFIED: 304,
        BAD_REQUEST: 400,
        UNAUTHORIZED: 401,
        FORBIDDEN: 403,
        NOT_FOUND: 404,
        UNSUPPORTED_ACTION: 405,
        VALIDATION_FAILED: 422,
        SERVER_ERROR: 500
    };
    * */
    public enum StatusConnection {
        SUCCESS,
        CREATED,
        BAD_REQUEST,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        UNSUPPORTED_ACTION,
        VALIDATION_FAILED,
        NO_PRIVILEGE,
        NO_CONNECTION,
        INTERNAL_SERVER_ERROR,
        OTHER;
    }


    public interface OnResponding {
        void onResponding(RequestAPI requestAPI, boolean IsSuccess, StatusConnection statusConnection, HashMap<String, Object> objectResult);
    }

    public interface Result {
        public void onResult(Object object, String function, boolean IsSuccess, int RequestStatus, String MessageStatus);

    }
}
