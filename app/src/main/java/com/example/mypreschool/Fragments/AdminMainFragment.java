package com.example.mypreschool.Fragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mypreschool.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminMainFragment extends Fragment {
    private Button btnSchoolAdmin, btnTeacherAdmin, btnParentAdmin, btnStudentAdmin;

    public AdminMainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_main, container, false);

        btnSchoolAdmin = view.findViewById(R.id.btnSchoolAdmin);
        btnTeacherAdmin = view.findViewById(R.id.btnTeacherAdmin);
        btnParentAdmin = view.findViewById(R.id.btnParentAdmin);
        btnStudentAdmin = view.findViewById(R.id.btnStudentAdmin);

        btnParentAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goParentAdminFragment();
            }
        });
        btnStudentAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goStudentAdminFragment();
            }
        });

        return view;
    }

    private void goParentAdminFragment(){
        Fragment hedef = new AdminParentFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flMainActivity, hedef);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void goStudentAdminFragment(){
        Fragment hedef = new AdminStudentFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flMainActivity, hedef);
        ft.addToBackStack(null);
        ft.commit();
    }

}
