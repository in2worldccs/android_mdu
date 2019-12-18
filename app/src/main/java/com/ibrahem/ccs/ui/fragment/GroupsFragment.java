package com.ibrahem.ccs.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahem.ccs.R;

public class GroupsFragment extends Fragment {

    private TextView txtGroup;
    private RecyclerView rvGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_fragment, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {

        txtGroup = view.findViewById(R.id.txtGroup);
        rvGroup = view.findViewById(R.id.rvGroup);
        rvGroup.setVisibility(View.GONE);

    }


}
