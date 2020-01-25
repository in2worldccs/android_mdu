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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.in2world.ccs.Database.SaveData;
import com.in2world.ccs.R;
import com.in2world.ccs.helper.MessageHelper;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.module.Auth;
import com.in2world.ccs.module.Data;
import com.in2world.ccs.module.Response;
import com.in2world.ccs.server.WebService;
import com.in2world.ccs.socket.SocketClient;
import com.in2world.ccs.socket.SocketIO;
import com.in2world.ccs.tools.GlobalData;
import com.in2world.ccs.ui.fragment.GroupsFragment;
import com.in2world.ccs.ui.fragment.UsersFragment;

import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.in2world.ccs.server.WebService.RESULT;
import static com.in2world.ccs.server.WebService.StatusConnection.BAD_REQUEST;
import static com.in2world.ccs.server.WebService.StatusConnection.INTERNAL_SERVER_ERROR;
import static com.in2world.ccs.server.WebService.StatusConnection.NO_CONNECTION;
import static com.in2world.ccs.server.WebService.StatusConnection.UNAUTHORIZED;
import static com.in2world.ccs.server.WebService.StatusConnection.VALIDATION_FAILED;
import static com.in2world.ccs.tools.GlobalData.CALL_NUMBER;
import static com.in2world.ccs.tools.GlobalData.CALL_STATUS;
import static com.in2world.ccs.tools.GlobalData.OUT_COMING;
import static com.in2world.ccs.tools.GlobalData.PROFILE_KEY;
import static com.in2world.ccs.tools.GlobalData.TOKEN_KEY;
import static com.in2world.ccs.tools.GlobalData.TOKEN_VALUE;
import static com.in2world.ccs.tools.GlobalData.mProfile;
import static com.in2world.ccs.tools.GlobalData.saveDataSIP;
import static com.in2world.ccs.tools.GlobalData.userList;

public class MainActivity extends AppCompatActivity implements WebService.OnResponding {

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

    @Override
    public void onResponding(WebService.RequestAPI requestAPI, boolean IsSuccess, WebService.StatusConnection statusConnection, HashMap<String, Object> objectResult) {
        Log.w(TAG, "onResponding: requestAPI " + requestAPI.toString());
        Log.w(TAG, "onResponding: statusConnection " + statusConnection);
        Log.w(TAG, "onResponding: dataResult " + objectResult.toString());

        try {
            if (requestAPI.equals(WebService.RequestAPI.USERS)) {
                if (IsSuccess) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());
                    if (result.getResult() == Response.SUCCESS) {

                        userList = result.getDataResponse().getUserList();

                        if (ValidationHelper.validList(userList)) {
                            Log.e(TAG, "onResponding: size "+userList.size() );
                            usersFragment.addData();
                        }

                    }
                } else if (statusConnection == NO_CONNECTION) {
                    Toast.makeText(this, getResources().getString(R.string.NO_CONNECTION), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == BAD_REQUEST) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == UNAUTHORIZED) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == VALIDATION_FAILED) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == INTERNAL_SERVER_ERROR) {
                    Toast.makeText(this, "حدث خطأ في الخادم ... جاري الأصلاح LOGIN ", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onResponding: Exception " + e.getMessage());
            Log.d(TAG, "onResponding: Exception getLocalizedMessage " + e.getLocalizedMessage());
            MessageHelper.AppDialog(this, R.string.error_connection, e.getMessage());
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        SocketIO.getInstance().getSocket().disconnect();
    }
}
