package com.example.mypreschool.Fragments.BottomNavigationFragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mypreschool.Adapters.TeacherContactAdapter;
import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.Classes.TeacherContact;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherProfileFragment extends Fragment {
    private final String TAG = "TEACHERPROFILEFRAGMENT";

    private ArrayList<TeacherContact> contacts;
    private Student student;
    private Teacher teacher;
    private FirebaseFirestore db;
    private CircleImageView civTeacher;
    private TextView tvTeacherName;
    private ListView lvContact;

    public TeacherProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_profile, container, false);

        db = FirebaseFirestore.getInstance();

        civTeacher = view.findViewById(R.id.civTeacher);
        tvTeacherName = view.findViewById(R.id.tvTeacherName);
        lvContact = view.findViewById(R.id.lvContact);

        bringTeacherDetails();

        return view;
    }

    private void bringTeacherDetails() {
        db.collection("Teachers").whereEqualTo("classID", student.getClassID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!documentSnapshot.exists()){
                        Log.d(TAG, "Teacher bilgisi bulunamadı.");
                        return;
                    }

                    teacher = new Teacher();
                    teacher.setTeacherName(documentSnapshot.getString("name"));
                    teacher.setTeacherPhoneNumber(documentSnapshot.getString("phoneNumber"));
                    teacher.setTeacherPhoto(documentSnapshot.getString("sgurl"));
                    teacher.setTeacherEmail(documentSnapshot.getString("email"));
                    teacher.setTeacherID(documentSnapshot.getId());
                }

                fillTeacherFields();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Teacher bilgisi getirme hata: " + e.getMessage());
            }
        });
    }

    private void fillTeacherFields() {
        Glide.with(civTeacher.getContext())
                .load(teacher.getTeacherPhoto())
                .into(civTeacher);

        tvTeacherName.setText(teacher.getTeacherName());
        contacts = new ArrayList<>();
        contacts.add(new TeacherContact("Call:", teacher.getTeacherPhoneNumber(), R.drawable.call));
        contacts.add(new TeacherContact("Chat", "", R.drawable.chat));
        contacts.add(new TeacherContact("E-mail:", teacher.getTeacherEmail(), R.drawable.email));
        TeacherContactAdapter adapter = new TeacherContactAdapter(getActivity(), contacts);
        lvContact.setAdapter(adapter);
    }

    public void setStudent(Student student){
        Log.d(TAG, "Student Name: " + student.getName());
        this.student = student;
    }
}
