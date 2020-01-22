package com.in2world.ccs.socket;

import android.app.Activity;
import android.util.Log;

import com.in2world.ccs.Database.SaveData;
import com.in2world.ccs.module.Data;
import com.in2world.ccs.ui.ChatActivity;
import com.in2world.ccs.ui.MainActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class SocketClient extends WebSocketClient {

    private static final String TAG = "SocketClient";


    Activity activity;

    public SocketClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public SocketClient(Activity activity , URI serverURI) {
        super(serverURI);
        Log.d(TAG, "SocketClient: serverURI "+serverURI.getHost());
        this.activity= activity;

    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("Hello, it is me. Mario :)");
        Log.w(TAG, "onOpen: new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.w(TAG, "onClose: closed with exit code " + code + " additional info: " + reason);
    }


    @Override
    public void onMessage(final String message) {
        Log.d(TAG, "onMessage: received message:a " + message);
        //client_s.set_v(message);

        if (message.equals("login_successfully")) {
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.onMessage();
            return;
        }
        SaveData.getInstance().saveString("object",message);
        Data json = SaveData.getInstance().getObject("object", Data.class);
        json.getMessage().setUserId(2);
        ChatActivity chatActivity = (ChatActivity) activity;
        chatActivity.onMessage(json);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        Log.d(TAG, "onMessage: received ByteBuffer message "+message.toString());
    }

    @Override
    public void onError(Exception ex) {
        Log.e(TAG, "onError: an error occurred:" + ex );
    }


}