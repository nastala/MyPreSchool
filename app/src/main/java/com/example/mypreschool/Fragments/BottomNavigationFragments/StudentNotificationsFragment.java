package com.example.mypreschool.Fragments.BottomNavigationFragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentNotificationsFragment extends Fragment {
    private LinearLayout btnAnnouncement, btnRequestPermission;
    private Student student;

    public StudentNotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_notifications, container, false);

        btnAnnouncement = view.findViewById(R.id.btnAnnouncement);
        btnRequestPermission = view.findViewById(R.id.btnRequestPermission);

        btnAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentAnnouncementFragment hedef = new StudentAnnouncementFragment();
                hedef.setStudent(student);
                goScreen(hedef);
            }
        });

        btnRequestPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentPermissionRequestFragment hedef = new StudentPermissionRequestFragment();
                hedef.setStudent(student);
                goScreen(hedef);
            }
        });

        return view;
    }

    private void goScreen(Fragment hedef){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flStudentMain, hedef).addToBackStack(null).commit();
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
