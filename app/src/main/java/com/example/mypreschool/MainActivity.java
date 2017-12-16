package com.example.mypreschool;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.mypreschool.Fragments.LoginFragment;

public class MainActivity extends AppCompatActivity {
    private FrameLayout flMainActivity;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flMainActivity = findViewById(R.id.flMainActivity);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.flMainActivity, new LoginFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (fragment != null && fragment.getChildFragmentManager().getBackStackEntryCount() > 0){
            fragment.getChildFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
    }
}
