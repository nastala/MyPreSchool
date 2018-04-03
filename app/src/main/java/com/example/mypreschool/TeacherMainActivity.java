package com.example.mypreschool;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.Fragments.TeacherFragments.TeacherChatMainFragment;
import com.example.mypreschool.Fragments.TeacherFragments.TeacherMainFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.HashMap;

public class TeacherMainActivity extends AppCompatActivity {
    private final String TAG = "TEACHERMAINACTIVITY";

    private boolean teacherControl = false;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Teacher teacher;
    private SharedPref sharedPref;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(!teacherControl) {
                Log.d(TAG, "Teacher gelmedi daha.");
                return false;
            }

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    TeacherMainFragment hedef = new TeacherMainFragment();
                    hedef.setTeacher(teacher);
                    goScreen(hedef);
                    return true;
                case R.id.navigation_dashboard:

                    return true;
                case R.id.navigation_notifications:

                    return true;

                case R.id.navigation_chat:
                    TeacherChatMainFragment fragment = new TeacherChatMainFragment();
                    fragment.setTeacher(teacher);
                    goScreen(fragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        sharedPref = new SharedPref(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if(sharedPref.getTokenRefresh()) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "token: " + token);
            tokeniYenile(token);
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        teacherDetaylariGetir();
    }

    private void goScreen(Fragment hedef){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flTeacherMain, hedef);
        ft.commit();
    }

    private void tokeniYenile(String token) {
        Log.d(TAG, "Token yenileme cagrildi");

        HashMap<String, Object> map = new HashMap<>();
        map.put("sgcm", token);

        if(mAuth.getUid() == null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            return;
        }

        db.collection("Users").document(mAuth.getUid()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Token yenilendi");
                sharedPref.setTokenRefresh(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Token yenileme hata: " + e.getMessage());
            }
        });
    }

    private void teacherDetaylariGetir(){
        teacher = new Teacher();

        if(mAuth.getUid() == null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }

        db.collection("Teachers").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists())
                    return;

                teacher.setTeacherSchoolID(documentSnapshot.getString("schoolID"));
                teacher.setTeacherEmail(documentSnapshot.getString("email"));
                teacher.setTeacherName(documentSnapshot.getString("name"));
                teacher.setTeacherPhoneNumber(documentSnapshot.getString("phoneNumber"));
                teacher.setTeacherClassID(documentSnapshot.getString("classID"));
                teacher.setTeacherPhoto(documentSnapshot.getString("sgurl"));

                teacherControl = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TEACHERMAIN", "TEACHER GETIRME HATA: " + e.getMessage());
            }
        });
    }
}
