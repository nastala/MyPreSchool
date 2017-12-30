package com.example.mypreschool.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.mypreschool.Adapters.StudentAdapter;
import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.R;
import com.example.mypreschool.StudentMainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SelectStudentScreenFragment extends Fragment {
    private static final String TAG = "SELECTSTUDENTSCREEN";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference mStorage;
    private ListView lvStudents;
    private ProgressBar pbListStudents;
    private ArrayList<Student> students;

    public SelectStudentScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_student_screen, container, false);

        students = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        lvStudents = view.findViewById(R.id.lvStudents);
        pbListStudents = view.findViewById(R.id.pbListStudent);
        Log.d(TAG, mAuth.getUid());

        lvStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), StudentMainActivity.class);
                i.putExtra("student", students.get(position));
                startActivity(i);
            }
        });

        bringStudents();
        return view;
    }

    private void bringStudents(){
        if(mAuth.getUid() == null)
            return;

        pbListStudents.setVisibility(View.VISIBLE);
        String uid = mAuth.getUid();
        db.collection("Students").whereEqualTo("parentID", uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if(documentSnapshots == null)
                    return;

                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    final Student student = new Student();
                    student.setName(documentSnapshot.getString("name"));
                    student.setSgurl(documentSnapshot.getString("sgurl"));
                    student.setClassID(documentSnapshot.getString("classID"));
                    student.setParentID(documentSnapshot.getString("parentID"));
                    student.setSchoolID(documentSnapshot.getString("schoolID"));
                    Log.d(TAG, student.getClassID());

                    students.add(student);
                }
                StudentAdapter adapter = new StudentAdapter(getActivity(), students);
                lvStudents.setAdapter(adapter);
                pbListStudents.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "BRING STUDENT ON FAILURE: " + e.getMessage());
                pbListStudents.setVisibility(View.GONE);
            }
        });
    }
}
