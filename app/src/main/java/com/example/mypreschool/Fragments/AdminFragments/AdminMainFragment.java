package com.example.mypreschool.Fragments.AdminFragments;


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
    private Button btnSchoolAdmin, btnTeacherAdmin, btnParentAdmin, btnStudentAdmin, btnAddAnnouncement;

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
        btnAddAnnouncement = view.findViewById(R.id.btnAddAnnouncement);

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
        btnSchoolAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSchoolAdminFragment();
            }
        });
        btnTeacherAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTeacherAdminFragment();
            }
        });
        btnAddAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAdminAnnouncementFragment();
            }
        });

        return view;
    }

    private void goAdminAnnouncementFragment(){
        Fragment hedef = new AdminAddAnnouncementFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flMainActivity, hedef);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void goSchoolAdminFragment(){
        Fragment hedef = new AdminSchoolFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flMainActivity, hedef);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void goTeacherAdminFragment(){
        Fragment hedef = new AdminTeacherFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flMainActivity, hedef);
        ft.addToBackStack(null);
        ft.commit();
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
