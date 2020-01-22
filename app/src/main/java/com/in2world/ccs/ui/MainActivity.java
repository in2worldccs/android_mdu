package com.in2world.ccs.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.in2world.ccs.Database.SaveData;
import com.in2world.ccs.R;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.module.Auth;
import com.in2world.ccs.module.Data;
import com.in2world.ccs.socket.SocketClient;
import com.in2world.ccs.ui.fragment.GroupsFragment;
import com.in2world.ccs.ui.fragment.UsersFragment;

import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.in2world.ccs.tools.GlobalData.CALL_NUMBER;
import static com.in2world.ccs.tools.GlobalData.CALL_STATUS;
import static com.in2world.ccs.tools.GlobalData.OUT_COMING;
import static com.in2world.ccs.tools.GlobalData.mProfile;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    UsersFragment usersFragment;
    GroupsFragment groupsFragment;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewAdapter adapter;

    private SocketClient socket;

    private void initView() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);
        initSocket();
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void init() {
        ViewAdapter adapter = new ViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Users"));
        tabLayout.addTab(tabLayout.newTab().setText("Groups"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        Auth auth = new Auth(mProfile.getUsername(), mProfile.getUsername());
        ChatActivity.Nickname = mProfile.getUsername();

        Data data = new Data("LOGIN", auth);
        sendMessage(data);

    }
    private void sendMessage(Data data) {

        SaveData.getInstance().saveObject("object", data);
        String json = SaveData.getInstance().getString("object");


        try {
            Log.d(TAG, "sendMessage: sendMessage " + json);
            socket.send(json);

        } catch (WebsocketNotConnectedException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void initSocket() {

        try {
            System.out.print("start\n");
            socket = new SocketClient(this, new URI("ws://ibrahemayyad.ga:8050/"));
            socket.connect();
            Log.d(TAG, "initSocket: ");
            Log.d(TAG, "initSocket: isOpen " + socket.isOpen());

        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "initSocket: e " + e);
        }
    }

    class ViewAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem: position " + position);

            if (!ValidationHelper.validObject(usersFragment))
                usersFragment = new UsersFragment();


            if (position == 1) {
                if (!ValidationHelper.validObject(groupsFragment))
                    groupsFragment = new GroupsFragment();
                return groupsFragment;
            }

            return usersFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {

            Log.d(TAG, "getPageTitle: position " + position);

            switch (position) {
                case 0:
                    return "Users";
                case 1:
                    return "Groups";
                default:
                    return "End";
            }

            // public CharSequence getPageTitle(int position) {
            //     switch (position) {
            //         case 0 : return "All";
            //         case 1 : return "Articles";
            //         case 2 : return "Interviews";
            //         case 3 : return "News";
            //         case 4 : return "Events";
            //         case 5 : return "Links";
            //         default:  return "End";
            //     }
            // }

        }
    }


    public void onMessage() {
        startActivity(new Intent(this, ChatActivity.class));

    }


}
