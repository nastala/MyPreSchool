package com.example.mypreschool.Fragments.TeacherFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.mypreschool.Adapters.GivenRequestsAdapter;
import com.example.mypreschool.Classes.GivenRequest;
import com.example.mypreschool.Classes.PermissionRequestClass;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherGivenPermissions2Fragment extends Fragment {
    private final String TAG = "TEACHERGIVENPER2";

    private ArrayList<GivenRequest> requests;
    private FirebaseFirestore db;
    private ProgressBar pbPermission;
    private ListView lvStudents;
    private PermissionRequestClass permissionRequestClass;

    private int size, count;

    public TeacherGivenPermissions2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_given_permissions2, container, false);

        db = FirebaseFirestore.getInstance();
        requests = new ArrayList<>();

        pbPermission = view.findViewById(R.id.pbPermission);
        lvStudents = view.findViewById(R.id.lvStudents);

        bringRequests();

        return view;
    }

    private void bringRequests() {
        pbPermission.setVisibility(View.VISIBLE);
        requests.clear();
        count = 0;

        db.collection("GivenRequests").whereEqualTo("permissionRequestID", permissionRequestClass.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if(documentSnapshots.isEmpty()){
                    Log.d(TAG, "Doc boş");
                    pbPermission.setVisibility(View.GONE);
                    return;
                }
                
                size = documentSnapshots.size();

                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    String permId = documentSnapshot.getString("permissionRequestID");
                    String studentId = documentSnapshot.getString("studentID");
                    boolean permission = documentSnapshot.getBoolean("permission");
                    GivenRequest request = new GivenRequest(studentId, permId, permission);

                    bringStudentName(request);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Given Requests Hata: " + e.getMessage());
                pbPermission.setVisibility(View.GONE);
            }
        });
    }

    private void bringStudentName(final GivenRequest request) {
        db.collection("Students").document(request.getStudentID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    Log.d(TAG, "Böyle bir öğrenci yok id: " + request.getStudentID());
                    return;
                }

                request.setStudentName(documentSnapshot.getString("name"));
                requests.add(request);
                count++;
                
                if(count >= size){
                    count = 0;
                    showLvRequests();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Student name getirme hata: " + e.getMessage());
                pbPermission.setVisibility(View.GONE);
            }
        });
    }

    private void showLvRequests() {
        if(getActivity() == null)
            return;
        
        if(requests.size() < 1){
            Log.d(TAG, "Requests size < 1");
            pbPermission.setVisibility(View.GONE);
        }
        
        sortRequests();

        GivenRequestsAdapter adapter = new GivenRequestsAdapter(getActivity(), requests);
        lvStudents.setAdapter(adapter);
        pbPermission.setVisibility(View.GONE);
        lvStudents.setVisibility(View.VISIBLE);
    }

    private void sortRequests() {

    }

    public void setPermissionRequestClass(PermissionRequestClass permissionRequestClass) {
        this.permissionRequestClass = permissionRequestClass;
    }
}
