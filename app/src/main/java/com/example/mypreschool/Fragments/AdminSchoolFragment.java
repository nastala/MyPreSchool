package com.example.mypreschool.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminSchoolFragment extends Fragment {
    private final String TAG = "ADMINSCHOOL";

    private ArrayList<String> schools;
    private EditText etSchoolName;
    private Button btnAddScholl;
    private ProgressBar pbAddSchool;
    private ListView lvSchools;
    private TextView tvAdminAddSchool;
    private FirebaseFirestore db;
    private ProgressBar pbAdmin;

    public AdminSchoolFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_school, container, false);

        db = FirebaseFirestore.getInstance();

        lvSchools = view.findViewById(R.id.lvSchools);
        tvAdminAddSchool = view.findViewById(R.id.tvAdminAddSchool);
        pbAdmin = view.findViewById(R.id.pbAdmin);

        tvAdminAddSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSchoolDialog();
            }
        });

        getSchools();

        return view;
    }

    private void getSchools(){
        schools = new ArrayList<>();

        pbAdmin.setVisibility(View.VISIBLE);

        db.collection("Schools").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!(documentSnapshot.exists())){
                        Log.d(TAG, "DOCUMENT NOT EXIST");
                        pbAdmin.setVisibility(View.GONE);
                        break;
                    }

                    schools.add(documentSnapshot.getString("name"));
                }

                pbAdmin.setVisibility(View.GONE);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, schools);
                lvSchools.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pbAdmin.setVisibility(View.GONE);
                Log.d(TAG, "DOCUMENT GETIRME HATA: " + e.getMessage());
            }
        });
    }

    private void showAddSchoolDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_add_school_dialog);

        etSchoolName = dialog.findViewById(R.id.etSchoolName);
        btnAddScholl = dialog.findViewById(R.id.btnAddSchool);
        pbAddSchool = dialog.findViewById(R.id.pbAddSchool);

        btnAddScholl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbAddSchool.setVisibility(View.VISIBLE);

                Map<String, Object> schoolDetail = new HashMap<>();
                schoolDetail.put("name", etSchoolName.getText().toString());

                db.collection("Schools").document().set(schoolDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "School Added", Toast.LENGTH_SHORT).show();
                        pbAddSchool.setVisibility(View.GONE);
                        getSchools();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "School Add Failed: " + e.getMessage());
                        pbAddSchool.setVisibility(View.GONE);
                    }
                });
            }
        });

        dialog.show();
    }

}
