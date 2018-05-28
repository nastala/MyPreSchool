package com.example.mypreschool.Fragments.TeacherFragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherMainFragment extends Fragment {
    private LinearLayout btnShareActivity, btnFoodList, btnRequestPermission, btnGivenPermissions, btnSleepState;
    private CircleImageView civTeacher;
    private TextView tvTeacherName;
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
        btnSleepState = view.findViewById(R.id.btnSleepState);
        civTeacher = view.findViewById(R.id.civTeacher);
        tvTeacherName = view.findViewById(R.id.tvTeacherName);

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

        btnSleepState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeacherStudentSleepStateFragment hedef = new TeacherStudentSleepStateFragment();
                hedef.setTeacher(teacher);
                ekranaGit(hedef);
            }
        });

        Glide.with(civTeacher.getContext())
                .load(teacher.getTeacherPhoto())
                .into(civTeacher);

        tvTeacherName.setText(teacher.getTeacherName());

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
