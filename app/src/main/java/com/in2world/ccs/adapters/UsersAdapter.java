package com.in2world.ccs.adapters;

import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.in2world.ccs.R;
import com.in2world.ccs.ui.DialerActivity;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    private static final String TAG = "AppStatisticsAdabter";
    private List<String> objectList;
    private LayoutInflater inflater;
    String password = "";
    Context context;

    private final static int FADE_DURATION = 400; // in milliseconds

    public UsersAdapter(Context context, List<String> objectList) {
        inflater = LayoutInflater.from(context);
        this.objectList = objectList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_users, parent, false);
        //View view = inflater.inflate(R.layout.item_list, parent, false);
        MyViewHolder holder = new MyViewHolder(view);


        return holder;
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final String current = objectList.get(position);
        holder.setData(current, position);
        //   setAnimation(holder.itemView, position);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                Toast.makeText(context, current, Toast.LENGTH_SHORT).show();
            }
        });

    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private ImageView call;
        private ImageView chat;


        public MyViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title);
            call = itemView.findViewById(R.id.call);
            chat = itemView.findViewById(R.id.chat);


        }

        public void setData(final String item, final int position) {

            txtTitle.setText(item);


            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialerActivity.sendCall(context, item);
                }
            });
        }


    }
}
