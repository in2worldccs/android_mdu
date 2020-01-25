package com.in2world.ccs.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.in2world.ccs.R;
import com.in2world.ccs.module.Group;
import com.in2world.ccs.module.User;
import com.in2world.ccs.tools.GlobalData;
import com.in2world.ccs.ui.ChatActivity;
import com.in2world.ccs.ui.DialerActivity;

import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyViewHolder> {

    private static final String TAG = "AppStatisticsAdabter";
    private List<Group> objectList;
    private LayoutInflater inflater;
    String password = "";
    Context context;

    private final static int FADE_DURATION = 400; // in milliseconds

    public GroupsAdapter(Context context, List<Group> objectList) {
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
        final Group current = objectList.get(position);
        holder.setData(current, position);
        //   setAnimation(holder.itemView, position);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                GlobalData.mGroup = current;
                GlobalData.ChatStatus = 2;
                context.startActivity(new Intent(context, ChatActivity.class));
                //context.startActivity(new Intent(context, ChatUsersActivity.class));
                Toast.makeText(context, current.getName(), Toast.LENGTH_SHORT).show();
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

        public void setData(final Group item, final int position) {

            txtTitle.setText(item.getName());
            call.setVisibility(View.GONE);

//            call.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    context.startActivity(new Intent(context, ChatUsersActivity.class));
//                }
//            });
        }


    }
}