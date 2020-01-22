package com.in2world.ccs.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText, nameText;
    ImageView profileImage;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);
      /*  messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        nameText = (TextView) itemView.findViewById(R.id.text_message_name);
        profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
*/
        profileImage = (ImageView) itemView.findViewById(R.id.image);
        nameText = (TextView) itemView.findViewById(R.id.tv_chat_name);
        messageText = (TextView) itemView.findViewById(R.id.tv_chat_text);
        timeText = (TextView) itemView.findViewById(R.id.tv_time);
    }

    public void bind(Context context , String message) {
        messageText.setText(message);

        // Format the stored timestamp into a readable String using method.
        timeText.setText(getTimeStamp(System.currentTimeMillis()));
  //      nameText.setText(message.getNickname());


      /*  if (message.getID() == 1){
            if (GlobalData.mParent.getPGender().equals("male")){
                profileImage.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_manager_m));
            }else {
                profileImage.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_woman_m));
            }
        }else {
            profileImage.setImageDrawable(AppUtil.getImage(context, message.getSender().getProfileUrl()));

        }*/
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