package com.ibrahem.ccs.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.ibrahem.ccs.R;
import com.ibrahem.ccs.helper.ValidationHelper;
import com.ibrahem.ccs.ui.fragment.GroupsFragment;
import com.ibrahem.ccs.ui.fragment.UsersFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    UsersFragment usersFragment;
    GroupsFragment groupsFragment;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewAdapter adapter;
    private void initView() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);

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


}
