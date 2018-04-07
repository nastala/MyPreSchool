package com.example.mypreschool.Fragments.TeacherFragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherMainFragment extends Fragment {
    private Button btnShareActivity, btnFoodList, btnRequestPermission, btnGivenPermissions;
    private Teacher teacher;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public TeacherMainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_main, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnShareActivity = view.findViewById(R.id.btnShareActivity);
        btnFoodList = view.findViewById(R.id.btnFoodList);
        btnRequestPermission = view.findViewById(R.id.btnRequestPermission);
        btnGivenPermissions = view.findViewById(R.id.btnGivenPermissions);

        btnShareActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherShareActivityFragment hedef = new TeacherShareActivityFragment();
                hedef.setTeacher(teacher);
                ekranaGit(hedef);
            }
        });

        btnFoodList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherMenuListFragment hedef = new TeacherMenuListFragment();
                hedef.setTeacher(teacher);
                ekranaGit(hedef);
            }
        });

        btnRequestPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeacherRequestPermissionFragment hedef = new TeacherRequestPermissionFragment();
                hedef.setTeacher(teacher);
                ekranaGit(hedef);
            }
        });

        btnGivenPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeacherGivenPermissionsFragment hedef = new TeacherGivenPermissionsFragment();
                hedef.setTeacher(teacher);
                ekranaGit(hedef);
            }
        });

        return view;
    }

    private void ekranaGit(Fragment hedef){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.flTeacherMain, hedef);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setTeacher(Teacher teacher){ this.teacher = teacher; }
}
