package com.example.mypreschool.Fragments.BottomNavigationFragments;


import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.R;
import com.example.mypreschool.SharedPref;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentMainFragment extends Fragment {
    private final String TAG = "STUDENTMAIN";

    private Button btnStatus;
    private Student student;
    private FirebaseFirestore db;

    public StudentMainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_main, container, false);

        db = FirebaseFirestore.getInstance();

        btnStatus = view.findViewById(R.id.btnStatus);
        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentCalendarFragment hedef = new StudentCalendarFragment();
                hedef.setStudent(student);
                ekraniGetir(hedef);
            }
        });

        return view;
    }

    private void ekraniGetir(Fragment hedef){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.flStudentMain, hedef);
        fragmentTransaction.addToBackStack(hedef.getTag());
        fragmentTransaction.commit();
    }

    public void setStudent(Student student) { this.student = student; }
}
