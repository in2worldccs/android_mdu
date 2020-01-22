package com.in2world.ccs.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.in2world.ccs.R;
import com.in2world.ccs.module.Message;
import com.in2world.ccs.tools.GlobalData;
import com.in2world.ccs.ui.ChatActivity;

import java.util.List;

/**
 * Created by ayyad on 4/1/2019.
 */

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final String TAG = "MessageListAdapter";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context mContext;
    private List<String> mMessageList;

    private int mPosition =0;
    public MessageListAdapter(Context context, List<String> messageList) {
        Log.d(TAG, "MessageListAdapter: ");
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        Log.d(TAG, "onCreateViewHolder: viewType "+viewType);
       if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_holder_me, parent, false);
            return new SentMessageHolder(view);

        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_holder_you, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String message = mMessageList.get(position);

       // Log.d(TAG, "onBindViewHolder: user "+message.getUserId() + " message "+message.getMessage());
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(mContext,message);
        }
    }



    @Override
     public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: position "+position);

         String message = mMessageList.get(position);
         if (GlobalData.mProfile.getUsername().equals(ChatActivity.usernmaeW)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
        /*if (message.getSender().getUserId() == VIEW_TYPE_MESSAGE_SENT) {
             // If the current user is the sender of the message
               return VIEW_TYPE_MESSAGE_SENT;
         } else {
             // If some other user sent the message
             return VIEW_TYPE_MESSAGE_RECEIVED;
         }*/
     }

}