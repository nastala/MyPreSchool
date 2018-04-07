package com.example.mypreschool.Fragments.BottomNavigationFragments;


import android.content.ActivityNotFoundException;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mypreschool.Adapters.TeacherContactAdapter;
import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.Classes.TeacherContact;
import com.example.mypreschool.ParentChatActivity;
import com.example.mypreschool.R;
import com.example.mypreschool.SharedPref;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    private FirebaseDatabase database;
    private CircleImageView civTeacher;
    private TextView tvTeacherName;
    private ListView lvContact;
    private FirebaseAuth mAuth;
    private boolean memberCheck;

    public TeacherProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

        civTeacher = view.findViewById(R.id.civTeacher);
        tvTeacherName = view.findViewById(R.id.tvTeacherName);
        lvContact = view.findViewById(R.id.lvContact);

        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + teacher.getTeacherPhoneNumber()));
                        try {
                            startActivity(callIntent);
                        }catch (ActivityNotFoundException e){
                            Log.d(TAG, "ActivityNotFound exc: " + e.getMessage());
                        }
                        Log.d(TAG, "Call tıklandı");
                        break;
                    case 1:
                        Log.d(TAG, "Chat tıklandı");
                        checkMembers();
                        break;
                    case 2:
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:" + teacher.getTeacherEmail()));
                        try {
                            startActivity(intent);
                        }catch (ActivityNotFoundException e){
                            Log.d(TAG, "ActivityNotFound exc: " + e.getMessage());
                        }
                        break;
                }
            }
        });

        bringTeacherDetails();

        return view;
    }

    private void checkMembers() {
        database.getReference().child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    pushMembers();
                    return;
                }

                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();

                boolean check = false;
                String key = null;

                while (dataSnapshots.iterator().hasNext()){
                    DataSnapshot dataSnapshot1 = dataSnapshots.iterator().next();

                    if(dataSnapshot1.getValue() == null) {
                        Log.d(TAG, "DataSnapshot1 null");
                        return;
                    }

                    try {
                        JSONObject jsonObject = new JSONObject(dataSnapshot1.getValue().toString());
                        if(jsonObject.getBoolean(mAuth.getUid()) && jsonObject.getBoolean(teacher.getTeacherID())){
                            Log.d(TAG, "Username, TeacherName eşit");
                            key = dataSnapshot1.getKey();
                            check = true;
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(!check)
                    pushMembers();
                else {
                    Intent intent = new Intent(getActivity(), ParentChatActivity.class);
                    intent.putExtra("key", key);
                    intent.putExtra("user2Name", teacher.getTeacherName());
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void pushMembers(){
        Map<String, Boolean> map = new HashMap<>();
        map.put(mAuth.getUid(), true);
        map.put(teacher.getTeacherID(), true);
        database.getReference().child("members").push().setValue(map);

        checkMembers();
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
