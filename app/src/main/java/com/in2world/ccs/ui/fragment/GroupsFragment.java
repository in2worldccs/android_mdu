package com.in2world.ccs.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.in2world.ccs.R;
import com.in2world.ccs.adapters.GroupsAdapter;
import com.in2world.ccs.adapters.UsersAdapter;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.module.Group;
import com.in2world.ccs.module.User;
import com.in2world.ccs.tools.GlobalData;
import com.in2world.ccs.ui.DialerActivity;
import com.in2world.ccs.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {
    private static final String TAG = "GroupsFragment";
    private TextView txtGroup;
    private RecyclerView rvGroup;
    GroupsAdapter groupsAdapter;
    LinearLayoutManager layoutManager;
    MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_fragment, container, false);
        activity = (MainActivity) getActivity();
        initView(view);
        return view;
    }
    private void initView(View view) {
        txtGroup = view.findViewById(R.id.txtGroup);
        rvGroup = view.findViewById(R.id.rvGroup);
        layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvGroup.setLayoutManager(layoutManager);
        txtGroup.setVisibility(View.GONE);
        rvGroup.setVisibility(View.VISIBLE);
        addData();
    }
    private void addData() {
        if (!ValidationHelper.validList(GlobalData.groupList)){
            txtGroup.setVisibility(View.VISIBLE);
            rvGroup.setVisibility(View.GONE);
            return;
        }
        Log.d(TAG, "addData: groupList "+GlobalData.groupList.size());
        groupsAdapter = new GroupsAdapter(activity, GlobalData.groupList);
        rvGroup.setAdapter(groupsAdapter);
    }

}
