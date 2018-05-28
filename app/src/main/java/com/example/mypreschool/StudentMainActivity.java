package com.example.mypreschool;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.Fragments.BottomNavigationFragments.ParentChatMainFragment;
import com.example.mypreschool.Fragments.BottomNavigationFragments.StudentAnnouncementFragment;
import com.example.mypreschool.Fragments.BottomNavigationFragments.StudentMainFragment;
import com.example.mypreschool.Fragments.BottomNavigationFragments.StudentNotificationsFragment;
import com.example.mypreschool.Fragments.BottomNavigationFragments.StudentShareListFragment;
import com.example.mypreschool.Fragments.BottomNavigationFragments.TeacherProfileFragment;
import com.example.mypreschool.Fragments.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;

public class StudentMainActivity extends AppCompatActivity {
    private final String TAG = "STUDENTMAIN";

    private Student student;
    private FrameLayout flStudentMain;
    private FirebaseAuth mAuth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    StudentMainFragment studentMainFragment = new StudentMainFragment();
                    studentMainFragment.setStudent(student);
                    ekraniGetir(studentMainFragment);
                    return true;
                case R.id.navigation_dashboard:
                    StudentShareListFragment studentShareListFragment = new StudentShareListFragment();
                    Log.d(TAG, student.getName());
                    studentShareListFragment.setStudent(student);
                    ekraniGetir(studentShareListFragment);

                    return true;
                case R.id.navigation_notifications:
                    StudentNotificationsFragment hedef = new StudentNotificationsFragment();
                    hedef.setStudent(student);
                    ekraniGetir(hedef);
                    return true;
                case R.id.navigation_chat:
                    ParentChatMainFragment parentChatMainFragment = new ParentChatMainFragment();
                    ekraniGetir(parentChatMainFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        student = (Student)(getIntent().getSerializableExtra("student"));
        mAuth = FirebaseAuth.getInstance();

        flStudentMain = findViewById(R.id.flStudentMain);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        StudentMainFragment studentMainFragment = new StudentMainFragment();
        studentMainFragment.setStudent(student);
        ekraniGetir(studentMainFragment);
    }

    public void setStudent(Student student){
        Log.d(TAG, "Student Name: " + student.getName() + " Class ID: " + student.getClassID());
        this.student = student;
    }

    private void ekraniGetir(Fragment hedef){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.flStudentMain, hedef);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parent_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TeacherProfileFragment teacherProfileFragment = new TeacherProfileFragment();
        teacherProfileFragment.setStudent(student);

        switch (item.getItemId()){
            case R.id.action_call_teacher:
                teacherProfileFragment.setExtra("call");
                ekraniGetir(teacherProfileFragment);
                break;

            case R.id.action_chat_teacher:
                teacherProfileFragment.setExtra("chat");
                ekraniGetir(teacherProfileFragment);
                break;

            case R.id.action_email_teacher:
                teacherProfileFragment.setExtra("email");
                ekraniGetir(teacherProfileFragment);
                break;

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

    public Student getStudent(){ return  student; }
}
