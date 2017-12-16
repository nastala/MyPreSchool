package com.example.mypreschool.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mypreschool.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminSchoolFragment extends Fragment {


    public AdminSchoolFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_school, container, false);



        return view;
    }

}
