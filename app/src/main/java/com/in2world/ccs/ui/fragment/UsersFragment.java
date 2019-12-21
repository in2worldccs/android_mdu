package com.in2world.ccs.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.in2world.ccs.R;

public class UsersFragment extends Fragment {
    private TextView txtUser;
    private RecyclerView rvUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.users_fragment, container, false);


        initView(view);

        return view;
    }

    private void initView(View view) {


        txtUser = view.findViewById(R.id.txtUser);
        rvUsers = view.findViewById(R.id.rvUsers);
        rvUsers.setVisibility(View.GONE);
    }

}