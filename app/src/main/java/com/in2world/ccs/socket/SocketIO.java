package com.in2world.ccs.socket;

import android.annotation.SuppressLint;
import android.util.Log;

import com.in2world.ccs.Database.SaveData;
import com.in2world.ccs.helper.Constants;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.tools.GlobalData;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.in2world.ccs.helper.Constants.SOCKET_SERVER_URL;
import static com.in2world.ccs.tools.GlobalData.TOKEN_VALUE;
import static com.in2world.ccs.tools.GlobalData.mProfile;
import static com.in2world.ccs.tools.GlobalData.mUser;

public class SocketIO {
    private static final String TAG = "SocketIO";
    public static SocketIO instance;

    private static Socket mSocket;

    public Socket getSocket() {
        return mSocket;
    }

    public static SocketIO getInstance() {
        if (instance == null) {
            synchronized (SocketIO.class) {
                if (instance == null) {
                    instance = new SocketIO();
                }
            }
        }
        return instance;
    }

    public static void init() {

        Log.d(TAG, "init: ");
        if (!ValidationHelper.validString(TOKEN_VALUE))
            return;
        if (!ValidationHelper.validObject(GlobalData.mProfile))
            return;
        try {
            HostnameVerifier myHostnameVerifier = new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Log.d(TAG, "verify: hostname " + hostname);
                    Log.d(TAG, "verify: session Id " + Arrays.toString(session.getId()));
                    Log.d(TAG, "verify: session Valid " + session.isValid());
                    Log.d(TAG, "verify: session Protocol " + session.getProtocol());
                    Log.d(TAG, "init: mSocket connect : "+mSocket.connected());
                    Log.d(TAG, "init: mSocket id : "+mSocket.id());
                    return true;
                }
            };
            TrustManager[] trustAllCerts= new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) {
                    Log.d(TAG, "checkClientTrusted: authType "+authType);
                }
                public void checkServerTrusted(X509Certificate[] chain, String authType)  {
                    Log.d(TAG, "checkServerTrusted: authType "+authType);
                }
            } };

            SSLContext mySSLContext = SSLContext.getInstance("TLS");
            mySSLContext.init(null, trustAllCerts, null);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(myHostnameVerifier)
                    .sslSocketFactory(mySSLContext.getSocketFactory())
                    .build();
            //default settings for all sockets
            IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
            IO.setDefaultOkHttpCallFactory(okHttpClient);
            // set as an option
            IO.Options opts = new IO.Options();
            opts.callFactory = okHttpClient;
            opts.webSocketFactory = okHttpClient;
            JSONObject dataJSON = new JSONObject();
            dataJSON.put("auth_token", mUser.getId());
            dataJSON.put("user_token", TOKEN_VALUE);
            opts.query = "auth_token="+mProfile.getId();
            mSocket = IO.socket(SOCKET_SERVER_URL, opts);
            mSocket.connect();
        }catch (URISyntaxException e) {
            Log.e(TAG, "init: URISyntaxException "+ e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "init: NoSuchAlgorithmException "+ e.getMessage());
        } catch (KeyManagementException e) {
            Log.e(TAG, "init: KeyManagementException "+ e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (instance == null)
            instance = new SocketIO();
    }


    private void listenerAction(String event){


    }
}
