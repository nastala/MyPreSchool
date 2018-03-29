package com.example.mypreschool.Fragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.mypreschool.Fragments.AdminFragments.AdminMainFragment;
import com.example.mypreschool.Fragments.TeacherFragments.TeacherMainFragment;
import com.example.mypreschool.ParentChatActivity;
import com.example.mypreschool.R;
import com.example.mypreschool.SharedPref;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LOGIN";

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar pbLogin;
    private FirebaseFirestore db;
    private SharedPref sharedPref;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        db = FirebaseFirestore.getInstance();
        sharedPref = new SharedPref(getActivity().getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        pbLogin = view.findViewById(R.id.pbLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                login(email, password);
            }
        });

        authKontrolEt();

        return view;
    }

    private void authKontrolEt(){
        if(mAuth.getCurrentUser() != null){
            String tip = sharedPref.getTip();
            if(tip.equals("teacher")){
                teacherEkraninaGit();
            }
            else
                veliOgrenciEkraninaGit();
        }
    }

    private void login(String email, String password){
        if(email.equals("admin") && password.equals("admin")){
            Log.d(TAG, "ADMIN LOGIN BASARILI");

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.flMainActivity, new AdminMainFragment());
            fragmentTransaction.commit();
            return;
        }

        pbLogin.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "LOGIN BASARILI");

                tipiGetir(authResult.getUser().getUid());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Login hata: " + e.getMessage());
            }
        });
    }

    private void tipiGetir(String id){
        db.collection("Users").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()) {
                    pbLogin.setVisibility(View.GONE);
                    return;
                }

                String tip = documentSnapshot.getString("type");
                String username = documentSnapshot.getString("userName");

                sharedPref.setTip(tip);
                sharedPref.setUsername(username);
                Log.d(TAG, "TIP GELDI " + tip);

                /*if(tip.equals("teacher")){
                    teacherEkraninaGit();
                }
                else {
                    veliOgrenciEkraninaGit();
                }*/

                Intent intent = new Intent(getActivity(), ParentChatActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "TIP GETIRME HATA: " + e.getMessage());
                pbLogin.setVisibility(View.GONE);
            }
        });
    }

    private void teacherEkraninaGit(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.flMainActivity, new TeacherMainFragment());
        fragmentTransaction.commit();
    }

    private void veliOgrenciEkraninaGit(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.flMainActivity, new SelectStudentScreenFragment());
        fragmentTransaction.commit();
    }
}
