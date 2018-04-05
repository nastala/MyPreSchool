package com.example.mypreschool.Fragments.TeacherFragments;


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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;
import com.example.mypreschool.Requests.PermissionRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeacherRequestPermissionFragment extends Fragment {
    private final String TAG = "TEACHERREQUESTPERMISSIO";

    private FirebaseFirestore db;
    private Teacher teacher;
    private EditText etTitle, etDetails, etPrice;
    private Button btnSubmit;
    private ProgressBar pbRequest;
    private String details, title;
    private double price;
    private ArrayList<String> parentTokens;

    public TeacherRequestPermissionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_request_permission, container, false);

        db = FirebaseFirestore.getInstance();
        parentTokens = new ArrayList<>();

        etDetails = view.findViewById(R.id.etDetails);
        etTitle = view.findViewById(R.id.etTitle);
        etPrice = view.findViewById(R.id.etPrice);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        pbRequest = view.findViewById(R.id.pbRequest);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check()){
                    addPermissionRequestToDB();
                }
            }
        });

        return view;
    }

    private void addPermissionRequestToDB() {
        btnSubmit.setClickable(false);
        pbRequest.setVisibility(View.VISIBLE);

        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("details", details);
        map.put("classID", teacher.getTeacherClassID());
        map.put("price", price);

        db.collection("PermissionRequests").document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Permission Requested Successfully!", Toast.LENGTH_SHORT).show();
                etDetails.setText("");
                etPrice.setText("");
                etTitle.setText("");
                pbRequest.setVisibility(View.GONE);
                btnSubmit.setClickable(true);
                getParentTokens();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "An error occurred!", Toast.LENGTH_SHORT).show();
                pbRequest.setVisibility(View.GONE);
                btnSubmit.setClickable(true);
                Log.d(TAG, "Permission kaydedilirken hata meydana geldi. Hata: " + e.getMessage());
            }
        });
    }

    private boolean check() {
        boolean check = true;

        details = etDetails.getText().toString();
        title = etTitle.getText().toString();

        if(etPrice.getText().toString().isEmpty())
            price = 0;
        else
            price = Double.valueOf(etPrice.getText().toString());

        if(details.isEmpty()){
            etDetails.setError("Details can not be empty!");
            check = false;
        }

        if(title.isEmpty()){
            etTitle.setError("Title can not be empty!");
            check = false;
        }

        return check;
    }

    private ArrayList<String> getParentTokens(){
        parentTokens.clear();

        db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("type", "parent").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!documentSnapshot.exists()){
                        Log.d(TAG, "Users'da parent yok");
                        return;
                    }

                    String token = documentSnapshot.getString("sgcm");
                    Log.d(TAG, "token: " + token);

                    if(checkToken(token, parentTokens)) {
                        parentTokens.add(token);
                        sendNotification(token, title);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Users getirme hata: " + e.getMessage());
            }
        });

        return parentTokens;
    }

    private void sendNotification(String token, String title) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response geldi. Response: " + response);
            }
        };

        PermissionRequest request = new PermissionRequest(token, title, listener);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    private boolean checkToken(String token, ArrayList<String> tokens) {
        boolean check = true;

        if(tokens == null)
            return true;

        for(String sgcm : tokens){
            if(sgcm.equals(token)) {
                check = false;
                break;
            }
        }

        return check;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
