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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.Fragments.LoginFragment;
import com.example.mypreschool.Fragments.TeacherFragments.TeacherChatMainFragment;
import com.example.mypreschool.Fragments.TeacherFragments.TeacherListSharedActivitiesFragment;
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
    private ProgressBar pbMain;

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
                    TeacherListSharedActivitiesFragment teacherListSharedActivitiesFragment = new TeacherListSharedActivitiesFragment();
                    teacherListSharedActivitiesFragment.setTeacher(teacher);
                    goScreen(teacherListSharedActivitiesFragment);
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

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        sharedPref = new SharedPref(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        pbMain = findViewById(R.id.pbMain);

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
        pbMain.setVisibility(View.VISIBLE);

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
                Log.d("TEACHERMAIN", "Teacher geldi " + teacher.getTeacherSchoolID());
                teacher.setTeacherEmail(documentSnapshot.getString("email"));
                teacher.setTeacherName(documentSnapshot.getString("name"));
                teacher.setTeacherPhoneNumber(documentSnapshot.getString("phoneNumber"));
                teacher.setTeacherClassID(documentSnapshot.getString("classID"));
                teacher.setTeacherPhoto(documentSnapshot.getString("sgurl"));

                teacherControl = true;
                TeacherMainFragment hedef = new TeacherMainFragment();
                hedef.setTeacher(teacher);
                goScreen(hedef);
                pbMain.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TEACHERMAIN", "TEACHER GETIRME HATA: " + e.getMessage());
                pbMain.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                if(mAuth != null)
                    mAuth.signOut();

                FragmentManager fm = this.getFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }

                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                this.finish();

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
