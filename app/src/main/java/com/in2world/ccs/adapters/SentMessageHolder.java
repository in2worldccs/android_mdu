package com.in2world.ccs.adapters;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.in2world.ccs.R;
import com.in2world.ccs.module.Message;

import java.text.SimpleDateFormat;


/**
 * Created by ayyad on 4/1/2019.
 */

public class SentMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText, nameText;
    ImageView profileImage;


    public SentMessageHolder(View itemView) {
        super(itemView);

        timeText = (TextView) itemView.findViewById(R.id.tv_time);
        messageText = (TextView) itemView.findViewById(R.id.tv_chat_text);

    }

    public void bind(String message) {
        messageText.setText(message);

        // Format the stored timestamp into a readable String using method.
        timeText.setText(getTimeStamp(System.currentTimeMillis()));
      //  nameText.setText(message.getSender().getNickname());

        // Insert the profile image from the URL into the ImageView.
        // Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
    }

    public static String getTimeStamp(long dateStr) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        String timestamp = "";


        // today = today.length() < 2 ? "0" + today : today;

        //   Date date = format.parse(dateStr);
        //    SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
        //    String dateToday = todayFormat.format(date);
        //     format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
        String date1 = format.format(dateStr);
        //timestamp = date1.toString();


        return date1;
    }


}