package com.example.mypreschool.Fragments.TeacherFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherChatMainFragment extends Fragment {
    private final String TAG = "TEACHERCHATMAINFRAGMENT";

    private FirebaseFirestore db;
    private Teacher teacher;

    public TeacherChatMainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_chat_main, container, false);

        return view;
    }

    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
}
