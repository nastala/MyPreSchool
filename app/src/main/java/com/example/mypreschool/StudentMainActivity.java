package com.example.mypreschool;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.Fragments.BottomNavigationFragments.StudentShareListFragment;
import com.example.mypreschool.Fragments.LoginFragment;

public class StudentMainActivity extends AppCompatActivity {
    private final String TAG = "STUDENTMAIN";

    private Student student;
    private FrameLayout flStudentMain;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:
                    StudentShareListFragment hedef = new StudentShareListFragment();
                    Log.d(TAG, student.getName());
                    hedef.setStudent(student);
                    fragmentTransaction.replace(R.id.flStudentMain, hedef);
                    fragmentTransaction.addToBackStack(hedef.getTag());
                    fragmentTransaction.commit();

                    return true;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        student = (Student)(getIntent().getSerializableExtra("student"));

        flStudentMain = findViewById(R.id.flStudentMain);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void setStudent(Student student){
        Log.d(TAG, "Student Name: " + student.getName() + " Class ID: " + student.getClassID());
        this.student = student;
    }

    public Student getStudent(){ return  student; }
}
