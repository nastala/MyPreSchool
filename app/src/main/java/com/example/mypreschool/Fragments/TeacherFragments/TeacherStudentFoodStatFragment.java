package com.example.mypreschool.Fragments.TeacherFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherStudentFoodStatFragment extends Fragment {
    private final String TAG = "TEACHERSTUDENTFOODSTAT";

    private FirebaseFirestore db;
    private Teacher teacher;
    private ArrayList<Student> students;
    private ArrayList<String> studentNames;
    private ListView lvStudents;
    private ProgressBar pbStat;

    public TeacherStudentFoodStatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_student_food_stat, container, false);

        db = FirebaseFirestore.getInstance();
        lvStudents = view.findViewById(R.id.lvStudents);
        pbStat = view.findViewById(R.id.pbStat);

        students = new ArrayList<>();
        studentNames = new ArrayList<>();

        studentlariGetir();

        return view;
    }

    private void studentlariGetir(){
        pbStat.setVisibility(View.VISIBLE);

        Log.d(TAG, "STUDENTLARI GETIRMEYE CALISIYORUM");
        Query query = db.collection("Students").whereEqualTo("classID", teacher.getTeacherClassID());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!(documentSnapshot.exists())){
                        Log.d(TAG, "STUDENT BULUNAMADI");
                        break;
                    }

                    Student student = new Student();
                    student.setName(documentSnapshot.getString("name"));
                    student.setStudentID(documentSnapshot.getId());
                    Log.d(TAG, "STUDENT NAME: " + student.getName());
                    students.add(student);
                    studentNames.add(student.getName());
                }
                lvStudentsDoldur();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "STUDENT GETIRME HATA: " + e.getMessage());
                pbStat.setVisibility(View.GONE);
            }
        });
    }

    private void lvStudentsDoldur(){
        if(studentNames == null || studentNames.size() < 1)
            return;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, studentNames);
        lvStudents.setAdapter(adapter);
        lvStudents.setVisibility(View.VISIBLE);
        pbStat.setVisibility(View.GONE);
    }

    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
}
