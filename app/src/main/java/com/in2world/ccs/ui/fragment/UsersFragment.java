package com.in2world.ccs.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.in2world.ccs.R;
import com.in2world.ccs.adapters.UsersAdapter;
import com.in2world.ccs.ui.DialerActivity;
import com.in2world.ccs.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.in2world.ccs.tools.GlobalData.SIP_username;

public class UsersFragment extends Fragment {
    private TextView txtUser;
    private RecyclerView rvUsers;
    UsersAdapter usersAdapter;
    List<String> listUsers = new ArrayList<>();
    DialerActivity dialerActivity;
    LinearLayoutManager layoutManager;
    MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.users_fragment, container, false);

        activity = (MainActivity) getActivity();

        initView(view);

        return view;
    }

    private void initView(View view) {

        txtUser = view.findViewById(R.id.txtUser);
        rvUsers = view.findViewById(R.id.rvUsers);
        txtUser.setVisibility(View.GONE);
        layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvUsers.setLayoutManager(layoutManager);

        addData();
    }

    private void addData() {
        listUsers = new ArrayList<>();
        listUsers.add("8001");
        listUsers.add("8002");
        listUsers.add("8003");
        listUsers.add("8004");
        listUsers.add("8005");
        listUsers.add("8006");


        /*
        *
        *
        * remove my accunt from users
        */
        for (int i = 0; i < listUsers.size(); i++) {
            if (listUsers.get(i).equals(SIP_username)) {
                listUsers.remove(i);
                break;
            }
        }

        usersAdapter = new UsersAdapter(activity, listUsers);
        rvUsers.setAdapter(usersAdapter);

    }

}