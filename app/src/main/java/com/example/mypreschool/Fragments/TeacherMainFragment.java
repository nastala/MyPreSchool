package com.example.mypreschool.Fragments;


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
    private Button btnShareActivity;
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
        btnShareActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherShareActivityFragment hedef = new TeacherShareActivityFragment();
                hedef.setTeacher(teacher);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.flMainActivity, hedef);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnShareActivity.setClickable(false);

        teacherDetaylariGetir();

        return view;
    }

    private void teacherDetaylariGetir(){
        teacher = new Teacher();

        db.collection("Teachers").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists())
                    return;

                teacher.setTeacherSchoolID(documentSnapshot.getString("schoolID"));
                teacher.setTeacherEmail(documentSnapshot.getString("email"));
                teacher.setTeacherName(documentSnapshot.getString("name"));
                teacher.setTeacherPhoneNumber(documentSnapshot.getString("phoneNumber"));
                teacher.setTeacherClassID("classID");
                btnShareActivity.setClickable(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TEACHERMAIN", "TEACHER GETIRME HATA: " + e.getMessage());
            }
        });
    }

}
